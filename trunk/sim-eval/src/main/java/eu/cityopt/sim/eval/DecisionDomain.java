package eu.cityopt.sim.eval;

/** The domain of a decision variable.
 */
public class DecisionDomain {
    final private Type valueType;

    public DecisionDomain(Type type) {
        valueType = type;
    }

    public Type getValueType() {
        return valueType;
    }

}