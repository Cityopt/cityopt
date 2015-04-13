package eu.cityopt.sim.eval.apros;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;

import javax.xml.transform.TransformerException;

import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.ConfigurationException;
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
    Executor executor;

    /**
     * Registers the Apros profiles found in a directory as known simulator
     * names.  The SimulatorManager instances will use the given Executor
     * for concurrency.
     */
    public static void register(Path profileDir, Executor executor) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(profileDir, PROFILE_GLOB)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    String profileName = entry.getFileName().toString();
                    SimulatorManagers.register(profileName,
                            new AprosManager(profileDir, profileName, executor));
                }
            }
        }
    }

    AprosManager(Path profileDir, String profileName, Executor executor) {
        this.profileDir = profileDir;
        this.profileName = profileName;
        this.executor = executor;
    }

    @Override
    public String getSimulatorName() {
        return profileName;
    }

    @Override
    public SimulationModel parseModel(InputStream inputStream)
            throws IOException, ConfigurationException {
        return new AprosModel(inputStream, this);
    }

    @Override
    public SimulationRunner makeRunner(SimulationModel model, Namespace namespace)
            throws IOException, ConfigurationException {
        AprosModel aprosModel = (AprosModel) model;
        try {
            return new AprosRunner(profileDir, profileName, executor,
                    namespace, aprosModel.uc_props, aprosModel.modelDir.getPath(),
                    aprosModel.resultFilePatterns);
        } catch (TransformerException e) {
            throw new ConfigurationException(
                    "Failed to process user component properties", e);
        }
    }

    @Override
    public void close() throws IOException {
    }
}
