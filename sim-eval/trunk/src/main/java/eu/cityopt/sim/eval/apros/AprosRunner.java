package eu.cityopt.sim.eval.apros;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.simantics.simulation.scheduling.Experiment;
import org.simantics.simulation.scheduling.JobConfiguration;
import org.simantics.simulation.scheduling.Server;
import org.simantics.simulation.scheduling.ServerFactory;
import org.simantics.simulation.scheduling.applications.Application;
import org.simantics.simulation.scheduling.applications.ProfileApplication;
import org.simantics.simulation.scheduling.files.FileSelector;
import org.simantics.simulation.scheduling.files.IDirectory;
import org.simantics.simulation.scheduling.files.LocalDirectory;
import org.simantics.simulation.scheduling.files.MemoryDirectory;
import org.simantics.simulation.scheduling.files.MemoryFile;
import org.simantics.simulation.scheduling.status.StatusLoggingUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
    
    final Namespace nameSpace;
    Document uc_structure;
    private final IDirectory modelDir;
    private static Templates a62scl = loadXSL();
    //TODO: Maybe share some stuff if there are multiple AprosRunners?
    private final TempDir tmp;
    private final Server server;
    private final String profile;
    final String[] resultFiles;
    final Path setup_scl;
    private Map<String, Map<String, String>> inputNames = new HashMap<>();
    /* A sequence of SCL set commands for input parameters
       not found in uc_structure. */
    private byte[] orphanSets;

    /**
     * Map input parameters to globally unique SCL identifiers.
     * @return an unmodifiable map component |-> parameter |-> SCL name
     */
    public Map<String, Map<String, String>> getInputNames() {
        return Collections.unmodifiableMap(inputNames);
    }
    
    private static synchronized XPath getXPath() {
        return XPathFactory.newInstance().newXPath();
    }

    @Override
    public AprosJob start(SimulationInput input) throws IOException {
        if (!input.isComplete()) {
            throw new IllegalArgumentException("Incomplete input");
        }
        Experiment xpt = server.createExperiment(new HashMap<>());
        MemoryDirectory
            mdir = new MemoryDirectory(modelDir.files(),
                                       new HashMap<>(modelDir.directories())),
            cdir = new MemoryDirectory();
        mdir.addDirectory("cityopt", cdir);
        cdir.addFile(setup_scl);
        Application launcher = new ProfileApplication(profile, "Launcher.exe");
        String[] args = makeScript(mdir, cdir, input);
        FileSelector res_sel = new FileSelector(resultFiles);
        JobConfiguration conf = new JobConfiguration(launcher, args,
                                                     mdir, res_sel);
        AprosJob ajob = new AprosJob(this, input, xpt, conf);
        Files.createDirectories(Paths.get("C:/DATA/cityopt/foo"));
        mdir.writeTo(Paths.get("C:/DATA/cityopt/foo"));
        xpt.start();
        return ajob;
    }
    
    private String[] makeScript(MemoryDirectory mdir, MemoryDirectory cdir,
                                SimulationInput input) {
        ByteArrayOutputStream inp_ba = new ByteArrayOutputStream();
        try (PrintStream inp = new PrintStream(inp_ba)) {
            for (Map.Entry<String, Map<String, String>>
                     ckv : inputNames.entrySet()) {
                for (Map.Entry<String, String>
                         pkv : ckv.getValue().entrySet()) {
                    inp.printf("%s = %s%n", pkv.getValue(),
                               input.get(ckv.getKey(), pkv.getKey()));
                }
            }            
        }
        cdir.addFile("inputs.scl", new MemoryFile(inp_ba.toByteArray()));
        //TODO stub
        /* XXX Bug: there needs to be at least one argument.
           Wonkiness in the simulation server or client library.
           No harm if there are more arguments than SCL main takes. */
        return new String[] {"sequence.scl", "0"};
    }

    public AprosRunner(
            String profile, Namespace ns,
            Document uc_props, Path modelDir, String... resultFiles)
                    throws TransformerException {
        this.profile = profile;
        nameSpace = ns;
        this.modelDir = new LocalDirectory(modelDir);
        this.resultFiles = resultFiles;
        uc_structure = (Document)uc_props.cloneNode(true);
        sanitizeUCS();
        makeInputNames();
        patchUCS();
        try {
            tmp = new TempDir(tmpPrefix);
            try {
                setup_scl = tmp.getPath().resolve("setup.scl");
                writeSetup();
                server = ServerFactory.createLocalServer(
                        Files.createDirectory(tmp.getPath().resolve("srv")));
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

    private static Templates loadXSL() {
        try (InputStream xslt = AprosRunner.class.getResourceAsStream(
                "xslt/a62scl.xsl")) {
            return TransformerFactory.newInstance().newTemplates(
                    new StreamSource(xslt));
        } catch (IOException | TransformerConfigurationException e) {
            throw new RuntimeException("Failed to load XSLT.", e);
        }
    }
    
    public static Transformer getTransformer()
            throws TransformerConfigurationException {
        return a62scl.newTransformer();
    } 

    private void sanitizeUCS() {
        class Replacer {
            Pattern p;
            String rs;
        }
        XPath xp = getXPath();
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
    
    private void makeInputNames() {
        Set<String> used_names = new HashSet<>();

        for (Map.Entry<String, Namespace.Component>
                 ckv : nameSpace.components.entrySet()) {
            if (ckv.getValue().inputs.isEmpty())
                continue;
            Map<String, String> names = new HashMap<>();
            inputNames.put(ckv.getKey(), names);
            for (Map.Entry<String, Type>
                     pkv : ckv.getValue().inputs.entrySet()) {
                String
                    name0 = sanitize(ckv.getKey() + "__" + pkv.getKey()),
                    name = name0;
                for (int i = 0; used_names.contains(name); ++i) {
                    name = name0 + "_" + i;
                }
                names.put(pkv.getKey(), name);
                used_names.add(name);
            }
        }
    }

    private void patchUCS() {
        XPath xp = getXPath();
        Map<QName, Object> vars = new HashMap<>();
        xp.setXPathVariableResolver(vars::get);
        ByteArrayOutputStream set_baos = new ByteArrayOutputStream();
        try (PrintStream set_str = new PrintStream(set_baos)) {
            final QName
                qn_comp = new QName("comp"),
                qn_param = new QName("param");
            XPathExpression
                xp_comp = xp.compile("//node[@moduleName = $comp]"),
                xp_value = xp.compile("./property[@name = $param]/@value");
            for (Map.Entry<String, Map<String, String>>
                     ckv : inputNames.entrySet()) {
                vars.put(qn_comp, ckv.getKey());
                NodeList nodes = (NodeList)xp_comp.evaluate(
                        uc_structure, XPathConstants.NODESET);
                //TODO if (nodes.getLength() > 1) freak out
                for (Map.Entry<String, String>
                         pkv : ckv.getValue().entrySet()) {
                    if (nodes.getLength() != 0) {
                        vars.put(qn_param, sanitize(pkv.getKey()));
                        NodeList vals = (NodeList)xp_value.evaluate(
                                nodes.item(0), XPathConstants.NODESET);
                        switch (vals.getLength()) {
                        case 1:
                            ((Attr)vals.item(0)).setValue(pkv.getValue());
                            continue;
                        default:
                            //TODO
                        case 0:
                        }
                    }
                    set_str.printf(
                            "  set \"%s#%s\" In.%s%n",
                            ckv.getKey(), pkv.getKey(), pkv.getValue());
                }
            }
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
        orphanSets = set_baos.toByteArray();
    }

    private void writeSetup() throws IOException, TransformerException {
        try (PrintStream setup = new PrintStream(setup_scl.toFile())) {
            getTransformer().transform(
                    new DOMSource(uc_structure),
                    new StreamResult(setup));
            setup.printf(
                    "%nsetup :: AprosSequence ()%n"
                    + "setup = do {%n"
                    + "  setupUCs;%n");
            setup.write(orphanSets);
            setup.printf("  return ()%n}%n");
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
