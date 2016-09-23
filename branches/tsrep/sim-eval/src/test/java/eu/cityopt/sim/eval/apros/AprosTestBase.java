package eu.cityopt.sim.eval.apros;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulatorManager;
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

    public AprosManager newSimulatorManager() {
        return new AprosManager(
                profileDir, Executors.newSingleThreadExecutor(), System.out);
    }

    public SimulationModel readModelResource(
            SimulatorManager mgr, String resname)
                    throws IOException, ConfigurationException {
        try (InputStream in = getClass().getResourceAsStream(resname)) {
            return mgr.parseModel(null, in);
        }
    }

    public SimulationModel readModelResourceProp(
            SimulatorManager mgr, String propname)
                    throws IOException, ConfigurationException {
        return readModelResource(mgr, props.getProperty(propname));
    }

    public SimulationInput getModelVars(
            SimulationModel model, Map<String, Map<String, String>> units)
                    throws IOException {
        Namespace ns = new Namespace(new Evaluator(),
                                     model.getDefaults().timeOrigin);
        ns.initConfigComponent();
        BufferedWriter warnings = new BufferedWriter(
                new OutputStreamWriter(System.err));
        SimulationInput defaultInput = model.findInputsAndOutputs(
                ns, units, 0, warnings);
        warnings.flush(); // Do not close stderr!
        return defaultInput;
    }
}
