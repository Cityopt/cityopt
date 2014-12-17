package eu.cityopt.sim.eval;

import javax.script.ScriptException;

/**
 * Specifies the value of a simulation input variable on the basis of
 * optimization decision variables.  This is only used in scenario
 * generation optimization.
 * 
 * @author Hannu Rummukainen
 */
public class InputExpression extends DoubleExpression {
    private final String componentName;
    private final String inputName;

    /**
     * Constructs an instance associated with a specific input variable.
     * 
     * @param componentName component of the input variable
     * @param inputName name of the input variable
     * @param source the expression specifying the input variable value
     * @param evaluator to be used when computing the value
     */
    InputExpression(String componentName, String inputName,
            String source, Evaluator evaluator) throws ScriptException {
        super(source, evaluator);
        this.componentName = componentName;
        this.inputName = inputName;
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
    InputExpression(String componentName, String inputName,
            Evaluator evaluator) throws ScriptException {
        this(componentName, inputName, componentName + "." + inputName,
                evaluator);
    }

    public String getComponentName() {
        return componentName;
    }

    public String getInputName() {
        return inputName;
    }
}
