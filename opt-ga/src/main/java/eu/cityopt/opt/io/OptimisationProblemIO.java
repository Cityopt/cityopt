package eu.cityopt.opt.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;

import javax.script.ScriptException;

import com.fasterxml.jackson.databind.ObjectReader;

import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.opt.OptimisationProblem;

/**
 * Reads OptimisationProblem instances from CSV files.
 * This is a simple facade over Jackson based (de)serialisation.
 *
 * @author Hannu Rummukainen
 */
public class OptimisationProblemIO {
    public static OptimisationProblem readCsv(Path path, Instant timeOrigin)
            throws ParseException, ScriptException, IOException {
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            return readCsv(fis, timeOrigin);
        }
    }

    public static OptimisationProblem readCsv(InputStream problemStream, Instant timeOrigin)
            throws ParseException, ScriptException, IOException {
        ObjectReader reader = JacksonCsvModule.getReader(JacksonCsvModule.getCsvMapper());
        JacksonBinder binder = new JacksonBinder(reader, problemStream);
        Namespace ns = binder.makeNamespace(timeOrigin);
        OptimisationProblem problem = new OptimisationProblem(
                null, new ExternalParameters(ns));
        binder.addToProblem(problem);
        return problem;
    }
}
