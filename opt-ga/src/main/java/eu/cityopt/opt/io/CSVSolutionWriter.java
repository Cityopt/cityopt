package eu.cityopt.opt.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.Solution;

/**
 * Write optimisation solutions to a CSV file.
 * @author ttekth
 */
public class CSVSolutionWriter implements SolutionWriter {
    public final OptimisationProblem problem;
    public final ObjectWriter writer;
    
    @Inject
    public CSVSolutionWriter(CsvMapper mapper, OptimisationProblem problem) {
        this.problem = problem;
        writer = mapper.writer()
                .without(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    }
    
    @Override
    public void writeHeader(OutputStream str) throws IOException {
        Stream<String> row
                = problem.decisionVars.stream().map(v -> v.toString());
        row = Stream.concat(row,
                            problem.constraints.stream().map(
                                    Constraint::getName));
        row = Stream.concat(row,
                            problem.objectives.stream().map(
                                    ObjectiveExpression::getName));
        writer.writeValue(str, row.collect(Collectors.toList()));
    }
    
    @Override
    public void writeSolution(
            OutputStream str, DecisionValues dvals, Solution sol)
                    throws IOException {
        Stream<Object> row
                = problem.decisionVars.stream().map(v -> dvals.get(v));
        row = Stream.concat(
                row,
                Arrays.stream(sol.constraintStatus.infeasibilities).boxed());
        row = Stream.concat(
                row,
                Arrays.stream(sol.objectiveStatus.objectiveValues).boxed());
        writer.writeValue(str, row.collect(Collectors.toList()));
    }
}
