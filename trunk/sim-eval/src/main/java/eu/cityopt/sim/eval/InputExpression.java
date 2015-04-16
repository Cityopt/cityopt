package eu.cityopt.sim.eval;

import javax.script.ScriptException;

/**
 * Specifies the value of a simulation input variable on the basis of
 * optimization decision variables.  This is only used in scenario
 * generation optimization.
 * 
 * @author Hannu Rummukainen
 */
public class InputExpression extends Expression {
    Symbol input;

    /**
     * Constructs an instance associated with a specific input variable.
     * 
     * @param componentName component of the input variable
     * @param inputName name of the input variable
     * @param source the expression specifying the input variable value
     * @param evaluator to be used when computing the value
     */
    public InputExpression(String componentName, String inputName,
            String source, Evaluator evaluator) throws ScriptException {
        super(source, componentName + "." + inputName, evaluator);
        this.input = new Symbol(componentName, inputName);
    }

    /**
     * Constructs an instance associated with a specific input variable,
     * equating the input variable with a decision variable of the same name.
     * The expression is defined as <code>componentName.inputName</code>
     * where componentName and inputName are the corresponding argument values.
     * 
     * @param componentName component of the input variable
     * @param inputName name of the input variable
     * @param evaluator to be used when computing the value
     */
    public InputExpression(String componentName, String inputName,
            Evaluator evaluator) throws ScriptException {
        this(componentName, inputName, componentName + "." + inputName,
                evaluator);
    }

    public Symbol getInput() {
        return input;
    }

    @Override
    protected String kind() {
        return "input";
    }
}
