package eu.cityopt.sim.eval;

import javax.script.Bindings;

/**
 * Associates names with values for evaluating expressions.
 * 
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public interface EvaluationContext {
    Bindings toBindings();

    BindingLayer getBindingLayer();
}
