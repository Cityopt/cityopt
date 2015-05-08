package eu.cityopt.opt.io;

import java.io.Closeable;
import java.io.IOException;

import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.opt.Solution;

/**
 * Write optimisation solutions to a file in some format.
 * @author ttekth
 */
public interface SolutionWriter extends Closeable {
    /**
     * Write a single solution.
     */
    public void writeSolution(DecisionValues dvals, Solution sol)
            throws IOException;
}