package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.Project;
import eu.cityopt.repository.ProjectRepository;

@Service("projectService")
public class ProjectServiceImpl implements ProjectService{

	@Autowired
	private ProjectRepository projectRepository;
	
	public ProjectRepository getProjectRepository() {
		return projectRepository;
	}

	public void setProjectRepository(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}

	public List<Project> findAll() {
		return projectRepository.findAll();
	}

	@Transactional
	public Project save(Project project) {
		return projectRepository.save(project);
	}

	@Transactional
	public void deleteAll() {
		projectRepository.deleteAll();
	}
	
	@Transactional
	public void delete(Project p) throws EntityNotFoundException {
		
		if(projectRepository.findOne(p.getPrjid()) == null) {
			throw new EntityNotFoundException();
		}
		
		projectRepository.delete(p);
	}
	
	@Transactional
	public Project update(Project toUpdate) throws EntityNotFoundException {
		
		if(projectRepository.findOne(toUpdate.getPrjid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public Project findByID(Integer id) {
		return projectRepository.findOne(id);
	}
}
