package eu.cityopt.sim.eval;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.script.CompiledScript;
import javax.script.ScriptException;

import org.junit.Before;
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
        ns.metrics.put("m3", Type.DOUBLE);
    }
    
    ExternalParameters externalParameters;
    SimulationInput input;
    
    @Before
    public void setupInput() {
        externalParameters = new ExternalParameters(ns);
        input = new SimulationInput(externalParameters);
        input.put("C1", "x5", 1.0);
        input.put("C1", "x6", 2.0);
        input.put("C1", "x7", 3.0);
        input.put("C1", "x8", 4.0);
        input.put("C2", "x9", 5.0);
    }
    
    @Test
    public void testDir() throws Exception {
        CompiledScript script = evaluator.getCompiler().compile("dir(C1)");
        @SuppressWarnings("unchecked")
        ArrayList<String> names = new ArrayList<String>(
                (List<String>)script.eval(input.toBindings()));
        Collections.sort(names);
        String[] good = {"x5", "x6", "x7", "x8"};
        assertArrayEquals(good, names.toArray());
    }

    @Test
    public void evaluate() throws Exception {
        ConstraintExpression[] constraints = {
                new ConstraintExpression(1,
                        "C2.x9 * (C1.x5 - C1.x6) + 0.02 * C1.x6 - 0.025 * C1.x5",
                        Double.NEGATIVE_INFINITY, 0.0, evaluator),
                new ConstraintExpression(2,
                        "C2.x9 * (C1.x8 - C1.x7) + 0.02 * C1.x7 - 0.015 * C1.x8",
                        Double.NEGATIVE_INFINITY, 0.0, evaluator) };
        MetricExpression[] metrics = {
                new MetricExpression(1, "m1", "-9 * C1.x5 - 15 * C1.x8", evaluator),
                new MetricExpression(2, "m2", "10 * (C1.x6 + C1.x7)", evaluator),
                new MetricExpression(3, "m3", "6 * C1.x1.values[0] + 16 * C1.x2.mean", evaluator)
        };
        ObjectiveExpression[] objectives = { new ObjectiveExpression(
                1, "m1 + m2", false, evaluator) };

        SimulationRunner actualRunner = new SimulationRunner() {
            @Override
            public Future<SimulationOutput> start(SimulationInput input) {
                return new SimJob(input);
            }
        };
        SimulationRunnerWithStorage runner = new SimulationRunnerWithStorage(
                actualRunner, new HashSimulationStorage());

        Future<SimulationOutput> job = runner.start(input);

        SimulationOutput output = job.get();
        String messages = output.getMessages();
        if (!messages.isEmpty()) {
            System.out.print(messages);
        }

        assertTrue(output instanceof SimulationResults);
        SimulationResults results = (SimulationResults)output;
        MetricValues mv = new MetricValues(results, Arrays.asList(metrics));
        ConstraintStatus cs = new ConstraintStatus(mv,
                Arrays.asList(constraints));
        ObjectiveStatus os = new ObjectiveStatus(mv,
                Arrays.asList(objectives));

        for (Map.Entry<String, Namespace.Component> entry : ns.components.entrySet()) {
            String componentName = entry.getKey().toString();
            Namespace.Component component = entry.getValue();
            for (String outputName : component.outputs.keySet()) {
                System.out.println(
                        componentName + "." + outputName + " = "
                        + results.getTS(entry.getKey(), outputName).getValues()[0]);
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
        assertArrayEquals(new double[] { 0.0, 5.0 }, cs.infeasibilities, delta);
        assertArrayEquals(new double[] { -69.0, 50.0, 0.0 }, mv.metricValues, delta);
        assertArrayEquals(new double[] { -19.0 }, os.objectiveValues, delta);
    }

    @Test(expected=ScriptException.class)
    public void accessNonexistentComponentMember() throws Exception {
        ConstraintExpression invalidConstraint = new ConstraintExpression(1,
                "C2.x9 * (C1.x5 - C1.x9) + 0.02 * C1.x6 - 0.025 * C1.x5",
                Double.NEGATIVE_INFINITY, 0.0, evaluator);

        double value = invalidConstraint.evaluate(input);
        System.out.println("infeasibility = " + value);
    }

    @Test
    public void timeSeriesAccess() throws ScriptException, InvalidValueException {
        ZonedDateTime zdt = ZonedDateTime.of(2014, 1, 1,  12, 0, 0,  0, ZoneId.systemDefault());
        long t0 = zdt.toInstant().toEpochMilli();
        long sec = 1000;
        long day = 24 * 60 * 60 * sec;
        ExternalParameters ep = new ExternalParameters(ns);

        long[] ta = new long[] { t0, t0 + day, t0 + day + sec };
        double[] va = new double[] { 1.0, 2.0, 5.0 };
        ep.put("a", evaluator.makeTimeSeries(ta, va));

        long[] tb = new long[] { t0, t0 + sec, t0 + day };
        double[] vb = new double[] { -4.0, 3.0, -2.0 };
        ep.put("b", evaluator.makeTimeSeries(tb, vb));

        double delta = 1.0e-12;
        assertEquals(2014, eval("a.datetimes[0].year", ep), delta);
        assertEquals(2, eval("a.datetimes[1].day", ep), delta);
        assertEquals(1, eval("a.datetimes[2].second", ep), delta);
        double f = (ta[2]-ta[1])/(double)(ta[2]-ta[0]);
        assertEquals((1-f) * 1.5 + f * 3.5, eval("a.mean", ep), delta);
        assertEquals(0.2887686695576, eval("a.stdev", ep), delta);

        for (int i = 0; i < vb.length; ++i) {
            assertEquals(vb[i], eval("b.values["+i+"]", ep), delta);
            assertEquals(vb[i]+3, eval("(b+3).values["+i+"]", ep), delta);
            assertEquals(3+vb[i], eval("(3+b).values["+i+"]", ep), delta);
            assertEquals(vb[i]-3, eval("(b-3).values["+i+"]", ep), delta);
            assertEquals(3-vb[i], eval("(3-b).values["+i+"]", ep), delta);
            assertEquals(vb[i]*3, eval("(b*3).values["+i+"]", ep), delta);
            assertEquals(3*vb[i], eval("(3*b).values["+i+"]", ep), delta);
            assertEquals(Math.abs(vb[i]),
                         eval("abs(b).values["+i+"]", ep), delta);
            assertEquals(-vb[i], eval("(-b).values["+i+"]", ep), delta);
            assertEquals(+vb[i], eval("(+b).values["+i+"]", ep), delta);
        }
        assertEquals(va[0]+vb[0], eval("(a+b).values[0]", ep), delta);
        // N.B. the following fails for now, because addition is not properly implemented yet
        f = (tb[1]-tb[0])/(double)(ta[1]-ta[0]);
        assertEquals(vb[1] + (1-f)*va[0] + f*va[1],
                     eval("(a+b).values[1]", ep), delta);
    }

    private double eval(String expression, EvaluationContext context)
            throws ScriptException, InvalidValueException {
        return new DoubleExpression(expression, evaluator).evaluate(context);
    }
}
