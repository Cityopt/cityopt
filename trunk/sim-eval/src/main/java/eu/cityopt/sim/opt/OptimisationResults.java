package eu.cityopt.sim.opt;

import java.util.Collection;
import java.util.Collections;

/**
 * Result from a single run of a search or optimisation algorithm.
 * The simulation results are stored separately via SimulationStorage.
 *
 * As soon as there is at least one successfully evaluated solution, returning
 * this result should be preferred to bailing out with an exception.
 *
 * @author Hannu Rummukainen
 */
public class OptimisationResults {
    /** Reason for stopping the algorithm. */
    public AlgorithmStatus status;

    /** All non-dominated feasible solutions found. */
    public Collection<Solution> paretoFront = Collections.emptyList();

    public boolean isFeasible() {
        return !paretoFront.isEmpty();
    }

    public String toString() {
        return status + " : " + (isFeasible() ? "FEASIBLE" : "INFEASIBLE");
    }
}
