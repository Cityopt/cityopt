package eu.cityopt.sim.eval;

/**
 * The domain of a real or integer decision variable.
 * @author ttekth
 */
public class NumericInterval<T extends Number & Comparable<T>>
extends NumberInterval {
    private T lowerBound, upperBound;    
    
    private NumericInterval(Type vt, T lb, T ub) {
        super(vt);
        if (lb.compareTo(ub) > 0)
            throw new IllegalArgumentException(String.format(
                    "Interval lower bound %s > upper bound %s", lb, ub));
        lowerBound = lb;
        upperBound = ub;
    }

    @Override
    public T getLowerBound() {
        return lowerBound;
    }

    @Override
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

    /**
     * Interval of numerical type.  Currently the types INTEGER, DOUBLE
     * and TIMESTAMP are supported.
     * @param type type of the decision variable
     * @param lb lower bound or null for no bound
     * @param ub upper bound or null for no bound
     * @return the interval
     * @throws IllegalArgumentException if type is unsupported or lb > ub 
     */
    public static DecisionDomain makeInterval(
            Type type, Object lb, Object ub) {
        switch (type) {
        case INTEGER:
            //TODO either move this to makeIntInterval or throw error on null bounds?
            return new NumericInterval<Integer>(type,
                    (lb != null) ? (Integer) lb : Integer.MIN_VALUE,
                    (ub != null) ? (Integer) ub : Integer.MAX_VALUE);
        case DOUBLE:
        case TIMESTAMP:
            return new NumericInterval<Double>(type, (Double) lb, (Double) ub);
        default:
            throw new IllegalArgumentException(
                    "Unsupported decision variable type " + type);
        }
    }
}
