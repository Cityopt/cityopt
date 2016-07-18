package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.SimulationResultDTO;
import eu.cityopt.model.OutputVariable;

public interface OutputVariableService extends CityOptService<OutputVariableDTO> {
	
	OutputVariableDTO save(OutputVariableDTO u);
	
	OutputVariableDTO update(OutputVariableDTO toUpdate) throws EntityNotFoundException;
	
	OutputVariableDTO findByID(int id) throws EntityNotFoundException;

	Set<SimulationResultDTO> getSimulationResults(int id) throws EntityNotFoundException;
}