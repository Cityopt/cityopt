package eu.cityopt.opt.io;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.cityopt.opt.io.JacksonBinder.Item;
import eu.cityopt.opt.io.JacksonBinder.Kind;

/**
 * Check item names for uniqueness.
 * The result is a Map of qualified names by Kind.
 * @author ttekth
 */
public class NameChecker extends AbstractBuilder<Map<Kind, Set<String>>> {
    public NameChecker() {
        super(new HashMap<>());
    }

    /**
     * @throws IllegalArgumentException on duplicate name
     */
    @Override
    public void add(Item item) {
        Set<String> ns = result.computeIfAbsent(
                item.getKind(), k -> new HashSet<>());
        String qn = item.getQName();
        if (!ns.add(qn)) {
            throw new IllegalArgumentException(
                    "duplicate " + item.getKind() + " name " + qn);
        }
    }
}
