package eu.cityopt.sim.eval;

import java.util.Arrays;

/**
 * Piecewise function defined by sequence of (t, v) points.
 *
 * @author Hannu Rummukainen
 */
public abstract class PiecewiseFunction {
    final double[] tt;
    final double[] vv;

    /** Degree of the interpolator.  0 for step interpolator, 1 for linear. */
    public final int degree;

    PiecewiseFunction(double[] tt, double[] vv, int degree) {
        this.tt = tt;
        this.vv = vv;
        this.degree = degree;
    }

    /**
     * Creates a piecewise function from a sequence of points.
     * 
     * @return a PiecewiseConstant instance if degree is zero, or a
     *         PiecewiseLinear instance if <code>degree</code> is one.
     * @throws IllegalArgumentException
     *             if the t coordinate sequence is not in strictly increasing
     *             order; when <code>degree</code> is one, non-consecutive
     *             vertical segments are also allowed.
     */
    public static PiecewiseFunction make(int degree, double[] tt, double[] vv) {
        // Check if the t coordinate sequence is valid.
        // If support for multiple consecutive vertical segments turns out
        // to be needed, we could in principle join such segments here.
        if (tt.length != vv.length) {
            throw new IllegalArgumentException("Different number of times and values");
        }
        double tp = (tt.length > 0) ? tt[0] : 0;
        double tpp = tp - 1;
        for (int i = 1; i < tt.length; ++i) {
            double t = tt[i];
            if (t <= tp) {
                if (degree == 1 && t == tp) {
                    if (tp == tpp) {
                        throw new IllegalArgumentException(
                                "Two consecutive vertical segments detected");
                    } // else ok
                } else {
                    throw new IllegalArgumentException(
                            "The t coordinates are not in increasing order");
                }
            }
            tpp = tp;
            tp = t;
        }

        switch (degree) {
        case 0: return new PiecewiseConstant(tt, vv);
        case 1: return new PiecewiseLinear(tt, vv);
        default:
            throw new IllegalArgumentException("Invalid degree "+degree);
        }
    }

    protected final static int firstEqual(double[] uu, int i) {
        double u = uu[i];
        while (i > 0 && uu[i-1] == u) {
            --i;
        }
        return i;
    }

    protected final static int lastEqual(double[] uu, int i) {
        double u = uu[i];
        int nx = uu.length - 1;
        while (i < nx && uu[i+1] == u) {
            ++i;
        }
        return i;
    }

    /**
     * Interpolates v at given t.
     *
     * @param at
     *            t coordinates at which to interpolate, in increasing order.
     *            Duplicates are allowed, and in such cases the first equal t
     *            coordinate gives the limit from the left, and the second equal
     *            coordinate gives the value at t.
     * @return values corresponding to the 'at' parameter
     */
    public double[] interpolate(double[] at) {
        int no = at.length;
        double[] vvo = new double[no];
        int ni = vv.length;
        if (no == 0 || ni == 0) {
            return vvo;
        }

        // Find first element of 'at' in the domain of the function
        int io0 = 0;
        if (at[0] < tt[0]) {
            int bs0 = Arrays.binarySearch(at, tt[0]);
            io0 = (bs0 < 0) ? ~bs0 : firstEqual(at, bs0);
            if (io0 == no) {
                // at[no-1] < tt[0] 
                return vvo;
            }
        }
        assert at[io0] >= tt[0] && (io0 == 0 || at[io0-1] < tt[0]);

        // Find first element of 'at' beyond the domain of the function.
        int io1 = no;
        if (at[no-1] > tt[ni-1]) {
            int bs1 = Arrays.binarySearch(at, io0, no, tt[ni-1]);
            io1 = (bs1 < 0) ? ~bs1 : lastEqual(at, bs1) + 1;
            if (io1 == 0) {
                // tt[ni-1] < at[0]
                return vvo;
            }
        }
        assert (io1 == no || at[io1] >= tt[ni-1]) && at[io1-1] <= tt[ni-1];

        // Find which segment at[io0] is on.
        // Point ii to the segment endpoint.
        int bs2 = Arrays.binarySearch(tt, at[io0]);
        int ii = (bs2 < 0) ? ~bs2 : firstEqual(tt, bs2);
        if (ii == ni) {
            // tt[ni-1] < at[io]
            return vvo;
        }
        assert tt[ii] >= at[io0] && (ii == 0 || tt[ii-1] <= at[io0]);

        return interpolate(ii, at, vvo, io0, io1);
    }

    protected abstract double[] interpolate(
            int ii, double[] at, double[] vvo, int io0, int io1);

    protected abstract double interpolateOnSegment(int i0, double t);

