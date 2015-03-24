package eu.cityopt.sim.eval;


/**
 * Access to stored simulation runs for the sim-eval module and scenario
 * generation code. Can be accessed concurrently.
 *<p>
 * All stored objects must refer to the same external parameter values.
 * 
 * @author Hannu Rummukainen
 */
public interface SimulationStorage extends Iterable<SimulationOutput> {
    /**
     * Finds the simulation output that results with the given input, if
     * available.
     * 
     * @param input
     *            the simulation input data.
     * @return either the corresponding deterministic simulation output, or null
     *         if the output is not available.  The output may be either a
     *         SimulationFailure instance, or a SimulationResults instance.
     */
    public SimulationOutput get(SimulationInput input);

    /**
     * Stores the simulation output that results with the given input. The
     * output may be either a SimulationFailure instance or a SimulationResults
     * instance. Also saves a human-readable name and description for the
     * simulated scenario.
     *
     * @param output
     *            the simulation output data, containing a reference to the
     *            corresponding input data and external parameter values.
     * @param scenarioNameAndDescription
     *            Array containing first a brief human-readable name for the
     *            simulated scenario, and then a more detailed description of
     *            the scenario.  The argument is ignored if the scenario has
     *            already been stored before.
     *            The argument may be left null, in which case a nondescript
     *            name may be generated.
     */
    public void put(SimulationOutput output,
            String[] scenarioNameAndDescription);

    /** Updates the metric values of a previously stored successful simulation. */
    public void updateMetricValues(MetricValues metricValues);

    //TODO save metrics & output in the same transaction
    //TODO save feasibility flag in scenario generation
}
