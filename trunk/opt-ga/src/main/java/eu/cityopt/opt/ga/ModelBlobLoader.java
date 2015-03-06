package eu.cityopt.opt.ga;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.SimulatorManager;

/**
 * Load a SimulationModel from file.
 * The model is loaded by our constructor and can be retrieved with
 * {@link #get()}.
 */
public class ModelBlobLoader implements Provider<SimulationModel> {
    private SimulationModel model;

    @Inject
    public ModelBlobLoader(SimulatorManager manager,
                           @Named("model") Path file)
            throws IOException, ConfigurationException {
        try (InputStream stream = new FileInputStream(file.toFile())) {
            model = manager.parseModel(stream);
        }
    }
    
    @Override
    public SimulationModel get() {return model;}
}
