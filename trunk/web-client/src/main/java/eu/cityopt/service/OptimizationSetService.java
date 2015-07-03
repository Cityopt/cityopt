package eu.cityopt.service;

import java.util.List;

import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.OptimizationSetDTO;

public interface OptimizationSetService extends CityOptService<OptimizationSetDTO>{

	OptimizationSetDTO update(OptimizationSetDTO toUpdate)
			throws EntityNotFoundException;

	OptimizationSetDTO save(OptimizationSetDTO u);
	
	OptimizationSetDTO findByName(String name);

	List<OptConstraintDTO> getOptConstraints(int optimizationSetId)
			throws EntityNotFoundException;

	/** 
	 * adds an OptConstraint to the optimization set. OptConstraint is created, if not existing
	 * @param optSetId
	 * @param ocDTO
	 * @return
	 * @throws EntityNotFoundException
	 */
	OptConstraintDTO addOptConstraint(int optSetId, OptConstraintDTO ocDTO) 
			throws EntityNotFoundException;
	
	/**
	 * Removes one OptConstraint from an OptimizationSet
	 * @param optSetId
	 * @param OptConstraintId
	 * @throws EntityNotFoundException
	 */
	void removeOptConstraint(int optSetId, int OptConstraintId) 
			throws EntityNotFoundException;

	
	
	
}