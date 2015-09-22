package eu.cityopt.sim.opt;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.ObjectiveStatus;
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

	@Override
	public String extendDescription(String initialDescription,
			ConstraintStatus constraints, ObjectiveStatus objectives) {
		StringBuilder sb = new StringBuilder(initialDescription);
		sb.append("; CONSTRAINTS: ");
		if (constraints != null) {
			sb.append(constraints.toString());
		} else {
			sb.append("N/A");
		}
		sb.append("; OBJECTIVES: ");
		if (objectives != null) {
			sb.append(objectives.toString());
		} else {
			sb.append("N/A");
		}
		return sb.toString();
	}
}
