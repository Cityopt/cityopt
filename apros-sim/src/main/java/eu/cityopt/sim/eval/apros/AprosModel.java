package eu.cityopt.sim.eval.apros;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
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

import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.Namespace.Component;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulatorManager;
import eu.cityopt.sim.eval.SyntaxChecker;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.eval.AlienModelException;
import eu.cityopt.sim.eval.util.TempDir;
import eu.cityopt.sim.eval.util.TimeUtils;
import eu.cityopt.sim.eval.util.UncloseableInputStream;

public class AprosModel implements SimulationModel {
    private static String USER_COMPONENT_PROPERTIES_FILENAME = "uc_props.xml";
    private static String MODEL_CONFIGURATION_FILENAME = "cityopt.properties";
    private static String OVERVIEW_IMAGE_FILENAME = "overview.png";
    private static final Pattern DESCRIPTION_FILENAME_PATTERN =
            Pattern.compile("^README(?:_(.*)|)[.]html?$", Pattern.CASE_INSENSITIVE);

    AprosManager manager;
    String profileName;
    TempDir modelDir;
    String[] tsInputFiles;
    String[] resultFilePatterns;
    final Document uc_props;
    final Defaults defaults = new Defaults();
    Duration nominalSimulationRuntime;
    List<Pair<String, String>> modelOutputs;
    final Map<Pair<String, String>, Pair<double[], double[]>>
        tsInputs = new HashMap<>();
    final Map<String, String> descriptions = new HashMap<>();
    byte[] overviewImageBytes;

    AprosModel(String profileName, InputStream inputStream, AprosManager manager)
            throws IOException, ConfigurationException {
        this.manager = manager;
        this.profileName = profileName;
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

    @SuppressWarnings("resource")
    Document extractModelFiles(InputStream inputStream, Path dir)
            throws IOException, ConfigurationException {
        Document ucs = null;
        Set<Path> modelFiles = new HashSet<>();
        Path dirn = dir.normalize();
        try (ZipInputStream zis = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                Path target = dirn.resolve(name).normalize();
                if (!target.startsWith(dirn))
                    throw new AccessDeniedException(
                            "Invalid path in zip file: " + name);
                Matcher descr = DESCRIPTION_FILENAME_PATTERN.matcher(name);
                if (entry.isDirectory()) {
                    Files.createDirectories(target);
                } else if (name.equalsIgnoreCase(USER_COMPONENT_PROPERTIES_FILENAME)) {
                    if (ucs != null) {
                        throw new ConfigurationException(
                                "Model package contains multiple "
                                + USER_COMPONENT_PROPERTIES_FILENAME
                                + " files");
                    }
                    ucs = loadUserComponentProperties(zis);
                } else if (name.equalsIgnoreCase(MODEL_CONFIGURATION_FILENAME)) {
                    loadModelConfiguration(new UncloseableInputStream(zis));
                } else if (descr.matches()) {
                    byte[] bytes = getEntryBytes(entry, zis);
                    String text = new String(bytes, "Windows-1252");
                    setDescription(descr.group(1), text, name);
                } else if (name.equalsIgnoreCase(OVERVIEW_IMAGE_FILENAME)) {
                    overviewImageBytes = getEntryBytes(entry, zis);
                } else {
                    Files.createDirectories(target.getParent());
                    Files.copy(new UncloseableInputStream(zis),
                            target, StandardCopyOption.REPLACE_EXISTING);
                    modelFiles.add(target);
                }
            }
        }
        if (profileName == null) {
            throw new AlienModelException(
                    "No Apros profile specified in "
                    + MODEL_CONFIGURATION_FILENAME);
        }
        if (modelFiles.isEmpty()) {
            throw new ConfigurationException(
                    "No model files found in zip package");
        }
        if (ucs == null) {
            throw new ConfigurationException(
                    "No " + USER_COMPONENT_PROPERTIES_FILENAME
                    + " file found in zip package");
        }
        readTsInputs(dir);
        filterSampleOutputs(modelFiles);
        return ucs;
    }

