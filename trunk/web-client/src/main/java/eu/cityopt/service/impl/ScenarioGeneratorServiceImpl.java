package eu.cityopt.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.AlgoParamDTO;
import eu.cityopt.DTO.AlgoParamValDTO;
import eu.cityopt.DTO.DecisionVariableDTO;
import eu.cityopt.DTO.ModelParameterDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.DTO.ScenarioGeneratorSimpleDTO;
import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.model.AlgoParam;
import eu.cityopt.model.AlgoParamVal;
import eu.cityopt.model.Algorithm;
import eu.cityopt.model.Component;
import eu.cityopt.model.DecisionVariable;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.ModelParameter;
import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.ScenGenObjectiveFunction;
import eu.cityopt.model.ScenGenOptConstraint;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.repository.AlgoParamRepository;
import eu.cityopt.repository.AlgoParamValRepository;
import eu.cityopt.repository.AlgorithmRepository;
import eu.cityopt.repository.DecisionVariableRepository;
import eu.cityopt.repository.InputParameterRepository;
import eu.cityopt.repository.ModelParameterRepository;
import eu.cityopt.repository.ObjectiveFunctionRepository;
import eu.cityopt.repository.OptConstraintRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenGenObjectiveFunctionRepository;
import eu.cityopt.repository.ScenGenOptConstraintRepository;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.repository.TypeRepository;
import eu.cityopt.service.CopyService;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ModelParameterGrouping;
import eu.cityopt.service.ScenarioGeneratorService;
import eu.cityopt.service.TypeService;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.service.SimulationService;

@Service
@SuppressWarnings("serial")
public class ScenarioGeneratorServiceImpl implements ScenarioGeneratorService {
	
	@Autowired
	private ScenarioGeneratorRepository scenarioGeneratorRepository;
	
	@Autowired
	private ScenGenObjectiveFunctionRepository scenGenObjectiveFunctionRepository;
	
	@Autowired
	private ObjectiveFunctionRepository objectiveFunctionRepository;
	
	@Autowired
	private OptConstraintRepository optConstraintRepository;
	
	@Autowired
	private ScenGenOptConstraintRepository scenGenOptConstraintRepository;
	
	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private AlgorithmRepository algorithmRepository;

	@Autowired
	private AlgoParamRepository algoParamRepository;

	@Autowired
	private AlgoParamValRepository algoParamValRepository;

	@Autowired
	private DecisionVariableRepository decisionVariableRepository;

	@Autowired
	private ModelParameterRepository modelParameterRepository;

	@Autowired
	private InputParameterRepository inputParameterRepository;

	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private CopyService copyService;

	@Autowired
	private ModelMapper modelMapper;
	
	
	@Transactional(readOnly=true)
	@Override
	public List<ScenarioGeneratorDTO> findAll() {
		return modelMapper.map(scenarioGeneratorRepository.findAll(), 
				new TypeToken<List<ScenarioGeneratorDTO>>() {}.getType());
	}

	@Transactional
	@Override
	public ScenarioGeneratorDTO save(ScenarioGeneratorDTO u) {
		ScenarioGenerator sg = modelMapper.map(u, ScenarioGenerator.class);
		sg.setAlgorithm(algorithmRepository.findOne(u.getAlgorithm().getAlgorithmid()));
		sg = scenarioGeneratorRepository.save(sg);
		return modelMapper.map(sg, ScenarioGeneratorDTO.class);
	}

