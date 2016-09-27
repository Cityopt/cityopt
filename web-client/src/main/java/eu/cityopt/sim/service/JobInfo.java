package eu.cityopt.sim.service;

import java.time.Instant;


/** 
 * Information about a user-initiated simulation or optimisation job.
 * @author Hannu Rummukainen
 */
public interface JobInfo {
    /** When the job was started. */
    public Instant getStarted();

    /** Estimate of job completion time. */
    public Instant getEstimatedCompletionTime();

    /** Fraction of work completed, ranging from 0 to 1. */
    public double getFractionComplete();

    /** Brief text description of the work completed to date. */
    public String formatEvaluationStatus();
}
