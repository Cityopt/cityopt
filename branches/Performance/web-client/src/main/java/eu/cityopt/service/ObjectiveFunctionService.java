package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.ObjectiveFunctionResultDTO;
import eu.cityopt.DTO.OptimizationSetDTO;

public interface ObjectiveFunctionService extends CityOptService<ObjectiveFunctionDTO> {

	ObjectiveFunctionDTO save(ObjectiveFunctionDTO u);
	
	ObjectiveFunctionDTO findByName(int prjID,String name) throws EntityNotFoundException;
	
	ObjectiveFunctionDTO findByNameAndScenGen(int scenGenID,String name);
	
	
	Boolean existsByName(int prjID,String name);

	ObjectiveFunctionDTO update(ObjectiveFunctionDTO toUpdate)
			throws EntityNotFoundException;

	Set<OptimizationSetDTO> getOptimizationSets(int objectiveFunctionId)
			throws EntityNotFoundException;
	
	List<ObjectiveFunctionResultDTO> findResultsByScenarioGenerator(int scenGenID, int objectiveFunctionId);

}