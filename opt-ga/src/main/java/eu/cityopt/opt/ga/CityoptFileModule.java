package eu.cityopt.opt.ga;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;

import javax.script.ScriptException;

import org.opt4j.core.config.Icons;
import org.opt4j.core.config.annotations.File;
import org.opt4j.core.config.annotations.Icon;
import org.opt4j.core.config.annotations.Info;
import org.opt4j.core.config.annotations.Parent;
import org.opt4j.core.start.Constant;
import org.opt4j.core.start.Opt4JModule;

import com.google.inject.throwingproviders.CheckedProvider;
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;

import eu.cityopt.sim.eval.EvaluationException;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulatorConfigurationException;
import eu.cityopt.sim.eval.SimulatorManager;
import eu.cityopt.sim.eval.SimulatorManagers;

@Parent(CityoptModule.class)
@Info("Read the Cityopt problem from files")
@Icon(Icons.DISK)
public class CityoptFileModule extends Opt4JModule {
    public interface ProblemReader<T> extends CheckedProvider<T> {
        T get() throws IOException, SimulatorConfigurationException,
                       EvaluationException, ScriptException, ParseException;
    }
    
    @Info("Simualtor name and version")
    @Constant(value = "simulator_name", namespace = CityoptFileModule.class)
    private String simulatorName = "Apros-Combustion-5.13.06-64bit";
    
    @Info("Time origin.  If empty read from the zip file.")
    @Constant(value = "time_origin", namespace = CityoptFileModule.class)
    private String time_origin = "2015-01-01T00:00:00Z";
    
    @Info("The zip file containing the model")
    @File(".zip")
    @Constant(value = "model", namespace = CityoptFileModule.class)
    private String modelFile = "";
    
    @Info("The optimisation problem definition file")
    @File(".csv")
    @Constant(value = "problem", namespace = CityoptFileModule.class)
    private String problemFile = "";
    
    @CheckedProvides(ProblemReader.class)
    public OptimisationProblem readProblem()
            throws IOException, SimulatorConfigurationException,
                   EvaluationException, ScriptException, ParseException {
        SimulatorManager mgr = SimulatorManagers.get(simulatorName);
        byte[] modelData = Files.readAllBytes(Paths.get(modelFile));
        SimulationModel model = mgr.parseModel(modelData);
        Path ppath = Paths.get(problemFile);
        Instant t0 = time_origin.isEmpty() ? model.getTimeOrigin()
                                           : Instant.parse(time_origin);
        Namespace ns = CSVReaders.readNamespace(t0, ppath);
        OptimisationProblem p = CSVReaders.readProblem(ns, ppath);
        p.runner = mgr.makeRunner(model, ns);
        return p;
    }

    @Override
    public void config() {
        install(ThrowingProviderBinder.forModule(this));
    }

    public String getTime_origin() {
        return time_origin;
    }

    public void setTime_origin(String time_origin) {
        this.time_origin = time_origin;
    }

    public String getSimulatorName() {
        return simulatorName;
    }


    public void setSimulatorName(String simulatorName) {
        this.simulatorName = simulatorName;
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
