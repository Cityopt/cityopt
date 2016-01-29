package eu.cityopt.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.OpenOptimizationSetDTO;
import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ProjectScenariosDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.SimulationModelDTO;
import eu.cityopt.repository.ProjectRepository;

//@Service
public interface ProjectService extends CityOptService<ProjectDTO> {

	public enum SortBy{
		name, prjid, description
	}
	
	ProjectDTO save(ProjectDTO projectDTO, Integer simulationModelId, Integer extParamValSetId);

	List<ProjectScenariosDTO> findAllWithScenarios();
	
	List<ProjectDTO> findAll(SortBy col);

	void deleteAll();

	void delete(int id) throws EntityNotFoundException;

	/**
	 * @param toUpdate
	 * @param simulationModelId
	 * @param extParamValSetId
	 * @return
	 * @throws EntityNotFoundException
	 */
	ProjectDTO update(ProjectDTO toUpdate, Integer simulationModelId, Integer extParamValSetId) throws EntityNotFoundException;
	
	Integer getSimulationmodelId(int prjid);
	
	Integer getDefaultExtParamSetId(int prjid);

	Set<ScenarioDTO> getScenarios(int prjid);

	void setScenarios(int prjid, Set<ScenarioDTO> scenarios);

	List<ComponentDTO> getComponents(int prjid);
	
	Page<ComponentDTO> getComponents(int prjid,int pageIndex);
	
	Set<ExtParamDTO> getExtParams(int prjid);
	
	Set<ExtParamValDTO> getExtParamVals(int prjid);
	
	List<ExtParamValSetDTO> getExtParamValSets(int prjid);
	
	Set<MetricDTO> getMetrics(int prjid);
	
	List<ProjectDTO> findByNameContaining(String name);
	
	ProjectDTO findByName(String name);

	Set<OptimizationSetDTO> getSearchOptimizationSets(int prjid) throws EntityNotFoundException;

	Set<ObjectiveFunctionDTO> getObjectiveFunctions(int prjid)
			throws EntityNotFoundException;

	Set<OptConstraintDTO> getOptConstraints(int prjid) throws EntityNotFoundException;

	Set<OpenOptimizationSetDTO> getSearchAndGAOptimizationSets(int prjid)
			throws EntityNotFoundException;
}
