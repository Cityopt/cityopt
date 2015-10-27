package eu.cityopt.opt.io;

import eu.cityopt.opt.io.JacksonBinder.Item;
import eu.cityopt.opt.io.JacksonBinder.Var;

public class UnitBuilder
extends AbstractBuilder<UnitMap> implements RobustImportBuilder {
    public UnitBuilder() {
        this(new UnitHashMap());
    }
    
    public UnitBuilder(UnitMap units) {
        super(units);
    }

    @Override
    public void add(Item item) {
        if (item instanceof Var) {
            Var var = (Var)item;
            result.put(var.getKind(), var.getQName(), var.unit);
        }
    }
}
