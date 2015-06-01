package eu.cityopt.opt.io;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.Evaluator;

public class ExportTest {
    @Test
    public void testMergedTimeSeries() {
        TimeSeriesData.Series ts1 = new TimeSeriesData.Series();
        ts1.times = new double[] {0, 1, 2, 3};
        ts1.values = ts1.times;
        TimeSeriesData.Series ts2 = new TimeSeriesData.Series();
        ts2.times = new double[] {0.2, 0.8, 1.5, 2.5};
        ts2.values = new double[] {4, 5, 6, 7};
        TimeSeriesData tsd = new TimeSeriesData(new EvaluationSetup(
                new Evaluator(), null));
        String[] names = {"ts1", "ts2"};
        tsd.seriesData.put(names[0], ts1);
        tsd.seriesData.put(names[1], ts2);
        MergedTimeSeries merge = new MergedTimeSeries(tsd);
        assertArrayEquals(names, merge.getNames().toArray());
        assertArrayEquals(
                new int[] {0, 1, 1, 0, 1, 0, 1, 0},
                merge.stream().mapToInt(e -> e.getSeries()).toArray());
        assertArrayEquals(
                new double[] {0,  .2, .8, 1, 1.5, 2, 2.5, 3},
                merge.stream().mapToDouble(e -> e.getTime()).toArray(),
                1e-16);
        assertArrayEquals(
                new double[] {0, 4, 5, 1, 6, 2, 7, 3},
                merge.stream().mapToDouble(e -> e.getValue()).toArray(),
                1e-16);
    }
}
