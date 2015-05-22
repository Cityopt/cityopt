package eu.cityopt.sim.eval.apros;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.io.ByteStreams;

import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.SimulatorManagers;
import eu.cityopt.sim.eval.TimeSeriesI;
import eu.cityopt.sim.eval.Type;

public class AprosRunnerTest extends AprosTestBase {
    private Namespace ns;

    @Test
    public void testGetTransformer() throws Exception {
        assertNotNull(AprosRunner.getTransformer());
    }
    
    private SimulationInput makeInput() throws Exception {
        final String dummy_comp = Namespace.CONFIG_COMPONENT;
        String
            t0 = props.getProperty("start_time"),
            t1 = props.getProperty("end_time"),
            picomp = props.getProperty("ip_comp"),
            piname = props.getProperty("ip_name"),
            ptype = props.getProperty("ip_type"),
            pvalue = props.getProperty("ip_value"),
            pocomp = props.getProperty("op_comp"),
            poname = props.getProperty("op_name");
        ns = new Namespace(new Evaluator(), Instant.ofEpochMilli(0));
        Type type = ptype != null ? Type.getByName(ptype) : null;
        boolean has_ip = false;
        if (t1 != null) {
            Namespace.Component dummy = ns.getOrNew(dummy_comp);
            dummy.inputs.put(Namespace.CONFIG_SIMULATION_START, Type.DOUBLE);
            dummy.inputs.put(Namespace.CONFIG_SIMULATION_END, Type.DOUBLE);
        }
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
        if (t1 != null) {
            in.put(dummy_comp, Namespace.CONFIG_SIMULATION_END,
                   Type.DOUBLE.parse(t1, ns));
            in.put(dummy_comp, Namespace.CONFIG_SIMULATION_START,
                   t0 != null ? Type.DOUBLE.parse(t0, ns) : 0.0);
        }
        if (has_ip && pvalue != null) {
            in.put(picomp, piname, type.parse(pvalue, ns));
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
        String nodes = props.getProperty("nodes");
        AprosManager mgr = (AprosManager)SimulatorManagers.get(profileName);
        if (nodes != null) {
            mgr.setNodes((new ObjectMapper()).readValue(
                    nodes, new TypeReference<List<Map<String, String>>>() {}));
        }
        
        return new AprosRunner(
                mgr, profileName,
                ns, ucs, modelDir,
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
                SimulationResults res = (SimulationResults)out;
                String
                    pocomp = props.getProperty("op_comp"),
                    poname = props.getProperty("op_name"),
                    pofile = props.getProperty("op_file");
                if (pocomp != null && poname != null) {
                    TimeSeriesI ts = res.getTS(
                            pocomp, poname);
                    assertNotNull(ts);
                    double[] t = ts.getTimes(), v = ts.getValues();
                    assertEquals(t.length, v.length);
                    if (pofile != null) {
                        System.out.println(
                                "Writing simulation output to " + pofile);
                        CsvMapper m = new CsvMapper();
                        CsvSchema sch = CsvSchema.builder()
                                .addColumn("time")
                                .addColumn(pocomp + "." + poname)
                                .build().withHeader();
                        try (SequenceWriter w = m.writer(sch).writeValues(
                                dataDir.resolve(pofile).toFile())) {
                            for (int i = 0; i != t.length; ++i) {
                                double[] row = {t[i], v[i]};
                                w.write(row);
                            }
                        }
                    }
                }
                assertTrue(res.isComplete());
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
