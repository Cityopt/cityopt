package eu.cityopt.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

import org.apache.commons.lang.StringUtils;

import eu.cityopt.DTO.DecisionVariableDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.ModelParameterDTO;
import eu.cityopt.DTO.ScenarioGeneratorSimpleDTO;
import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.Type;

/**
 * Grouping and classification of model parameters, to support scenario
 * generator usage.  Instances are created by  
 * {@link ScenarioGeneratorService#getModelParameterGrouping(int)}
 * and changes are only saved by calling
 * {@link ScenarioGeneratorService#setModelParameterGrouping(int, ModelParameterGrouping)}.
 * <p>
 * This class keeps track of all model parameters, and those decision
 * variables that a) define a parameter group or b) are directly linked
 * to a decision-valued model parameter.  Any other decision variables
 * are ignored.
 * <p>
 * There are four kinds of model parameters in a scenario generator run:
 * <ul>
 *   <li>
 *     Constant-valued parameters always have the same value, stored as
 *     the 'value' in the ModelParameter table.  (If both a value and an
 *     expression is stored, then the expression is ignored.)
 *	 <li>
 *     Decision-valued parameters function as a decision variable.
 *     Technically, the name of the parameter is the same as the name of
 *     a decision variable (including the component name), and the
 *     'expression' in the ModelParameter table is also the parameter name.
 *   <li>
 *     Multi-valued parameters have multiple constant values, which are
 *     varied according to a decision variable.  Specifically, the expression
 *     is of the form "[1, 3, 5][G1]" where 1, 3 and 5 are the constant values
 *     and G1 is an integer-valued decision variable.  Such integer variables
 *     define <em>groups</em> of parameters.
 *   <li>
 *     Expression-valued parameters have an arbitrary expression in the
 *     ModelParameter table.  If the expression is not of the special forms
 *     described above, then the parameter is considered expression-valued.
 * </ul>
 *
 * @author Hannu Rummukainen
 */
public class ModelParameterGrouping {
	public class MultiValue {
		/// Comma-separated list of values, e.g. "1.0, 5, 15"
		@Getter String valueString;

		/// Number of values in the list
		@Getter int numberOfValues;

		// Group name
		@Getter Group group;
	}

	public class Group {
		@Getter DecisionVariableDTO variable;

		/// Name of the group.  Equivalent to name of the decision variable.
		public String getName() {
			return variable.getName();
		}

		/// Value count stored in decision variable.
		@Getter int storedNumberOfValues;

		/// Smallest number of values detected in parameters of the group,
		/// or 0 if there are no parameters in the group.
		public int getMinNumberOfValues() {
			int min = Integer.MAX_VALUE;
			for (MultiValue mv : members.values()) {
				min = Math.min(mv.numberOfValues, min);
			}
			return (members.isEmpty() ? 0 : min);
		}

		/// Largest number of values detected in parameters of the group,
		/// or 0 if there are no parameters in the group.
		public int getMaxNumberOfValues() {
			int max = 0;
			for (MultiValue mv : members.values()) {
				max = Math.max(mv.numberOfValues, max);
			}
			return max;
		}

		/// Map from InputParameter ids to parameter-specific information
		/// on members of this group.
		@Getter Map<Integer, MultiValue> members = new HashMap<>();

		public Group(DecisionVariableDTO variable, int storedNumberOfValues) {
			this.variable = variable;
			this.storedNumberOfValues = storedNumberOfValues;
		}

		String makeUpperBound() {
			return Integer.toString(Math.max(0, getMinNumberOfValues() - 1));
		}
	}

	private ScenarioGeneratorSimpleDTO scenGenDTO;
	private TypeDTO integerType;
	private EvaluationSetup evaluationSetup;

	private Set<String> reservedGroupNames = new HashSet<>();

	/** Map from InputParameter id to data. */
	@Getter Map<Integer, InputParameterDTO> inputParameters = new HashMap<>();

	/**
	 * Map from InputParameter id to single constant value, for parameters
	 * that have a constant value.
	 */
	@Getter Map<Integer, String> constantValued = new HashMap<>();

	/**
	 * Map from InputParameter id to a general expression, for parameters
	 * that are defined by an arbitrary expression.
	 */
	@Getter Map<Integer, String> expressionValued = new HashMap<>();

