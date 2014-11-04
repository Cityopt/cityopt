package eu.cityopt.sim.eval;

import javax.script.Bindings;
import javax.script.ScriptException;

/**
 * Associates names with values for evaluating expressions.
 * 
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public interface EvaluationContext {
    /**
     * Converts the context to a set of bindings for evaluation. These are
     * top-level bindings, with component names bound to special scripting
     * language objects that contain component-level names as attributes.
     *
     * @throws ScriptException
     */
    Bindings toBindings() throws ScriptException;

    BindingLayer getBindingLayer();
}
