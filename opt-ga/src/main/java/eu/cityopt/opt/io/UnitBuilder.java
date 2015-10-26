package eu.cityopt.opt.io;

import java.text.ParseException;

import javax.script.ScriptException;

import eu.cityopt.opt.io.JacksonBinder.Item;
import eu.cityopt.opt.io.JacksonBinder.Var;

public class UnitBuilder extends AbstractBuilder<UnitMap> {
    public UnitBuilder() {
        super(new UnitMap());
    }

    @Override
    public void add(Item item) throws ParseException, ScriptException {
        if (item instanceof Var) {
            Var var = (Var)item;
            result.put(var.getKind(), var.getQName(), var.unit);
        }
    }
}
