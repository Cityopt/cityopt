package eu.cityopt.opt.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.HashSimulationStorage;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.Solution;

/**
 * Write optimisation solutions to a CSV file.
 * @author ttekth
 */
public class CSVSolutionWriter implements SolutionWriter {
    public final OptimisationProblem problem;
    public final HashSimulationStorage storage;
    public final ObjectWriter writer;
    private final OutputStream str;
    private SequenceWriter seq = null;
    public List<String> metrics;

    static class Context {
        @Inject
        OptimisationProblem problem;

        @Inject(optional=true)
        HashSimulationStorage storage;
    }

    @Inject
    public CSVSolutionWriter(CsvMapper mapper, Context context,
                             @Assisted OutputStream str) {
        problem = context.problem;
        storage = context.storage;
        writer = mapper.writer()
                .without(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        this.str = str;
    }

    private synchronized void writeHeader() throws IOException {
        if (seq == null) {
            seq = writer.writeValues(str);
        }
        Stream<String> row
                = problem.decisionVars.stream().map(v -> v.toString());
        if (storage == null) {
            metrics = null;
        } else {
            metrics = problem.getNamespace().metrics.entrySet().stream()
                    .filter(kv -> !kv.getValue().isTimeSeriesType())
                    .map(kv -> kv.getKey()).collect(Collectors.toList());
            row = Stream.concat(row, metrics.stream());

        }
        row = Stream.concat(row,
                            problem.constraints.stream().map(
                                    Constraint::getName));
        row = Stream.concat(row,
                            problem.objectives.stream().map(
                                    ObjectiveExpression::getName));
        seq.write(row.collect(Collectors.toList()));
    }

    @Override
    public synchronized void writeSolution(DecisionValues dvals, Solution sol)
            throws IOException {
        if (seq == null) {
            writeHeader();
        }
        Stream<Object> row
                = problem.decisionVars.stream().map(v -> dvals.get(v));
        if (metrics != null) {
            MetricValues met = storage.getMetrics(sol.input);
            row = Stream.concat(row, metrics.stream().map(n -> met.get(n)));
        }
        row = Stream.concat(
                row,
                Arrays.stream(sol.constraintStatus.infeasibilities).boxed());
        if (sol.objectiveStatus != null) {
            row = Stream.concat(
                    row, Arrays.stream(
                            sol.objectiveStatus.objectiveValues).boxed());
        }
        seq.write(row.collect(Collectors.toList()));
    }

    /**
     * Flush internal buffers and the underlying stream.
     */
    @Override
    public synchronized void flush() throws IOException {
        if (seq != null) {
            seq.flush();
        }
    }

    /**
     * Flush any internal buffers.  This does not close the underlying stream.
     */
    @Override
    public synchronized void close() throws IOException {
        if (seq != null) {
            try {
                seq.close();
            } finally {
                seq = null;
            }
        }
    }
}
