package eu.cityopt.sim.eval.apros;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationRunner;

/**
 * A factory of AprosJobs for one model.
 * @author ttekth
 */
public class AprosRunner implements SimulationRunner {
    private Document uc_structure;
    private Map<String, String> name_map = new HashMap<String, String>();
    private Transformer a62scl;

    @Override
    public AprosJob start(SimulationInput input) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public AprosRunner(Document uc_props) {
        try {
            a62scl = getTransformer();
        } catch (IOException | TransformerException e) {
            throw new RuntimeException("Failed to load XSLT.", e);
        }
        this.uc_structure = (Document)uc_props.cloneNode(true);
        sanitize();
    }

    static Transformer getTransformer()
            throws IOException, TransformerConfigurationException {
        TransformerFactory xff = TransformerFactory.newInstance();
        try (InputStream xslt = ClassLoader.getSystemResourceAsStream(
                "xslt/a62scl.xsl")) {
            return xff.newTransformer(new StreamSource(xslt));
        }
    }

    private void sanitize() {
    }
}
