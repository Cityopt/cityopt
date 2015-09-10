package eu.cityopt.service.impl;

import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.SimulationResultDTO;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.repository.OutputVariableRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.OutputVariableService;

@Service("OutputVariableService")
public class OutputVariableServiceImpl implements OutputVariableService {
	
	@Autowired
	private OutputVariableRepository outputVariableRepository;
	
	@Autowired 
	private ModelMapper modelMapper;
	
	@Transactional(readOnly=true)
	public List<OutputVariableDTO> findAll() {
		return modelMapper.map(outputVariableRepository.findAll(), 
				new TypeToken<List<OutputVariableDTO>>() {}.getType());
	}

	@Transactional
	public OutputVariableDTO save(OutputVariableDTO outVar) {
		OutputVariable outVarModel = modelMapper.map(outVar, OutputVariable.class);
		outVarModel = outputVariableRepository.save(outVarModel);
		return modelMapper.map(outVarModel, OutputVariableDTO.class);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(outputVariableRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		outputVariableRepository.delete(id);
	}
	
	@Transactional
	public OutputVariableDTO update(OutputVariableDTO toUpdate) throws EntityNotFoundException {

		if(outputVariableRepository.findOne(toUpdate.getOutvarid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	public OutputVariableDTO findByID(int id) throws EntityNotFoundException {
		OutputVariable outVarModel = outputVariableRepository.findOne(id);
		if(outputVariableRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(outVarModel, OutputVariableDTO.class);
	}

	@Override
	@Transactional(readOnly=true)
	public Set<SimulationResultDTO> getSimulationResults(int id) throws EntityNotFoundException {
		
		OutputVariable outVar = outputVariableRepository.findOne(id);
		Set<SimulationResult> simRes = outVar.getSimulationresults();
		return modelMapper.map(simRes, new TypeToken<Set<SimulationResultDTO>>() {}.getType());
	}
	
}
