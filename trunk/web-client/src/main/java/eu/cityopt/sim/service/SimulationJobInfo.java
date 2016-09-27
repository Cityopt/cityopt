package eu.cityopt.sim.service;

import java.time.Duration;
import java.time.Instant;

public class SimulationJobInfo implements JobInfo {
    public Instant started;
    public Instant estimatedCompletionTime;
    public double fractionComplete;

    public SimulationJobInfo(Instant started, Instant estimatedCompletionTime) {
        long done = Duration.between(started, Instant.now()).toMillis();
        long all = Duration.between(started, estimatedCompletionTime).toMillis();
        fractionComplete = Math.min(1.0, (double)done / all);
    }

    @Override
    public Instant getStarted() { return started; }

    @Override
    public Instant getEstimatedCompletionTime() { return estimatedCompletionTime; }

    @Override
    public double getFractionComplete() { return fractionComplete; }

    @Override
    public String toString() {
        return "Started: " + started
                + "; Estimated complete: " + estimatedCompletionTime;
    }

    @Override
    public String formatEvaluationStatus() {
        return 100 * fractionComplete + " %";
    }
}
