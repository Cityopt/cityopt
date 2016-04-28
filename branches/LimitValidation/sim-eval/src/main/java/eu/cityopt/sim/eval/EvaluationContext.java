package eu.cityopt.sim.eval;

import javax.script.Bindings;

/**
 * Associates names with values for evaluating expressions.
 * 
 * @author Hannu Rummukainen
 */
public interface EvaluationContext {
    /**
     * Converts the context to a set of bindings for evaluation. These are
     * top-level bindings, with component names bound to special scripting
     * language objects that contain component-level names as attributes.
     */
    Bindings toBindings();

    EvaluationSetup getEvaluationSetup();

    BindingLayer getBindingLayer();
}
