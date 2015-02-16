package eu.cityopt.sim.eval.apros;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
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
import eu.cityopt.sim.eval.SimulatorConfigurationException;
import eu.cityopt.sim.eval.util.TempDir;
import eu.cityopt.sim.eval.util.UncloseableInputStream;

public class AprosModel implements SimulationModel {
    private static String USER_COMPONENT_PROPERTIES_FILENAME = "uc_props.xml";
    private static String MODEL_CONFIGURATION_FILENAME = "cityopt.properties";

    TempDir modelDir;
    String[] resultFiles;
    final Document uc_props;
    Instant timeOrigin;

    AprosModel(byte[] modelData) throws IOException, SimulatorConfigurationException {
        modelDir = new TempDir("sim");
        resultFiles = new String[] { "results.dat" };
        try {
            uc_props = extractModelFiles(modelData, modelDir.getPath());
        } catch (Throwable t) {
            modelDir.close();
            modelDir = null;
            throw t;
        }
    }

    Document extractModelFiles(byte[] modelZipBytes, Path dir)
            throws IOException, SimulatorConfigurationException {
        Document ucs = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(modelZipBytes);
                ZipInputStream zis = new ZipInputStream(bis)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                Path target = dir.resolve(name);
                if (entry.isDirectory()) {
                    Files.createDirectories(target);
                } else if (name.equalsIgnoreCase(USER_COMPONENT_PROPERTIES_FILENAME)) {
                    if (ucs != null) {
                        throw new SimulatorConfigurationException(
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
                }
            }
        }
        return ucs;
    }

    private Document loadUserComponentProperties(ZipInputStream zis)
            throws IOException, SimulatorConfigurationException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(new UncloseableInputStream(zis));
        } catch (ParserConfigurationException | SAXException e) {
            throw new SimulatorConfigurationException(
                    "Failed to read user component properties from "
                    + USER_COMPONENT_PROPERTIES_FILENAME, e);
        }
    }

    private void loadModelConfiguration(InputStream inputStream)
            throws IOException, SimulatorConfigurationException {
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
                this.timeOrigin = parseDateTime(value).toInstant();
                break;
            default:
                throw new SimulatorConfigurationException(
                        "Unknown property " + key + " in " + MODEL_CONFIGURATION_FILENAME);
            }
        }
    }

    private static ZonedDateTime parseDateTime(String dateString) {
        try {
            return ZonedDateTime.parse(dateString);
        } catch (DateTimeParseException e) {}
        try {
            return LocalDateTime.parse(dateString).atZone(ZoneId.systemDefault());
        } catch (DateTimeParseException e) {}
        return LocalDate.parse(dateString).atStartOfDay(ZoneId.systemDefault());
    }

    @Override
    public void close() throws IOException {
        if (modelDir != null) {
            modelDir.close();
            modelDir = null;
        }
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