	/**
	 * Map from InputParameter id to decision variable data, for parameters
	 * that are directly linked to a decision variable of the same name.
	 */
	@Getter Map<Integer, DecisionVariableDTO> decisionValued = new HashMap<>();

	/**
	 * Map from InputParameter id to multiple defined values, for parameters
	 * whose expression links a list of values to a group variable.
	 */
	@Getter Map<Integer, MultiValue> multiValued = new HashMap<>();

	/**
	 *  Map from group name to group data.
	 *  Includes groups that have no associated parameters.
	 */
	@Getter Map<String, Group> groupsByName = new HashMap<>();

	/**
	 * To be called via ScenarioGeneratorService.
	 * @see ScenarioGeneratorService#getModelParameterGrouping(int)
	 */
	public ModelParameterGrouping(
			List<ModelParameterDTO> modelParameters,
			List<DecisionVariableDTO> decisionVariables,
			ScenarioGeneratorSimpleDTO scenGenDTO,
			TypeDTO integerType,
			EvaluationSetup evaluationSetup) {
		this.scenGenDTO = scenGenDTO;
		this.integerType = integerType;
		this.evaluationSetup = evaluationSetup;

		Map<Integer, DecisionVariableDTO> linkedVariables = new HashMap<>();
		for (DecisionVariableDTO variable : decisionVariables) {
			boolean reserveGroupName =
					groupNamePattern.matcher(variable.getName()).matches();
			if (variable.getInputparameter() != null) {
				int inputId = variable.getInputparameter().getInputid();
				linkedVariables.put(inputId, variable);
			} else {
				Group group = toGroup(variable);
				if (group != null) {
					groupsByName.put(variable.getName(), group);
					reserveGroupName = false;
				}
			}
			if (reserveGroupName) {
				reservedGroupNames.add(variable.getName());
			}
		}
		for (ModelParameterDTO mp : modelParameters) {
			int inputId = mp.getInputparameter().getInputid();
			this.inputParameters.put(inputId, mp.getInputparameter());
			if (mp.getValue() == null && mp.getExpression() != null) {
				String expr = mp.getExpression().trim();
				if (expr.equals(mp.getInputparameter().getQualifiedName())) {
					decisionValued.put(inputId, linkedVariables.remove(inputId));
				} else {
					MultiValue multivalue = parseMultiValueExpr(mp.getExpression(), inputId);
					if (multivalue != null) {
						multiValued.put(inputId, multivalue);
					} else {
						expressionValued.put(inputId, expr);
					}
				}
			} else {
				constantValued.put(inputId, mp.getValue());
			}
		}
	}

	private static final String groupNameRegex = "G[1-9][0-9]*";
	private static final Pattern groupNamePattern = Pattern.compile(groupNameRegex);
	private static final Pattern groupIndexingPattern = Pattern.compile(
			"^\\s*\\[\\s*(.+)\\s*\\]\\s*\\[\\s*("+groupNameRegex+")\\s*\\]\\s*$");

	private MultiValue parseMultiValueExpr(String expression, int inputId) {
		Matcher matcher = groupIndexingPattern.matcher(expression);
		if (matcher.matches()) {
			String groupName = matcher.group(2);
			Group group = groupsByName.get(groupName);
			if (group != null) {
				int count = 0;
				try {
					count = numberOfValuesInList(inputId, matcher.group(1));
				} catch (ParseException e) {
					// Invalid value list
					return null;
				}
				MultiValue multivalue = new MultiValue();
				multivalue.valueString = matcher.group(1);
				multivalue.numberOfValues = count;
				multivalue.group = group;
				group.members.put(inputId, multivalue);
				return multivalue;
			}
		}
		return null;
	}

	/**
	 * Finds an unused decision variable name of the form Gnnn where nnn is a
	 * decimal number. Avoids names of decision variables that were defined at
	 * the time this ModelParameterGrouping was constructed.
	 */
	String pickGroupName() {
		String name = null;
		int i = 0;
		do {
			++i;
			name = "G" + i;
		} while (reservedGroupNames.contains(name) || groupsByName.containsKey(name));
		return name;
	}

