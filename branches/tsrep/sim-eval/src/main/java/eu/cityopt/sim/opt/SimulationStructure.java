package eu.cityopt.sim.opt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulationRunner;

/**
 * Container for a simulation model, its inputs, outputs and related metrics.
 * Does not contain any numerical data.
 *
 * @author Hannu Rummukainen
 */
public class SimulationStructure {
    public SimulationModel model;
    public Namespace namespace;
    public Collection<MetricExpression> metrics = new ArrayList<>();

    /**
     * Construct an empty structure.
     */
    public SimulationStructure(SimulationModel model, Namespace namespace) {
        this.model = model;
        this.namespace = namespace;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public SimulationRunner makeRunner()
            throws IOException, ConfigurationException {
        return model.getSimulatorManager().makeRunner(model, namespace);
    }
}
