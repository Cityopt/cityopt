package eu.cityopt.sim.eval.apros;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
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

import org.apache.commons.lang3.StringEscapeUtils;
import org.simantics.simulation.scheduling.Experiment;
import org.simantics.simulation.scheduling.JobConfiguration;
import org.simantics.simulation.scheduling.applications.Application;
import org.simantics.simulation.scheduling.applications.ProfileApplication;
import org.simantics.simulation.scheduling.files.FileSelector;
import org.simantics.simulation.scheduling.files.IDirectory;
import org.simantics.simulation.scheduling.files.LocalDirectory;
import org.simantics.simulation.scheduling.files.LocalFile;
import org.simantics.simulation.scheduling.files.MemoryDirectory;
import org.simantics.simulation.scheduling.files.MemoryFile;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.Type;

/**
 * A factory of {@link AprosJob}s for one model.
 * @author ttekth
 */
public class AprosRunner implements SimulationRunner {
    final AprosManager manager;
    final Namespace nameSpace;
    Document uc_structure;
    private final IDirectory modelDir;
    private static Templates a62scl = loadXSL();
    private final String profile;
    final String[] resultFiles;
    final Path setup_scl;
    private boolean isOpen = true;
    /* Unique SCL names for input parmeters.  Built by makeInputNames.
       Maps component |-> parameter |-> name. */
    private Map<String, Map<String, String>> inputNames = new HashMap<>();
    /* A sequence of SCL set commands for input parameters
       not found in uc_structure. */
    private byte[] orphanSets;

    /**
     * Constructor.  This should only be called by
     * {@link AprosManager#makeRunner}, which everyone else should use
     * to create AprosRunners.
     * 
     * @param mgr the AprosManager that manages this runner. 
     * @param profile names a subdirectory of mgr.profileDir that contains
     *   the Apros profile to use.
     * @param ns a {@link Namespace} defining the inputs and outputs
     *   (everything else in ns is ignored).
     * @param uc_props the XML document describing the user component structure
     *   of the Apros model.
     * @param modelDir a directory of model-related files that are shipped to
     *   the simulation server.  sequence.scl in this directory contains the
     *   main program (<code>main :: &lt;Proc&gt; ()</code>) and should
     *   import <code>file:cityopt/setup.scl</code>.  The cityopt directory
     *   is created by AprosRunner and must not exist in modelDir.  The main
     *   program should load the model (with <code>loadIC</code>), call
     *   <code>setup :: AprosSequence ()</code> in the imported setup.scl
     *   and simulate.  The main program is also responsible for managing
     *   Apros <code>IO_SET</code>s for output.
     * @param resultFiles wildcards for Apros output files.
     *   All matching files are fetched from the simulation server, parsed
     *   as Apros <code>IO_SET</code> data and searched for the outputs
     *   in ns.  Unknown outputs in the files are ignored.
     * @throws TransformerException if setup.scl cannot be generated,
     *   possibly because of malformed uc_props.
     * @throws IOException 
     * @see eu.cityopt.sim.eval.SimulatorManagers#get
     */
    AprosRunner(AprosManager mgr, String profile, Namespace ns,
                Document uc_props, Path modelDir, String... resultFiles)
            throws TransformerException, IOException {
        manager = mgr;
        this.profile = profile;
        nameSpace = ns;
        this.modelDir = new LocalDirectory(modelDir);
        this.resultFiles = resultFiles;
        uc_structure = (Document)uc_props.cloneNode(true);
        sanitizeUCS();
        makeInputNames();
        patchUCS();
        manager.installProfile(profile);
        setup_scl = Files.createTempFile(
                manager.tmp.getPath(), "setup", ".scl");
        try {
            writeSetup();
        } catch (Exception e) {
            Files.deleteIfExists(setup_scl);
            throw e;
        }
    }

