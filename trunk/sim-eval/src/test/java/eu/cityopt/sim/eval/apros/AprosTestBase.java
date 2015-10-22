package eu.cityopt.sim.eval.apros;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.junit.*;

import eu.cityopt.sim.eval.SimulatorManagers;

public class AprosTestBase {
    private final static String propsName = "/apros/test.properties";
    protected static Properties props;
    protected static Path dataDir;
    protected static Path profileDir;
    protected static String profileName;

    @BeforeClass
    public static void setupProps() throws Exception {
        URL purl = AprosRunnerTest.class.getResource(propsName);
        props = new Properties();
        try (InputStream str = purl.openStream()) {
            props.load(str);
        }
        dataDir = Paths.get(purl.toURI()).getParent();
        profileDir = dataDir.resolve(
                props.getProperty("profile_dir"));
        profileName = props.getProperty("profile");
        AprosManager.register(
                profileDir, Executors.newSingleThreadExecutor(), System.out);
    }
    
    @AfterClass
    public static void closeSimulators() {
        SimulatorManagers.shutdown();
    }
}