    /**
     * Integral over the interval [t0, t1].
     * 
     * @param t0
     *            start point of the integration interval
     * @param t1
     *            end point of the integration interval.
     * @param scale
     *            the integral is divided by this scale factor. As a special
     *            case, a scale factor of 0 can be used to integrate over a
     *            zero-width interval.
     */
    public double integrate(double t0, double t1, double scale) {
        int n = vv.length;
        if (scale == 0) {
            if (n == 0) {
                return 0.0;
            }
            double d0 = Math.max(tt[0],  Math.min(t0, t1));
            double d1 = Math.min(tt[n-1], Math.max(t0,  t1));
            if (d0 > d1) {
                return 0.0;
            } else if (d1 == d0) {
                double v = interpolate(new double[] { d0 })[0];
                return (t0 <= t1) ? v : -v;
            } else {
                return (t0 <= t1) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
            }
        } else if (n < 2 || t0 == t1) {
            return 0.0;
        } else {
            if (t0 > t1) {
                scale = -scale;
                double tx = t0; t0 = t1; t1 = tx; //swap
            }
            // Locate the first non-vertical segment on the integration
            // interval, and point i0 to the segment startpoint.
            t0 = Math.max(t0, tt[0]);
            int bs0 = Arrays.binarySearch(tt, t0);
            int i0 = (bs0 >= 0) ? lastEqual(tt, bs0) : ~bs0 - 1;
            if (i0 >= n-1) {
                return 0.0;
            }

            // Locate the last non-vertical segment of the integration interval,
            // and point i1 to the segment startpoint.
            t1 = Math.min(t1, tt[n-1]);
            int bs1 = Arrays.binarySearch(tt, i0, n, t1);
            int i1 = (bs1 >= 0) ? firstEqual(tt, bs1) - 1 : ~bs1 - 1;
            if (i1 < 0) {
                return 0.0;
            }

            assert t0 <= t1 && i0 <= i1;
            assert tt[i0] <= t0 && t0 < tt[i0+1];
            assert tt[i1] < t1 && t1 <= tt[i1+1];
            return integrate(i0, i1, t0, t1) / scale;
        }
    }

    protected abstract double integrate(int i0, int i1, double t0, double t1);

    /** Mean value of the function over its domain. */
    public double mean() {
        if (tt.length == 0) {
            return 0.0;
        } else {
            double t0 = tt[0];
            double t1 = tt[tt.length-1];
            return integrate(t0, t1, t1-t0);
        }
    }

    /** Variance of the function over its domain. */
    public abstract double variance(double mean);

