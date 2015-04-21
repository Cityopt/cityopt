package eu.cityopt.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.AlgoParamDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.model.Algorithm;
import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.OptSearchConst;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.ScenGenObjectiveFunction;
import eu.cityopt.model.ScenGenOptConstraint;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.repository.ObjectiveFunctionRepository;
import eu.cityopt.repository.OptConstraintRepository;
import eu.cityopt.repository.ScenGenObjectiveFunctionRepository;
import eu.cityopt.repository.ScenGenOptConstraintRepository;
import eu.cityopt.repository.ScenarioGeneratorRepository;

@Service
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
	public OptConstraintDTO addSearchConstraint(int scenGenId, OptConstraintDTO ocDTO) 
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
}
