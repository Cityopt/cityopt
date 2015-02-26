package eu.cityopt.service;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.stereotype.Service;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ProjectScenariosDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.repository.ProjectRepository;

//@Service
public interface ProjectService extends CityOptService<ProjectDTO> {

	ProjectDTO save(ProjectDTO projectDTO);

	List<ProjectScenariosDTO> findAllWithScenarios();

	void deleteAll();

	void delete(int id) throws EntityNotFoundException;

	ProjectDTO update(ProjectDTO toUpdate) throws EntityNotFoundException;

	Set<ScenarioDTO> getScenarios(int prjid);

	void setScenarios(int prjid, Set<ScenarioDTO> scenarios);

	List<ComponentDTO> getComponents(int prjid);
	
	Set<ExtParamDTO> getExtParams(int prjid);
	
	Set<ExtParamValDTO> getExtParamVals(int prjid);
	
	Set<MetricDTO> getMetrics(int prjid);
	
	List<ProjectDTO> findByName(String name);

}
