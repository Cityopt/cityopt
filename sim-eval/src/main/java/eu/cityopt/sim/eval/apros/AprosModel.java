package eu.cityopt.sim.eval.apros;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.Namespace.Component;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulatorManager;
import eu.cityopt.sim.eval.SyntaxChecker;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.eval.util.TempDir;
import eu.cityopt.sim.eval.util.TimeUtils;
import eu.cityopt.sim.eval.util.UncloseableInputStream;

public class AprosModel implements SimulationModel {
    private static String USER_COMPONENT_PROPERTIES_FILENAME = "uc_props.xml";
    private static String MODEL_CONFIGURATION_FILENAME = "cityopt.properties";

    AprosManager manager;
    TempDir modelDir;
    String[] resultFilePatterns;
    final Document uc_props;
    Instant timeOrigin;
    List<String[]> modelOutputs;

    AprosModel(InputStream inputStream, AprosManager manager)
            throws IOException, ConfigurationException {
        this.manager = manager; 
        modelDir = new TempDir("cityopt_model");
        resultFilePatterns = new String[] { "results.dat" };
        try {
            uc_props = extractModelFiles(inputStream, modelDir.getPath());
        } catch (Throwable t) {
            modelDir.close();
            modelDir = null;
            throw t;
        }
    }

