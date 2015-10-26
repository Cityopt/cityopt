package eu.cityopt.opt.io;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit information for CSV import/export purposes.
 */
public class UnitMap {
    public Map<JacksonBinder.Kind, Map<String, String>>
        byKind = new EnumMap<>(JacksonBinder.Kind.class);
    
    public void put(JacksonBinder.Kind kind, String qname, String unit) {
        byKind.computeIfAbsent(kind, k -> new HashMap<>())
                .put(qname, unit);
    }

    public String get(JacksonBinder.Kind kind, String qname) {
        Map<String, String> byName = byKind.get(kind);
        return byName == null ? null : byName.get(qname);
    }
    
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
