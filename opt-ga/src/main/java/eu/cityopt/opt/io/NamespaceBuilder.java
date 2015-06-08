package eu.cityopt.opt.io;

import java.text.ParseException;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Named;
import javax.script.ScriptException;

import eu.cityopt.opt.io.JacksonBinder.CompVar;
import eu.cityopt.opt.io.JacksonBinder.DecisionVar;
import eu.cityopt.opt.io.JacksonBinder.ExtParam;
import eu.cityopt.opt.io.JacksonBinder.Input;
import eu.cityopt.opt.io.JacksonBinder.Item;
import eu.cityopt.opt.io.JacksonBinder.Metric;
import eu.cityopt.opt.io.JacksonBinder.Output;
import eu.cityopt.opt.io.JacksonBinder.Var;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.Type;

/**
 * An {@link ImportBuilder} for {@link Namespace}
 * @author ttekth
 *
 */
public class NamespaceBuilder extends AbstractBuilder<Namespace> {
    /**
     * Build on an existing Namespace.
     * N.B.: Namespaces must not be changed when in use.
     */
    public NamespaceBuilder(Namespace initial) {
        super(initial);
    }
    
    @Inject
    public NamespaceBuilder(
            Evaluator evaluator,
            @Named("timeOrigin") Instant timeOrigin) {
        this(new Namespace(evaluator, timeOrigin, true));
    }

    private static void addToNSMap(Var var, Map<String, Type> map) {
        if (map.putIfAbsent(var.name, var.type) != null) {
            throw new IllegalArgumentException(
                    "duplicate " + var.getKind() + " name " + var.getQName());
        }
    }
    
    private void addToNSComp(
            CompVar var,
            Function<Namespace.Component, Map<String, Type>> getMap) {
        addToNSMap(var, getMap.apply(result.getOrNew(var.comp)));
    }
    
    @Override
    public void add(Item item) {
        try {
            super.add(item);
        } catch (ParseException | ScriptException e) {
            throw new RuntimeException("This can't happen", e);
        }
    }

    @Override
    protected void add(ExtParam item) {
        addToNSMap(item, result.externals);
    }

    @Override
    protected void add(DecisionVar item) {
        if (item.comp != null) {
            addToNSComp(item, c -> c.decisions);
        } else {
            addToNSMap(item, result.decisions);
        }
    }

    @Override
    protected void add(Input item) {
        addToNSComp(item, c -> c.inputs);
    }

    @Override
    protected void add(Output item) {
        addToNSComp(item, c -> c.outputs);
    }

    @Override
    protected void add(Metric item) {
        addToNSMap(item, result.metrics);
    }
}
