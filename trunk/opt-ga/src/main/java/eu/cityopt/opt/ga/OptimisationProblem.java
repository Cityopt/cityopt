package eu.cityopt.opt.ga;

import java.util.Collection;
import java.util.Map;

import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.DecisionDomain;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationRunner;

/** The data for a Cityopt optimisation problem.
 * 
 * @author ttekth
 */
public class OptimisationProblem {
    public SimulationRunner runner;
    public SimulationInput inputConst;
    public Map<String, Map<String, DecisionDomain>>
        decisionVars;
    public Collection<InputExpression> inputExprs;
    public Collection<Constraint> constraints;
    public Collection<MetricExpression> metrics;
    public Collection<ObjectiveExpression> objs;
}
