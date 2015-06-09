package eu.cityopt.opt.io;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.time.Instant;

import org.junit.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

import eu.cityopt.opt.ga.ProblemFromBinder;
import eu.cityopt.opt.ga.TimeSeriesLoader;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.test.TestResources;

public class ExportBuilderTest {
    private static TestResources res;
    private static Instant t0;
    private static Path pfile;
    private static Path[] tsfiles;
  
    public static class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Instant.class).annotatedWith(Names.named("timeOrigin"))
                    .toInstance(t0);
            bind(Path.class).annotatedWith(Names.named("problem"))
                    .toInstance(pfile);
            bind(Path[].class).annotatedWith(Names.named("timeseries"))
                    .toInstance(tsfiles);
            bind(SimulationModel.class).toProvider(Providers.of(null));
            bind(TimeSeriesData.class).toProvider(TimeSeriesLoader.class);
            bind(OptimisationProblem.class)
                    .toProvider(ProblemFromBinder.class);
        }
    }
    
    private TestModule tm;
    private Injector inj;

    @BeforeClass
    public static void setupResources() throws Exception {
        res = new TestResources();
        t0 = Instant.parse(res.properties.getProperty("time_origin"));
        pfile = res.getPath("problem_file");
        tsfiles = res.getPaths("ts_files");
    }
    
    @Before
    public void setupInj() throws Exception {
        tm = new TestModule();
        inj = Guice.createInjector(new JacksonCsvModule(), tm);
    }

    @Test
    public void printStuff() throws Exception {
        OptimisationProblem p = inj.getInstance(OptimisationProblem.class);
        ExportBuilder bld = new ExportBuilder(p.getNamespace());
        ExportDirector.build(p, bld, null, null);
        ObjectWriter wtr = inj.getInstance(
                Key.get(ObjectWriter.class, Names.named("scenario")));
        wtr.without(JsonGenerator.Feature.AUTO_CLOSE_TARGET).writeValue(
                System.out, bld.getBinder());
        CsvTimeSeriesWriter tsw = inj.getInstance(CsvTimeSeriesWriter.class);
        tsw.write(System.out, bld.getTimeSeriesData());
    }

}
