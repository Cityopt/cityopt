package eu.cityopt.sim.opt;

import java.util.Iterator;

/**
 * Interface for following the progress of an {@link OptimisationAlgorithm}.
 * The methods may be called concurrently from multiple threads.
 *
 * @author Hannu Rummukainen
 */
public interface OptimisationStateListener extends OptimisationLog {
    /**
     * Updates the current status string. This should be a short string such as
     * "iteration 37" or "63/127 scenarios".
     */
    public void setProgressState(String state);

    /**
     * Updates the pareto-optimality status of the solutions.
     * @param solutions the currently pareto-optimal solutions
     */
    public void updateParetoFront(Iterator<Solution> solutions);
}
