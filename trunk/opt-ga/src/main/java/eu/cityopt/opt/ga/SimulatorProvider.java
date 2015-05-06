package eu.cityopt.opt.ga;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.opt4j.core.start.Constant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.SimulatorManager;
import eu.cityopt.sim.eval.SimulatorManagers;
import eu.cityopt.sim.eval.apros.AprosManager;
import eu.cityopt.sim.eval.apros.AprosRunner;

/**
 * A Provider wrapper around {@link SimulatorManagers}.
 * @author ttekth
 */
public class SimulatorProvider implements Provider<SimulatorManager> {
    private SimulatorManager manager;

    @Override
    public SimulatorManager get() {return manager;}

    /**
     * Register all known SimulatorManagers and retrieve one by name.
     * New registration calls may need to be added here if new simulator
     * types are introduced.
     * @param aprosDir Apros profile directory to be registered.
     * @param simulator simulator name
     * @throws IOException if registration fails.
     * @throws ConfigurationException
     *     if the named simulator does not exist.
     */
    @Inject
    public SimulatorProvider(
            @Constant(value="aprosDir", namespace=SimulatorProvider.class)
            String aprosDir,
            @Constant(value="simulator", namespace=SimulatorProvider.class)
            String simulator)
                    throws IOException, ConfigurationException {
        AprosManager.register(
                Paths.get(aprosDir), Executors.newCachedThreadPool());
        manager = SimulatorManagers.get(simulator);
    }
    
    /**
     * Sets the simulator node configuration.
     * @param config node configuration file containing a JSON object array
     */
    @Inject(optional=true)
    public void setNodeConfig(@Named("nodeConfig") Path config)
            throws IOException {
        AprosRunner.nodes = (new ObjectMapper()).readValue(
                config.toFile(),
                new TypeReference<List<Map<String, String>>>() {});
    }
}
