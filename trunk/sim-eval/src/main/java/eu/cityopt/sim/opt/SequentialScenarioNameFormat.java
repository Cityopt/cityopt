package eu.cityopt.sim.opt;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.SimulationInput;

/**
 * Formats scenario names of the form prefix-N where N is a sequence number,
 * and descriptions containing decision variable values.
 *
 * @author Hannu Rummukainen
 */
public class SequentialScenarioNameFormat implements ScenarioNameFormat {
    private final String prefix;
    private final Collection<DecisionVariable> variableOrder;
    private AtomicInteger sequenceNumber = new AtomicInteger();

    public SequentialScenarioNameFormat(
            String generatorRunName, Collection<DecisionVariable> variableOrder) {
        this.prefix = generatorRunName;
        this.variableOrder = variableOrder;
    }

    @Override
    public String[] format(DecisionValues decisions, SimulationInput input) {
        return format(decisions);
    }

    @Override
    public String[] format(DecisionValues decisions) {
        String[] result = new String[2];
        result[0] = prefix + "-" + sequenceNumber.incrementAndGet();
        result[1] = decisions.formatString(variableOrder);
        return result;
    }
}