	@Transactional
	@Override
	public void delete(int id) throws EntityNotFoundException {
		
		if(scenarioGeneratorRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		scenarioGeneratorRepository.delete(id);
	}
	
	@Transactional
	@Override
	public ScenarioGeneratorDTO update(ScenarioGeneratorDTO toUpdate) throws EntityNotFoundException {
		
		if(scenarioGeneratorRepository.findOne(toUpdate.getScengenid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional
	@Override
	public ScenarioGeneratorDTO update(int scenGenId, String name, Integer algorithmId) throws EntityNotFoundException {
		ScenarioGenerator sg =  scenarioGeneratorRepository.findOne(scenGenId);
		if (sg == null) {
			throw new EntityNotFoundException();
		}
		if (name != null) {
			sg.setName(name);
		}
		if (algorithmId != null) {
			sg.setAlgorithm(algorithmRepository.findOne(algorithmId));
		}
		sg = scenarioGeneratorRepository.save(sg);
		return modelMapper.map(sg, ScenarioGeneratorDTO.class);
	}

	@Transactional(readOnly=true)
	@Override
	public ScenarioGeneratorDTO findByID(int id) throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(id);
		
		if(sg == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(sg, ScenarioGeneratorDTO.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<AlgoParamDTO> getAlgoParams(int scenGenId) throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		
		if(sg == null) {
			throw new EntityNotFoundException();
		}
		
		Algorithm a = sg.getAlgorithm();
		if(a == null)
			return new ArrayList<AlgoParamDTO>();
		
		return modelMapper.map(a.getAlgoparams(), 
				new TypeToken<List<AlgoParamDTO>>() {}.getType());
	}

	@Transactional(readOnly=true)
	@Override
	public List<AlgoParamValDTO> getAlgoParamVals(int scenGenId) throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		if (sg == null || sg.getAlgorithm() == null) {
			throw new EntityNotFoundException();
		}
		List<AlgoParamVal> relevantValues = sg.getAlgoparamvals().stream().filter(
				v -> (v.getAlgoparam().getAlgorithm().getAlgorithmid() == sg.getAlgorithm().getAlgorithmid()))
				.collect(Collectors.toList());
		return sortByParamId(modelMapper.map(relevantValues,
				new TypeToken<List<AlgoParamValDTO>>() {}.getType()));
	}

	@Transactional
	@Override
	public List<AlgoParamValDTO> getOrCreateAlgoParamVals(int scenGenId) throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		if (sg == null || sg.getAlgorithm() == null) {
			throw new EntityNotFoundException();
		}
		List<AlgoParamVal> newVals = getOrCreateAlgoParamVals(sg);
		if ( ! newVals.isEmpty()) {
			algoParamValRepository.save(newVals);
			scenarioGeneratorRepository.save(sg);
		}
		return getAlgoParamVals(scenGenId);
	}

	private List<AlgoParamValDTO> sortByParamId(List<AlgoParamValDTO> vals) {
		return vals.stream().sorted(
				(a, b) -> Integer.compare(a.getAlgoparam().getAparamsid(), b.getAlgoparam().getAparamsid()))
				.collect(Collectors.toList());
	}

	private List<AlgoParamVal> getOrCreateAlgoParamVals(ScenarioGenerator sg) {
		Map<Integer, AlgoParam> algoParams = new HashMap<>();
		for (AlgoParam ap : sg.getAlgorithm().getAlgoparams()) {
			algoParams.put(ap.getAparamsid(), ap);
		}
		for (AlgoParamVal apv : sg.getAlgoparamvals()) {
			algoParams.remove(apv.getAlgoparam().getAparamsid());
		}
		List<AlgoParamVal> newVals = new ArrayList<>();
		for (AlgoParam algoParam : algoParams.values()) {
			AlgoParamVal apv = new AlgoParamVal();
			apv.setAlgoparam(algoParam);
			apv.setValue(algoParam.getDefaultvalue());
			apv.setScenariogenerator(sg);
			sg.getAlgoparamvals().add(apv);
			newVals.add(apv);
		}
		return newVals;
	}

	@Transactional
	@Override
	public void setAlgoParamVals(int scenGenId, Map<Integer, String> valueByParamId) throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		if (sg == null || sg.getAlgorithm() == null) {
			throw new EntityNotFoundException();
		}
		for (AlgoParamVal apv : sg.getAlgoparamvals()) {
			int paramId = apv.getAlgoparam().getAparamsid();
			if (valueByParamId.containsKey(paramId)) {
				apv.setValue(valueByParamId.remove(paramId));
			}
		}
		for (Map.Entry<Integer, String> entry : valueByParamId.entrySet()) {
			AlgoParam algoParam = algoParamRepository.findOne(entry.getKey());
			AlgoParamVal apv = new AlgoParamVal();
			apv.setAlgoparam(algoParam);
			apv.setValue(entry.getValue());
			apv.setScenariogenerator(sg);
			sg.getAlgoparamvals().add(apv);
		}
		algoParamValRepository.save(sg.getAlgoparamvals());
		scenarioGeneratorRepository.save(sg);
	}

	@Transactional(readOnly=true)
	@Override
	public List<ModelParameterDTO> getModelParameters(int scenGenId)
			throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		
		if(sg == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(sg.getModelparameters(), 
				new TypeToken<List<ModelParameterDTO>>() {}.getType());
	}

	@Transactional
	@Override
	public void setModelParameters(int scenGenId, List<ModelParameterDTO> modelParams)
			throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		if (sg == null) {
			throw new EntityNotFoundException();
		}
		Map<Integer, ModelParameterDTO> dtoByInputId = new HashMap<>();
		for (ModelParameterDTO dto : modelParams) {
			dtoByInputId.put(dto.getInputparameter().getInputid(), dto);
		}
		Set<ModelParameter> newSet = new HashSet<>();
		for (ModelParameter mp : sg.getModelparameters()) {
			int inputId = mp.getInputparameter().getInputid();
			ModelParameterDTO dto = dtoByInputId.remove(inputId);
			if (dto != null) {
				mp.setValue(dto.getValue());
				mp.setExpression(dto.getExpression());
				newSet.add(mp);
			} else {
				modelParameterRepository.delete(mp);
			}
		}
		for (ModelParameterDTO dto : dtoByInputId.values()) {
			InputParameter input = inputParameterRepository.findOne(dto.getInputparameter().getInputid());
			if (input != null) {
				ModelParameter mp = new ModelParameter();
				mp.setInputparameter(input);
				mp.setValue(dto.getValue());
				mp.setExpression(dto.getExpression());
				mp.setScenariogenerator(sg);
				newSet.add(mp);
			}
		}
		sg.setModelparameters(newSet);
		modelParameterRepository.save(newSet);
		scenarioGeneratorRepository.save(sg);
	}

	@Transactional
	@Override
	public ModelParameterGrouping getModelParameterGrouping(int scenGenId) throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		if (sg == null) {
			throw new EntityNotFoundException();
		}
		List<ModelParameterDTO> modelParameters = getModelParameters(scenGenId);
		List<DecisionVariableDTO> decisionVariables = getDecisionVariables(scenGenId);
		TypeDTO integerType = appContext.getBean(TypeService.class).findByName("Integer");
		EvaluationSetup setup = appContext.getBean(SimulationService.class).getDummyEvaluationSetup();
		ScenarioGeneratorSimpleDTO sgDTO =  modelMapper.map(sg, ScenarioGeneratorSimpleDTO.class);
		return new ModelParameterGrouping(modelParameters, decisionVariables,
				sgDTO, integerType, setup);
	}

