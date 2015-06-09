package eu.cityopt.sim.eval;

public abstract class NumberInterval extends DecisionDomain {
    public NumberInterval(Type type) {
        super(type);
    }

    public abstract Number getLowerBound();
    public abstract Number getUpperBound();
}