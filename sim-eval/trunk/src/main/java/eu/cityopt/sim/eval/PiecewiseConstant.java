package eu.cityopt.sim.eval;

import java.util.Arrays;

/**
 * Piecewise constant function defined by a sequence of (t, v) points.
 * Between two defined points, the value is defined by the earlier point.
 * 
 * @see PiecewiseFunction#make(double[], double[], int)
 *
 * @author Hannu Rummukainen
 */
public class PiecewiseConstant extends PiecewiseFunction {

    PiecewiseConstant(double[] tt, double[] vv) {
        super(tt, vv, 0);
    }

    @Override
    protected double[] interpolate(
            int ii, double[] at, double[] vvo, int io0, int io1) {
        int ni = tt.length;
        double ti = tt[ii];
        for (int io = io0; io < io1; ++io) {
            double to = at[io];
            if (to > ti) {
                ++ii;
                ti = tt[ii];
                if (to > ti) {
                    int bs = Arrays.binarySearch(tt, ii+1, ni, to);
                    ii = (bs < 0) ? ~bs : bs;
                    ti = tt[ii];
                }
            }
            assert ti == tt[ii] && to <= ti && (ii == 0 || to > tt[ii-1]);
            if (to == ti) {
                if (io + 1 < io1 && at[io + 1] == to) {
                    // Left value requested.
                    vvo[io] = (ii > 0) ? vv[ii-1] : 0.0;
                } else {
                    vvo[io] = vv[ii];
                }
            } else { // to < ti
                vvo[io] = vv[ii-1];
            }
        }
        return vvo;
    }

    @Override
    public double integrate(int i0, int i1, double t0, double t1) {
        if (i0 == i1) {
            return (t1 - t0) * vv[i0];
        } else {
            double t = tt[i0 + 1];
            double vs = (t - t0) * vv[i0];
            for (int i = i0 + 1; i < i1; ++i) {
                double tp = t;
                t = tt[i + 1];
                vs += (t - tp) * vv[i];
            }
            vs += (t1 - t) * vv[i1];
            return vs;
        }
    }

    @Override
    public double variance(double mean) {
        int n = vv.length;
        if (n < 2) {
            return  0.0;
        } else {
            double t = tt[0];
            double vss = 0;
            for (int i = 1; i < n; ++i) {
                double tn = tt[i];
                double dt = tn - t;
                double v = vv[i-1] - mean;
                vss += dt * v * v;
                t = tn;
            }
            double ts = t - tt[0];
            return vss / ts;
        }
    }

    @Override
    public PiecewiseConstant abs() {
        int n = vv.length;
        double[] tto = new double[n];
        double[] vvo = new double[n]; 
        for (int i = 0; i < n; ++i) {
            tto[i] = tt[i];
            vvo[i] = Math.abs(vv[i]);
        }
        return new PiecewiseConstant(tto, vvo); 
    }

    @Override
    protected double[] forCombine(int d, boolean zeroBegin, boolean zeroEnd) {
        if (d == 0) {
            return tt;
        } else {
            int ni = tt.length;
            if (ni == 0) {
                return tt;
            } else {
                boolean addVerticalBegin = zeroBegin && vv[0] != 0;
                int no = 2 * ni - (addVerticalBegin ? 0 : 1);
                double[] tto = new double[no];
                tto[0] = tt[0];
                int io = 1;
                if (addVerticalBegin) {
                    tto[io] = tt[0];
                    ++io;
                }
                for (int ii = 1; ii < ni; ++ii) {
                    tto[io] = tt[ii];
                    tto[io + 1] = tt[ii];
                    io += 2;
                }
                assert io == no;
                return tto;
            }
        }
    }
}
