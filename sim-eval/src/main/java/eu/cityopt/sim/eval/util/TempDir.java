package eu.cityopt.sim.eval.util;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A Closeable wrapper for temporary directories.
 * Intended to be used in try-with-resources statements.
 * The directory (including all contents) is automatically deleted on close.
 */
public class TempDir implements Closeable {
    private static DelayedDeleter delayedDeleter;
    private static final Logger logger = LoggerFactory.getLogger(TempDir.class);

    private final Path path;
    private boolean is_closed;

    /** Path to the temporary directory. */
    public Path getPath() {return path;}

    /**
     * Create a temporary directory and store its path.
     * @param prefix passed to {@link
     *     java.nio.file.Files#createTempDirectory
     *     Files.createTempDirectory}
     * @throws IOException
     */
    public TempDir(String prefix) throws IOException {
        path = Files.createTempDirectory(prefix);
        is_closed = false;
    }

    /** Check whether close() has been called. */
    public boolean isClosed() {return is_closed;}

    /** Delete the directory and its contents. */
    @Override
    public void close() throws IOException {
        if (is_closed)
            return;
        is_closed = true;
        try {
            deleteTree(path);
        } catch (AccessDeniedException e) {
            if (delayedDeleter != null) {
                delayedDeleter.add(path);
            } else {
                throw e;
            }
        }
    }

    static void use(DelayedDeleter instance) {
        delayedDeleter = instance;
    }

    /* Initially copied from the Javadoc of FileVisitor.
       However, Windows seems to throw AccessDeniedExceptions for no
       clear reason.  Little that one can do except leave files behind.
       Coping with that makes this even more complicated than it
       would normally be. */
    private static class Deleter extends SimpleFileVisitor<Path> {
        IOException firstExc;

        private void suppress(IOException exc) {
            if (firstExc == null) {
                firstExc = exc;
            } else {
                firstExc.addSuppressed(exc);
            }
        }

        private void addSuppressedTo(Throwable to) {
            if (firstExc != null) {
                to.addSuppressed(firstExc);
                for (Throwable t : firstExc.getSuppressed()) {
                    to.addSuppressed(t);
                }
            }
        }

        @Override
        public FileVisitResult visitFile(
                Path file, BasicFileAttributes attrs)
                        throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file,
                IOException exc) throws IOException {

            if (exc instanceof AccessDeniedException) {
                logger.warn("visitFileFailed: " + exc);
                suppress(exc);
            } else {
                addSuppressedTo(exc);
                throw exc;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(
                Path dir, IOException exc) throws IOException {
            try {
                if (exc != null) {
                    addSuppressedTo(exc);
                    throw exc;
                }
                Files.delete(dir);
            } catch (AccessDeniedException e) {
                logger.warn("postVisitDirectory: " + e);
                suppress(e);
            } catch (DirectoryNotEmptyException e) {}
            return FileVisitResult.CONTINUE;
        }
    }

    /**
     * Delete path, recursively if it is a directory.
     * During recursive deletion AccessDeniedExceptions, commonly thrown
     * by Windows, are suppressed.  After traversing the whole tree the
     * first AccessDeniedException is rethrown.  DirectoryNotEmptyExceptions
     * are ignored.  Other IOExceptions are thrown immediately, interrupting
     * traversal.  Suppressed AccessDeniedExceptions can be retrieved with
     * {@link Throwable#getSuppressed()} from the eventually thrown exception.
     * <p>
     * Even if no exception is thrown there is no guarantee that
     * path was deleted.  E.g., a new file may have been created in a
     * directory during its traversal, causing a DirectoryNotEmptyException,
     * which is swallowed by deleteTree.
     *
     * @param path
     */
    public static void deleteTree(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Deleter del = new Deleter();
            Files.walkFileTree(path, del);
            if (del.firstExc != null) {
                throw del.firstExc;
            }
        } else {
            Files.delete(path);
        }
    }
}
