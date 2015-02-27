package eu.cityopt.sim.eval;

/**
 * The domain of a real or integer decision variable.
 * @author ttekth
 */
public class NumericInterval<T extends Number & Comparable<T>>
extends DecisionDomain {
    private T lowerBound, upperBound;    
    
    private NumericInterval(Type vt, T lb, T ub) {
        super(vt);
        if (lb.compareTo(ub) > 0)
            throw new IllegalArgumentException(String.format(
                    "Interval lower bound %s > upper bound %s", lb, ub));
        lowerBound = lb;
        upperBound = ub;
    }

    public T getLowerBound() {
        return lowerBound;
    }

    public T getUpperBound() {
        return upperBound;
    }
    
    /**
     * As a convenience nulls are permitted here to denote absent bounds.
     * They are converted to appropriate infinities.
     * @param lb lower bound
     * @param ub upper bound
     * @return the interval
     * @throws IllegalArgumentException if lb > ub
     */
    public static NumericInterval<Double> makeRealInterval(
            Double lb, Double ub) {
        return new NumericInterval<Double>(
                Type.DOUBLE, (lb != null ? lb : Double.NEGATIVE_INFINITY),
                ub != null ? ub : Double.POSITIVE_INFINITY);
    }
    
    /**
     * Integer intervals must always be bounded.
     * @param lb lower bound
     * @param ub upper bound
     * @return the interval
     * @throws IllegalArgumentException if lb > ub
     */
    public static NumericInterval<Integer> makeIntInterval(int lb, int ub) {
        return new NumericInterval<Integer>(Type.INTEGER, lb, ub);
    }
}
