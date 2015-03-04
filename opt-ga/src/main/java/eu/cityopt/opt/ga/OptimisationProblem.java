package eu.cityopt.opt.ga;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.DecisionDomain;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.SimulatorConfigurationException;

/** The data for a Cityopt optimisation problem.
 * 
 * @author ttekth
 */
@Singleton
public class OptimisationProblem {
    public SimulationModel model;
    public SimulationInput inputConst;
    public Map<String, Map<String, DecisionDomain>>
        decisionVars = new HashMap<>();
    public Collection<InputExpression> inputExprs = new ArrayList<>();
    public List<Constraint> constraints = new ArrayList<>();
    public Collection<MetricExpression> metrics = new ArrayList<>();
    public List<ObjectiveExpression> objs= new ArrayList<>();
    
    /**
     * Construct an empty problem.
     * The model and namespace will be initialised, everything else is
     * left empty.
     */
    @Inject
    public OptimisationProblem(SimulationModel model, Namespace ns) {
        this.model = model;
        ExternalParameters ext = new ExternalParameters(ns);
        inputConst = new SimulationInput(ext);
    }
    
    public Namespace getNamespace() {
        return inputConst.getNamespace();
    }
    
    public ExternalParameters getExternalParameters() {
        return inputConst.getExternalParameters();
    }

    public SimulationRunner makeRunner()
            throws IOException, SimulatorConfigurationException {
        return model.getSimulatorManager().makeRunner(model, getNamespace());
    }
}
