package eu.cityopt.sim.eval;

import java.time.Instant;

/**
 * Common configuration for the evaluation of expressions related to the
 * same simulation model.
 */
public class EvaluationSetup {
    /** The expression evaluator to use. */
    public final Evaluator evaluator;

    /**
     * Origin of simulation time. Gives the real-world meaning of time 0 for all
     * related TimeSeries objects.
     */
    public final Instant timeOrigin;

    /**
     * Origin of simulation time as a number of seconds since
     * 1970-01-01T00:00:00Z.
     */
    final double originTimestamp;

    public EvaluationSetup(Evaluator evaluator, Instant timeOrigin) {
        this.evaluator = evaluator;
        this.timeOrigin = timeOrigin;
        this.originTimestamp = (timeOrigin != null)
                ? timeOrigin.toEpochMilli() / 1000.0 : Double.NaN;
    }
}
