package eu.cityopt.sim.eval.apros;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.simantics.simulation.scheduling.Experiment;
import org.simantics.simulation.scheduling.Job;
import org.simantics.simulation.scheduling.JobConfiguration;
import org.simantics.simulation.scheduling.Server;
import org.simantics.simulation.scheduling.ServerFactory;
import org.simantics.simulation.scheduling.applications.Application;
import org.simantics.simulation.scheduling.applications.ProfileApplication;
import org.simantics.simulation.scheduling.files.FileSelector;
import org.simantics.simulation.scheduling.files.LocalDirectory;
import org.simantics.simulation.scheduling.files.MemoryDirectory;
import org.simantics.simulation.scheduling.status.StatusLoggingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.runner.TempDir;

/**
 * A factory of AprosJobs for one model.
 * @author ttekth
 */
public class AprosRunner implements SimulationRunner {
    /** Directory containing apros profiles (as subdirectories). */
    public static Path profileDir = Paths.get(".");
    /** Prefix for temporary directories. */
    public static String tmpPrefix = "cityopt_apros";
    /** Number of cores to parallilise for. */
    public static int cores = 2;
    
    Document uc_structure;
    final LocalDirectory modelDir;
    private static Transformer a62scl = null;
    //TODO: Maybe share some stuff if there are multiple AprosRunners?
    private final TempDir tmp;
    private final Server server;
    private final String profile;
    final String resultFile;

    @Override
    public AprosJob start(SimulationInput input) {
        Experiment xpt = server.createExperiment(
                new HashMap<String, String>());
        MemoryDirectory mdir = new MemoryDirectory(modelDir.files(),
                                                   modelDir.directories());
        Application launcher = new ProfileApplication(profile, "Launcher.exe");
        FileSelector res_sel = new FileSelector(resultFile);
        String[] args = makeScript(mdir, input);
        JobConfiguration conf = new JobConfiguration(launcher, args,
                                                     mdir, res_sel);
        Job job = xpt.createJob("job", conf);
        StatusLoggingUtils.redirectJobLog(job, System.out);
        xpt.start();

        return new AprosJob(this, input, job);
    }
    
    private String[] makeScript(MemoryDirectory mdir, SimulationInput input) {
        // TODO stub
        return new String[] {"sequence.scl", "0"};
    }

    public AprosRunner(
            String profile, Namespace ns,
            Document uc_props, Path modelDir, String resultFile) {
        this.profile = profile;
        this.modelDir = new LocalDirectory(modelDir);
        this.resultFile = resultFile;
        if (a62scl == null) {
            a62scl = getTransformer();
        }
        uc_structure = (Document)uc_props.cloneNode(true);
        sanitizeUCS();
        patchUCS(ns);
        try {
            tmp = new TempDir(tmpPrefix);
            try {
                server = ServerFactory.createLocalServer(tmp.getPath());
                server.installProfile(profile, new LocalDirectory(
                        profileDir.resolve(profile)));
                Map<String, String> p = new HashMap<String, String>();
                p.put("type", "local");
                p.put("cpu", String.valueOf(cores));
                server.createNode(p);
                StatusLoggingUtils.logServerStatus(System.out, server);
            } catch (Exception e) {
                tmp.close();
                throw e;
            }
        } catch (IOException e) {
            throw new RuntimeException("Simulation server setup failed.", e);
        }
    }

    private void patchUCS(Namespace ns) {
        // TODO Auto-generated method stub
        
    }

    static Transformer getTransformer() {
        TransformerFactory xff = TransformerFactory.newInstance();
        try (InputStream xslt = AprosRunner.class.getResourceAsStream(
                "xslt/a62scl.xsl")) {
            return xff.newTransformer(new StreamSource(xslt));
        } catch (IOException | TransformerException e) {
            throw new RuntimeException("Failed to load XSLT.", e);
        }
    }

    private void sanitizeUCS() {
        //TODO stub
    }
    
    public String sanitize(String name) {
        //TODO stub
        return name;
    }

    @Override
    public void close() throws IOException {
        if (!tmp.isClosed()) {
            server.dispose();
            tmp.close();
        }
    }
}
