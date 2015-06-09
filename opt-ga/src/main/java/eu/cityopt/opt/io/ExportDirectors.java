package eu.cityopt.opt.io;

import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.opt.OptimisationProblem;

/** Static utility methods for running {@link ExportBuilder}.
 * 
 * @author ttekth
 */
public class ExportDirectors {
    /**
     * Export an {@link OptimisationProblem}.
     * @param problem Problem to export
     * @param builder Builder to export with
     * @param scenario Scenario name
     * @param extSet External parameter set name
     * @return the builder
     */
    public static ExportBuilder build(
            OptimisationProblem problem, ExportBuilder builder,
            String scenario, String extSet) {
        builder.add(problem.getExternalParameters(), extSet);
        builder.add(problem.inputConst);
        Namespace ns = problem.getNamespace();
        problem.inputExprs.forEach(ie -> builder.add(ie, ns));
        builder.addOutputs(ns);
        problem.metrics.forEach(me -> builder.add(me, ns));
        problem.decisionVars.forEach(builder::add);
        problem.constraints.forEach(builder::add);
        problem.objectives.forEach(builder::add);
        return builder;
    }
}
