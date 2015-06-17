package eu.cityopt.opt.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;

import javax.script.ScriptException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import eu.cityopt.sim.eval.EvaluationSetup;
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
    private static CsvMapper
            mapper = JacksonCsvModule.getCsvMapper(),
            tsMapper = JacksonCsvModule.getTsCsvMapper();
    private static ObjectReader
            reader = JacksonCsvModule.getProblemReader(mapper);
    private static ObjectWriter
            writer = JacksonCsvModule.getProblemWriter(mapper),
            tsWriter = JacksonCsvModule.getTsWriter(tsMapper);
    
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
        JacksonBinder binder = new JacksonBinder(reader, problemStream);
        EvaluationSetup setup = timeSeriesData.getEvaluationSetup();
        Namespace ns = binder.makeNamespace(setup.evaluator, setup.timeOrigin);
        return binder.buildWith(
                new ProblemBuilder(null, ns, timeSeriesData))
                .getResult();
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
        JacksonBinder binder = new JacksonBinder(reader, structureStream);
        Namespace ns = binder.makeNamespace(setup.evaluator, setup.timeOrigin);
        SimulationStructureBuilder bld = new SimulationStructureBuilder(
                new SimulationStructure(null, ns));
        return binder.buildWith(bld).getResult();
    }
    
    /**
     * Export an OptimisationProblem to CSV files.
     * The output streams are left open.
     * @param problem Problem to export
     * @param problemOut Output stream for the problem description
     * @param tsOut Output stream for time series data
     */
    public static void writeProblemCsv(
            OptimisationProblem problem,
            OutputStream problemOut, OutputStream tsOut) throws IOException {
        ObjectWriter problem_wtr = writer.without(
                JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        CsvTimeSeriesWriter ts_wtr = new CsvTimeSeriesWriter(tsWriter);
        ExportBuilder bld = new ExportBuilder(problem.getNamespace());
        ExportDirectors.build(problem, bld, null);
        problem_wtr.writeValue(problemOut, bld.getBinder());
        ts_wtr.write(tsOut, bld.getTimeSeriesData());
    }
    
    /**
     * Export a SimulationStructure to a CSV file.
     * The output stream is left open.
     */
    public static void writeStructureCsv(
            SimulationStructure sim, OutputStream out) throws IOException {
        ObjectWriter wtr = writer.without(
                JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        ExportBuilder bld = new ExportBuilder(sim.getNamespace());
        ExportDirectors.build(sim, bld);
        wtr.writeValue(out, bld.getBinder());
    }
    
    /**
     * Export an OptimisationProblem to CSV files.
     */
    public static void writeProblemCsv(
            OptimisationProblem problem, Path problemFile, Path tsFile)
                    throws IOException {
        try (OutputStream pr = Files.newOutputStream(problemFile);
             OutputStream ts = Files.newOutputStream(tsFile)) {
            writeProblemCsv(problem, pr, ts);
        }
    }
    
    public static void write(
            ExportBuilder builder, OutputStream problemOut, OutputStream tsOut)
                    throws IOException {
        ObjectWriter wtr = writer.without(
                JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        CsvTimeSeriesWriter ts_wtr = new CsvTimeSeriesWriter(tsWriter);
        wtr.writeValue(problemOut, builder.getBinder());
        ts_wtr.write(tsOut, builder.getTimeSeriesData());
    }
    
    public static void write(
            ExportBuilder builder, Path problemFile, Path tsFile)
                    throws IOException {
        try (OutputStream pr = Files.newOutputStream(problemFile);
             OutputStream ts = Files.newOutputStream(tsFile)) {
            write(builder, pr, ts);
        }
    }
}
