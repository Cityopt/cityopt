package eu.cityopt.sim.eval.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps track of temporary directories that could not be immediately deleted
 * due to file locking issues. To use this class, you need to call
 * {@link #activate()} before starting any simulations, and then call
 * {@link #tryDelete()} at appropriate times, e.g. at application shutdown.
 *
 * @author Hannu Rummukainen
 */
public class DelayedDeleter {
    private static DelayedDeleter instance;

    private Set<Path> paths = ConcurrentHashMap.newKeySet();
    private Object deleteMutex = new Object();

    /** Tells TempDir to use delayed deletion and returns the singleton instance. */
    public static synchronized DelayedDeleter activate() {
        if (instance == null) {
            instance = new DelayedDeleter();
            TempDir.use(instance);
        }
        return instance;
    }

    public void add(Path path) {
        paths.add(path);
    }

    /** Attempts to delete remaining queued directories. */
    public void tryDelete() {
        synchronized (deleteMutex) {
            Iterator<Path> it = paths.iterator();
            while (it.hasNext()) {
                Path path = it.next();
                try {
                    TempDir.deleteTree(path);
                    it.remove();
                } catch (IOException e) {}
            }
        }
    }
}
