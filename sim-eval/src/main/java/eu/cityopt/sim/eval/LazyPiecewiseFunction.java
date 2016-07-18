package eu.cityopt.sim.eval;

import java.util.function.Supplier;

/**
 * A lazy piecewise function.
 *
 * When (or if) function values are first requested, the provided supplier
 * is called and its return value cached.  Methods of this class then delegate
 * to that return value.
 * @author Timo Korvola
 *
 */
public class LazyPiecewiseFunction extends PiecewiseFunction {
    private final Supplier<PiecewiseFunction> sup;
    private volatile PiecewiseFunction pwf;

    /**
     * Construct a lazy piecewise function.  The actual implementation
     * is provided by sup.get().
     */
    public LazyPiecewiseFunction(Supplier<PiecewiseFunction> sup) {
        this.sup = sup;
    }

    private PiecewiseFunction force() {
        if (pwf != null) {
            return pwf;
        }
        synchronized (this) {
            if (pwf == null) {
                pwf = sup.get();
            }
        }
        return pwf;
    }

    @Override
    public double[] getTimes() {
        return force().getTimes();
    }

    @Override
    public double[] getValues() {
        return force().getValues();
    }

    @Override
    public int getDegree() {
        return force().getDegree();
    }

    @Override
    protected double[] interpolate(
            int ii, double[] at, double[] vvo, int io0, int io1) {
        return force().interpolate(ii, at, vvo, io0, io1);
    }

    @Override
    protected double interpolateOnSegment(int i0, double t) {
        return force().interpolateOnSegment(i0, t);
    }

    @Override
    protected double integrate(int i0, int i1, double t0, double t1) {
        return force().integrate(i0, i1, t0, t1);
    }

    @Override
    public double variance(double mean) {
        return force().variance(mean);
    }

    @Override
    public PiecewiseFunction abs() {
        return force().abs();
    }

    @Override
    protected double[] forCombine(int d, boolean zeroBegin, boolean zeroEnd) {
        return force().forCombine(d, zeroBegin, zeroEnd);
    }
}
