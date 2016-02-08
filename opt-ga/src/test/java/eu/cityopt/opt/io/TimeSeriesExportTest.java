package eu.cityopt.opt.io;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.MergedTimeSeries;
import eu.cityopt.sim.eval.TimeSeriesData;

public class TimeSeriesExportTest {
    private TimeSeriesData.Series ts1 = new TimeSeriesData.Series();
    private TimeSeriesData.Series ts2 = new TimeSeriesData.Series();
    //Numeric comparison tolerance.
    private final double delta = 1e-16;
    private EvaluationSetup evsup = new EvaluationSetup(
            new Evaluator(), Instant.parse("2015-01-01T00:00:00Z"));
    private TimeSeriesData tsd = new TimeSeriesData(evsup);
    private String[] names = {"ts1", "ts2"};
    private final Injector inj;
    
    public TimeSeriesExportTest() {
        inj = Guice.createInjector(new JacksonCsvModule());
    }
    
    @Before
    public void setupTimeSeriesData() {
        ts1.times = new double[] {0, 1};
        ts1.values = ts1.times;
        ts2.times = new double[] {0, 2};
        ts2.values = new double[] {2, 3};
        tsd.getMap().put(names[0], ts1);
        tsd.getMap().put(names[1], ts2);
    }

    @Test
    public void testMergedTimeSeries() {
        ts1.times = new double[] {0, 1, 2, 3};
        ts1.values = ts1.times;
        ts2.times = new double[] {0.2, 0.8, 1.5, 2.5};
        ts2.values = new double[] {4, 5, 6, 7};
        MergedTimeSeries merge = new MergedTimeSeries(tsd);
        assertArrayEquals(names, merge.getNames().toArray());
        assertArrayEquals(
                new int[] {0, 1, 1, 0, 1, 0, 1, 0},
                merge.stream().mapToInt(e -> e.getSeries()).toArray());
        assertArrayEquals(
                new double[] {0,  .2, .8, 1, 1.5, 2, 2.5, 3},
                merge.stream().mapToDouble(e -> e.getTime()).toArray(),
                delta);
        assertArrayEquals(
                new double[] {0, 4, 5, 1, 6, 2, 7, 3},
                merge.stream().mapToDouble(e -> e.getValue()).toArray(),
                delta);
    }
    
    private void reimport(CsvTimeSeriesWriter wtr)
            throws IOException, ParseException {
        //wtr.write(System.out, tsd);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        wtr.write(bout, tsd);
        CsvTimeSeriesData tsd2 = new CsvTimeSeriesData(evsup);
        tsd2.read(new ByteArrayInputStream(bout.toByteArray()), "<none>");
        assertArrayEquals(ts1.times, tsd2.getSeries(names[0]).times, delta);
        assertArrayEquals(ts1.values, tsd2.getSeries(names[0]).values, delta);
        assertArrayEquals(ts2.times, tsd2.getSeries(names[1]).times, delta);
        assertArrayEquals(ts2.values, tsd2.getSeries(names[1]).values, delta);
    }
    
    @Test
    public void testReimport() throws Exception {
        CsvTimeSeriesWriter wtr = inj.getInstance(CsvTimeSeriesWriter.class);
        reimport(wtr);
    }
    
    @Test
    public void testNumeric() throws Exception {
        CsvTimeSeriesWriter wtr = inj.getInstance(CsvTimeSeriesWriter.class);
        wtr.setNumeric(true);
        reimport(wtr);
    }
}
