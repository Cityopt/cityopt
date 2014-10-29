package eu.cityopt.sim.eval;

import java.util.Arrays;
import java.util.concurrent.Future;

import org.junit.Test;

public class TestEval {
    @Test
    public void evaluate() throws Exception {
        Namespace project = new Namespace();
        project.externals.put("a", Type.TIMESERIES);
        project.externals.put("b", Type.TIMESERIES);
        project.inputs.put("x5", Type.DOUBLE);
        project.inputs.put("x6", Type.DOUBLE);
        project.inputs.put("x7", Type.DOUBLE);
        project.inputs.put("x8", Type.DOUBLE);
        project.inputs.put("x9", Type.DOUBLE);
        project.outputs.put("x1", Type.TIMESERIES);
        project.outputs.put("x2", Type.TIMESERIES);
        project.outputs.put("x3", Type.TIMESERIES);
        project.outputs.put("x4", Type.TIMESERIES);
        project.metrics.put("m1", Type.DOUBLE);
        project.metrics.put("m2", Type.DOUBLE);

        Evaluator evaluator = new Evaluator();
        ConstraintExpression[] constraints = new ConstraintExpression[] {
                new ConstraintExpression(1,
                        "x9 * (x5 - x6) + 0.02 * x6 - 0.025 * x5",
                        Double.NEGATIVE_INFINITY, 0.0, evaluator),
                new ConstraintExpression(2,
                        "x9 * (x8 - x7) + 0.02 * x7 - 0.015 * x8",
                        Double.NEGATIVE_INFINITY, 0.0, evaluator) };
        MetricExpression[] metrics = new MetricExpression[] {
                new MetricExpression(1, "m1", "-9 * x5 - 15 * x8", evaluator),
                new MetricExpression(2, "m2", "10 * (x6 + x7)", evaluator)
                // TODO implement script access to TimeSeries
                // new MetricExpression(
                //      3, "m3", "6 * x1[0] + 16 * mean(x2)", evaluator)
        };
        ObjectiveExpression[] objectives = new ObjectiveExpression[] { new ObjectiveExpression(
                1, "m1 + m2", false, evaluator) };

        SimulationRunnerWithStorage runner = new SimulationRunnerWithStorage(
                new SimRunner(), new HashSimulationStorage());

        ExternalParameters externalParameters = new ExternalParameters(project);
        SimulationInput input = new SimulationInput(externalParameters);
        input.put("x5", 1.0);
        input.put("x6", 2.0);
        input.put("x7", 3.0);
        input.put("x8", 4.0);
        input.put("x9", 5.0);
        Future<SimulationOutput> job = runner.start(input);

        SimulationOutput output = job.get();
        String messages = output.getMessages();
        if (!messages.isEmpty()) {
            System.out.print(messages);
        }

        if (output instanceof SimulationResults) {
            SimulationResults results = (SimulationResults) output;
            MetricValues mv = new MetricValues(results, Arrays.asList(metrics));
            ConstraintStatus cs = new ConstraintStatus(mv,
                    Arrays.asList(constraints));
            ObjectiveStatus os = new ObjectiveStatus(mv,
                    Arrays.asList(objectives));

            for (String outputName : project.outputs.keySet()) {
                System.out.println(outputName + " = "
                        + results.getTS(outputName).values[0]);
            }

            for (int i = 0; i < constraints.length; ++i) {
                System.out.println("Constraint "
                        + constraints[i].getConstraintId() + ": "
                        + cs.infeasibilities[i]);
            }
            System.out.println("Feasible: " + cs.feasible);

            for (int i = 0; i < metrics.length; ++i) {
                System.out.println("Metric " + metrics[i].getMetricName()
                        + ": " + mv.metricValues[i]);
            }

            for (int i = 0; i < objectives.length; ++i) {
                System.out.println("Objective "
                        + objectives[i].getObjectiveId() + ": "
                        + os.objectiveValues[i]);
            }
        } else {
            System.out.println("Simulation failed.");
        }
    }
}
