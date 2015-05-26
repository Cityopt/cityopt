package eu.cityopt.opt.io;

import java.text.ParseException;

import javax.script.ScriptException;

import eu.cityopt.opt.io.JacksonBinder.Constr;
import eu.cityopt.opt.io.JacksonBinder.DecisionVar;
import eu.cityopt.opt.io.JacksonBinder.ExtParam;
import eu.cityopt.opt.io.JacksonBinder.Input;
import eu.cityopt.opt.io.JacksonBinder.Item;
import eu.cityopt.opt.io.JacksonBinder.Metric;
import eu.cityopt.opt.io.JacksonBinder.Obj;
import eu.cityopt.opt.io.JacksonBinder.Output;

/**
 * A template for a {@link JacksonBuilder}.
 * Concrete implementations typically extend this.
 * 
 * @author ttekth
 * @param <Result> The type of object constructed.
 */
public abstract class AbstractBuilder<Result> implements JacksonBuilder {
    protected final Result result; 

    /**
     * Set up the initial state.  Typically it would be an empty Result,
     * whatever that means.
     * @param initial object to build on
     */
    protected AbstractBuilder(Result initial) {
        result = initial;
    }

    /**
     * Dispatch by item Kind.  Implementers may override Kind-specific
     * add methods (the default ones do nothing) or override this method
     * and do their own dispatching.
     */
    @Override
    public void add(Item item) throws ParseException, ScriptException {
        switch (item.getKind()) {
        case EXT:
            add((ExtParam)item);
            break;
        case DV:
            add((DecisionVar)item);
            break;
        case IN:
            add((Input)item);
            break;
        case OUT:
            add((Output)item);
            break;
        case MET:
            add((Metric)item);
            break;
        case CON:
            add((Constr)item);
            break;
        case OBJ:
            add((Obj)item);
        }
    }
    
    protected void add(ExtParam item) throws ParseException, ScriptException {}
    protected void add(DecisionVar item)
            throws ParseException, ScriptException {}
    protected void add(Input item) throws ParseException, ScriptException {}
    protected void add(Output item) throws ParseException, ScriptException {}
    protected void add(Metric item) throws ParseException, ScriptException {}
    protected void add(Constr item) throws ParseException, ScriptException {}
    protected void add(Obj item) throws ParseException, ScriptException {}

    public Result getResult() {
        return result;
    }
}
