package eu.cityopt.sim.opt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Maintains a Pareto front, i.e. a collection of non-dominated solutions. 
 *
 * @author Hannu Rummukainen
 */
public class ParetoFront {
    /** The non-dominated solutions. */
    public List<Solution> solutions = new ArrayList<>();

    /**
     * Updates the collection of non-dominated solutions, given a new solution.
     * Any existing solutions dominated by the new solution are removed. The new
     * solution is added to the collection unless it is dominated by an existing
     * solution.
     * 
     */
    void add(Solution newSolution) {
        if (newSolution != null && newSolution.input != null
                && newSolution.constraintStatus.isDefinitelyFeasible()) {
            Iterator<Solution> it = solutions.iterator();
            while (it.hasNext()) {
                Solution oldSolution = it.next();
                Integer cmp = oldSolution.compareTo(newSolution);
                if (cmp != null) {
                    if (cmp < 0) {
                        // oldSolution dominates newSolution
                        return;
                    } else if (cmp > 0) {
                        // newSolution dominates oldSolution
                        it.remove();
                    }
                }
            }
            solutions.add(newSolution);
        }
    }
}
