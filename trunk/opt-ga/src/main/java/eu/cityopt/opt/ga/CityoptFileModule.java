package eu.cityopt.opt.ga;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import org.opt4j.core.config.Icons;
import org.opt4j.core.config.annotations.File;
import org.opt4j.core.config.annotations.Icon;
import org.opt4j.core.config.annotations.Info;
import org.opt4j.core.config.annotations.Parent;
import org.opt4j.core.start.Constant;
import org.opt4j.core.start.Opt4JModule;

import com.google.inject.name.Names;

import eu.cityopt.sim.eval.HashSimulationStorage;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.eval.SimulatorManager;

@Parent(CityoptModule.class)
@Info("Read the Cityopt problem from files")
@Icon(Icons.DISK)
public class CityoptFileModule extends Opt4JModule {
    @Info("Directory containing Apros profiles")
    @File
    @Constant(value="aprosDir", namespace=SimulatorProvider.class)
    private String aprosDir = "c:/apros/profiles";

    @Info("Simulator name and version")
    @Constant(value="simulator", namespace=SimulatorProvider.class)
    private String simulator = "Apros-Combustion-5.13.06-64bit";
    
    @Info("Time origin.  If empty read from the zip file.")
    @Constant(value="timeOrigin", namespace=CityoptFileModule.class)
    private String timeOrigin = "2015-01-01T00:00:00Z";
    
    @Info("The zip file containing the model")
    @File(".zip")
    @Constant(value="modelFile", namespace=CityoptFileModule.class)
    private String modelFile = "";
    
    @Info("The optimisation problem definition file")
    @File(".csv")
    @Constant(value="problemFile", namespace=CityoptFileModule.class)
    private String problemFile = "";

    @Override
    public void config() {
        install(new CityoptModule());
        install(new JacksonCsvModule());
        bind(SimulatorManager.class).toProvider(SimulatorProvider.class);
        bind(SimulationModel.class).toProvider(ModelBlobLoader.class);
        bind(OptimisationProblem.class).toProvider(ProblemFromBinder.class);
        bind(Path.class).annotatedWith(Names.named("model")).toInstance(
                Paths.get(modelFile));
        bind(Path.class).annotatedWith(Names.named("problem")).toInstance(
                Paths.get(problemFile));
        bind(Instant.class).annotatedWith(Names.named("timeOrigin"))
                .toInstance(Instant.parse(timeOrigin));
        bind(SimulationStorage.class).to(HashSimulationStorage.class)
                .in(SINGLETON);
    }

    public String getAprosDir() {
        return aprosDir;
    }

    public void setAprosDir(String aprosDir) {
        this.aprosDir = aprosDir;
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

    public String getModelFile() {
        return modelFile;
    }


    public void setModelFile(String modelFile) {
        this.modelFile = modelFile;
    }


    public String getProblemFile() {
        return problemFile;
    }


    public void setProblemFile(String problemFile) {
        this.problemFile = problemFile;
    }
}
