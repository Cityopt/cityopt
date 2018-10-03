package eu.cityopt.opt.io;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.*;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

import eu.cityopt.opt.ga.TimeSeriesLoader;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.TimeSeriesData;
import eu.cityopt.sim.eval.TimeSeriesI;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.SimulationStructure;

public class ImportTest extends TestBase {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public static class TsTestModule extends TestModule {
        Evaluator evaluator = new Evaluator();

        @Override
        protected void configure() {
            super.configure();
            bind(Evaluator.class).toInstance(evaluator);
            bind(TimeSeriesData.class).toProvider(TimeSeriesLoader.class);
            bind(SimulationModel.class).toProvider(Providers.of(null));
        }
    }

    @Test
    public void testIsMaximize() {
        String[] cases = {"min", "Min", "MIN", "max", "Max", "MAX", "bork"};
        Boolean[] exptd = {false, false, false, true, true, true, null};
        List<Boolean> results = Lists.transform(
                Arrays.asList(cases), JacksonBinder.Obj::isMaximize);
        assertArrayEquals(exptd, results.toArray());
    }

    @Test
    public void testNameClash() throws Exception {
        TestModule tm = new TestModule();
        ObjectMapper json = new ObjectMapper();
        json.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        String row
            = "{'kind': 'obj', 'name': 'foo', 'type': 'min',"
            + " 'expression': '1'}";
        JacksonBinder binder = json.readValue(
                String.format("[%s, %s]", row, row), JacksonBinder.class);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("duplicate");
        binder.makeNamespace(new Evaluator(), tm.t0);
    }

    @Test
    public void testNoClash() throws Exception {
        TestModule tm = new TestModule();
        ObjectMapper json = new ObjectMapper();
        json.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        String rowf
            = "{'kind': 'in', 'component': '%s', 'name': 'var',"
            + " 'type': 'Double', 'value': '1'}";
        String rowsf = String.format("[%s, %s]", rowf, rowf);
        JacksonBinder binder = json.readValue(
                String.format(rowsf, "c1", "c2"), JacksonBinder.class);
        binder.makeNamespace(new Evaluator(), tm.t0);
    }

