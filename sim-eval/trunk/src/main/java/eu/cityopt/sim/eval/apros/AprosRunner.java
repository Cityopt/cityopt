package eu.cityopt.sim.eval.apros;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.simantics.simulation.scheduling.Experiment;
import org.simantics.simulation.scheduling.JobConfiguration;
import org.simantics.simulation.scheduling.Server;
import org.simantics.simulation.scheduling.ServerFactory;
import org.simantics.simulation.scheduling.applications.Application;
import org.simantics.simulation.scheduling.applications.ProfileApplication;
import org.simantics.simulation.scheduling.files.FileSelector;
import org.simantics.simulation.scheduling.files.LocalDirectory;
import org.simantics.simulation.scheduling.files.MemoryDirectory;
import org.simantics.simulation.scheduling.status.StatusLoggingUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationRunner;
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
    
    final Namespace nameSpace;
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
        String[] args = makeScript(mdir, input);
        FileSelector res_sel = new FileSelector(resultFile);
        JobConfiguration conf = new JobConfiguration(launcher, args,
                                                     mdir, res_sel);
        AprosJob ajob = new AprosJob(this, input, xpt, conf);
        xpt.start();
        return ajob;
    }
    
    private String[] makeScript(MemoryDirectory mdir, SimulationInput input) {
        // TODO stub
        return new String[] {"sequence.scl", "0"};
    }

    public AprosRunner(
            String profile, Namespace ns,
            Document uc_props, Path modelDir, String resultFile) {
        this.profile = profile;
        nameSpace = ns;
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
        try (InputStream xslt = AprosRunner.class.getResourceAsStream(
                "xslt/a62scl.xsl")) {
            return TransformerFactory.newInstance().newTransformer(
                    new StreamSource(xslt));
        } catch (IOException | TransformerException e) {
            throw new RuntimeException("Failed to load XSLT.", e);
        }
    }

    private void sanitizeUCS() {
        class Replacer {
            Pattern p;
            String rs;
        }
        XPath xp = XPathFactory.newInstance().newXPath();
        Map<String, Replacer> smap = new HashMap<>();
        try {
            NodeList names = (NodeList)xp.evaluate(
                    "//node[@isUC != 'False']/property/@name",
                    uc_structure, XPathConstants.NODESET);
            for (int in = 0; in != names.getLength(); ++in) {
                Attr na = (Attr)names.item(in);
                String name = na.getValue();
                Replacer rep = smap.get(name);
                if (rep == null) {
                    String sname = sanitize(name);
                    if (sname.equals(name))
                        continue;
                    rep = new Replacer() {{
                        p = Pattern.compile(
                                "\\b" + Pattern.quote(name) + "\\b");
                        rs = sname;
                    }};
                    smap.put(name, rep);
                }
                na.setValue(rep.rs);
            }
            NodeList xprs = (NodeList)xp.evaluate(
                    "//property[@type = 'expression']/@value",
                    uc_structure, XPathConstants.NODESET);
            for (int ix = 0; ix != xprs.getLength(); ++ix) {
                Attr xa = (Attr)xprs.item(ix);
                String
                    xs = xa.getValue(),
                    xs2 = xs;
                for (Replacer rep : smap.values()) {
                    xs2 = rep.p.matcher(xs2).replaceAll(rep.rs);
                }
                if (!xs2.equals(xs)) {
                    xa.setValue(xs2);
                }
            }
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static final Pattern re = Pattern.compile("^([^a-z])"); 
    
    public String sanitize(String name) {
        return re.matcher(name).replaceAll("_$1");
    }

    @Override
    public void close() throws IOException {
        if (!tmp.isClosed()) {
            server.dispose();
            tmp.close();
        }
    }
}
