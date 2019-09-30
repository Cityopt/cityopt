package eu.cityopt.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Pattern;


/**
 * Access to shared test resources.
 * @author ttekth
 */
public class TestResources {
    private final static String propsName = "/test.properties";
    private final static URL
            propURL = TestResources.class.getResource(propsName);

    /**
     * URI of the test property file.
     * Used for resolving relative URIs in the property file.
     */
    public final URI baseURI = propURL.toURI();
    public final Properties properties = new Properties();

    /**
     * Load the test property file.
     */
    public TestResources() throws IOException, URISyntaxException {
        try (InputStream str = propURL.openStream()) {
            properties.load(str);
        }
    }

    /**
     * Return the test data directory.
     * This is the directory containing the property file.  Note that it
     * can be inside a jar.
     */
    public Path getDir() {
        try {
            return Paths.get(baseURI).getParent();
        } catch (FileSystemNotFoundException e) {
            String[] parts = baseURI.toString().split("!");
            if (parts.length != 2) {
                throw e;
            } else {
                try {
                    return FileSystems.newFileSystem(
                            URI.create(parts[0]), Collections.emptyMap())
                            .getPath(parts[1]).getParent();
                } catch (IOException e2) {
                    throw e;
                }
            }
        }
    }

    /**
     * Return the value of a property as a Path.
     * The path is resolved with respect to {@link #getDir()}.
     * @param propname name of the property containing the path
     */
    public Path getPath(String propname) {
        String prop = properties.getProperty(propname);
        return prop == null ? null : getDir().resolve(prop);
    }

    /**
     * Return the value of a property as an array of Paths.
     * The system path separator is used for separating the entries,
     * which are resolved with respect to {@link #getDir()}.  Empty
     * entries are omitted.
     */
    public Path[] getPaths(String propname) {
        String sep = Pattern.quote(System.getProperty("path.separator"));
        String prop = properties.getProperty(propname);
        return prop == null ? null : Arrays.stream(prop.split(sep))
                .filter(s -> !s.isEmpty())
                .map(getDir()::resolve).toArray(Path[]::new);
    }

    /**
     * Return an InputStream to a resource defined by a property.
     * The value of the property is resolved with {@link #getPath(String)}.
     */
    public InputStream getStream(String propname)
            throws MalformedURLException, IOException {
        Path p = getPath(propname);
        return p == null ? null : Files.newInputStream(p);
    }
}
