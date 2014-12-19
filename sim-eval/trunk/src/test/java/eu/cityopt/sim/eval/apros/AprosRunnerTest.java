package eu.cityopt.sim.eval.apros;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

import org.junit.Test;

public class AprosRunnerTest {

    @Test
    public void testGetTransformer() throws Exception {
        Transformer tf = AprosRunner.getTransformer();
    }
}
