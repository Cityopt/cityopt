package eu.cityopt.sim.eval;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        return (componentName != null)
                ? componentName.hashCode() ^ ~name.hashCode()
                : name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if ( ! (obj instanceof Symbol)) {
            return false;
        }
        Symbol other = (Symbol) obj;
        return Objects.equals(componentName, other.componentName)
                && name.equals(other.name);
    }
}