    private byte[] getEntryBytes(ZipEntry entry, ZipInputStream zis)
            throws IOException {
        byte[] bytes = new byte[(int)entry.getSize()];
        try (DataInputStream dis = new DataInputStream(
                new UncloseableInputStream(zis))) {
            dis.readFully(bytes);
        }
        return bytes;
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
        String psep = Pattern.quote(System.getProperty("path.separator"));
        List<String> unknown = new ArrayList<>();
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            switch (key) {
            case "timeSeriesInputFiles":
                this.tsInputFiles = value.split(psep);
                break;
            case "resultFiles":
                this.resultFilePatterns = value.split(psep);
                break;
            case "timeOrigin":
                defaults.timeOrigin = TimeUtils.parseISO8601(value);
                break;
            case "simulationStart":
            	defaults.simulationStart = TimeUtils.parseISO8601(value);
            	break;
            case "simulationEnd":
            	defaults.simulationEnd = TimeUtils.parseISO8601(value);
            	break;
            case "nominalSimulationRuntime":
                this.nominalSimulationRuntime =
                    Duration.ofNanos((long)(1e9 * Double.valueOf(value)));
                break;
            case "aprosProfile":
                if (this.profileName == null) {
                    if (!manager.checkProfile(value)) {
                        throw new ConfigurationException(
                                "Invalid Apros profile " + value);
                    }
                    this.profileName = value;
                }
                break;
            default:
                unknown.add(key);
            }
        }
        if (!unknown.isEmpty()) {
            String msg = "Unknown properties in "
                       + MODEL_CONFIGURATION_FILENAME + ": "
                       + String.join(", ", unknown);
            throw profileName == null ? new AlienModelException(msg)
                                      : new ConfigurationException(msg);
        }
    }

    private void setDescription(String lang, String text, String context)
            throws ConfigurationException {
        if (lang == null) {
            lang = "en";
        }
        if (!Arrays.asList(Locale.getISOLanguages()).contains(lang)) {
            throw new ConfigurationException(
                    "Unknown language " + lang + " in " + context);
        }
        descriptions.put(lang, text);
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
        try (BufferedReader in = AprosIO.makeReader(path)) {
            List<Pair<String, String>>
                variables = AprosIO.parseResultHeader(in);
            if (modelOutputs == null) {
                modelOutputs = new ArrayList<>();
            }
            modelOutputs.addAll(variables);
        } catch (IOException e) {
            throw new IOException(path.getFileName() + ": " + e.getMessage(), e);
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
    public String getSimulatorName() {
        return profileName;
    }

    @Override
    public Defaults getDefaults() {
        return defaults;
    }

    @Override
    public Duration getNominalSimulationRuntime() {
        return nominalSimulationRuntime;
    }

    @Override
    public String getDescription(List<Locale.LanguageRange> priorityList) {
        String tag = Locale.lookupTag(priorityList, descriptions.keySet());
        return (tag != null) ? descriptions.get(tag) : null;
    }

    @Override
    public byte[] getOverviewImageData() {
        return overviewImageBytes;
    }

    //FIXME Use the logging framework.  Writers may throw, that would be bad.
    @Override
    public SimulationInput findInputsAndOutputs(
            Namespace newNamespace, Map<String, Map<String, String>> units,
            int detailLevel, Writer warningWriter)
                    throws IOException {
        try {
            newNamespace.initConfigComponent();
            SyntaxChecker syntaxChecker = new SyntaxChecker(
                    newNamespace.evaluator);
            Map<Pair<String, String>, Object> defaultValues = new HashMap<>();
            findUcInputs(uc_props, detailLevel, syntaxChecker, newNamespace,
                         units, defaultValues, warningWriter);
            findTsInputs(syntaxChecker, newNamespace, defaultValues,
                         warningWriter);
            findOutputs(syntaxChecker, newNamespace, warningWriter);
            SimulationInput defaultInput = new SimulationInput(
                    new ExternalParameters(newNamespace));
            if (defaults.timeOrigin != null) {
                if (defaults.simulationStart != null) {
                    defaultInput.put(
                            Namespace.CONFIG_COMPONENT,
                            Namespace.CONFIG_SIMULATION_START,
                            TimeUtils.toSimTime(
                                    defaults.simulationStart,
                                    defaults.timeOrigin));
                }
                if (defaults.simulationEnd != null) {
                    defaultInput.put(
                            Namespace.CONFIG_COMPONENT,
                            Namespace.CONFIG_SIMULATION_END,
                            TimeUtils.toSimTime(
                                    defaults.simulationEnd,
                                    defaults.timeOrigin));
                }
            }
            defaultValues.forEach(
                    (k, v) -> defaultInput.put(k.getLeft(), k.getRight(), v));
            return defaultInput;
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private void readTsInputs(Path dir) throws IOException {
        if (tsInputFiles == null)
            return;
        for (String fname : tsInputFiles) {
            try (BufferedReader rd = AprosIO.makeReader(
                    dir.resolve(fname))) {
                AprosIO.readFile(
                        rd, null, (name, times, values) -> tsInputs.put(
                                name, Pair.of(times, values)));
            }
        }
    }

    private void findTsInputs(SyntaxChecker chk, Namespace ns,
                              Map<Pair<String, String>, Object> defaults,
                              Writer warn)
                                      throws IOException {
        final Type DEFAULT_TS = Type.TIMESERIES_LINEAR;
        for (Map.Entry<Pair<String, String>, Pair<double[], double[]>>
                ent : tsInputs.entrySet()) {
            String
                cn = ent.getKey().getLeft(),
                vn = ent.getKey().getRight();
            if (!chk.isValidTopLevelName(cn)
                    || !chk.isValidAttributeName(vn)) {
                //FIXME Cope.  What a mess.
                throw new IllegalArgumentException(String.format(
                        "Syntax error in ts input name %s.%s",
                        cn, vn));
            }
            Namespace.Component c = ns.getOrNew(cn);
            Type typ = c.inputs.get(vn);
            if (typ == null) {
                typ = DEFAULT_TS;
                c.inputs.put(vn, typ);
            } else if (!typ.isTimeSeriesType()) {
                warn.write(String.format(
                        "Time series input %s.%s replaces"
                        + "scalar type %s.%n",
                        cn, vn, typ));
                typ = DEFAULT_TS;
                c.inputs.put(vn, typ);
            }
            defaults.put(ent.getKey(),
                         ns.evaluator.makeTS(typ, ent.getValue().getLeft(),
                                             ent.getValue().getRight()));
        }
    }

    void findOutputs(SyntaxChecker syntaxChecker, Namespace newNamespace, Writer warnings)
            throws IOException {
        if (modelOutputs == null) {
            warnings.write("Cannot determine output variables: "
                    + "No valid sample result files found in the zip package.\n");
        } else {
            boolean validOutputsFound = false;
            List<String> invalidComponentNames = new ArrayList<>();
            List<String> invalidOutputNames = new ArrayList<>();
            List<String> duplicateOutputNames = new ArrayList<>();
            for (Pair<String, String> variable : modelOutputs) {
                String componentName = variable.getLeft();
                String outputName = variable.getRight();
                if (syntaxChecker.isValidTopLevelName(componentName)) {
                    if (syntaxChecker.isValidAttributeName(outputName)) {
                        Namespace.Component
                            component = newNamespace.getOrNew(componentName);
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
                warnings.write("Invalid output name"+s+": "
                        + String.join(" ", invalidComponentNames) + "\n");
            }
            if ( ! invalidOutputNames.isEmpty()) {
                String s = (invalidOutputNames.size() == 1) ? "" : "s";
                warnings.write("Invalid output variable name"+s+": "
                        + String.join(" ", invalidOutputNames) + "\n");
            }
            if ( ! duplicateOutputNames.isEmpty()) {
                String s = (duplicateOutputNames.size() == 1) ? "" : "s";
                warnings.write("Duplicate output parameter name"+s+": "
                        + String.join(" ", duplicateOutputNames) + "\n");
            }
            if (!validOutputsFound) {
                warnings.write("No valid output parameters found.\n");
            }
        }
    }

    private static void findUcInputs(
            Node rootNode, int detailLevel, SyntaxChecker syntaxChecker,
            Namespace newNamespace, Map<String, Map<String, String>> units,
            Map<Pair<String, String>, Object> defaults, Writer warnings)
                    throws IOException {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        try {
            XPathExpression propExpr = xpath.compile("property[@type=\"constant\"]");
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
                            Node propNameNode = propNode.getAttributes().getNamedItem("name");
                            Node propValueNode = propNode.getAttributes().getNamedItem("value");
                            Node propUnitNode = propNode.getAttributes().getNamedItem("unit");
                            if (propNameNode == null || propValueNode == null) {
                                throw new IOException("Invalid property in module '" + moduleName
                                        + "' in " + USER_COMPONENT_PROPERTIES_FILENAME);
                            }
                            String propName = propNameNode.getNodeValue();
                            String propValue = propValueNode.getNodeValue();
                            String propUnit = (propUnitNode != null)
                            		? propUnitNode.getNodeValue() : null;
                            try {
                                double value = Double.valueOf(propValue);

                                if (syntaxChecker.isValidAttributeName(propName)) {
                                    Component component = newNamespace.getOrNew(moduleName);
                                    Type old = component.inputs.putIfAbsent(propName, Type.DOUBLE);
                                    if (old == null) {
                                        defaults.putIfAbsent(
                                                Pair.of(moduleName, propName),
                                                value);
                                        if (propUnit != null && units != null
                                                && !propUnit.isEmpty()) {
                                            units.computeIfAbsent(moduleName,  k -> new HashMap<>())
                                                 .putIfAbsent(propName, propUnit);
                                        }
                                    } else {
                                        duplicateInputNames.add(moduleName + "." + propName);
                                    }
                                    validInputsFound = true;
                                } else {
                                    invalidInputNames.add(propName);
                                }
                            } catch (NumberFormatException e) {
                                // The value is not a floating point literal -
                                // assume the input is not user-adjustable.
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
                    if (level > detailLevel) {
                        warnings.write("Increased detail level from "
                                + detailLevel + " to " + level + ".\n");
                    }
                    if ( ! invalidComponentNames.isEmpty()) {
                        String s = (invalidComponentNames.size() == 1) ? "" : "s";
                        warnings.write("Invalid input component name"+s+": "
                                + String.join(" ", invalidComponentNames) + "\n");
                    }
                    if ( ! invalidInputNames.isEmpty()) {
                        String s = (invalidInputNames.size() == 1) ? "" : "s";
                        warnings.write("Invalid input parameter name"+s+": "
                                + String.join(" ", invalidInputNames) + "\n");
                    }
                    if ( ! duplicateInputNames.isEmpty()) {
                        String s = (duplicateInputNames.size() == 1) ? "" : "s";
                        warnings.write("Duplicate input parameter name"+s+": "
                                + String.join(" ", duplicateInputNames) + "\n");
                    }
                    if (!validInputsFound) {
                        warnings.write("No valid input parameters found.\n");
                    }
                    return;
                }
                previousModuleCount = moduleNodes.getLength();
                ++level;
            }
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}