	/**
	 * Adds a new group. The group is initially empty.
	 * @see ScenarioGeneratorService
	 */
	public Group addGroup() {
		String name = pickGroupName();
		DecisionVariableDTO dv = new DecisionVariableDTO();
		dv.setName(name);
		dv.setLowerbound("0");
		dv.setUpperbound("0");
		dv.setType(integerType);
		dv.setInputparameter(null);
		dv.setScenariogenerator(scenGenDTO);
		return addGroup(dv);
	}

	public Group addGroup(DecisionVariableDTO variable) {
		Group group = toGroup(variable);
		if (group == null) {
			throw new IllegalArgumentException("Invalid group variable " + variable.getName());
		}
		groupsByName.put(variable.getName(), group);
		return group;
	}

	private Group toGroup(DecisionVariableDTO variable) {
		if (variable.getName() != null && variable.getInputparameter() == null
				&& variable.getType() != null
				&& groupNamePattern.matcher(variable.getName()).matches()
				&& variable.getType().getName().equalsIgnoreCase(Type.INTEGER.name)
				&& StringUtils.equals(variable.getLowerbound().trim(), "0")
				&& StringUtils.isNumeric(variable.getUpperbound().trim())) {
			try {
				int count = Integer.parseInt(variable.getUpperbound()) + 1;
				if (count > 0) {
					return new Group(variable, count); 
				}
			} catch (NumberFormatException e) {
				// Invalid upper bound.
			}
		}
		return null;
	}

	/**
	 * Finds erroneous groups where not all parameters have the same number of
	 * values. If the value lists are not corrected, then the minimum number of
	 * values in each group will be used, and any extra values are ignored.
	 * Returns an empty set if all groups are ok.
	 */ 
	public Set<Group> findMismatchingGroups() {
		Set<Group> mismatches = new HashSet<>();
		for (Group group : groupsByName.values()) {
			if (group.getMinNumberOfValues() != group.getMaxNumberOfValues()) {
				mismatches.add(group);
			}
		}
		return mismatches;
	}

