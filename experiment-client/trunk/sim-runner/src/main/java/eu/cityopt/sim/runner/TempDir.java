package eu.cityopt.sim.runner;

import java.io.Closeable;
import java.io.IOException;
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
            // Copied from the Javadoc of FileVisitor.
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(
                        Path file, BasicFileAttributes attrs)
                                throws IOException
                {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e)
                        throws IOException
                {
                    if (e == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        // directory iteration failed
                        throw e;
                    }
                }
            });
        }
    }

}
