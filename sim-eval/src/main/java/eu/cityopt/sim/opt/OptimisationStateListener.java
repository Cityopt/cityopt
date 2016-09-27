package eu.cityopt.sim.opt;

import java.util.Iterator;

/**
 * Interface for following the progress of an {@link OptimisationAlgorithm}.
 * The methods may be called concurrently from multiple threads.
 *
 * @author Hannu Rummukainen
 */
public interface OptimisationStateListener extends OptimisationLog {
    /** Sets the maximum number of iterations to be expected.
     *  This is optional: if not called, then iterations are not counted.
     */
    public void setMaxIterations(int maxIterations);

    /** Sets the number of iterations completed.
     *  Counting iterations is optional: see {@link #setMaxIterations(int)}.
     */
    public void setIteration(int iteration);

    /** Sets the maximum number of individual evaluations to be expected. 
     *  Should be called in advance, if the maximum is known.
     */
    public void setMaxEvaluations(int maxEvaluations);

    /** To be called once after each individual evaluation. */
    public void evaluationCompleted();

    /**
     * Updates the pareto-optimality status of the solutions.
     * @param solutions the currently pareto-optimal solutions
     */
    public void updateParetoFront(Iterator<Solution> solutions);
}
