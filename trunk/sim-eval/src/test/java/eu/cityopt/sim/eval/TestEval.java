package eu.cityopt.sim.eval;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
        ns = new Namespace(evaluator, Instant.ofEpochMilli(1),
                Arrays.asList(new String[] { "C1", "C2" }), true);
        ns.externals.put("a", Type.TIMESERIES_LINEAR);
        ns.externals.put("b", Type.TIMESERIES_LINEAR);
        ns.externals.put("li", Type.LIST_OF_INTEGER);
        ns.externals.put("ld", Type.LIST_OF_DOUBLE);
        ns.components.get("C1").inputs.put("x5", Type.DOUBLE);
        ns.components.get("C1").inputs.put("x6", Type.DOUBLE);
        ns.components.get("C1").inputs.put("x7", Type.DOUBLE);
        ns.components.get("C1").inputs.put("x8", Type.DOUBLE);
        ns.components.get("C2").inputs.put("x9", Type.DOUBLE);
        ns.components.get("C1").outputs.put("x1", Type.TIMESERIES_LINEAR);
        ns.components.get("C1").outputs.put("x2", Type.TIMESERIES_LINEAR);
        ns.components.get("C2").outputs.put("x3", Type.TIMESERIES_LINEAR);
        ns.components.get("C2").outputs.put("x4", Type.TIMESERIES_LINEAR);
        ns.metrics.put("m1", Type.DOUBLE);
        ns.metrics.put("m2", Type.DOUBLE);
        ns.metrics.put("m3", Type.DOUBLE);
        ns.metrics.put("m4", Type.TIMESERIES_LINEAR);

        ns.decisions.put("d", Type.DOUBLE);
        ns.decisions.put("i", Type.INTEGER);
        ns.components.get("C1").decisions.put("x8", Type.DOUBLE);
        ns.components.get("C2").decisions.put("x9", Type.DOUBLE);
        ns.components.get("C2").decisions.put("d", Type.DOUBLE);
    }

    ExternalParameters externalParameters;
    SimulationInput basicInput;
    DecisionValues decisionValues;
    SimulationInput derivedInput;

    @Before
    public void setupInput() throws ScriptException {
        double[] ta = new double[] { 0.0 };
        double[] va = new double[] { -1.0 };
        externalParameters = new ExternalParameters(ns);
        externalParameters.put("a", evaluator.makeTS(Type.TIMESERIES_LINEAR, ta, va));
        externalParameters.put("b", evaluator.makeTS(Type.TIMESERIES_LINEAR, ta, va));
        externalParameters.put("li", Arrays.asList(1, 2, 3));
        externalParameters.put("ld", Arrays.asList(4.0, 5.0, 6.0));
        basicInput = new SimulationInput(externalParameters);
        basicInput.put("C1", "x5", 1.0);
        basicInput.put("C1", "x6", 2.0);
        basicInput.put("C1", "x7", 3.0);
        basicInput.put("C1", "x8", 4.0);
        basicInput.put("C2", "x9", 5.0);

        decisionValues = new DecisionValues(externalParameters);
        decisionValues.put(null, "i", 1);
        decisionValues.put(null, "d", 2.0);
        decisionValues.put("C1",  "x8",  4.0);
        decisionValues.put("C2", "x9", 5.0);

        InputExpression[] inputExpressions = {
                new InputExpression("C1", "x5", "-a.values[0]", evaluator),
                new InputExpression("C1", "x6", "d", evaluator),
                new InputExpression("C1", "x7", "i+d", evaluator),
                new InputExpression("C1", "x8", "C1.x8", evaluator),
                new InputExpression("C2", "x9", "C2.x9", evaluator)
        };
        derivedInput = new SimulationInput(decisionValues, Arrays.asList(inputExpressions));
    }

    @Test
    public void derivedInput() throws Exception {
        assertEquals(1.0, derivedInput.get("C1", "x5"));
        assertEquals(2.0, derivedInput.get("C1", "x6"));
        assertEquals(3.0, derivedInput.get("C1", "x7"));
        assertEquals(4.0, derivedInput.get("C1", "x8"));
        assertEquals(5.0, derivedInput.get("C2", "x9"));
        assertEquals(basicInput, derivedInput);
        assertEquals(basicInput.hashCode(), derivedInput.hashCode());
    }

    @Test
    public void evaluateMockSimulation_NoDecisionVariables() throws Exception {
        evaluateMockSimulation(basicInput, false);
    }

    @Test
    public void evaluateMockSimulation_WithDecisionVariables() throws Exception {
        evaluateMockSimulation(derivedInput, true);
    }

    public void evaluateMockSimulation(
            SimulationInput input, boolean withDecisions) throws Exception {
        System.out.println("--- withDecisions=" + withDecisions + " ---");
        final double delta = 1.0e-12;
        Constraint[] constraints = {
                new Constraint(1,
                        "C2.x9 * (C1.x5 - C1.x6) + 0.02 * C1.x6 - 0.025 * C1.x5",
                        Double.NEGATIVE_INFINITY, 0.0, evaluator),
                new Constraint(2,
                        "C2.x9 * (C1.x8 - C1.x7) + 0.02 * C1.x7 - 0.015 * C1.x8",
                        Double.NEGATIVE_INFINITY, 0.0, evaluator),
                new Constraint(3,
                        "C2.x4", -10000, 10000, evaluator) };
        MetricExpression[] metrics = {
                new MetricExpression(1, "m1", "-9 * C1.x5 - 15 * C1.x8", evaluator),
                new MetricExpression(2, "m2", "10 * (C1.x6 + C1.x7)", evaluator),
                new MetricExpression(3, "m3", "6 * C1.x1.values[0] + 16 * mean(C1.x2)", evaluator),
                new MetricExpression(4, "m4", "C2.x3 + C1.x1", evaluator)
        };
        ObjectiveExpression[] objectives = { new ObjectiveExpression(
                1, "m1 + m2", false, evaluator) };

        ConstraintContext precc = null;
        if (withDecisions) {
            precc = new ConstraintContext(decisionValues, derivedInput);
            assertEquals(6.0, eval("C1.x5 + C1.x8 + i", precc), delta);
            assertEquals(12.0, eval("C1.x6 * d * C1.x7", precc), delta);
        }

        SimulationRunner actualRunner = new SimulationRunner() {
            @Override
            public CompletableFuture<SimulationOutput> start(SimulationInput input) {
                return CompletableFuture.completedFuture(computeJob(input));
            }

            @Override
            public void close() throws IOException {}
        };
        SimulationOutput output;
        Executor executor = Executors.newSingleThreadExecutor();
        try (SimulationRunner runner = new SimulationRunnerWithStorage(
                actualRunner, new HashSimulationStorage(), executor)) {

            Future<SimulationOutput> job = runner.start(input);
            output = job.get();
        }
        String messages = output.getMessages();
        if (!messages.isEmpty()) {
            System.out.print(messages);
        }

        assertTrue(output instanceof SimulationResults);
        SimulationResults results = (SimulationResults)output;
        MetricValues mv = new MetricValues(results, Arrays.asList(metrics));

        ConstraintStatus cs = new ConstraintStatus(mv, Arrays.asList(constraints));
        if (withDecisions) {
            ConstraintContext postcc = new ConstraintContext(precc, mv);
            ConstraintStatus cs2 = new ConstraintStatus(
                    postcc, Arrays.asList(constraints), false);
            assertArrayEquals(new double[] { 0.0, 5.0, 0.0 }, cs2.infeasibilities, delta);
        }

        ObjectiveStatus os = new ObjectiveStatus(mv, Arrays.asList(objectives));

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

        String[] metricValues = new String[metrics.length];
        for (int i = 0; i < metrics.length; ++i) {
            String n = metrics[i].getMetricName();
            metricValues[i] = mv.get(n).toString();
            System.out.println("Metric " + n + ": " + metricValues[i]);
        }

        for (int i = 0; i < objectives.length; ++i) {
            System.out.println("Objective "
                    + objectives[i].getObjectiveId() + ": "
                    + os.objectiveValues[i]);
        }

        assertArrayEquals(new double[] { 0.0, 5.0, 0.0 }, cs.infeasibilities, delta);
        assertArrayEquals(new String[] { "-69.0", "50.0", "0.0", "TimeSeries(1, [0.0], [-1.0])" },
                metricValues);
        assertArrayEquals(new double[] { -19.0 }, os.objectiveValues, delta);
    }

    SimulationOutput computeJob(SimulationInput input) {
        SimulationResults out = new SimulationResults(input, "");
        double x5 = (Double) input.get("C1", "x5");
        double x6 = (Double) input.get("C1", "x6");
        double x7 = (Double) input.get("C1", "x7");
        double x8 = (Double) input.get("C1", "x8");
        double x9 = (Double) input.get("C2", "x9");
        double x3 = x5 - x6;
        double x4 = x8 - x7;
        out.put("C1", "x1", ts((50 * x9 - 0.5) * (x3 + x4)));
        out.put("C1", "x2", ts((-50 * x9 + 1.5) * (x3 + x4)));
        out.put("C2", "x3", ts(x3));
        out.put("C2", "x4", ts(x4));
        return out;
    }

    private TimeSeriesI ts(double value) {
        return evaluator.makeTS( 
                Type.TIMESERIES_LINEAR, new double[] { 0 }, new double[] { value });
    }

    @Test(expected=ScriptException.class)
    public void accessNonexistentComponentMember() throws Exception {
        Constraint invalidConstraint = new Constraint(1,
                "C2.x9 * (C1.x5 - C1.x9) + 0.02 * C1.x6 - 0.025 * C1.x5",
                Double.NEGATIVE_INFINITY, 0.0, evaluator);

        double value = invalidConstraint.infeasibility(basicInput);
        System.out.println("infeasibility = " + value);
    }

    @Test
    public void testDir() throws Exception {
        CompiledScript script = evaluator.getCompiler().compile("dir(C1)");
        @SuppressWarnings("unchecked")
        ArrayList<String> names = new ArrayList<String>(
                (List<String>)evaluator.eval(
                        script, basicInput.toBindings(), basicInput.getEvaluationSetup()));
        Collections.sort(names);
        String[] good = {"x5", "x6", "x7", "x8"};
        assertArrayEquals(good, names.toArray());
    }

    @Test
    public void listAccess() throws Exception {
        assertEquals(1, eval("li[0]", basicInput), 0.0);
        assertEquals(2, eval("li[1]", basicInput), 0.0);
        assertEquals(3, eval("li[2]", basicInput), 0.0);
        assertEquals(3, eval("len(li)", basicInput), 0.0);

        assertEquals(4, eval("ld[0]", basicInput), 0.0);
        assertEquals(5, eval("ld[1]", basicInput), 0.0);
        assertEquals(6, eval("ld[2]", basicInput), 0.0);
        assertEquals(3, eval("len(ld)", basicInput), 0.0);
    }

    /** Test our global Python functions with non-timeseries data. */
    @Test
    public void globalPythonFunctions() throws Exception {
        assertEquals(3, eval("mean([2, 4])", basicInput), 0.0);
        assertEquals(2, eval("var([2, 4])", basicInput), 0.0);
        assertEquals(Math.sqrt(2.0), eval("stdev([2, 4])", basicInput), 0.0);
        assertEquals(2, eval("min([2, 4])", basicInput), 0.0);
        assertEquals(4, eval("max([2, 4])", basicInput), 0.0);

        assertEquals(1, eval("min(2, 4, 1, 3)", basicInput), 0.0);
        assertEquals(4, eval("max(2, 4, 1, 3)", basicInput), 0.0);

        assertEquals(1, eval("min([2], [4], [1], [3], key=lambda a: a[0])[0]", basicInput), 0.0);
        assertEquals(4, eval("max([2], [4], [1], [3], key=lambda a: a[0])[0]", basicInput), 0.0);
    }

    private double eval(String expression, EvaluationContext context) throws ScriptException {
        return new Expression(expression, evaluator).evaluateDouble(context);
    }

    @Test
    public void identifierValidation() throws ScriptException {
        SyntaxChecker sc = new SyntaxChecker(evaluator);
        assertTrue(sc.isValidTopLevelName("_Arina_9"));
        assertFalse(sc.isValidTopLevelName("Örinä"));
        assertFalse(sc.isValidTopLevelName("min"));
        assertFalse(sc.isValidTopLevelName("class"));

        assertTrue(sc.isValidAttributeName("_Arina_9"));
        assertFalse(sc.isValidAttributeName("Örinä"));
        assertTrue(sc.isValidAttributeName("min"));
        assertFalse(sc.isValidAttributeName("class"));

        assertEquals("Orina", sc.normalizeTopLevelName("Örinä"));
        assertEquals("as_", sc.normalizeTopLevelName("as"));
        assertEquals("min_", sc.normalizeTopLevelName("min"));
        assertEquals("min_", sc.normalizeTopLevelName("min_"));
        assertEquals("pow_", sc.normalizeTopLevelName("pow"));
        assertEquals("pow_", sc.normalizeTopLevelName("pow_"));

        assertEquals("_Orina_", sc.normalizeAttributeName("_Örinä'"));
        assertEquals("as_", sc.normalizeAttributeName("as"));
        assertEquals("min", sc.normalizeAttributeName("min"));
        assertEquals("min_", sc.normalizeAttributeName("min_"));
    }

    @Test
    public void expressionValidation_free() throws ScriptException {
        testExpressionValidation(new SyntaxChecker(evaluator, null, false), false);
    }

    @Test
    public void expressionValidation_complete() throws ScriptException {
        testExpressionValidation(new SyntaxChecker(evaluator, ns, true), true);
    }

    public void testExpressionValidation(SyntaxChecker sc, boolean complete)
            throws ScriptException {
        assertNull(msg(sc.checkConstraintExpression("1+1")));
        assertNotNull(msg(sc.checkConstraintExpression("")));
        assertNotNull(msg(sc.checkConstraintExpression("C1.x5.mean.xxx.yyy")));
        assertNull(msg(sc.checkConstraintExpression("C1.x1.mean")));
        assertNull(msg(sc.checkConstraintExpression("C1.x5")));
        assertNull(msg(sc.checkConstraintExpression("a")));
        assertNotNull(msg(sc.checkConstraintExpression("C1.x5.mean.at(1)")));
        assertNull(msg(sc.checkConstraintExpression("C1.x1.at(1)")));
        assertNull(msg(sc.checkConstraintExpression("a.at(1)")));
        assertNotNull(msg(sc.checkConstraintExpression("b(1)")));
        assertNull(msg(sc.checkConstraintExpression("min(1)")));
        assertNull(msg(sc.checkConstraintExpression("m1")));
        assertNull(msg(sc.checkExternalExpression("2**a")));
        if (complete) {
            assertNotNull(msg(sc.checkExternalExpression("m1")));
            assertNotNull(msg(sc.checkConstraintExpression("m1.mean")));
            assertNotNull(msg(sc.checkMetricExpression("m1")));
            assertNotNull(msg(sc.checkMetricExpression("m1.mean")));
            assertNotNull(msg(sc.checkMetricExpression("C1.x9")));
            assertNotNull(msg(sc.checkMetricExpression("C1.x9.mean")));
            assertNull(msg(sc.checkMetricExpression("C1.x1")));
            assertNull(msg(sc.checkMetricExpression("C1.x1.mean")));
            assertNotNull(msg(sc.checkPreConstraintExpression("C1.x1")));
            assertNotNull(msg(sc.checkPreConstraintExpression("C1.x1.mean")));
            assertNotNull(msg(sc.checkInputExpression("C1.x7")));
            assertNotNull(msg(sc.checkInputExpression("C2.x8")));
        }
        assertNull(msg(sc.checkInputExpression("C1.x8")));
        assertNull(msg(sc.checkInputExpression("C2.x9")));
        assertNull(msg(sc.checkInputExpression("i+d")));
        assertNull(msg(sc.checkPreConstraintExpression("C1.x5")));
        assertNull(msg(sc.checkPreConstraintExpression("a")));
    }

    public String msg(SyntaxChecker.Error error) {
        if (error != null) {
            assertEquals(0, error.line);
            return error.message;
        } else {
            return null;
        }
    }
}
