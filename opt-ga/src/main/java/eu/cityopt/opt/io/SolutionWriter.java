package eu.cityopt.opt.io;

import java.io.IOException;
import java.io.OutputStream;

import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.opt.Solution;

/**
 * Write optimisation solutions to a file in some format.
 * @author ttekth
 */
public interface SolutionWriter {
    /**
     * Call this once before writing the solutions.
     */
    public void writeHeader(OutputStream str) throws IOException;

    /**
     * Write a single solution.
     */
    public void writeSolution(
            OutputStream wtr, DecisionValues dvals, Solution sol)
            throws IOException;
}