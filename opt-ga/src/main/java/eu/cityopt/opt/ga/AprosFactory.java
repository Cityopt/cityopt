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
     * factories with Guice (unless you change the config file contents,
     * so don't).
     * @param config node configuration file containing a JSON object array
     */
    @Inject(optional=true)
    public void setNodeConfig(@Named("nodeConfig") Path config)
            throws IOException {
        nodes = (new ObjectMapper()).readValue(
                config.toFile(),
                new TypeReference<List<Map<String, String>>>() {});
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
