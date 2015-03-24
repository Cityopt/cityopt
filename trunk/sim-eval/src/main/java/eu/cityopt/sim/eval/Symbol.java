package eu.cityopt.sim.eval;

/**
 * The qualified name of a variable or parameter.
 *
 * @author Hannu Rummukainen
 */
public class Symbol {
    /** Component name.  May be null. */
    public final String componentName;

    /** Variable or parameter name.  Non-null. */
    public final String name;

    public Symbol(String componentName, String name) {
        this.componentName = componentName;
        this.name = name;
    }

    public String toString() {
        return (componentName != null)
                ? componentName + "." + name
                : name;
    }
}
