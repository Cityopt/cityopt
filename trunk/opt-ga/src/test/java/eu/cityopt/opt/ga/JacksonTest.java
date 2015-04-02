package eu.cityopt.opt.ga;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.opt4j.core.start.Opt4JTask;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;
import com.google.inject.name.Names;

import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.opt.OptimisationProblem;

public class JacksonTest {
    private final static String propsName = "/test.properties";
    private static Properties props;
    private static Path dataDir;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    public static class TestModule extends AbstractModule {
        Instant t0 = Instant.parse(props.getProperty("time_origin"));
        Path mfile = dataDir.resolve(props.getProperty("model_file"));
        Path pfile = dataDir.resolve(props.getProperty("problem_file"));
       
        @Override
        protected void configure() {
            bind(Instant.class).annotatedWith(Names.named("timeOrigin"))
                    .toInstance(t0);
            bind(Path.class).annotatedWith(Names.named("model"))
                    .toInstance(mfile);
            bind(Path.class).annotatedWith(Names.named("problem"))
                    .toInstance(pfile);
        }
    }
   
    @BeforeClass
    public static void setupProps() throws Exception {
        URL purl = JacksonTest.class.getResource(propsName);
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
                Arrays.asList(cases), JacksonBinder.Obj::isMaximize);
        assertArrayEquals(exptd, results.toArray());
    }
    
    public void checkProblem(OptimisationProblem p) {
        assertTrue(p.inputConst.getExternalParameters().isComplete());
        assertFalse(p.inputConst.isComplete());
        assertEquals(1, p.constraints.size());
        assertEquals(2, p.decisionVars.size());
        assertFalse(p.inputConst.isComplete());
        assertEquals(2, p.inputExprs.size());
        assertEquals(2, p.metrics.size());
        assertEquals(1, p.objectives.size());
    }
    
    @Test
    public void testNameClash() throws Exception {
        TestModule tm = new TestModule();
        ObjectMapper json = new ObjectMapper();
        json.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        String row
            = "{'kind': 'ext', 'name': 'foo', 'type': 'Double', 'value': '1'}";
        JacksonBinder binder = json.readValue(
                String.format("[%s, %s]", row, row), JacksonBinder.class);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("duplicate");
        binder.makeNamespace(tm.t0);
    }

    @Test
    public void testReadCSV() throws Exception {
        TestModule tm = new TestModule();
        JacksonCsvModule jm = new JacksonCsvModule();
        Injector csv_inj = Guice.createInjector(jm, tm);
        JacksonBinder binder = csv_inj.getInstance(JacksonBinder.class);
        ObjectMapper json = new ObjectMapper();
        json.enable(SerializationFeature.INDENT_OUTPUT);
        json.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        json.writeValue(System.out, binder);
        ObjectWriter wtr = csv_inj.getInstance(
                Key.get(ObjectWriter.class, Names.named("problem")));
        wtr.without(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
                .writeValue(System.out, binder);
        Namespace ns = binder.makeNamespace(tm.t0);
        OptimisationProblem p = new OptimisationProblem(
                null, new ExternalParameters(ns));
        binder.addToProblem(p);
        checkProblem(p);
        assertNull(p.model);
    }
    
    private CityoptFileModule getCityoptFileModule() {
        TestModule tm = new TestModule();
        CityoptFileModule cfm = new CityoptFileModule();
        cfm.setModelFile(tm.mfile.toString());
        cfm.setProblemFile(tm.pfile.toString());
        cfm.setTimeOrigin(tm.t0.toString());
        return cfm;
    }
    
    @Test
    @Ignore("model file likely incorrect")
    public void testInjectProblem() throws Exception {
        CityoptFileModule cfm = getCityoptFileModule();
        Opt4JTask task = new Opt4JTask(false);
        try {
            task.init(cfm);
            task.open();
            OptimisationProblem p = task.getInstance(OptimisationProblem.class);
            checkProblem(p);
            assertNotNull(p.model);
        } finally {
            task.close();
        }
    }
    
    @Test
    public void graphCityoptFile() throws IOException {
        String dotname = props.getProperty("dot_file");
        if (dotname == null || dotname.isEmpty()) {
            System.out.println("Dependency graph output skipped.");
            return;
        }
        Injector cf_inj = Guice.createInjector(getCityoptFileModule());
        Injector gv_inj = Guice.createInjector(new GraphvizModule());
        GraphvizGrapher g = gv_inj.getInstance(GraphvizGrapher.class);
        try (PrintWriter out = new PrintWriter(dotname)) {
            System.out.println("Writing graph to " + dotname);
            g.setOut(out);
            g.graph(cf_inj);
        }
    }
}
