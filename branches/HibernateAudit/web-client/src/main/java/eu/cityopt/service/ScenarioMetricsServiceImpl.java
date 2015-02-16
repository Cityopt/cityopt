package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.ScenarioMetrics;
import eu.cityopt.repository.ScenarioMetricsRepository;

@Service("ScenarioMetricsService")
public class ScenarioMetricsServiceImpl implements ScenarioMetricsService {
	
	@Autowired
	private ScenarioMetricsRepository scenarioMetricsRepository;
	
	public List<ScenarioMetrics> findAll() {
		return scenarioMetricsRepository.findAll();
	}

	@Transactional
	public ScenarioMetrics save(ScenarioMetrics u) {
		return scenarioMetricsRepository.save(u);
	}

	@Transactional
	public void delete(ScenarioMetrics u) throws EntityNotFoundException {
		
		if(scenarioMetricsRepository.findOne(u.getScenmetricid()) == null) {
			throw new EntityNotFoundException();
		}
		
		scenarioMetricsRepository.delete(u);
	}
	
	@Transactional
	public ScenarioMetrics update(ScenarioMetrics toUpdate) throws EntityNotFoundException {
		
		if(scenarioMetricsRepository.findOne(toUpdate.getScenmetricid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public ScenarioMetrics findByID(Integer id) {
		return scenarioMetricsRepository.findOne(id);
	}
	
}
