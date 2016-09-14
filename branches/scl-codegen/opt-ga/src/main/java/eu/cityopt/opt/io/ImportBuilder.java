package eu.cityopt.opt.io;

import java.text.ParseException;

import javax.script.ScriptException;

/**
 * A builder interface for {@link JacksonBinder}.
 * Implementations make something from a JacksonBinder and are usually
 * called via {@link JacksonBinder#buildWith}, which just loops over the
 * items and calls {@link #add}.  Builders typically extend
 * {@link AbstractBuilder}.
 * @author ttekth
 */
public interface ImportBuilder {
    /**
     * Add a new item.  For future extensibility most implementers
     * should ignore items of unknown Kind.
     * @throws ParseException if a value does not parse according to its type
     * @throws ScriptException if an expression does not compile.
     */
    void add(JacksonBinder.Item item) throws ParseException, ScriptException;
}
