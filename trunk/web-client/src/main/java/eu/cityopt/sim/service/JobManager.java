package eu.cityopt.sim.service;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
public class JobManager<T, I> {
    private static Logger log = Logger.getLogger(JobManager.class); 

    public static class JobData<T, I> {
        public Future<T> job;
        public I data;

        JobData(Future<T> job, I data) {
            this.job = job;
            this.data = data;
        }
    }

    private volatile boolean shutdown = false;
    private Map<Integer, JobData<T, I>> activeJobs = new ConcurrentHashMap<>();

    JobData<T, I> putJob(int jobId, Future<T> job, I data) {
        JobData<T, I> entry = new JobData<>(job, data);
        JobData<T, I> old = activeJobs.put(jobId, entry);
        if (old != null) {
            old.job.cancel(true);
        }
        return entry;
    }

    void removeJob(int jobId, JobData<T, I> entry) {
        activeJobs.remove(jobId, entry);
    }

    Set<Entry<Integer, JobData<T, I>>> getActiveJobs() {
        return activeJobs.entrySet();
    }

    boolean cancelJob(int jobId) {
        JobData<T, I> old = activeJobs.remove(jobId);
        if (old != null) {
            return old.job.cancel(true);
        }
        return false;
    }

    /** Cancels all ongoing jobs. */
    void cancelAllJobs() {
        if (!activeJobs.isEmpty()) {
            log.info("Canceling jobs...");
            int count = 0;
            Iterator<Map.Entry<Integer, JobData<T, I>>>
                it = activeJobs.entrySet().iterator();
            while (it.hasNext()) {
                it.next().getValue().job.cancel(true);
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
