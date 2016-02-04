package eu.cityopt.sim.eval.apros;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.xml.transform.TransformerException;

import org.simantics.simulation.scheduling.Experiment;
import org.simantics.simulation.scheduling.Server;
import org.simantics.simulation.scheduling.ServerFactory;
import org.simantics.simulation.scheduling.files.LocalDirectory;
import org.simantics.simulation.scheduling.status.StatusLoggingUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.SimulatorManager;
import eu.cityopt.sim.eval.SimulatorManagers;
import eu.cityopt.sim.eval.util.TempDir;

/**
 * A factory of {@link AprosModel}s and {@link AprosRunner}s.
 *
 * @author Hannu Rummukainen
 */
public class AprosManager implements SimulatorManager {
    static final String PROFILE_GLOB = "Apros-*";

    /** Prefix for temporary directories. */
    public static String tmpPrefix = "cityopt_apros";

    /** Server node configuration. */
    private List<Map<String, String>> nodes = ImmutableList.of(
            ImmutableMap.of("type", "local",
                            "cpu", "2"));
    
    private boolean nodesConfigured = false; 
    
    /** Profiles installed on the server. */
    private final Set<String> profiles = new HashSet<>();

    final Path profileDir;
    final Executor executor;

    final TempDir tmp;
    final Server server;

    /**
     * Registers the Apros profiles found in a directory as known simulator
     * names.  The SimulatorManager instances will use the given Executor
     * for concurrency.
     */
    public static void register(Path profileDir, Executor executor,
                                PrintStream log) throws IOException {
        AprosManager manager = null;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(profileDir, PROFILE_GLOB)) {
            for (Path entry : stream) {
                if (isProfileDirectory(entry)) {
                    String profileName = entry.getFileName().toString();
                    if (manager == null) {
                        manager = new AprosManager(profileDir, executor, log);
                    }
                    SimulatorManagers.register(profileName, manager);
                }
            }
        }
    }

    AprosManager(Path profileDir, Executor executor, PrintStream log) {
        this.profileDir = profileDir;
        this.executor = executor;
        try {
            tmp = new TempDir(tmpPrefix);
            try {
                server = ServerFactory.createLocalServer(
                        Files.createDirectory(tmp.getPath().resolve("srv")));
                StatusLoggingUtils.logServerStatus(log, server);
            } catch (Exception e) {
                tmp.close();
                throw e;
            }
        } catch (IOException e) {
            throw new RuntimeException("Simulation server setup failed.", e);
        }
    }
    
    synchronized void installProfile(String profile) {
        if (!profiles.contains(profile)) {
            server.installProfile(profile, new LocalDirectory(
                    profileDir.resolve(profile)));
            profiles.add(profile);
        }
    }
    
    Experiment createExperiment() {
        synchronized (this) {
            if (!nodesConfigured) {
                for (Map<String, String> p : nodes) {
                    server.createNode(p);
                }
                nodesConfigured = true;
            }
        }
        return server.createExperiment(new HashMap<>());
    }

    @Override
    public SimulationModel parseModel(String simulatorName, InputStream inputStream)
            throws IOException, ConfigurationException {
        return new AprosModel(simulatorName, inputStream, this);
    }

    @Override
    public SimulationRunner makeRunner(SimulationModel model, Namespace namespace)
            throws IOException, ConfigurationException {
        AprosModel aprosModel = (AprosModel) model;
        String profile = aprosModel.profileName;
        try {
            return new AprosRunner(
                    this, profile, namespace, aprosModel.uc_props,
                    aprosModel.tsInputFile,
                    aprosModel.modelDir.getPath(),
                    aprosModel.resultFilePatterns);
        } catch (TransformerException e) {
            throw new ConfigurationException(
                    "Failed to process user component properties", e);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (!tmp.isClosed()) {
            try {
                server.dispose();
            } finally {
                tmp.close();
            }
        }
    }

    boolean checkProfile(String value) {
        return isProfileDirectory(profileDir.resolve(value));
    }

    static boolean isProfileDirectory(Path path) {
        return Files.isDirectory(path);
    }

    /**
     * Set the server node configuration.
     * Call this before the first call to {@link #createExperiment()}.
     * Afterwards it has no effect, not even for later experiments.
     */
    public void setNodes(List<Map<String, String>> nodes) {
        this.nodes = nodes;
    }
}
