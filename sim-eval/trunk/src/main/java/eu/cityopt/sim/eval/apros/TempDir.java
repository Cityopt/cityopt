package eu.cityopt.sim.eval.apros;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.SimpleFileVisitor;

/** A Closeable wrapper for temporary directories.
 * Intended to be used in try-with-resources statements.
 * The directory (including all contents) is automatically deleted on close.
 */
public class TempDir implements Closeable {
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
        if (Files.isDirectory(path)) {
            /* Initially copied from the Javadoc of FileVisitor.
               However, Windows seems to throw AccessDeniedExceptions for no
               clear reason.  Little one can do except leave files behind.
               Coping with that makes this even more complicated than it
               would normally be. */
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

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
                        System.err.println("visitFileFailed: " + exc);
                    } else {
                        throw exc;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(
                        Path dir, IOException exc) throws IOException {
                    try {
                        if (exc != null)
                            throw exc;
                        Files.delete(dir);
                    } catch (AccessDeniedException e) {
                        System.err.println("postVisitDirectory: " + e);
                    } catch (DirectoryNotEmptyException e) {}
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}
