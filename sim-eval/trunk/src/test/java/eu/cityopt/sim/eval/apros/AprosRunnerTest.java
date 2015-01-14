package eu.cityopt.sim.eval.apros;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
import eu.cityopt.sim.eval.TimeSeriesI;
import eu.cityopt.sim.eval.Type;

public class AprosRunnerTest {
    private final static String propsName = "/apros/test.properties";
    private static Properties props;
    private static Path dataDir;
    private Namespace ns;
    
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
    
    private SimulationInput makeInput() throws Exception {
        String
            picomp = props.getProperty("ip_comp"),
            piname = props.getProperty("ip_name"),
            ptype = props.getProperty("ip_type"),
            pvalue = props.getProperty("ip_value"),
            pocomp = props.getProperty("op_comp"),
            poname = props.getProperty("op_name");
        ns = new Namespace(new Evaluator());
        Type type = ptype != null ? Type.getByName(ptype) : null;
        boolean has_ip = false;
        if (picomp != null) {
            Namespace.Component comp = ns.getOrNew(picomp);
            if (piname != null && type != null) {
                comp.inputs.put(piname, type);
                has_ip = true;
            }
        }
        if (pocomp != null) {
            Namespace.Component comp = ns.getOrNew(pocomp);
            if (poname != null) {
                comp.outputs.put(poname, Type.TIMESERIES_LINEAR);
            }
        }
        ExternalParameters dumb = new ExternalParameters(ns);
        SimulationInput in = new SimulationInput(dumb);
        if (has_ip && pvalue != null) {
            in.put(picomp, piname, type.parse(pvalue));
        }
        return in;
    }
    
    private AprosRunner makeRunner() throws Exception {
        if (ns == null)
            makeInput();
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
        SimulationInput in = makeInput();
        try (AprosRunner arun = makeRunner()) {
            AprosJob job = arun.start(in);
            String ccdir = props.getProperty("copy_conf_dir");
            if (ccdir != null) {
                System.out.println("Copying job conf to " + ccdir);
                Path p = Files.createDirectories(Paths.get(ccdir));
                job.conf.inputDirectory.writeTo(p);
            }
            System.out.println("---8<--- inputs.scl");
            job.conf.inputDirectory.directories().get(
                    "cityopt").files().get(
                    "inputs.scl").writeTo(System.out);
            System.out.println("--->8--- end of inputs.scl");
            SimulationOutput out = job.get();
            System.out.println("---8<--- job log");
            System.out.print(out.getMessages());
            System.out.println("--->8--- end of job log");
            if (out instanceof SimulationResults) {
                String
                    pocomp = props.getProperty("op_comp"),
                    poname = props.getProperty("op_name"),
                    pofile = props.getProperty("op_file");
                if (pocomp != null && poname != null) {
                    TimeSeriesI ts = ((SimulationResults)out).getTS(
                            pocomp, poname);
                    assertNotNull(ts);
                    double[] t = ts.getTimes(), v = ts.getValues();
                    assertEquals(t.length, v.length);
                    if (pofile != null) {
                        System.out.println(
                                "Writing simulation output to " + pofile);
                        CSVFormat fmt = CSVFormat.DEFAULT.withHeader(
                                "time", pocomp + "." + poname);
                        try (Writer w = new FileWriter(
                                dataDir.resolve(pofile).toFile());
                             CSVPrinter prn = fmt.print(
                                     new BufferedWriter(w))) {
                            for (int i = 0; i != t.length; ++i) {
                                prn.printRecord(t[i], v[i]);
                            }
                        }
                    }
                }
              
            } else {
                fail("Simulation failed.");
            }
        }
    }
    
    @Test
    public void testSanitize() throws Exception {
        Pattern re = Pattern.compile("^[a-z][a-zA-Z0-9_]*$");
        String[] ids = {"a1b", "A1b", "_", "__", "_A1b", "Z", "z_Z", "zz_Z"};
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
