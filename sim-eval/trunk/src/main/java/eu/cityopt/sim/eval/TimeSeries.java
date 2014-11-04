package eu.cityopt.sim.eval;

import java.util.Locale;

public class TimeSeries {
    public long[] timeMillis;
    public double[] values;

    private boolean statisticsOk;
    private double mean;
    private double var;

    public TimeSeries(long[] timeMillis, double[] values) {
        this.timeMillis = timeMillis;
        this.values = values;
    }

    public double getMean() {
        if (!statisticsOk) {
            computeStatistics();
        }
        return mean;
    }

    public double getStdev() {
        if (!statisticsOk) {
            computeStatistics();
        }
        return Math.sqrt(var);
    }

    void computeStatistics() {
        // We assume that the values can be interpolated linearly between time points.
        int n = values.length;
        if (n == 0) {
            mean = 0.0;
            var = 0.0;
        } else if (n == 1) {
            mean = values[0];
            var = 0.0;
        } else {
            {
                long t = timeMillis[1];
                long dt = t - timeMillis[0];
                double v0 = values[0];
                double v = values[1];
                double vs = dt * v0;
                for (int i = 2; i < n; ++i) {
                    long t1 = timeMillis[i];
                    long dt1 = t1 - t;
                    double v1 = values[i];
                    vs += (dt + dt1) * v;
                    t = t1;
                    dt = dt1;
                    v = v1;
                }
                vs += dt * v;
                double ts = t - timeMillis[0];
                mean = vs / (2.0 * ts);
            }
            {
                long t = timeMillis[1];
                long dt = t - timeMillis[0];
                double v0 = values[0] - mean;
                double v = values[1] - mean;
                double vss = dt * v0 * (v0 + v);
                for (int i = 2; i < n; ++i) {
                    long t1 = timeMillis[i];
                    long dt1 = t1 - t;
                    double v1 = values[i] - mean;
                    vss += v * ((dt + dt1) * v + dt1 * v1);
                    t = t1;
                    dt = dt1;
                    v = v1;
                }
                vss += dt * v * v;
                double ts = t - timeMillis[0];
                var = vss / (3.0 * ts);
            }
        }
        statisticsOk = true;
    }

    public String toString() {
        return String.format(Locale.ROOT, "{ length = %d, mean = %g, stdev = %g }",
                values.length, getMean(), getStdev());
    }
}
