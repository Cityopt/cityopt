package eu.cityopt.sim.eval;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Future;

import javax.script.ScriptException;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestEval {
    static Evaluator evaluator;
    static Namespace ns;

    @BeforeClass
    public static void setup() throws Exception {
        evaluator = new Evaluator();
        ns = new Namespace(evaluator, Arrays.asList(new String[] { "C1", "C2" }));
        ns.externals.put("a", Type.TIMESERIES);
        ns.externals.put("b", Type.TIMESERIES);
        ns.components.get("C1").inputs.put("x5", Type.DOUBLE);
        ns.components.get("C1").inputs.put("x6", Type.DOUBLE);
        ns.components.get("C1").inputs.put("x7", Type.DOUBLE);
        ns.components.get("C1").inputs.put("x8", Type.DOUBLE);
        ns.components.get("C2").inputs.put("x9", Type.DOUBLE);
        ns.components.get("C1").outputs.put("x1", Type.TIMESERIES);
        ns.components.get("C1").outputs.put("x2", Type.TIMESERIES);
        ns.components.get("C2").outputs.put("x3", Type.TIMESERIES);
        ns.components.get("C2").outputs.put("x4", Type.TIMESERIES);
        ns.metrics.put("m1", Type.DOUBLE);
        ns.metrics.put("m2", Type.DOUBLE);
    }

    @Test
    public void evaluate() throws Exception {
        ConstraintExpression[] constraints = new ConstraintExpression[] {
                new ConstraintExpression(1,
                        "C2.x9 * (C1.x5 - C1.x6) + 0.02 * C1.x6 - 0.025 * C1.x5",
                        Double.NEGATIVE_INFINITY, 0.0, evaluator),
                new ConstraintExpression(2,
                        "C2.x9 * (C1.x8 - C1.x7) + 0.02 * C1.x7 - 0.015 * C1.x8",
                        Double.NEGATIVE_INFINITY, 0.0, evaluator) };
        MetricExpression[] metrics = new MetricExpression[] {
                new MetricExpression(1, "m1", "-9 * C1.x5 - 15 * C1.x8", evaluator),
                new MetricExpression(2, "m2", "10 * (C1.x6 + C1.x7)", evaluator)
                // TODO implement script access to TimeSeries
                // new MetricExpression(
                //      3, "m3", "6 * C1.x1[0] + 16 * mean(C1.x2)", evaluator)
        };
        ObjectiveExpression[] objectives = new ObjectiveExpression[] { new ObjectiveExpression(
                1, "m1 + m2", false, evaluator) };

        SimulationRunner actualRunner = new SimulationRunner() {
            @Override
            public Future<SimulationOutput> start(SimulationInput input) {
                return new SimJob(input);
            }
        };
        SimulationRunnerWithStorage runner = new SimulationRunnerWithStorage(
                actualRunner, new HashSimulationStorage());

        ExternalParameters externalParameters = new ExternalParameters(ns);
        SimulationInput input = new SimulationInput(externalParameters);
        input.put("C1", "x5", 1.0);
        input.put("C1", "x6", 2.0);
        input.put("C1", "x7", 3.0);
        input.put("C1", "x8", 4.0);
        input.put("C2", "x9", 5.0);
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

            for (Map.Entry<String, Namespace.Component> entry : ns.components.entrySet()) {
                String componentName = entry.getKey().toString();
                Namespace.Component component = entry.getValue();
                for (String outputName : component.outputs.keySet()) {
                    System.out.println(componentName + "." + outputName + " = "
                            + results.getTS(entry.getKey(), outputName).values[0]);
                }
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

            final double delta = 1.0e-12;
            assertArrayEquals(cs.infeasibilities, new double[] { 0.0, 5.0 }, delta);
            assertArrayEquals(mv.metricValues, new double[] { -69.0, 50.0 }, delta);
            assertArrayEquals(os.objectiveValues, new double[] { -19.0 }, delta);
        } else {
            System.out.println("Simulation failed.");
            assertTrue(false);
        }
    }

    @Test(expected=ScriptException.class)
    public void accessNonexistentComponentMember() throws Exception {
        ExternalParameters externalParameters = new ExternalParameters(ns);
        SimulationInput input = new SimulationInput(externalParameters);
        input.put("C1", "x5", 1.0);
        input.put("C1", "x6", 2.0);
        input.put("C1", "x7", 3.0);
        input.put("C1", "x8", 4.0);
        input.put("C2", "x9", 5.0);

        ConstraintExpression invalidConstraint = new ConstraintExpression(1,
                "C2.x9 * (C1.x5 - C1.x9) + 0.02 * C1.x6 - 0.025 * C1.x5",
                Double.NEGATIVE_INFINITY, 0.0, evaluator);

        double value = invalidConstraint.evaluate(input);
        System.out.println("infeasibility = " + value);
    }
}
