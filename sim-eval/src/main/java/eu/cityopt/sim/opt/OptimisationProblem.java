package eu.cityopt.sim.opt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;

/** The data for a simulation optimisation problem.
 * 
 * @author Timo Korvola
 */
public class OptimisationProblem extends SimulationStructure {
    public SimulationInput inputConst;
    public List<DecisionVariable> decisionVars = new ArrayList<>();
    public Collection<InputExpression> inputExprs = new ArrayList<>();
    public List<Constraint> constraints = new ArrayList<>();
    public List<ObjectiveExpression> objectives = new ArrayList<>();

    /**
     * Construct an empty problem.
     * The model will be initialised, everything else is left empty.
     * The empty inputConst references the given external parameters object.
     */
    public OptimisationProblem(SimulationModel model, ExternalParameters ext) {
        super(model, ext.getNamespace());
        inputConst = new SimulationInput(ext);
    }

    public ExternalParameters getExternalParameters() {
        return inputConst.getExternalParameters();
    }
}
