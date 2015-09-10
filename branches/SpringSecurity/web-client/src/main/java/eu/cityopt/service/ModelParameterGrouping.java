package eu.cityopt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import eu.cityopt.DTO.DecisionVariableDTO;
import eu.cityopt.DTO.ModelParameterDTO;
import eu.cityopt.sim.eval.Type;

public class ModelParameterGrouping {
	public class MultiValue {
		/// Comma-separated list of values, e.g. "1.0, 5, 15"
		@Getter String values;
		// Group name
		@Getter String groupName;
	}

	public class Group {
		@Getter DecisionVariableDTO variable;

		/// InputParameter ids of parameters in the group
		@Getter List<Integer> parameters = new ArrayList<>();

		public Group(DecisionVariableDTO variable) {
			this.variable = variable;
		}
	}

	/// InputParameter ids of parameters defined by a single constant value
	@Getter Set<Integer> constantValued = new HashSet<>();

	/// InputParameter ids of parameters defined by a general expression
	/// (i.e. not by a list of values with a group index)
	@Getter Set<Integer> expressionValued = new HashSet<>();

	/// InputParameter ids of parameters used as decision variables
	/// (i.e. their expression is simply a decision variable of the same name)
	@Getter Set<Integer> decisionValued = new HashSet<>();

	/// Map from InputParameter id to multiple defined values
	@Getter Map<Integer, MultiValue> multiValued = new HashMap<>();

	/**
	 *  Map from group name to group data.
	 *  Includes groups with no associated parameters.
	 */
	@Getter Map<String, Group> groupsByName = new HashMap<>();

	public ModelParameterGrouping(List<ModelParameterDTO> modelParameters,
			List<DecisionVariableDTO> decisionVariables) {
		for (DecisionVariableDTO variable : decisionVariables) {
			if (variable.getName() != null && variable.getInputparameter() == null
					&& variable.getType() != null
					&& variable.getType().getName().equalsIgnoreCase(Type.INTEGER.name)
					&& StringUtils.equals(variable.getLowerbound().trim(), "0")
					&& !StringUtils.isBlank(variable.getUpperbound())) {
				groupsByName.put(variable.getName(), new Group(variable));
			}
		}
		for (ModelParameterDTO mp : modelParameters) {
			int inputId = mp.getInputparameter().getInputid();
			if (mp.getValue() == null && mp.getExpression() != null) {
				if (mp.getExpression().trim().equals(
						mp.getInputparameter().getQualifiedName())) {
					decisionValued.add(inputId);
				} else {
					MultiValue multivalue = parseMultiValueExpr(mp.getExpression(), inputId);
					if (multivalue != null) {
						multiValued.put(inputId, multivalue);
					} else {
						expressionValued.add(inputId);
					}
				}
			} else {
				constantValued.add(inputId);
			}
		}
	}

	private Pattern groupIndexingPattern = Pattern.compile(
			"^\\s*\\[\\s*(.+)\\s*\\]\\s*\\[\\s*(\\w+)\\s*\\]\\s*$");

	private MultiValue parseMultiValueExpr(String expression, int inputId) {
		Matcher matcher = groupIndexingPattern.matcher(expression);
		if (matcher.matches()) {
			String groupName = matcher.group(2);
			Group group = groupsByName.get(groupName);
			if (group != null) {
				MultiValue multivalue = new MultiValue();
				// TODO: Could parse list of integers, or list of doubles properly...
				multivalue.values = matcher.group(1);
				multivalue.groupName = groupName;
				group.parameters.add(inputId);
				return multivalue;
			}
		}
		return null;
	}

	public String makeMultiValueExpr(String commaSeparatedValues, String groupName) {
		return "[" + commaSeparatedValues + "][" + groupName + "]";
	}
}
