package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.ScenarioMetrics;
import eu.cityopt.repository.ScenarioMetricsRepository;

@Service("ScenarioMetricsService")
@Transactional
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
	public void delete(int id) throws EntityNotFoundException {
		
		if(scenarioMetricsRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		scenarioMetricsRepository.delete(id);
	}
	
	@Transactional
	public ScenarioMetrics update(ScenarioMetrics toUpdate) throws EntityNotFoundException {
		
		if(scenarioMetricsRepository.findOne(toUpdate.getScenmetricid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public ScenarioMetrics findByID(int id) {
		return scenarioMetricsRepository.findOne(id);
	}
	
}