	/**
	 * Deletes groups with no parameters in them. Should ONLY be used if all
	 * variables are known to be group variables (the multi-scenario use case).
	 * Otherwise user-defined variables with the appropriate name, type and
	 * lower bound can be confused with group variables and deleted.
	 */
	public void deleteEmptyGroups() {
		Iterator<Map.Entry<String, Group>> iter = groupsByName.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Group> entry = iter.next();
			if (entry.getValue().members.isEmpty()) {
				iter.remove();
			}
		}
	}

	/**
	 * Sets a model parameter to be multi-valued.
	 *
	 * @param inputId identifier of the corresponding InputParameter.
	 * @param valueList the values as a comma-delimited string, e.g. "1, 3, 5"
	 * @param groupName name of an existing group.
	 * @throws ParseException if valueList is not valid.  Not all possible errors
	 *   are detected.
	 */
	public void setMultiValue(int inputId, String valueList, String groupName)
			throws ParseException {
		String inputName = checkInput(inputId).getQualifiedName();
		int count = numberOfValuesInList(inputId, valueList);
		Group group = groupsByName.get(groupName);
		if (group == null) {
			throw new ParseException(inputName + ": No such group " + groupName, 0);
		}
		remove(inputId);

		MultiValue multivalue = new MultiValue();
		multivalue.valueString = valueList;
		multivalue.numberOfValues = count;
		multivalue.group = group;

		multiValued.put(inputId, multivalue);
		group.members.put(inputId, multivalue);
	}

	/**
	 * Sets a model parameter to be constant-valued.
	 * @param inputId identifies the InputParameter.
	 * @param value the constant value as a string
	 * @throws ParseException if the value is not valid for the parameter
	 */
	public void setConstantValue(int inputId, String value) throws ParseException {
		validateTypedValue(inputId, value);
		remove(inputId);
		constantValued.put(inputId, value);
	}

	/**
	 * Sets a model parameter to be decision-valued.
	 * @param inputId the InputParameter to be treated as a decision variable
	 * @param variable specifies the bounds and type of the decision variable
	 */
	public void setDecisionValued(
			int inputId, DecisionVariableDTO variable) {
    	InputParameterDTO inputDTO = checkInput(inputId);
		variable.setInputparameter(inputDTO);
		remove(inputId);
		decisionValued.put(inputId, variable);
	}

	/**
	 * Sets a model parameter to be expression-valued.
	 * @param inputId identifies the InputParameter
	 * @param expression the expression that determines the parameter value
	 * @throws ParseException if the expression is not valid
	 */
	public void setExpressionValue(int inputId, String expression) throws ParseException {
    	checkInput(inputId);
		constantValued.remove(inputId);
		expressionValued.put(inputId, expression);
		decisionValued.remove(inputId);
		multiValued.remove(inputId);
	}

	/**
	 * Sets the value or expression of a model parameter, based on whether the
	 * value is a constant or an expression.  Does not support multi-valued or
	 * decision-valued parameters, since setting them requires more information.
	 * @param inputId identifier of an InputParameter
	 * @param text either a constant value or an arbitrary expression.
	 * @throws ParseException if the value is not valid
	 */
	public void setFreeText(int inputId, String text) throws ParseException {
		try {
			validateTypedValue(inputId, text);
			// Validation succeeded. Make the parameter constant-valued.
			remove(inputId);
			constantValued.put(inputId, text);
		} catch (ParseException e) {
			// Not a constant.  Interpret as an expression.
	    	InputParameterDTO inputDTO = checkInput(inputId);
	    	if (inputDTO.getQualifiedName().equals(text.trim())
	    			&& decisionValued.containsKey(inputId)) {
	    		// The parameter remains decision-valued.
	    	} else {
	    		// Make the parameter expression-valued.
				remove(inputId);
	    		expressionValued.put(inputId, text);
	    	}
		}
	}

	/**
	 * Returns the value or expression of a parameter, or in case of a multi-valued
	 * parameter, the list of values as a string.
	 */
	public String getFreeText(int inputId) {
		InputParameterDTO inputDTO = checkInput(inputId);
		if (constantValued.containsKey(inputId)) {
			return constantValued.get(inputId);
		} else if (decisionValued.containsKey(inputId)) {
			return inputDTO.getQualifiedName();
		} else if (expressionValued.containsKey(inputId)) {
			return expressionValued.get(inputId);
		} else {
			MultiValue mv = multiValued.get(inputId);
			return mv.valueString;
		}
	}

	private InputParameterDTO checkInput(int inputId) {
		InputParameterDTO input = inputParameters.get(inputId);
		if (input == null) {
			throw new IllegalArgumentException("No such input parameter: inputId=" + inputId);
		}
		return input;
	}

	private void remove(int inputId) {
		constantValued.remove(inputId);
		expressionValued.remove(inputId);
		decisionValued.remove(inputId);
		MultiValue oldMultivalue = multiValued.remove(inputId);
		if (oldMultivalue != null) {
			oldMultivalue.group.members.remove(inputId);
		}
	}

    private void validateTypedValue(int inputId, String value) throws ParseException {
    	InputParameterDTO inputDTO = checkInput(inputId);
    	TypeDTO typeDTO = inputDTO.getType();
        Type simType = Type.getByName((typeDTO != null) ? typeDTO.getName() : null);
        try {
            simType.parse(value, evaluationSetup);
        } catch (ParseException e) {
            throw new ParseException(inputDTO.getQualifiedName() + ": " + e.getMessage(), 0);
        }
    }

	public List<DecisionVariableDTO> listManagedDecisionVariables() {
		List<DecisionVariableDTO> result = new ArrayList<>();
		result.addAll(decisionValued.values());
		for (Group group : groupsByName.values()) {
			DecisionVariableDTO var = group.getVariable();
			var.setUpperbound(group.makeUpperBound());
			result.add(var);
		}
		return result;
	}

	public List<ModelParameterDTO> listModelParameters(
			ScenarioGeneratorSimpleDTO scenGen) {
		Map<Integer, ModelParameterDTO> modelParameters = new HashMap<>();
		copyModelParametersTo(modelParameters, scenGen);
		return new ArrayList<>(modelParameters.values());
	}

	public boolean isManageable(DecisionVariableDTO variable) {
		return (variable.getInputparameter() != null
				|| toGroup(variable) != null);
	}

	/** To be called from ScenarioGeneratorService */
	public void copyDecisionVariablesTo(
			Map<Integer, DecisionVariableDTO> decisionVariables,
			ScenarioGeneratorSimpleDTO scenGen) {
		Map<Integer, DecisionVariableDTO> manageableVariables = new HashMap<>();
		Iterator<Map.Entry<Integer, DecisionVariableDTO>> iter = decisionVariables.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, DecisionVariableDTO> entry = iter.next();
			if (isManageable(entry.getValue())) {
				iter.remove();
				manageableVariables.put(entry.getKey(), entry.getValue());
			}
		}
		for (DecisionVariableDTO myVar : listManagedDecisionVariables()) {
			DecisionVariableDTO targetVar = manageableVariables.get(myVar.getDecisionvarid());
			if (targetVar == null) {
				myVar.setScenariogenerator(scenGen);
				targetVar = myVar;
			} else {
				targetVar.setDecisionvarid(myVar.getDecisionvarid());
				targetVar.setInputparameter(myVar.getInputparameter());
				targetVar.setLowerbound(myVar.getLowerbound());
				targetVar.setUpperbound(myVar.getUpperbound());
				targetVar.setType(myVar.getType());
				targetVar.setScenariogenerator(scenGen);
			}
			decisionVariables.put(targetVar.getDecisionvarid(), targetVar);
		}
	}

	/** To be called from ScenarioGeneratorService */
	public void copyModelParametersTo(
			Map<Integer, ModelParameterDTO> modelParameters,
			ScenarioGeneratorSimpleDTO scenGen) {
		for (Map.Entry<Integer, String> entry : constantValued.entrySet()) {
			int inputId = entry.getKey();
			ModelParameterDTO mp = modelParameters.computeIfAbsent(
					inputId, k -> new ModelParameterDTO());
			if (mp.getInputparameter() == null)
				mp.setInputparameter(inputParameters.get(inputId));
			mp.setScenariogenerator(scenGen);
			mp.setValue(entry.getValue());
			mp.setExpression(null);
		}
		for (Map.Entry<Integer, String> entry : expressionValued.entrySet()) {
			int inputId = entry.getKey();
			ModelParameterDTO mp = modelParameters.computeIfAbsent(
					inputId, k -> new ModelParameterDTO());
			if (mp.getInputparameter() == null)
				mp.setInputparameter(inputParameters.get(inputId));
			mp.setScenariogenerator(scenGen);
			mp.setValue(null);
			mp.setExpression(entry.getValue());
		}
		for (Map.Entry<Integer, DecisionVariableDTO> entry : decisionValued.entrySet()) {
			int inputId = entry.getKey();
			DecisionVariableDTO var = entry.getValue();
			ModelParameterDTO mp = modelParameters.computeIfAbsent(
					inputId, k -> new ModelParameterDTO());
			if (mp.getInputparameter() == null)
				mp.setInputparameter(var.getInputparameter());
			mp.setScenariogenerator(scenGen);
			mp.setValue(null);
			mp.setExpression(var.getInputparameter().getQualifiedName());
		}
		for (Map.Entry<Integer, MultiValue> entry : multiValued.entrySet()) {
			int inputId = entry.getKey();
			ModelParameterDTO mp = modelParameters.computeIfAbsent(
					inputId, k -> new ModelParameterDTO());
			if (mp.getInputparameter() == null)
				mp.setInputparameter(inputParameters.get(inputId));
			mp.setScenariogenerator(scenGen);
			mp.setValue(null);
			mp.setExpression(makeMultiValueExpr(entry.getValue()));
		}
	}

	/**
	 * Gets the number of values in a list expressed as a string of the
	 * form "1, 3, 5".
	 * @throws ParseException in case the argument is not a valid value list.
	 *    Not all possible errors are detected here.
	 */
	public int numberOfValuesInList(int inputId, String valueList) throws ParseException {
		InputParameterDTO input = checkInput(inputId);
		try {
			// TODO should parse according to the input parameter type...
			return Type.preparseListLength("[" + valueList + "]");
		} catch (ParseException e) {
			throw new ParseException(input.getQualifiedName() + ": " + e.getMessage(), 0);
		}
	}

	/**
	 * Combines a list of values and a group name into an expression
	 * that can be stored in the ModelParameter database table. 
	 */
	public String makeMultiValueExpr(String commaSeparatedValues, String groupName) {
		return "[" + commaSeparatedValues + "][" + groupName + "]";
	}

	String makeMultiValueExpr(MultiValue mv) {
		return makeMultiValueExpr(mv.getValueString(), mv.getGroup().getName());
	}
}
