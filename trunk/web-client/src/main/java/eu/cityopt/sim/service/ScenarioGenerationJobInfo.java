package eu.cityopt.sim.service;

import java.time.Instant;

/**
 * Information about an ongoing optimisation run.
 * @see ScenarioGenerationService#getRunningOptimisations()
 */
public class ScenarioGenerationJobInfo implements JobInfo {
    public int scenarios;
    public int maxScenarios;

    public int iteration;
    public int maxIterations;

    public Instant started;
    public Instant estimatedCompletionTime;
    public Instant deadline;

    ScenarioGenerationJobInfo(Instant started, Instant deadline) {
        this.started = started;
        this.deadline = deadline;
    }

    /** Copy constructor */
    ScenarioGenerationJobInfo(ScenarioGenerationJobInfo other) {
        this.scenarios = other.scenarios; 
        this.maxScenarios = other.maxScenarios;
        this.iteration = other.iteration;
        this.maxIterations = other.maxIterations;
        this.started = other.started;
        this.estimatedCompletionTime = other.estimatedCompletionTime;
        this.deadline = other.deadline;
    }

    @Override
    public Instant getStarted() {
        return started;
    }

    @Override
    public double getFractionComplete() {
        return (double)scenarios / maxScenarios;
    }

    @Override
    public Instant getEstimatedCompletionTime() {
        return estimatedCompletionTime;
    }

    @Override
    public String formatEvaluationStatus() {
        return "Scenarios: " + scenarios
                + ((maxScenarios == 0) ? "" : "/" + maxScenarios)
                + ((maxIterations == 0) ? ""
                        : "; Iteration: " + iteration + "/" + maxIterations);
    }
    
    @Override
    public String toString() {
        return "Started: " + started
                + "; Estimated completion: " + estimatedCompletionTime
                + "; Deadline: " + deadline
                + "; " + formatEvaluationStatus();
    }
}
