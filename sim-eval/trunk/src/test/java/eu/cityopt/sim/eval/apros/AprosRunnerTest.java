package eu.cityopt.sim.eval.apros;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.google.common.io.ByteStreams;

import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;

public class AprosRunnerTest {
    private final static String propsName = "/apros/test.properties";
    private static Properties props;
    private static Path dataDir;
    
    @BeforeClass
    public static void setupProps() throws Exception {
        URL purl = AprosRunnerTest.class.getResource(propsName);
        props = new Properties();
        try (InputStream str = purl.openStream()) {
            props.load(str);
        }
        dataDir = Paths.get(purl.toURI()).getParent();
        AprosRunner.profileDir = dataDir.resolve(
                props.getProperty("profile_dir"));
    }
    
    @Test
    public void testGetTransformer() throws Exception {
        assertNotNull(AprosRunner.getTransformer());
    }
    
    private AprosRunner makeRunner() throws Exception {
        Namespace ns = new Namespace(new Evaluator(), Collections.emptyList());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document ucs = db.parse(
                dataDir.resolve(props.getProperty("uc_props")).toFile());
        Path modelDir = dataDir.resolve(props.getProperty("model_dir"));
        
        return new AprosRunner(
                props.getProperty("profile"), ns, ucs, modelDir,
                props.getProperty("result_file"));
    }
    
    @Test
    public void testRun() throws Exception {
        try (AprosRunner arun = makeRunner()) {
            ExternalParameters dumb = new ExternalParameters(arun.nameSpace);
            SimulationInput in = new SimulationInput(dumb);
            AprosJob job = arun.start(in);
            System.out.println("---8<--- inputs.scl");
            job.conf.inputDirectory.directories().get(
                    "cityopt").files().get(
                    "inputs.scl").writeTo(System.out);
            System.out.println("--->8--- end of inputs.scl");
            SimulationOutput out = job.get();
            System.out.println("---8<--- job log");
            System.out.print(out.getMessages());
            System.out.println("--->8--- end of job log");
            assertTrue(out instanceof SimulationResults);
        }
    }
    
    @Test
    public void testSanitize() throws Exception {
        Pattern re = Pattern.compile("^[a-z_][a-zA-Z0-9_]*$");
        String[] ids = {"a1b", "A1b", "_", "__", "_A1b"};
        Set<String> sids = new HashSet<>();
        
        try (AprosRunner arun = makeRunner()) {
            for (String id : ids) {
                String sid = arun.sanitize(id);
                assertTrue("Invalid id " + id + " |-> " + sid,
                           re.matcher(sid).find());
                assertTrue("Duplicate id " + id + " |-> " + sid,
                           sids.add(sid));
            }
        }
    }
    
    @Test
    public void printSetup() throws Exception {
        try (AprosRunner arun = makeRunner();
             InputStream scl = new FileInputStream(arun.setup_scl.toFile())) {
            System.out.println("---8<--- setup.scl");
            ByteStreams.copy(scl, System.out);
            System.out.println("--->8--- end of setup.scl");
        }
    }
}
