package eu.cityopt.sim.eval;

/**
 * The domain of a real or integer decision variable.
 * @author ttekth
 */
public class NumericInterval<T extends Number> extends DecisionDomain {
    private T lowerBound, upperBound;    
    
    private NumericInterval(Type vt, T lb, T ub) {
        super(vt);
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
     */
    public static NumericInterval<Double> makeRealInterval(
            Double lb, Double ub) {
        return new NumericInterval<Double>(
                Type.DOUBLE, (lb != null ? lb : Double.NEGATIVE_INFINITY),
                ub != null ? ub : Double.POSITIVE_INFINITY);
    }
    
    public static NumericInterval<Integer> makeIntInterval(int lb, int ub) {
        return new NumericInterval<Integer>(Type.INTEGER, lb, ub);
    }
}
