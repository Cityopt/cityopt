package eu.cityopt.opt.io;

import java.text.ParseException;

import javax.script.ScriptException;

/**
 * A builder interface for {@link JacksonBinder}.
 * Since JacksonBinder is just a list of Items, a GoF style Director
 * would be overkill.  Just loop over the list and call the builder.
 * Builders typically extend {@link AbstractBuilder}.
 * @author ttekth
 */
public interface JacksonBuilder {
    /**
     * Add a new item.  For future extensibility most implementers
     * should ignore items of unknown Kind.
     * @throws ParseException if a value does not parse according to its type
     * @throws ScriptException if an expression does not compile.
     */
    void add(JacksonBinder.Item item) throws ParseException, ScriptException;
}
