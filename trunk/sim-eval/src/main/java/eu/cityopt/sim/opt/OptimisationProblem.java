package eu.cityopt.sim.opt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.Type;

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

    /** Sets a constant value for any undefined input parameters. */
    public void fillDefaultInput(SimulationInput defaultInput) {
        for (Map.Entry<String, Namespace.Component> entry
                : inputConst.getNamespace().components.entrySet()) {
            String componentName = entry.getKey();
            for (Map.Entry<String, Type> inputType : entry.getValue().inputs.entrySet()) {
                String inputName = inputType.getKey();
                if ( ! hasInputFor(componentName, inputName)) {
                    inputConst.put(componentName, inputName,
                            defaultInput.get(componentName, inputName));
                }
            }
        }
    }

    boolean hasInputFor(String componentName, String inputName) {
        if (inputConst.contains(componentName, inputName)) {
            return true;
        }
        for (InputExpression expr : inputExprs) {
            if (expr.getInput().componentName.equals(componentName)
                    && expr.getInput().name.equals(inputName)) {
                return true;
            }
        }
        return false;
    }
}
