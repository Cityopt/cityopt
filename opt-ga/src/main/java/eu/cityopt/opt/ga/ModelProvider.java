package eu.cityopt.opt.ga;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.inject.Singleton;

import org.opt4j.core.start.Constant;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.SimulationModel;

/**
 * Load a SimulationModel from file.
 * The model is loaded by our constructor and can be retrieved with
 * {@link #get()}.
 */
@Singleton
public class ModelProvider implements Provider<SimulationModel> {
    private SimulationModel model;

    @Inject
    public ModelProvider(ModelFactory factory,
            @Constant(value="simulator", namespace=ModelProvider.class)
            String simulator,
            @Named("model") Path file)
            throws IOException, ConfigurationException {
        try (InputStream stream = new FileInputStream(file.toFile())) {
            model = factory.loadModel(simulator.isEmpty() ? null : simulator,
                                      stream);
        }
    }
    
    @Override
    public SimulationModel get() {return model;}
}
