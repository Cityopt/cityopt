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
import com.google.inject.name.Named;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.apros.AprosManager;
import eu.cityopt.sim.eval.apros.AprosRunner;

/**
 * Registers {@link AprosManager}s for all profiles in a directory
 * 
 * @author ttekth
 */
public class AprosFactory extends ModelFactory {
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
                Paths.get(aprosDir), Executors.newCachedThreadPool());
    }
    
    /**
     * Sets the simulator node configuration.
     * @param config node configuration file containing a JSON object array
     */
    //TODO Do this right.
    @Inject(optional=true)
    public void setNodeConfig(@Named("nodeConfig") Path config)
            throws IOException {
        AprosRunner.nodes = (new ObjectMapper()).readValue(
                config.toFile(),
                new TypeReference<List<Map<String, String>>>() {});
    }
}
