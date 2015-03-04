package eu.cityopt.opt.ga;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import javax.inject.Singleton;

import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulatorConfigurationException;
import eu.cityopt.sim.eval.SimulatorManager;

@Singleton
public class ProblemFromFiles extends OptimisationProblem {
    private static SimulationModel readModel(
            SimulatorManager simulator, Path file)
            throws IOException, SimulatorConfigurationException {
        try (InputStream stream = new FileInputStream(file.toFile())) {
            return simulator.parseModel(stream);
        }
    }
    
    @Inject
    public ProblemFromFiles(
            SimulatorManager simulator,
            @Constant(value="timeOrigin", namespace=ProblemFromFiles.class)
            String timeOrigin,
            @Constant(value="modelFile", namespace=ProblemFromFiles.class)
            String modelFile,
            @Constant(value="problemFile", namespace=ProblemFromFiles.class)
            String problemFile)
            throws IOException, SimulatorConfigurationException {
        this(readModel(simulator, Paths.get(modelFile)),
             Paths.get(problemFile),
             timeOrigin.isEmpty() ? null : Instant.parse(timeOrigin));
    }
    
    public ProblemFromFiles(SimulationModel model, Path problemFile, Instant t0)
            throws IOException, SimulatorConfigurationException {
        super(model, CSVReaders.readNamespace(
                t0 != null ? t0 : model.getTimeOrigin(),
                problemFile));
        CSVReaders.readProblemFile(this, problemFile);
    }
}