    @Override
    public AprosJob start(SimulationInput input) throws IOException {
        if (!input.isComplete()) {
            throw new IllegalArgumentException("Incomplete input");
        }
        Instant runStart = Instant.now();
        Experiment xpt = manager.createExperiment();
        MemoryDirectory
            mdir = new MemoryDirectory(modelDir.files(),
                                       new HashMap<>(modelDir.directories())),
            cdir = new MemoryDirectory();
        mdir.addDirectory("cityopt", cdir);
        cdir.addFile("setup.scl", new LocalFile(setup_scl));
        Application launcher = new ProfileApplication(profile, "Launcher.exe");
        String[] args = makeScript(mdir, cdir, input);
        FileSelector res_sel = new FileSelector(resultFiles);
        JobConfiguration conf = new JobConfiguration(launcher, args,
                                                     mdir, res_sel);
        AprosJob ajob = new AprosJob(
                manager.executor, input, xpt, conf, runStart);
        xpt.start();
        return ajob;
    }

    @Override
    public synchronized void close() throws IOException {
        if (isOpen) {
            isOpen = false;
            Files.deleteIfExists(setup_scl);
        }
    }

    private static final Pattern re1 = Pattern.compile("^(z+)_");
    private static final Pattern re2 = Pattern.compile("^([^a-z])");

    /**
     * Convert a string into a valid SCL variable name.
     * The input string is assumed to consist of characters that are permitted
     * in identifiers.  This function just ensures that the name starts with
     * a lower case letter; it does not replace invalid characters in the
     * string.  The mapping is injective: different inputs yield different
     * outputs. 
     * @param name string to convert
     * @return name or some prefix + name
     */
    public String sanitize(String name) {
        String foo = re1.matcher(name).replaceAll("$1z_");
        return re2.matcher(foo).replaceAll("z_$1");
    }

    /**
     * Map input parameters to globally unique SCL identifiers.
     * @return an unmodifiable map component |-&gt; parameter |-&gt; SCL name
     */
    public Map<String, Map<String, String>> getInputNames() {
        return Collections.unmodifiableMap(inputNames);
    }
    
    public static Transformer getTransformer()
            throws TransformerConfigurationException {
        return a62scl.newTransformer();
    }

    private static synchronized XPath getXPath() {
        return XPathFactory.newInstance().newXPath();
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
                               repr(input.get(ckv.getKey(), pkv.getKey())));
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
    
    /**
     * Return a SCL representation of obj.  Must work with any types
     * usable as input parameters.
     */
    private static String repr(Object obj) {
        if (obj instanceof String) {
            return "\"" + StringEscapeUtils.escapeJava((String)obj) + "\"";
        } else {
            return obj.toString();
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
    
    /**
     * Sanitize all property names.
     * Also replace original names with sanitized ones in expressions.
     */
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

    /**
     * Modify the user component structure to reference inputNames.
     * Add inputs left over to orphanSets.
     */
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
                if (ckv.getKey().equals(Namespace.CONFIG_COMPONENT))
                    continue;
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
                            ((Attr)vals.item(0)).setValue(
                                    "In." + pkv.getValue());
                            continue;
                        default:
                            //TODO freak out
                        case 0:
                            //nothing
                        }
                    }
                    set_str.printf(
                            "  set \"%s#%s\" In.%s;%n",
                            ckv.getKey(), pkv.getKey(), pkv.getValue());
                }
            }
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
        orphanSets = set_baos.toByteArray();
    }

    private void writeSetup() throws IOException, TransformerException {
        String[]
            start = {"import \"Sequence\"",
                     "import \"file:cityopt/inputs.scl\" as In",
                     ""},
            middle = {"",
                      "setup :: AprosSequence ()",
                      "setup = mdo {",
                      "  setupUCs;"},
            end = {"  return ()",
                   "}"};
                
        try (PrintStream setup = new PrintStream(setup_scl.toFile())) {
            for (String s: start)
                setup.println(s);
            getTransformer().transform(
                    new DOMSource(uc_structure),
                    new StreamResult(setup));
            for (String s: middle)
                setup.println(s);
            setup.write(orphanSets);
            for (String s: end)
                setup.println(s);
            Map<String, String> dummy = inputNames.get(
                    Namespace.CONFIG_COMPONENT);
            if (dummy != null) {
                setup.println();
                for (Map.Entry<String, String> kv : dummy.entrySet()) {
                    setup.printf("%s = In.%s%n",
                                 sanitize(kv.getKey()), kv.getValue());
                }
            }
        }
    }
}
