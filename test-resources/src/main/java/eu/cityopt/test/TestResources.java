package eu.cityopt.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
     * This is the directory containing the property file.
     */
    public Path getDir() {
        return Paths.get(baseURI).getParent();
    }
    
    /**
     * Return the value of a property as a Path.
     * The path is resolved with respect to {@link #getDir()}.
     * @param propname name of the property containing the path
     */
    public Path getPath(String propname) {
        return getDir().resolve(properties.getProperty(propname));
    }
    
    /**
     * Return the value of a property as an array of Paths.
     * The system path separator is used for separating the entries,
     * which are resolved with respect to {@link #getDir()}.  Empty
     * entries are omitted. 
     */
    public Path[] getPaths(String propname) {
        String sep = Pattern.quote(System.getProperty("path.separator"));
        return Arrays.stream(properties.getProperty(propname).split(sep))
                .filter(s -> !s.isEmpty())
                .map(getDir()::resolve).toArray(Path[]::new);
    }

    /**
     * Return an InputStream to a resource defined by a property.
     * The value of the property is resolved as a URL relative
     * to that of the property file.
     * @param propname name of the property containing the URI.
     */
    public InputStream getStream(String propname)
            throws MalformedURLException, IOException {
        return baseURI.resolve(properties.getProperty(propname))
                .toURL().openStream();
    }
}
