package eu.cityopt.sim.eval;

public class DecisionVariable {
    public final String componentName;
    public final String name;
    public final DecisionDomain domain;

    public DecisionVariable(String componentName, String name, DecisionDomain domain) {
        this.componentName = componentName;
        this.name = name;
        this.domain = domain;
    }

    public String toString() {
        return (componentName != null)
                ? componentName + "." + name
                : name;
    }
}
