package eu.cityopt.opt.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Properties;

import org.junit.BeforeClass;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.test.TestResources;

public class TestBase {
    protected static TestResources res;

    public static class TestModule extends AbstractModule {
        Properties props = res.properties;
        public Instant t0 = Instant.parse(props.getProperty("time_origin"));
        public Path mfile = res.getPath("model_file");
        public Path pfile = res.getPath("problem_file");
        Path sfile = res.getPath("scenario_file");
        public Path[] tsfiles = res.getPaths("ts_files");

        @Override
        protected void configure() {
            bind(Instant.class).annotatedWith(Names.named("timeOrigin"))
                    .toInstance(t0);
            bind(Path.class).annotatedWith(Names.named("model"))
                    .toInstance(mfile);
            bind(Path.class).annotatedWith(Names.named("problem"))
                    .toInstance(pfile);
            bind(Path.class).annotatedWith(Names.named("scenario"))
                    .toInstance(sfile);
            bind(Path[].class).annotatedWith(Names.named("timeseries"))
                    .toInstance(tsfiles);
        }
    }

    @BeforeClass
    public static void setupProps() throws Exception {
        res = new TestResources();
    }

    public static void checkProblem(OptimisationProblem p) {
        assertTrue(p.inputConst.getExternalParameters().isComplete());
        assertFalse(p.inputConst.isComplete());
        assertEquals(1, p.constraints.size());
        assertEquals(2, p.decisionVars.size());
        assertEquals(2, p.inputExprs.size());
        assertEquals(2, p.metrics.size());
        assertEquals(1, p.objectives.size());
    }
}