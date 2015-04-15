package eu.cityopt.opt.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.ParseException;

import javax.script.ScriptException;

import com.fasterxml.jackson.databind.ObjectReader;

import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.SimulationStructure;

/**
 * Reads {@link OptimisationProblem} and {@link SimulationStructure} instances
 * from CSV files. This is a simple facade over Jackson based (de)serialisation.
 *
 * @author Hannu Rummukainen
 */
public class OptimisationProblemIO {
    public static OptimisationProblem readProblemCsv(
            Path path, TimeSeriesData timeSeriesData)
                    throws ParseException, ScriptException, IOException {
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            return readProblemCsv(fis, timeSeriesData);
        }
    }

    public static OptimisationProblem readProblemCsv(
            InputStream problemStream, TimeSeriesData timeSeriesData)
                    throws ParseException, ScriptException, IOException {
        ObjectReader reader = JacksonCsvModule.getProblemReader(JacksonCsvModule.getCsvMapper());
        JacksonBinder binder = new JacksonBinder(reader, problemStream, timeSeriesData);
        EvaluationSetup setup = timeSeriesData.getEvaluationSetup();
        Namespace ns = binder.makeNamespace(setup.evaluator, setup.timeOrigin);
        OptimisationProblem problem = new OptimisationProblem(
                null, new ExternalParameters(ns));
        binder.addToProblem(problem);
        return problem;
    }

    public static SimulationStructure readStructureCsv(
            Path path, EvaluationSetup setup)
                    throws ParseException, ScriptException, IOException {
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            return readStructureCsv(fis, setup);
        }
    }

    public static SimulationStructure readStructureCsv(
            InputStream structureStream, EvaluationSetup setup)
                    throws ParseException, ScriptException, IOException {
        ObjectReader reader = JacksonCsvModule.getProblemReader(JacksonCsvModule.getCsvMapper());
        JacksonBinder binder = new JacksonBinder(reader, structureStream, null);
        Namespace ns = binder.makeNamespace(setup.evaluator, setup.timeOrigin);
        SimulationStructure structure = new SimulationStructure(null, ns);
        binder.addMetrics(structure.metrics, ns);
        return structure;
    }
}
