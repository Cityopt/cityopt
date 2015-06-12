package eu.cityopt.opt.io;

import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.SimulationStructure;

/**
 * Static utility methods for running {@link ExportBuilder}.
 * These loop over one thing or another and feed the contents to
 * an ExportBuilder.
 * 
 * @author ttekth
 */
public class ExportDirectors {
    /**
     * Export an {@link OptimisationProblem}.
     * @param problem Problem to export
     * @param builder Builder to export with
     * @param extSet External parameter set name
     * @return the builder
     */
    public static ExportBuilder build(
            OptimisationProblem problem, ExportBuilder builder,
            String extSet) {
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
    
    /**
     * Export a {@link SimulationStructure}.
     * The data consist of inputs, outputs and metrics.  Types and metric
     * expressions are included, input and output values are not.
     * @param sim Structure to export
     * @param builder Builder to export with
     * @return the builder
     */
    public static ExportBuilder build(
            SimulationStructure sim, ExportBuilder builder) {
        Namespace ns = sim.getNamespace();
        builder.addExtParams(ns);
        builder.addInputs(ns);
        builder.addOutputs(ns);
        sim.metrics.forEach(me -> builder.add(me, ns));
        return builder;
    }
}
