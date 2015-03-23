package eu.cityopt.sim.eval.apros;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.SimulatorManager;
import eu.cityopt.sim.eval.util.TempDir;
import eu.cityopt.sim.eval.util.TimeUtils;
import eu.cityopt.sim.eval.util.UncloseableInputStream;

public class AprosModel implements SimulationModel {
    private static String USER_COMPONENT_PROPERTIES_FILENAME = "uc_props.xml";
    private static String MODEL_CONFIGURATION_FILENAME = "cityopt.properties";

    AprosManager manager;
    TempDir modelDir;
    String[] resultFiles;
    final Document uc_props;
    Instant timeOrigin;

    AprosModel(InputStream inputStream, AprosManager manager)
            throws IOException, ConfigurationException {
        this.manager = manager; 
        modelDir = new TempDir("sim");
        resultFiles = new String[] { "results.dat" };
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
        boolean filesFound = true;
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
                    filesFound = true;
                }
            }
        }
        if (!filesFound) {
            throw new ConfigurationException("No model files found in zip package");
        }
        if (ucs == null) {
            throw new ConfigurationException("No " + USER_COMPONENT_PROPERTIES_FILENAME
                    + " file found in zip package");
        }
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
                this.resultFiles =
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
}
