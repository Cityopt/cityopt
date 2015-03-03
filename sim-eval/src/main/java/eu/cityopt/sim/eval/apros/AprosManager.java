package eu.cityopt.sim.eval.apros;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.transform.TransformerException;

import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.SimulatorConfigurationException;
import eu.cityopt.sim.eval.SimulatorManager;
import eu.cityopt.sim.eval.SimulatorManagers;

/**
 * A factory of {@link AprosModel}s and {@link AprosRunner}s.
 *
 * @author Hannu Rummukainen
 */
public class AprosManager implements SimulatorManager {
    static final String PROFILE_GLOB = "Apros-*";

    Path profileDir;
    String profileName;

    /**
     * Registers the Apros profiles found in a directory as known simulator
     * names.
     */
    public static void register(Path profileDir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(profileDir, PROFILE_GLOB)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    String profileName = entry.getFileName().toString();
                    SimulatorManagers.register(profileName,
                            new AprosManager(profileDir, profileName));
                }
            }
        }
    }

    AprosManager(Path profileDir, String profileName) {
        this.profileDir = profileDir;
        this.profileName = profileName;
    }

    @Override
    public String getSimulatorName() {
        return profileName;
    }

    @Override
    public SimulationModel parseModel(InputStream inputStream)
            throws IOException, SimulatorConfigurationException {
        return new AprosModel(inputStream, this);
    }

    @Override
    public SimulationRunner makeRunner(SimulationModel model, Namespace namespace)
            throws IOException, SimulatorConfigurationException {
        AprosModel aprosModel = (AprosModel) model;
        try {
            return new AprosRunner(profileDir, profileName, namespace, aprosModel.uc_props,
                    aprosModel.modelDir.getPath(), aprosModel.resultFiles);
        } catch (TransformerException e) {
            throw new SimulatorConfigurationException(
                    "Failed to process user component properties", e);
        }
    }
}
