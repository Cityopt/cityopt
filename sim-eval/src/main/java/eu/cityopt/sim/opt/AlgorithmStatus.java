package eu.cityopt.sim.opt;

/**
 * Reasons for stopping a simulation optimisation algorithm.
 *
 * @author Hannu Rummukainen
 */
public enum AlgorithmStatus {
    /**
     * Algorithm successfully completed, meeting its convergence criteria within
     * the allowed runtime.
     */
    COMPLETED_RESULTS,

    /**
     * Algorithm used all available time. Further improvements may have been
     * possible.
     */
    COMPLETED_TIME,

    /** Algorithm interrupted by user or another system. */
    INTERRUPTED,

    /** 
     * Algorithm failed due to e.g. too many simulation or evaluation failures,
     * or an internal error.
     */
    FAILED
}