    @Test
    public void testReadProblem() throws Exception {
        TsTestModule tm = new TsTestModule();
        JacksonCsvModule jm = new JacksonCsvModule();
        Injector csv_inj = Guice.createInjector(jm, tm);
        JacksonBinder binder = csv_inj.getInstance(JacksonBinder.class);
        TimeSeriesData tsdata = csv_inj.getInstance(TimeSeriesData.class);
        ObjectMapper json = new ObjectMapper();
        json.enable(SerializationFeature.INDENT_OUTPUT);
        json.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        json.writeValue(System.out, binder);
        ObjectWriter wtr = csv_inj.getInstance(
                Key.get(ObjectWriter.class, Names.named("problem")));
        wtr.without(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
                .writeValue(System.out, binder);
        Namespace ns = binder.makeNamespace(tm.evaluator, tm.t0);
        OptimisationProblem p = binder.buildWith(
                new ProblemBuilder(null, ns, tsdata)).getResult();
        checkProblem(p);
        assertNull(p.model);
    }

    @Test
    public void testReadSimulationProject() throws Exception {
        EvaluationSetup setup = new EvaluationSetup(
                new Evaluator(), Instant.EPOCH);
        SimulationStructure s = OptimisationProblemIO.readStructureCsv(
                res.getDir().resolve("test-project.csv"), setup, null);
        assertNull(s.model);
        assertEquals(8, s.namespace.components.size());

        Namespace.Component c = s.namespace.components.get("SAMPLE_DISTRICT");
        assertEquals(9, c.inputs.size());
        assertEquals(0, c.outputs.size());
        assertEquals(Type.DOUBLE, c.inputs.get("CHP_thermal_power_rating"));

        c = s.namespace.components.get("HEATING_LOAD");
        assertEquals(0, c.inputs.size());
        assertEquals(1, c.outputs.size());
        assertEquals(Type.TIMESERIES_LINEAR, c.outputs.get("MULTIPLYER_OUTPUT"));

        assertEquals(2, s.namespace.metrics.size());
        assertEquals(2, s.metrics.size());
        assertEquals(Type.DOUBLE, s.namespace.metrics.get("fuelconsumption"));
    }

    @Test
    public void readTsVars() throws Exception {
        TsTestModule tm = new TsTestModule();
        JacksonCsvModule jm = new JacksonCsvModule();
        Injector csv_inj = Guice.createInjector(jm, tm);
        ObjectMapper json = new ObjectMapper();
        json.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        String vars
            = "[{'kind': 'in', 'component': 'c', 'name': 'i',"
            + "  'type': 'TimeSeries/linear', 'value': 'dummy'},"
            + " {'kind': 'ext', 'name': 'x',"
            + "  'type': 'TimeSeries/step', 'value': 'dummy'}]";
        JacksonBinder binder = json.readValue(vars, JacksonBinder.class);
        TimeSeriesData tsd = csv_inj.getInstance(TimeSeriesData.class);
        Namespace ns = binder.buildWith(
                csv_inj.getInstance(NamespaceBuilder.class))
                .getResult();
        OptimisationProblem p = binder.buildWith(
                new ProblemBuilder(null, ns, tsd)).getResult();
        TimeSeriesI
            tsi = p.inputConst.getTS("c", "i"),
            tsx = p.getExternalParameters().getTS("x");
        assertNotNull(tsi);
        assertEquals(1, tsi.getDegree());
        assertNotNull(tsx);
        assertEquals(0, tsx.getDegree());
        assertArrayEquals(tsi.getTimes(), tsx.getTimes(), 1e-6);
        assertArrayEquals(tsi.getValues(), tsx.getValues(), 1e-6);
    }

    @Test
    public void readScenarioCSVwoGu() throws Exception {
    	ObjectReader reader = JacksonCsvModule.getScenarioProblemReader(JacksonCsvModule.getCsvMapper());
    	JacksonBinderScenario binder;
    	try (InputStream fis = res.getStream("scenario_file")) {
    	    binder = new JacksonBinderScenario(reader, fis);
    	}
    	binder.getItems().forEach( i -> System.out.println(i.scenarioname));
    }

    @Test
    public void readScenarioCsv() throws JsonProcessingException, IOException {
    	TsTestModule tm = new TsTestModule();
        JacksonCsvModule jm = new JacksonCsvModule();
        Injector csv_inj = Guice.createInjector(jm, tm);
        JacksonBinderScenario binder = csv_inj.getInstance(JacksonBinderScenario.class);
        ObjectMapper json = new ObjectMapper();
        json.enable(SerializationFeature.INDENT_OUTPUT);
        json.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
        json.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        json.writeValue(System.out, binder);
        ObjectWriter wtr = csv_inj.getInstance(
                Key.get(ObjectWriter.class, Names.named("scenario")));
        wtr.without(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
                .writeValue(System.out, binder);

        binder.getItems().forEach( i -> System.out.println(i.scenarioname));
    }

    @Test
    public void testReadCsvFacade() throws Exception {
        TestModule tm = new TestModule();
        CsvTimeSeriesData tsd = new CsvTimeSeriesData(
                new EvaluationSetup(new Evaluator(), tm.t0));
        for (Path tsfile : tm.tsfiles) {
            tsd.read(tsfile);
        }
        OptimisationProblem p = OptimisationProblemIO.readProblemCsv(tm.pfile, tsd, null);
        checkProblem(p);
        assertNull(p.model);
        TimeSeriesI ts = p.getExternalParameters().getTS("fuel_cost");
        assertEquals(3, ts.getValues().length);
    }

    @Test
    public void testReadTimeSeries() throws Exception {
        Evaluator evaluator = new Evaluator();
        Instant t0 = Instant.parse("2050-01-01T00:00:00Z");
        EvaluationSetup setup = new EvaluationSetup(evaluator, t0);
        CsvTimeSeriesData tsd = new CsvTimeSeriesData(setup);
        String name = "/timeseries.csv";
        try (InputStream is = getClass().getResourceAsStream(name)) {
            tsd.read(is, name);
        }
        TimeSeriesData.Series sd = tsd.getSeries("fuel_cost");
        System.out.println("times = " + Arrays.toString(sd.times));
        System.out.println("fuel_cost = " + Arrays.toString(sd.values));
        final double delta = 1e-12;
        assertArrayEquals(new double[] { -t0.toEpochMilli()/1000, -1.0, 0.0 },
                sd.times, delta);
        assertArrayEquals(new double[] { 10.0, 10.0, 10.0 }, sd.values, delta);
        Injector inj = Guice.createInjector(new JacksonCsvModule());
        CsvTimeSeriesWriter
                wtr = inj.getInstance(CsvTimeSeriesWriter.class);
        //wtr.setNumeric(true);
        wtr.write(System.out, tsd);
    }
}
