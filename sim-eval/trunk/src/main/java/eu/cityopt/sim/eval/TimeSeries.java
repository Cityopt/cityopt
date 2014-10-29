package eu.cityopt.sim.eval;

public class TimeSeries {
    public long[] timeMillis;
    public double[] values;

    public TimeSeries(long[] timeMillis, double[] values) {
        this.timeMillis = timeMillis;
        this.values = values;
    }
}
