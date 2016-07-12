package eu.cityopt.opt.ga.adapter;

import java.time.Instant;

import org.opt4j.core.optimizer.Control;
import org.opt4j.core.optimizer.StopException;
import org.opt4j.core.optimizer.TerminationException;

/**
 * Opt4J execution control with timeout.
 *
 * @author Hannu Rummukainen
 */
public class TimeoutControl extends Control {
    final Instant deadline;
    volatile boolean timeout = false;

    public TimeoutControl(Instant deadline) {
        this.deadline = deadline;
    }

    @Override
    public synchronized void checkpoint() throws TerminationException {
        super.checkpoint();
        terminateOnTimeout();
    }

    @Override
    public synchronized void checkpointStop() throws TerminationException,
            StopException {
        super.checkpointStop();
        terminateOnTimeout();
    }

    private void terminateOnTimeout() throws TerminationException {
        if (Instant.now().compareTo(deadline) >= 0) {
            timeout = true;
            throw new TerminationException();
        }
    }
}
