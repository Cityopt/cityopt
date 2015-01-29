package eu.cityopt.sim.eval.apros;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulatorConfigurationException;

public class AprosModel implements SimulationModel {
    private static String USER_COMPONENT_PROPERTIES_FILENAME = "nodes.xml";
    //TODO: support result file name configuration
    private static String CITYOPT_CONFIGURATION_FILENAME = "cityopt.properties";

    TempDir modelDir;
    String[] resultFiles;
    final Document uc_props;

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
                Path target = dir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(target);
                } else if (entry.getName().equalsIgnoreCase(USER_COMPONENT_PROPERTIES_FILENAME)) {
                    if (ucs != null) {
                        throw new SimulatorConfigurationException(
                                "Model package contains multiple "
                                + USER_COMPONENT_PROPERTIES_FILENAME + " files");
                    }
                    try {
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        ucs = db.parse(new UncloseableInputStream(zis));
                    } catch (ParserConfigurationException | SAXException e) {
                        throw new SimulatorConfigurationException(
                                "Failed to read user component properties from "
                                + USER_COMPONENT_PROPERTIES_FILENAME, e);
                    }
                } else {
                    Files.createDirectories(target.getParent());
                    Files.copy(new UncloseableInputStream(zis),
                            target, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        return ucs;
    }

    @Override
    public void close() throws IOException {
        if (modelDir != null) {
            modelDir.close();
            modelDir = null;
        }
    }
}
