package eu.cityopt.opt.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;

import javax.script.ScriptException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import eu.cityopt.opt.io.JacksonBinderScenario.ScenarioItem;
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
            reader = JacksonCsvModule.getProblemReader(mapper),
            screader = JacksonCsvModule.getScenarioProblemReader(mapper);
    private static ObjectWriter
            prwriter = JacksonCsvModule.getProblemWriter(mapper),
            scwriter = JacksonCsvModule.getScenarioWriter(mapper);
    private static CsvTimeSeriesWriter
            tswriter = new CsvTimeSeriesWriter(
                    JacksonCsvModule.getTsWriter(tsMapper));

    public static OptimisationProblem readProblemCsv(
            Path path, TimeSeriesData timeSeriesData, Namespace ns)
                    throws ParseException, ScriptException, IOException {
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            return readProblemCsv(fis, timeSeriesData, ns);
        }
    }

    public static OptimisationProblem readProblemCsv(
            InputStream problemStream, TimeSeriesData timeSeriesData, Namespace ns)
                    throws ParseException, ScriptException, IOException {
        JacksonBinder binder = new JacksonBinder(reader, problemStream);
        EvaluationSetup setup = timeSeriesData.getEvaluationSetup();
        if (ns == null)
            ns = binder.makeNamespace(setup.evaluator, setup.timeOrigin);
        return binder.buildWith(
                new ProblemBuilder(null, ns, timeSeriesData))
                .getResult();
    }

    public static SimulationStructure readStructureCsv(
            Path path, EvaluationSetup setup, Namespace ns)
                    throws ParseException, ScriptException, IOException {
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            return readStructureCsv(fis, setup, ns);
        }
    }

    public static SimulationStructure readStructureCsv(
            InputStream structureStream, EvaluationSetup setup, Namespace ns)
                    throws ParseException, ScriptException, IOException {
        JacksonBinder binder = new JacksonBinder(reader, structureStream);
        if (ns == null)
            ns = binder.makeNamespace(setup.evaluator, setup.timeOrigin);
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
        ExportBuilder bld = new ExportBuilder(problem.getNamespace());
        ExportDirectors.build(problem, bld, null);
        writeSingle(bld, problemOut);
        TimeSeriesData tsd = bld.getTimeSeriesData();
        if (!tsd.isEmpty()) {
            writeTimeSeries(tsd, tsOut);
        }
    }

    /**
     * Export a SimulationStructure to a CSV file.
     * The output stream is left open.
     */
    public static void writeStructureCsv(
            SimulationStructure sim, OutputStream out) throws IOException {
        ObjectWriter wtr = prwriter.without(
                JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        ExportBuilder bld = new ExportBuilder(sim.getNamespace());
        ExportDirectors.buildStructure(sim, bld);
        wtr.writeValue(out, bld.getBinder());
    }

    /**
     * Export an OptimisationProblem to CSV files.
     * Only creates tsFile if there are time series to export.
     */
    public static void writeProblemCsv(
            OptimisationProblem problem, Path problemFile, Path tsFile)
                    throws IOException {
        ExportBuilder bld = new ExportBuilder(problem.getNamespace());
        ExportDirectors.build(problem, bld, null);
        writeSingle(bld, problemFile);
        TimeSeriesData tsd = bld.getTimeSeriesData();
        if (!tsd.isEmpty()) {
            writeTimeSeries(tsd, tsFile);
        }
    }

    /**
     * Write out multi-scenario data.
     * Scenario and external parameter set names are included.  Does not
     * close the stream.
     */
    public static void writeMulti(ExportBuilder builder, OutputStream out)
            throws IOException {
        scwriter.without(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
                .writeValue(out, builder.getScenarioBinder());
    }

    /**
     * Write out multi-scenario data.
     * @see #writeMulti(ExportBuilder, OutputStream)
     */
    public static void writeMulti(ExportBuilder builder, Path outFile)
            throws IOException {
        scwriter.writeValue(outFile.toFile(), builder.getScenarioBinder());
    }

    public static List<ScenarioItem> readMulti(Path inFile) throws IOException {
        JacksonBinderScenario
        binder = new JacksonBinderScenario(screader, inFile);
        return binder.getItems();
    }

    public static List<ScenarioItem> readMulti(InputStream inStream)
            throws IOException {
        JacksonBinderScenario
            binder = new JacksonBinderScenario(screader, inStream);
        return binder.getItems();
    }

    /**
     * Write out single scenario data.
     * Scenario and external parameter set names are not included, hence this
     * should not be used on data containing multiple scenarios or external
     * parameter sets.  Does not close the stream.
     */
    public static void writeSingle(ExportBuilder builder, OutputStream out)
            throws IOException {
        prwriter.without(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
                .writeValue(out, builder.getBinder());
    }

    /**
     * Write out single scenario data.
     * @see #writeSingle(ExportBuilder, OutputStream)
     */
    public static void writeSingle(ExportBuilder builder, Path outFile)
            throws IOException {
        prwriter.writeValue(outFile.toFile(), builder.getBinder());
    }

    /**
     * Write out time series data.  Does not close the stream.
     */
    public static void writeTimeSeries(TimeSeriesData tsd, OutputStream out)
            throws IOException {
        tswriter.write(out, tsd);
    }

    /**
     * Write out time series data.
     */
    public static void writeTimeSeries(TimeSeriesData tsd, Path outFile)
            throws IOException {
        try (OutputStream out = Files.newOutputStream(outFile)) {
            writeTimeSeries(tsd, out);
        }
    }

    /**
     * Write out time series data.
     */
    public static void writeTimeSeries(ExportBuilder bld, Path outFile)
            throws IOException {
        writeTimeSeries(bld.getTimeSeriesData(), outFile);
    }
}
