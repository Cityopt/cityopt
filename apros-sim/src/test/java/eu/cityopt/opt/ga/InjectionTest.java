package eu.cityopt.opt.ga;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;
import org.opt4j.core.start.Opt4JTask;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;

import eu.cityopt.opt.io.CityoptOutputModule;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.opt.io.TestBase;

public class InjectionTest extends TestBase {

    private AprosFileModule getCityoptFileModule() {
        TestModule tm = new TestModule();
        AprosFileModule cfm = new AprosFileModule();
        cfm.setModelFile(tm.mfile.toString());
        cfm.setProblemFile(tm.pfile.toString());
        String sep = System.getProperty("path.separator");
        String p = Arrays.stream(tm.tsfiles)
                .map(Path::toString).collect(Collectors.joining(sep));
        cfm.setTimeSeriesFile(p);
        cfm.setTimeOrigin(tm.t0.toString());
        return cfm;
    }

    @Test
    public void testInjectProblem() throws Exception {
        AprosFileModule cfm = getCityoptFileModule();
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

    // dot -Tpdf -Grankdir=LR -O cfm-deps.dot
    @Test
    public void graphCityoptModules() throws IOException {
        String dotname = res.properties.getProperty("dot_file");
        assumeFalse("Output file name (dot_file) not set",
                    dotname == null || dotname.isEmpty());
        AprosDistributorModule distr = new AprosDistributorModule();
        CityoptOutputModule output = new CityoptOutputModule();
        Injector cf_inj = Guice.createInjector(
                getCityoptFileModule(), distr, output);
        Injector gv_inj = Guice.createInjector(new GraphvizModule());
        GraphvizGrapher g = gv_inj.getInstance(GraphvizGrapher.class);
        try (PrintWriter out = new PrintWriter(dotname)) {
            System.out.println("Writing graph to " + dotname);
            g.setOut(out);
            g.graph(cf_inj);
        }
    }


}
