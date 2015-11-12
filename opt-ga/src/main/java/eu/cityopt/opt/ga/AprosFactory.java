package eu.cityopt.opt.ga;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.opt4j.core.start.Constant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.apros.AprosManager;

/**
 * Registers {@link AprosManager}s for all profiles in a directory
 * 
 * @author ttekth
 */
public class AprosFactory extends ModelFactory {
    private List<Map<String, String>> nodes = null;

    /**
     * @param aprosDir Apros profile directory to be registered.
     * @throws IOException if registration fails.
     * @throws ConfigurationException
     *     if the named simulator does not exist.
     */
    @Inject
    public AprosFactory(
            @Constant(value="aprosDir", namespace=AprosFactory.class)
            String aprosDir)
                    throws IOException, ConfigurationException {
        AprosManager.register(
                Paths.get(aprosDir), Executors.newCachedThreadPool(),
                System.out);
    }
    
    /**
     * Sets the simulator node configuration.
     * Because AprosManager is shared and static, this can work a bit
     * unpredictably.  For reliable results you should call this either
     * never or always after constructing a AprosFactory, always with the
     * same file.  Fortunately that is what happens if you create your
     * factories with Guice (unless you change the config file,
     * so don't).
     * @param config node configuration or null to stick with the AprosManager
     *   default.
     */
    @Inject(optional=true)
    public void setNodeConfig(
            @Named("nodeConfig") List<Map<String, String>> config) {
        nodes = config;
    }
    
    /**
     * A combination of {@link #readNodeConfig} and
     * {@link #setNodeConfig(List)}.
     */
    public void setNodeConfig(Path config) throws IOException {
        setNodeConfig(readNodeConfig(config));
    }
            
    
    /**
     * Read and return a node configuration from a JSON file.  The file
     * should contain a JSON array of objects, all fields strings.
     */
    public static List<Map<String, String>> readNodeConfig(Path conffile)
            throws IOException {
        return (new ObjectMapper()).readValue(
                conffile.toFile(),
                new TypeReference<List<Map<String, String>>>() {});
    }
    
    private static int parseCpus(String s) {
        try {
            return s == null ? 1 : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
    
    /**
     * Return the total number of CPUs in the given node configuration.
     * The number of CPUs in each node is expected to be stored under key
     * "cpu".  These map values are parsed as ints and summed over nodes.
     * Parse errors and missing values are treated as 1.
     */
    public static Integer numCpus(List<Map<String, String>> config) {
        return config.stream().map(m -> m.get("cpu"))
                .mapToInt(AprosFactory::parseCpus).sum();
    }

    @Override
    public SimulationModel loadModel(String simulator, InputStream in)
            throws IOException, ConfigurationException {
        SimulationModel model = super.loadModel(simulator, in);
        if (nodes != null) {
            ((AprosManager)model.getSimulatorManager()).setNodes(nodes);
        }
        return model;
    }
}