    Document extractModelFiles(InputStream inputStream, Path dir)
            throws IOException, ConfigurationException {
        Document ucs = null;
        Set<Path> modelFiles = new HashSet<>();
        try (ZipInputStream zis = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                Path target = dir.resolve(name);
                if (entry.isDirectory()) {
                    Files.createDirectories(target);
                } else if (name.equalsIgnoreCase(USER_COMPONENT_PROPERTIES_FILENAME)) {
                    if (ucs != null) {
                        throw new ConfigurationException(
                                "Model package contains multiple "
                                + USER_COMPONENT_PROPERTIES_FILENAME + " files");
                    }
                    ucs = loadUserComponentProperties(zis);
                } else if (name.equalsIgnoreCase(MODEL_CONFIGURATION_FILENAME)) {
                    loadModelConfiguration(new UncloseableInputStream(zis));
                } else {
                    Files.createDirectories(target.getParent());
                    Files.copy(new UncloseableInputStream(zis),
                            target, StandardCopyOption.REPLACE_EXISTING);
                    modelFiles.add(target);
                }
            }
        }
        if (modelFiles.isEmpty()) {
            throw new ConfigurationException("No model files found in zip package");
        }
        if (ucs == null) {
            throw new ConfigurationException("No " + USER_COMPONENT_PROPERTIES_FILENAME
                    + " file found in zip package");
        }
        filterSampleOutputs(modelFiles);
        return ucs;
    }

    private Document loadUserComponentProperties(ZipInputStream zis)
            throws IOException, ConfigurationException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(new UncloseableInputStream(zis));
        } catch (ParserConfigurationException | SAXException e) {
            throw new ConfigurationException(
                    "Failed to read user component properties from "
                    + USER_COMPONENT_PROPERTIES_FILENAME, e);
        }
    }

    private void loadModelConfiguration(InputStream inputStream)
            throws IOException, ConfigurationException {
        Properties properties = new Properties();
        properties.load(inputStream);
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            switch (key) {
            case "resultFiles":
                this.resultFilePatterns =
                    value.split(Pattern.quote(System.getProperty("path.separator")));
                break;
            case "timeOrigin":
                this.timeOrigin = TimeUtils.parseISO8601(value);
                break;
            default:
                throw new ConfigurationException(
                        "Unknown property " + key + " in " + MODEL_CONFIGURATION_FILENAME);
            }
        }
    }

    private void filterSampleOutputs(Set<Path> modelFiles) throws IOException {
        for (String pattern : resultFilePatterns) {
            PathMatcher matcher = modelDir.getPath().getFileSystem().getPathMatcher(
                    "glob:**/" + pattern);
            for (Path path : modelFiles) {
                if (matcher.matches(path)) {
                    readSampleOutput(path);
                    Files.delete(path);
                }
            }
        }
    }

    private void readSampleOutput(Path path) throws IOException,
            FileNotFoundException {
        try (InputStream stream = new FileInputStream(path.toFile());
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(stream))) {
            try {
                List<String[]> variables = AprosJob.parseResultHeader(in);
                if (modelOutputs == null) {
                    modelOutputs = new ArrayList<>();
                }
                modelOutputs.addAll(variables);
            } catch (IOException e) {
                throw new IOException(path.getFileName() + ": " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (modelDir != null) {
            modelDir.close();
            modelDir = null;
        }
    }

    @Override
    public SimulatorManager getSimulatorManager() {
        return manager;
    }

    @Override
    public Instant getTimeOrigin() {
        return timeOrigin;
    }

    @Override
    public Document getAprosUserComponentStructure() {
        return uc_props;
    }

    @Override
    public String findInputsAndOutputs(
            Namespace newNamespace, int detailLevel) throws IOException {
        try {
            SyntaxChecker syntaxChecker = new SyntaxChecker(newNamespace.evaluator);
            String inputWarnings = findInputs(
                    uc_props, detailLevel, syntaxChecker, newNamespace);
            String outputWarnings = findOutputs(syntaxChecker, newNamespace);
            return inputWarnings + outputWarnings;
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    String findOutputs(SyntaxChecker syntaxChecker, Namespace newNamespace) {
        String warnings = "";
        if (modelOutputs == null) {
            warnings += "Cannot determine output variables: "
                    + "No valid sample result files found in the zip package.\n";
        } else {
            boolean validOutputsFound = false;
            List<String> invalidComponentNames = new ArrayList<>();
            List<String> invalidOutputNames = new ArrayList<>();
            List<String> duplicateOutputNames = new ArrayList<>();
            for (String[] variable : modelOutputs) {
                String componentName = variable[0];
                String outputName = variable[1];
                if (syntaxChecker.isValidTopLevelName(componentName)) {
                    if (syntaxChecker.isValidAttributeName(outputName)) {
                        Namespace.Component component = newNamespace.getOrNew(variable[0]);
                        Type old = component.outputs.putIfAbsent(
                                outputName, Type.TIMESERIES_LINEAR);
                        if (old != null) {
                            duplicateOutputNames.add(componentName + "." + outputName);
                        }
                        validOutputsFound = true;
                    } else {
                        invalidOutputNames.add(outputName);
                    }
                } else {
                    invalidComponentNames.add(componentName); 
                }
            }
            if ( ! invalidComponentNames.isEmpty()) {
                String s = (invalidComponentNames.size() == 1) ? "" : "s";
                warnings += "Invalid output name"+s+": "
                        + String.join(" ", invalidComponentNames) + "\n";
            }
            if ( ! invalidOutputNames.isEmpty()) {
                String s = (invalidOutputNames.size() == 1) ? "" : "s";
                warnings += "Invalid output variable name"+s+": "
                        + String.join(" ", invalidOutputNames) + "\n";
            }
            if ( ! duplicateOutputNames.isEmpty()) {
                String s = (duplicateOutputNames.size() == 1) ? "" : "s";
                warnings += "Duplicate output parameter name"+s+": "
                        + String.join(" ", duplicateOutputNames) + "\n";
            }
            if (!validOutputsFound) {
                warnings += "No valid output parameters found.\n";
            }
        }
        return warnings.toString();
    }

    private static String findInputs(
            Node rootNode, int detailLevel,
            SyntaxChecker syntaxChecker, Namespace newNamespace) {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        try {
            XPathExpression propExpr = xpath.compile("property[@name]");
            int previousModuleCount = 0;
            int level = Math.max(0, detailLevel);
            while (true) {
                String moduleExpr =
                        "//node[@moduleName and count(ancestor::node) <= " + level + "]";
                NodeList moduleNodes = (NodeList) xpath.evaluate( 
                        moduleExpr, rootNode, XPathConstants.NODESET);
                List<String> invalidComponentNames = new ArrayList<>();
                List<String> invalidInputNames = new ArrayList<>();
                List<String> duplicateInputNames = new ArrayList<>();
                boolean propertiesSeen = false;
                boolean validInputsFound = false;
                for (int i = 0; i < moduleNodes.getLength(); ++i) {
                    Node moduleNode = moduleNodes.item(i);
                    String moduleName =
                            moduleNode.getAttributes().getNamedItem("moduleName").getNodeValue();
                    NodeList propNodes = (NodeList) propExpr.evaluate(
                            moduleNode, XPathConstants.NODESET);
                    if (syntaxChecker.isValidTopLevelName(moduleName)) {
                        for (int j = 0; j < propNodes.getLength(); ++j) {
                            Node propNode = propNodes.item(j);
                            String propName = propNode.getAttributes().getNamedItem(
                                    "name").getNodeValue();
                            if (syntaxChecker.isValidAttributeName(propName)) {
                                Component component = newNamespace.getOrNew(moduleName);
                                Type old = component.inputs.putIfAbsent(propName, Type.DOUBLE);
                                if (old != null) {
                                    duplicateInputNames.add(moduleName + "." + propName);
                                }
                                validInputsFound = true;
                            } else {
                                invalidInputNames.add(propName);
                            }
                        }
                    } else if (propNodes.getLength() > 0) {
                        invalidComponentNames.add(moduleName);
                    }
                    if (propNodes.getLength() > 0) {
                        propertiesSeen = true;
                    }
                }
                // Exit if there are any properties on this level, or if the number
                // of module nodes is no longer increasing.
                if (propertiesSeen || moduleNodes.getLength() <= previousModuleCount) {
                    String warnings = "";
                    if (level > detailLevel) {
                        warnings += "Increased detail level from "
                                + detailLevel + " to " + level + ".\n";
                    }
                    if ( ! invalidComponentNames.isEmpty()) {
                        String s = (invalidComponentNames.size() == 1) ? "" : "s";
                        warnings += "Invalid input component name"+s+": "
                                + String.join(" ", invalidComponentNames) + "\n";
                    }
                    if ( ! invalidInputNames.isEmpty()) {
                        String s = (invalidInputNames.size() == 1) ? "" : "s";
                        warnings += "Invalid input parameter name"+s+": "
                                + String.join(" ", invalidInputNames) + "\n";
                    }
                    if ( ! duplicateInputNames.isEmpty()) {
                        String s = (duplicateInputNames.size() == 1) ? "" : "s";
                        warnings += "Duplicate input parameter name"+s+": "
                                + String.join(" ", duplicateInputNames) + "\n";
                    }
                    if (!validInputsFound) {
                        warnings += "No valid input parameters found.\n";
                    }
                    return warnings;
                }
                previousModuleCount = moduleNodes.getLength();
                ++level;
            }
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}
