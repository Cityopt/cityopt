package eu.cityopt.opt.ga;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.inject.Singleton;

import org.junit.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationModel;

public class JacksonTest {
    private final static String propsName = "/test.properties";
    private static Properties props;
    private static Path dataDir;
    
    public static class TestModule extends AbstractModule {
        Instant t0 = Instant.parse(props.getProperty("time_origin"));
        SimulationModel model = null;
       
        @Provides
        @Singleton
        public Namespace getNamespace(JacksonBinder binder) {
            return binder.makeNamespace(t0);
        }
        
        @Provides
        public SimulationModel getModel() {return model;}

        @Override
        protected void configure() {
            bind(Path.class).annotatedWith(Names.named("problem")).toInstance(
                    dataDir.resolve(props.getProperty("problem_file")));
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
        assertEquals(1, p.constraints.size());
        assertEquals(2, p.decisionVars.values().stream()
                .mapToInt(m -> m.size()).sum());
        assertFalse(p.inputConst.isComplete());
        assertEquals(2, p.inputExprs.size());
        assertEquals(2, p.metrics.size());
        assertEquals(1, p.objs.size());
    }

    @Test
    public void testReadCSV() throws Exception {
        TestModule tm = new TestModule();
        Injector csv_inj = Guice.createInjector(
                new JacksonCsvModule(), tm);
        JacksonBinder binder = csv_inj.getInstance(JacksonBinder.class);
        ObjectMapper json = new ObjectMapper();
        json.enable(SerializationFeature.INDENT_OUTPUT);
        json.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        json.writeValue(System.out, binder);
        ObjectWriter wtr = csv_inj.getInstance(ObjectWriter.class);
        wtr.without(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
                .writeValue(System.out, binder);
        Namespace ns = tm.getNamespace(binder);
        OptimisationProblem p = new OptimisationProblem(null, ns);
        binder.addToProblem(p);
        checkProblem(p);
        assertNull(p.model);
    }
    
    @Test
    @Ignore
    public void testInjectProblem() throws Exception {
        TestModule tm = new TestModule();
        Injector csv_inj = Guice.createInjector(
                new JacksonCsvModule(), tm);
        OptimisationProblem p = csv_inj.getInstance(OptimisationProblem.class);
        checkProblem(p);
    }
}