	@Transactional
	@Override
	public void setModelParameterGrouping(int scenGenId,
			ModelParameterGrouping grouping) throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		if (sg == null) {
			throw new EntityNotFoundException();
		}
		Map<Integer, ModelParameterDTO> modelParameterMap = new HashMap<>();
		for (ModelParameterDTO mp : getModelParameters(scenGenId)) {
			modelParameterMap.put(mp.getInputparameter().getInputid(), mp);
		}
		grouping.copyModelParametersTo(modelParameterMap, null);
		setModelParameters(scenGenId, new ArrayList<>(modelParameterMap.values()));

		updateDecisionVariables(scenGenId, grouping);
	}

	@Transactional
	@Override
	public void updateDecisionVariables(int scenGenId,
			ModelParameterGrouping grouping) throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		if (sg == null) {
			throw new EntityNotFoundException();
		}

		Map<Integer, DecisionVariableDTO> decisionVariableMap = new HashMap<>();
		for (DecisionVariableDTO dv : getDecisionVariables(scenGenId)) {
			decisionVariableMap.put(dv.getDecisionvarid(), dv);
		}
		grouping.copyDecisionVariablesTo(decisionVariableMap, null);
		setDecisionVariables(scenGenId, new ArrayList<>(decisionVariableMap.values()));
	}


	@Transactional
	@Override
	public OptConstraintDTO addOptConstraint(int scenGenId, OptConstraintDTO ocDTO) 
			throws EntityNotFoundException {
		
		ScenarioGenerator scenGen = scenarioGeneratorRepository.findOne(scenGenId);
		if(scenGen == null) {
			throw new EntityNotFoundException();
		}
		
		OptConstraint oc = modelMapper.map(ocDTO, OptConstraint.class);
		oc = optConstraintRepository.save(oc);
		ScenGenOptConstraint scgoc = new ScenGenOptConstraint();
		scgoc.setOptconstraint(oc);
		scgoc.setScenariogenerator(scenGen);;
		scenGen.getScengenoptconstraints().add(scgoc);
		
		scenGenOptConstraintRepository.save(scgoc);
		scenarioGeneratorRepository.save(scenGen);
		
		return modelMapper.map(oc, OptConstraintDTO.class);
	}
	
	@Transactional
	@Override
	public void removeOptConstraint(int scenGenId, int optConstraintId) 
			throws EntityNotFoundException {
		
		ScenGenOptConstraint scenGenOptConst = scenGenOptConstraintRepository.findByScenGenIdAndOptConstId(scenGenId, optConstraintId);
		if(scenGenOptConst == null) {
			throw new EntityNotFoundException();
		}
		
		scenGenOptConstraintRepository.delete(scenGenOptConst);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<OptConstraintDTO> getOptConstraints(int scenGenId) throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		
		if(sg == null) {
			throw new EntityNotFoundException();
		}
		
		List<OptConstraint> osList = scenGenOptConstraintRepository.findOptConstraintsforScenGen(sg.getScengenid());
		
		return modelMapper.map(osList, new TypeToken<List<OptConstraintDTO>>() {}.getType());

	}

	@Transactional(readOnly=true)
	@Override
	public List<ObjectiveFunctionDTO> getObjectiveFunctions(int scenGenId)
			throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		
		if(sg == null) {
			throw new EntityNotFoundException();
		}
		
		List<ObjectiveFunction> ofList = scenGenObjectiveFunctionRepository.findObjectiveFunctionsforScenGen(sg.getScengenid());
		
		return modelMapper.map(ofList, new TypeToken<List<ObjectiveFunctionDTO>>() {}.getType());
	}
	
	@Transactional
	@Override
	public ObjectiveFunctionDTO addObjectiveFunction(int scenGenId, ObjectiveFunctionDTO obtFuncDTO) 
			throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		if(sg == null) {
			throw new EntityNotFoundException();
		}
		ObjectiveFunction obtFunc = modelMapper.map(obtFuncDTO, ObjectiveFunction.class);
		obtFunc = objectiveFunctionRepository.save(obtFunc);
		ScenGenObjectiveFunction sgof = new ScenGenObjectiveFunction();
		sgof.setObjectivefunction(obtFunc);
		sgof.setScenariogenerator(sg);
		sgof = scenGenObjectiveFunctionRepository.save(sgof);
		obtFunc.getScengenobjectivefunctions().add(sgof);
		sg.getScengenobjectivefunctions().add(sgof);
		
		return modelMapper.map(obtFunc, ObjectiveFunctionDTO.class);
	}

	@Transactional
	@Override
	public void removeObjectiveFunction(int scenGenId, int objectiveFunctionId)
			throws EntityNotFoundException {
		ScenGenObjectiveFunction scenGenObjectiveFunction = scenGenObjectiveFunctionRepository.findByScenGenIdAndOptFunctionId(scenGenId, objectiveFunctionId);
		
		if(scenGenObjectiveFunction == null) {
			throw new EntityNotFoundException();
		}
		
		scenGenObjectiveFunctionRepository.delete(scenGenObjectiveFunction);
	}

	@Transactional(readOnly = true)
	@Override
	public List<DecisionVariableDTO> getDecisionVariables(int scenGenId) throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		if (sg == null) {
			throw new EntityNotFoundException();
		}
		return modelMapper.map(sg.getDecisionvariables(),
				new TypeToken<List<DecisionVariableDTO>>() {}.getType());
	}

	@Transactional
	@Override
	public DecisionVariableDTO addDecisionVariable(int scenGenId,
			DecisionVariableDTO decVarDTO) throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		if (sg == null || decVarDTO.getType() == null) {
			throw new EntityNotFoundException();
		}
		DecisionVariable dv = modelMapper.map(decVarDTO, DecisionVariable.class);
		dv.setType(typeRepository.findOne(decVarDTO.getType().getTypeid()));
		dv.setScenariogenerator(sg);
		sg.getDecisionvariables().add(dv);
		dv = decisionVariableRepository.save(dv);
		scenarioGeneratorRepository.save(sg);

		return modelMapper.map(dv, DecisionVariableDTO.class);
	}

	@Transactional
	@Override
	public void removeDecisionVariable(int scenGenId, int decVarId) throws EntityNotFoundException {
		DecisionVariable dv = decisionVariableRepository.findOne(decVarId);
		if (dv.getScenariogenerator().getScengenid() != scenGenId) {
			throw new EntityNotFoundException();
		}
		decisionVariableRepository.delete(dv);
	}

	@Transactional
	@Override
	public void setDecisionVariables(int scenGenId, List<DecisionVariableDTO> variables)
			throws EntityNotFoundException {
		ScenarioGenerator sg = scenarioGeneratorRepository.findOne(scenGenId);
		if (sg == null) {
			throw new EntityNotFoundException();
		}
		Map<Integer, DecisionVariableDTO> dtoByVarId = new HashMap<>();
		for (DecisionVariableDTO dto : variables) {
			dtoByVarId.put(dto.getDecisionvarid(), dto);
		}
		Set<DecisionVariable> newSet = new HashSet<>();
		for (DecisionVariable dv : sg.getDecisionvariables()) {
			int varId = dv.getDecisionvarid();
			DecisionVariableDTO dto = dtoByVarId.remove(varId);
			if (dto != null) {
				dv.setName(dto.getName());
				dv.setLowerbound(dto.getLowerbound());
				dv.setUpperbound(dto.getUpperbound());
				dv.setType((dto.getType() != null)
						? typeRepository.findOne(dto.getType().getTypeid())
						: null);
				dv.setInputparameter((dto.getInputparameter() != null)
						? inputParameterRepository.findOne(dto.getInputparameter().getInputid())
						: null);
				newSet.add(dv);
			} else {
				decisionVariableRepository.delete(dv);
			}
		}
		for (DecisionVariableDTO dto : dtoByVarId.values()) {
			DecisionVariable dv = new DecisionVariable();
			dv.setName(dto.getName());
			dv.setLowerbound(dto.getLowerbound());
			dv.setUpperbound(dto.getUpperbound());
			dv.setType((dto.getType() != null)
					? typeRepository.findOne(dto.getType().getTypeid())
					: null);
			dv.setInputparameter((dto.getInputparameter() != null)
					? inputParameterRepository.findOne(dto.getInputparameter().getInputid())
					: null);
			dv.setScenariogenerator(sg);
			newSet.add(dv);
		}
		sg.setDecisionvariables(newSet);
		decisionVariableRepository.save(newSet);
		scenarioGeneratorRepository.save(sg);
	}

	@Transactional
	@Override
	public ScenarioGeneratorDTO create(int projectId, String name) {
		ScenarioGenerator sg = new ScenarioGenerator();
		sg.setProject(projectRepository.findOne(projectId));
		sg.setAlgorithm(findDefaultAlgorithm());
		ExtParamValSet epvs = sg.getProject().getDefaultextparamvalset();
		if (epvs != null) {
			sg.setExtparamvalset(copyService.copyExtParamValSet(epvs, epvs.getName() + "(" + name +")"));
		} else {
			sg.setExtparamvalset(null);
		}
		sg.setName(name);
		sg = scenarioGeneratorRepository.save(sg);
		List<AlgoParamVal> newAPV = getOrCreateAlgoParamVals(sg);
		initModelParameters(sg);
		algoParamValRepository.save(newAPV);
		return modelMapper.map(sg, ScenarioGeneratorDTO.class);
	}

	private void initModelParameters(ScenarioGenerator sg) {
		sg.getModelparameters().clear();
		for (Component comp : sg.getProject().getComponents()) {
			for (InputParameter input : comp.getInputparameters()) {
				ModelParameter mp = new ModelParameter();
				mp.setInputparameter(input);
				mp.setValue(input.getDefaultvalue());
				mp.setScenariogenerator(sg);
				sg.getModelparameters().add(mp);
			}
		}
		modelParameterRepository.save(sg.getModelparameters());
	}

	private Algorithm findDefaultAlgorithm() {
		//TODO might want to make the default algorithm configurable
		final String DEFAULT_ALGORITHM = "genetic algorithm";
		Algorithm last = null;
		for (Algorithm algorithm : algorithmRepository.findAll()) {
			if (DEFAULT_ALGORITHM.equals(algorithm.getDescription())) {
				return algorithm;
			}
			last = algorithm;
		}
		return last;
	}
}
