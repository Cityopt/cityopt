package eu.cityopt.opt.io;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * An EnumMap / HashMap implementation of UnitMap.
 */
public class UnitHashMap extends UnitMap {
    public Map<JacksonBinder.Kind, Map<String, String>>
        byKind = new EnumMap<>(JacksonBinder.Kind.class);
    
    @Override
    public void put(JacksonBinder.Kind kind, String qname, String unit) {
        byKind.computeIfAbsent(kind, k -> new HashMap<>())
                .put(qname, unit);
    }

    @Override
    public String get(JacksonBinder.Kind kind, String qname) {
        Map<String, String> byName = byKind.get(kind);
        return byName == null ? null : byName.get(qname);
    }
}
