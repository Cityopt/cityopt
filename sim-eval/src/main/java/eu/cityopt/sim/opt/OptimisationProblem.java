package eu.cityopt.sim.opt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulationRunner;

/** The data for a Cityopt optimisation problem.
 * 
 * @author Timo Korvola
 */
public class OptimisationProblem {
    public String runName;
    public SimulationModel model;
    public SimulationInput inputConst;
    public List<DecisionVariable> decisionVars = new ArrayList<>();
    public Collection<InputExpression> inputExprs = new ArrayList<>();
    public List<Constraint> constraints = new ArrayList<>();
    public Collection<MetricExpression> metrics = new ArrayList<>();
    public List<ObjectiveExpression> objectives = new ArrayList<>();

    /**
     * Construct an empty problem.
     * The model will be initialised, everything else is left empty.
     * The empty inputConst references the given external parameters object.
     */
    public OptimisationProblem(SimulationModel model, ExternalParameters ext) {
        this.model = model;
        inputConst = new SimulationInput(ext);
    }

    public Namespace getNamespace() {
        return inputConst.getNamespace();
    }

    public ExternalParameters getExternalParameters() {
        return inputConst.getExternalParameters();
    }

    public SimulationRunner makeRunner()
            throws IOException, ConfigurationException {
        return model.getSimulatorManager().makeRunner(model, getNamespace());
    }
}
