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
    
    public static NumericInterval<Double> makeRealInterval(
            double lb, double ub) {
        return new NumericInterval<Double>(Type.DOUBLE, lb, ub);
    }
    
    public static NumericInterval<Integer> makeIntInterval(int lb, int ub) {
        return new NumericInterval<Integer>(Type.INTEGER, lb, ub);
    }
}
