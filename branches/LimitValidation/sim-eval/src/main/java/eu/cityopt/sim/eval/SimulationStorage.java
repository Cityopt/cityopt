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
    /** Data associated with the evaluation of a single set of simulation input. */
    public static class Put {
        /** Short scenario name.  May be null. */
        public String name;

        /** More detailed scenario description.  May be null. */
        public String description;

        /** Simulation input. Must be non-null. */
        public SimulationInput input;

        /**
         * Simulation output computed from the input.
         * May be SimulationResults, SimulationFailure or null.
         */
        public SimulationOutput output;

        /** Evaluated metric values.  May be null. */
        public MetricValues metricValues;

        /** Decision variable values from which the input was constructed.  May be null. */
        public DecisionValues decisions;

        /** Evaluated constraint infeasibilities.  May be null. */
        public ConstraintStatus constraintStatus;

        /** Evaluated objective values.  May be null. */
        public ObjectiveStatus objectiveStatus;

        public Put(SimulationInput input) {
            this.input = input;
        }

        public Put(SimulationInput input, String[] nameAndDescription) {
            this.input = input;
            this.name = nameAndDescription[0];
            this.description = nameAndDescription[1];
        }
    }

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
     * Stores all evaluation data associated with specific simulation output.
     * If a scenario with the same input has already been stored before, the
     * scenario name and description are not changed.
     *
     * @param put
     *            the data to be stored.  References will be retained to the
     *            SimulationInput and SimulationOutput objects.
     */
    public void put(Put put);

    /** Updates the metric values of a previously stored successful simulation. */
    public void updateMetricValues(MetricValues metricValues);
}
