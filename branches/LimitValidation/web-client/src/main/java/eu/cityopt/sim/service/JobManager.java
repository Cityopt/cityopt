package eu.cityopt.sim.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

/**
 * Implementation convenience class for managing the set of currently active
 * simulation and optimisation jobs.
 * 
 * @author Hannu Rummukainen
 * @param <T>
 */
public class JobManager<T> {
    private static Logger log = Logger.getLogger(JobManager.class); 

    private volatile boolean shutdown = false;
    private Map<Integer, Future<T>> activeJobs = new ConcurrentHashMap<>();

    void putJob(int jobId, Future<T> job) {
        Future<T> oldJob = activeJobs.put(jobId, job);
        if (oldJob != null) {
            oldJob.cancel(true);
        }
    }

    void removeJob(int jobId, Future<T> job) {
        activeJobs.remove(jobId, job);
    }

    Set<Integer> getJobIds() {
        return new HashSet<Integer>(activeJobs.keySet());
    }

    boolean cancelJob(int jobId) {
        Future<T> oldJob = activeJobs.remove(jobId);
        if (oldJob != null) {
            return oldJob.cancel(true);
        }
        return false;
    }

    /** Cancels all ongoing jobs. */
    void cancelAllJobs() {
        if (!activeJobs.isEmpty()) {
            log.info("Canceling jobs...");
            int count = 0;
            Iterator<Map.Entry<Integer, Future<T>>> it = activeJobs.entrySet().iterator();
            while (it.hasNext()) {
                it.next().getValue().cancel(true);
                it.remove();
                ++count;
            }
            log.info("Canceled " + count + " jobs.");
        }        
    }

    void shutdown() {
        shutdown = true;
        cancelAllJobs();
    }

    boolean isShutdown() {
        return shutdown;
    }
}