    /** Supremum value over the domain of the function. */
    public double sup() {
        double m = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < tt.length; ++i) {
            double v = vv[i];
            if (v > m) {
                m = v;
            }
        }
        return m;
    }

    /** Infimum value over the domain of the function. */
    public double inf() {
        double m = Double.POSITIVE_INFINITY;
        for (int i = 0; i < tt.length; ++i) {
            double v = vv[i];
            if (v < m) {
                m = v;
            }
        }
        return m;
    }

    /**
     * Computes the interpolated absolute value function.
     * 
     * @return a piecewise function of the same type as this function
     */
    public abstract PiecewiseFunction abs();

    /**
     * Limits the domain of the function to the given interval.
     * @return a piecewise function of the same type as this function
     */
    public PiecewiseFunction slice(double t0, double t1) {
        if (t0 > t1) {
            throw new IllegalArgumentException(
                    "Invalid slice: start "+t0+" greater than end "+t1);
        }
        int n = tt.length;
        if (n == 0 || t0 > tt[n-1] || t1 < tt[0]) {
            return make(degree, new double[0], new double[0]);
        }

        // Point i0 to the start point of the segment defining the right value at t0.
        t0 = Math.max(t0, tt[0]);
        int bs0 = Arrays.binarySearch(tt, t0);
        int i0 = (bs0 >= 0) ? lastEqual(tt, bs0) : ~bs0 - 1;
        assert tt[i0] <= t0 && (i0 == n-1 || t0 < tt[i0+1]);
        double v0 = (i0 == n-1) ? vv[i0] : interpolateOnSegment(i0, t0);

        // Point i1 to the start point of the segment defining the left value at t1.
        t1 = Math.min(t1, tt[n-1]);
        if (t0 == t1) {
            return make(degree, new double[] { t0 }, new double[] { v0 } );
        }
        int bs1 = Arrays.binarySearch(tt, i0, n, t1);
        int i1 = (bs1 >= 0) ? lastEqual(tt, bs1) - 1 : ~bs1 - 1;
        assert (tt[i1] < t1 && t1 <= tt[i1+1]) || (tt[i1] == t1 && t1 == tt[i1+1]);
        assert t0 < t1 && i0 <= i1;
        double v1 = (t1 == tt[i1+1]) ? vv[i1+1] : interpolateOnSegment(i1, t1);

        int ni = i1 - i0;
        int no = ni + 2;
        double[] tto = new double[no];
        double[] vvo = new double[no];
        int o = 0;
        tto[o] = t0;
        vvo[o] = v0;
        ++o;
        System.arraycopy(tt, i0+1, tto, o, ni);
        System.arraycopy(vv, i0+1, vvo, o, ni);
        o += ni;
        tto[o] = t1;
        vvo[o] = v1;
        ++o;
        assert o == no;
        return make(degree, tto, vvo);
    }

    interface UnaryOperation {
        /** Compute operation on source, writing result in target. */
        void transform(double[] source, double[] target);
    }

    /**
     * Performs a unary operation on the piecewise function. The result is
     * defined at exactly the same t coordinates as this function. The values
     * array is passed to the given UnaryOperation instance, which fills in the
     * values of the result function.
     */
    public PiecewiseFunction transform(UnaryOperation op) {
        double[] vvo = new double[vv.length];
        op.transform(vv, vvo);
        return make(degree, tt.clone(), vvo);
    }

    interface BinaryOperation {
        /** Compute operation on source and target, overwriting target. */
        void operate(double[] source, double[] target);
    }

    /**
     * Performs a binary operation between two piecewise functions. The values
     * are computed at t coordinates in which either of the two functions are
     * defined: the corresponding values of the two functions are passed as
     * arrays to the given BinaryOperation instance, which fills in the values
     * of the result function. In case of discontinuities, the values on both
     * sides of a discontinuity are processed separately.
     */
    public PiecewiseFunction combine(
            PiecewiseFunction other, BinaryOperation op) {
        int d = Math.max(this.degree, other.degree);
        int tn = this.tt.length;
        int on = other.tt.length;
        double tt0 = tn > 0 ? this.tt[0] : Double.POSITIVE_INFINITY;
        double tt1 = tn > 0 ? this.tt[tn - 1] : Double.NEGATIVE_INFINITY; 
        double ot0 = on > 0 ? other.tt[0] : Double.POSITIVE_INFINITY;
        double ot1 = on > 0 ? other.tt[on - 1] : Double.NEGATIVE_INFINITY;
        //
        // Merge the t coordinate sequences, and interpolate the functions
        // at the merged t coordinates.  When the result degree is 1, step
        // functions are converted to linear as follows: the forCombine
        // method duplicates the t coordinates, and the interpolate method
        // then creates the necessary vertical edges at duplicated coordinates.
        //
        // If either end of a function is inside the combined domain, that end
        // must explicitly be set 0, so that interpolation of the combined
        // function works properly.  The forCombine and interpolate methods
        // handle the issue by using extra vertical edges in piecewise linear
        // results; however the last interpolated value has to be adjusted below.
        //
        double[] ttr = merge(this.forCombine(d, tt0 > ot0, tt1 < ot1),
                other.forCombine(d, ot0 > tt0, ot1 < tt1));
        double[] vvt = this.interpolate(ttr);
        if (tt1 < ot1) {
            int bst = Arrays.binarySearch(ttr, tt1);
            assert bst >= 0;
            int i = lastEqual(ttr, bst);
            vvt[i] = 0;
        }
        double[] vvo = other.interpolate(ttr);
        if (ot1 < tt1) {
            int bso = Arrays.binarySearch(ttr, ot1);
            assert bso >= 0;
            int i = lastEqual(ttr, bso);
            vvo[i] = 0;
        }

        op.operate(vvt, vvo);

        return make(d, ttr, vvo);
    }

    protected abstract double[] forCombine(
            int d, boolean zeroBegin, boolean zeroEnd);

    /**
     * Merges two sorted arrays. In case of duplicates, keeps the maximum number
     * that occurs in either array.
     */
    private static double[] merge(double[] tta, double[] ttb) {
        int na = tta.length;
        int nb = ttb.length;

        // Count the size of the result.
        int no = 0;
        int ia = 0;
        int ib = 0;
        while (ia < na && ib < nb) {
            ++no;
            double ta = tta[ia];
            double tb = ttb[ib];
            if (ta <= tb) ++ia;
            if (tb <= ta) ++ib;
        }
        no += na - ia;
        no += nb - ib;

        // Create the result by merging.
        double[] tto = new double[no];
        int io = 0;
        ia = 0;
        ib = 0;
        while (ia < na && ib < nb) {
            double ta = tta[ia];
            double tb = ttb[ib];
            if (ta < tb) {
                tto[io] = ta;
                ++io;
                ++ia;
            } else if (tb < ta) {
                tto[io] = tb;
                ++io;
                ++ib;
            } else { // ta == tb
                tto[io] = ta;
                ++io;
                ++ia;
                ++ib;
            } 
        }
        while (ia < na) {
            tto[io] = tta[ia];
            ++io;
            ++ia;
        }
        while (ib < nb) {
            tto[io] = ttb[ib];
            ++io;
            ++ib;
        }
        assert io == tto.length;
        return tto;
    }

    @Override
    public String toString() {
        int MAX_PRINT = 100;
        int n = Math.min(MAX_PRINT, tt.length);
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append('{');
        for (int i = 0; i < n; ++i) {
            sb.append(" (");
            sb.append(tt[i]);
            sb.append(", ");
            sb.append(vv[i]);
            sb.append(')');
        }
        if (tt.length > n) {
            sb.append(" ...");
        }
        sb.append(" length=");
        sb.append(tt.length);
        sb.append(" }");
        return sb.toString();
    }
}
