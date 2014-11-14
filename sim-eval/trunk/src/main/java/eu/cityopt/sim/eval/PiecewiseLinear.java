package eu.cityopt.sim.eval;

import java.util.Arrays;

/**
 * Piecewise linear function defined by a sequence of (t, v) points.
 * <p>
 * The function may have vertical segments, but not two of them consecutively.
 * At the t of a vertical segment, the latter v in the sequence defines the
 * function value. You can get both v values by interpolating with an argument
 * vector that contains the t of the vertical segment twice. (This feature is
 * needed to support arithmetic between piecewise functions, and conversion 
 * of step-interpolated functions to linearly interpolated functions.)
 *
 * @see PiecewiseFunction#make(double[], double[], int)
 *
 * @author Hannu Rummukainen
 */
public class PiecewiseLinear extends PiecewiseFunction {

    PiecewiseLinear(double[] tt, double[] vv) {
        super(tt, vv, 1);
    }

    private static final double interpolate(
            double t0, double t1, double v0, double v1, double t) {
        assert t0 <= t && t <= t1 && t0 < t1;
        double f = (t - t0) / (double) (t1 - t0);
        return f * v1 + (1-f) * v0;
    }

    @Override
    protected double[] interpolate(int ii,
            double[] at, double[] vvo, int io0, int io1) {
        int ni = tt.length;
        double ti = tt[ii];
        for (int io = io0; io < io1; ++io) {
            double to = at[io];
            if (to > ti) {
                ++ii;
                ti = tt[ii];
                if (to > ti) {
                    ++ii;
                    ti = tt[ii];
                    if (to > ti) {
                        int bs = Arrays.binarySearch(tt, ii+1, ni, to);
                        if (bs >= 0) {
                            // In case of a vertical segment, select the earlier index.
                            ii = (tt[bs-1] == to) ? bs-1 : bs;
                            ti = to;
                        } else {
                            ii = ~bs;
                            ti = tt[ii];
                        }
                    }
                }
            }
            assert ti == tt[ii] && to <= ti && (ii == 0 || to >= tt[ii-1]);
            if (to == ti) {
                if (io + 1 < io1 && at[io + 1] == to) {
                    // Left value requested.  In the beginning, it's always zero.
                    vvo[io] = (ii > 0) ? vv[ii] : 0.0;
                } else if (ii + 1 < ni && tt[ii + 1] == ti) {
                    // Right value requested, and two values are available.
                    // Pick the latter one.
                    ++ii;
                    vvo[io] = vv[ii];
                } else {
                    vvo[io] = vv[ii];
                }
            } else { // to < ti
                vvo[io] = interpolate(tt[ii-1], ti, vv[ii-1], vv[ii], to);
            }
        }
        return vvo;
    }

    @Override
    public double integrate(int i0, int i1, double t0, double t1) {
        // Integrate by the trapezoid method.
        double v0 = interpolate(tt[i0], tt[i0+1], vv[i0], vv[i0+1], t0);
        double v1 = interpolate(tt[i1], tt[i1+1], vv[i1], vv[i1+1], t1);
        if (i0 == i1) {
            return (t1 - t0) * 0.5 * (v0 + v1);
        } else {
            double tp = t0;
            double t = tt[i0 + 1];
            double vs = (t - tp) * v0;
            for (int i = i0 + 1; i < i1; ++i) {
                double tn = tt[i+1];
                vs += (tn - tp) * vv[i];
                tp = t;
                t = tn;
            }
            vs += (t1 - tp) * vv[i1];
            vs += (t1 - t) * v1;
            return 0.5 * vs;
        }
    }

    @Override
    public double variance(double mean) {
        int n = vv.length;
        if (n < 2) {
            return  0.0;
        } else {
            double t = tt[1];
            double tp = tt[0];
            double v0 = vv[0] - mean;
            double v = vv[1] - mean;
            double vss = (t - tp) * v0 * (v0 + v);
            for (int i = 2; i < n; ++i) {
                double tn = tt[i];
                double vn = vv[i] - mean;
                vss += v * ((tn - tp) * v + (tn - t) * vn);
                tp = t;
                t = tn;
                v = vn;
            }
            vss += (t - tp) * v * v;
            double ts = t - tt[0];
            return vss / (3.0 * ts);
        }
    }

    @Override
    public PiecewiseLinear abs() {
        int ni = vv.length;
        int no = ni;
        if (ni > 0) {
            double vp = vv[0];
            for (int ii = 1; ii < ni; ++ii) {
                double v = vv[ii];
                if (((v < 0 && vp > 0) || (v > 0 && vp < 0))
                        && (tt[ii] != tt[ii-1])) {
                    ++no;
                }
                vp = v;
            }
        }
        double[] tto = new double[no];
        double[] vvo = new double[no];
        if (no > 0) {
            double tp = tt[0];
            double vp = vv[0];
            tto[0] = tp;
            vvo[0] = Math.abs(vp);
            int io = 1;
            for (int ii = 1; ii < ni; ++ii) {
                double t = tt[ii];
                double v = vv[ii];
                if (((v < 0 && vp > 0) || (v > 0 && vp < 0))
                        && (t != tp)) {
                    tto[io] = tp + (t - tp) * ((-vp) / (v - vp));
                    vvo[io] = 0.0;
                    ++io;
                }
                tto[io] = t;
                vvo[io] = Math.abs(v);
                ++io;
                tp = t;
                vp = v;
            }
            assert io == no;
        }
        return new PiecewiseLinear(tto, vvo);
    }

    @Override
    protected double[] forCombine(int d, boolean zeroBegin, boolean zeroEnd) {
        int ni = tt.length;
        if (ni == 0) {
            return tt;
        } else {
            boolean addVerticalBegin = zeroBegin && vv[0] != 0
                    && !(ni > 1  && tt[0] == tt[1]);
            boolean addVerticalEnd = zeroEnd && vv[ni-1] != 0
                    && !(ni > 1 && tt[ni-1] == tt[ni-2]);
            int no = ni + (addVerticalBegin ? 1 : 0) + (addVerticalEnd ? 1 : 0);
            if (no > ni) {
                double[] tto = new double[no];
                int io = 0;
                if (addVerticalBegin) {
                    tto[io] = tt[0];
                    ++io;
                }
                System.arraycopy(tt, 0, tto, io, ni);
                io += ni;
                if (addVerticalEnd) {
                    tto[io] = tt[ni-1];
                    ++io;
                }
                assert io == no;
                return tto;
            } else {
                return tt;
            }
        }
    }
}
