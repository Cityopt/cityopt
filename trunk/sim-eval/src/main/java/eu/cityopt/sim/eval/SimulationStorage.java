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
     * instance.
     * 
     * @param output
     *            the simulation output data, containing a reference to the
     *            corresponding input data and external parameter values.
     */
    public void put(SimulationOutput output);

    /**
     * Stores the simulation output that results with the given input. The
     * output may be either a SimulationFailure instance or a SimulationResults
     * instance. Also saves a human-readable name and description for the
     * simulated scenario.
     *
     * @param output
     *            the simulation output data, containing a reference to the
     *            corresponding input data and external parameter values.
     * @param scenarioName
     *            brief human-readable name for the simulated scenario
     * @param scenarioDescription
     *            more detailed human-readable description for the simulated
     *            scenario
     */
    public void put(SimulationOutput output,
            String scenarioName, String scenarioDescription);

    /** Updates the metric values of a previously stored successful simulation. */
    public void updateMetricValues(MetricValues metricValues);

    //TODO save metrics & output in the same transaction
    //TODO save feasibility flag in scenario generation
}
