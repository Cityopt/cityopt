package eu.cityopt.sim.eval.apros;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;

import org.junit.*;
import org.w3c.dom.Document;

import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.Namespace;

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
        Transformer tf = AprosRunner.getTransformer();
    }
    
    @Test
    public void testAprosRunner() throws Exception {
        Namespace ns = new Namespace(new Evaluator(), Collections.emptyList());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document ucs = db.parse(
                dataDir.resolve(props.getProperty("uc_props")).toFile());
        Path modelDir = dataDir.resolve(props.getProperty("model_dir"));
        try (AprosRunner arun = new AprosRunner(
                props.getProperty("profile"), ns, ucs, modelDir)) {
            
        }
    }
}
