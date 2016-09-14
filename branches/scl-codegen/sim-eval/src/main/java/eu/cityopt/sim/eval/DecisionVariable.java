package eu.cityopt.sim.eval;

/**
 * A named decision variable with a domain.
 *
 * @see DecisionValues for storing decision variable values
 *
 * @author Hannu Rummukainen
 */
public class DecisionVariable extends Symbol {
    public final DecisionDomain domain;

    public DecisionVariable(String componentName, String name, DecisionDomain domain) {
        super(componentName, name);
        this.domain = domain;
    }
}
