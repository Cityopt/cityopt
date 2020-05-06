package eu.cityopt.opt.ga;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.opt4j.core.config.annotations.File;
import org.opt4j.core.config.annotations.Info;
import org.opt4j.core.problem.ProblemModule;
import org.opt4j.core.start.Constant;

import com.google.inject.name.Names;
import com.google.inject.util.Providers;

import eu.cityopt.opt.io.JacksonCsvModule;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.HashSimulationStorage;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.eval.TimeSeriesData;
import eu.cityopt.sim.opt.OptimisationProblem;

/**
 * Configure {@link CityoptModule} for file-based input.
 * Installs CityoptModule.  Supports the Opt4J GUI.  Subclasses
 * need an @Info annotation to the class and their config must call
 * super.config(), then bind ModelFactory and the model file
 * (with #bindModelFile).  The latter is because the file extension
 * for the UI may vary depending on model type, so we don't want to fix it
 * here.
 * @author ttekth
 *
 */
public abstract class CityoptFileModule extends ProblemModule {
    @Info("Simulator name and version. If empty read from the model file.")
    @Constant(value="simulator", namespace=ModelProvider.class)
    protected String simulator = "";

    @Info("Time origin. If empty read from the zip file.")
    protected String timeOrigin = "2015-01-01T00:00:00Z";

    @Info("The optimisation problem definition file")
    @File(".csv")
    protected String problemFile = "";

    @Info("The optimisation problem time series file")
    @File(".csv")
    protected String timeSeriesFile = "";

    /**
     * Bind the model file to p.
     */
    protected void bindModelFile(Path p) {
        bind(Path.class).annotatedWith(Names.named("model")).toInstance(p);
    }

    @Override
    public void config() {
        install(new CityoptModule());
        install(new JacksonCsvModule());
        bind(SimulationModel.class).toProvider(ModelProvider.class);
        addOptimizerStateListener(ModelCleanup.class);
        bind(OptimisationProblem.class).toProvider(ProblemFromBinder.class);
        bind(Path.class).annotatedWith(Names.named("problem")).toInstance(
                Paths.get(problemFile));
        bind(TimeSeriesData.class).toProvider(TimeSeriesLoader.class);
        String sep = Pattern.quote(System.getProperty("path.separator"));
        Path[] tspaths = Arrays.stream(timeSeriesFile.split(sep))
                .filter(s -> !s.isEmpty()).map(Paths::get)
                .toArray(Path[]::new);
        bind(Path[].class).annotatedWith(Names.named("timeseries"))
                .toInstance(tspaths);
        if (timeOrigin.isEmpty()) {
            bind(Instant.class).annotatedWith(Names.named("timeOrigin"))
                .toProvider(Providers.of(null));
        } else {
            bind(Instant.class).annotatedWith(Names.named("timeOrigin"))
                .toInstance(Instant.parse(timeOrigin));
        }
        bind(HashSimulationStorage.class).in(SINGLETON);
        bind(SimulationStorage.class).to(HashSimulationStorage.class);
        bind(Evaluator.class).in(SINGLETON);
    }

    public String getSimulator() {
        return simulator;
    }

    public void setSimulator(String simulator) {
        this.simulator = simulator;
    }

    public String getTimeOrigin() {
        return timeOrigin;
    }

    public void setTimeOrigin(String timeOrigin) {
        this.timeOrigin = timeOrigin;
    }

    public String getProblemFile() {
        return problemFile;
    }

    public void setProblemFile(String problemFile) {
        this.problemFile = problemFile;
    }

    public String getTimeSeriesFile() {
        return timeSeriesFile;
    }

    public void setTimeSeriesFile(String timeSeriesFile) {
        this.timeSeriesFile = timeSeriesFile;
    }
}
