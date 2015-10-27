package eu.cityopt.opt.io;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit information for CSV import/export purposes.
 */
public abstract class UnitMap {
    public abstract String get(JacksonBinder.Kind kind, String qname);

    public abstract void put(JacksonBinder.Kind kind, String qname, String unit);

    /**
     * Loop through the binder and set the unit of every item that
     * we know of. 
     */
    public void apply(JacksonBinder binder) {
        for (JacksonBinder.Item it : binder.getItems()) {
            setUnit(it);
        }
    }

    /**
     * Loop through the binder and set the unit of every item that
     * we know of. 
     */
    public void apply(JacksonBinderScenario binder) {
        for (JacksonBinderScenario.ScenarioItem it : binder.getItems()) {
            setUnit(it.getItem());
        }
    }

    /**
     * Loop through the internal binder of the ExportBuilder and
     * set the unit of every item that we know of.
     */
    public void apply(ExportBuilder builder) {
        // N.B.: not getBinder()
        apply(builder.getScenarioBinder());
    }

    private void setUnit(JacksonBinder.Item it) {
        if (it instanceof JacksonBinder.Var) {
            JacksonBinder.Var var = (JacksonBinder.Var)it;
            String unit = get(var.getKind(), var.getQName());
            if (unit != null) {
                var.unit = unit;
            }
        }
    }

}