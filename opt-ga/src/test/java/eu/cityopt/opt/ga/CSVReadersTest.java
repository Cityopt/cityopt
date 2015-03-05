package eu.cityopt.opt.ga;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;

import eu.cityopt.sim.eval.Namespace;

@Deprecated
public class CSVReadersTest {
    private final static String propsName = "/test.properties";
    private static Properties props;
    private static Path dataDir;
    
    @BeforeClass
    public static void setupProps() throws Exception {
        URL purl = CSVReadersTest.class.getResource(propsName);
        props = new Properties();
        try (InputStream str = purl.openStream()) {
            props.load(str);
        }
        dataDir = Paths.get(purl.toURI()).getParent();
    }

    @Test
    public void testIsMaximize() {
        String[] cases = {"min", "Min", "MIN", "max", "Max", "MAX", "bork"};
        Boolean[] exptd = {false, false, false, true, true, true, null};
        List<Boolean> results = Lists.transform(
                Arrays.asList(cases), CSVReaders::isMaximize);
        assertArrayEquals(exptd, results.toArray());
    }

    @Test
    public void testReadProblem() throws Exception {
        Instant t0 = Instant.parse(props.getProperty("time_origin"));
        Path prob_file = dataDir.resolve(props.getProperty("problem_file"));
        Namespace ns = CSVReaders.readNamespace(t0, prob_file);
        OptimisationProblem p = new OptimisationProblem(null, ns);
        CSVReaders.readProblemFile(p, prob_file);
        assertEquals(1, p.constraints.size());
        assertEquals(2, p.decisionVars.values().stream()
                .mapToInt(m -> m.size()).sum());
        assertFalse(p.inputConst.isComplete());
        assertEquals(2, p.inputExprs.size());
        assertEquals(2, p.metrics.size());
        assertEquals(1, p.objs.size());
        assertNull(p.model);
    }
}
