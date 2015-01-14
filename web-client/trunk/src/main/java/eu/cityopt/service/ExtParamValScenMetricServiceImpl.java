package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.ExtParamValScenMetric;
import eu.cityopt.repository.ExtParamValScenMetricRepository;

@Service("ExtParamValScenMetricService")
public class ExtParamValScenMetricServiceImpl implements ExtParamValScenMetricService {
	
	@Autowired
	private ExtParamValScenMetricRepository extParamValScenMetricRepository;
	
	public List<ExtParamValScenMetric> findAll() {
		return extParamValScenMetricRepository.findAll();
	}

	@Transactional
	public ExtParamValScenMetric save(ExtParamValScenMetric u) {
		return extParamValScenMetricRepository.save(u);
	}

	@Transactional
	public void delete(Integer id) throws EntityNotFoundException {
		
		if(extParamValScenMetricRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		extParamValScenMetricRepository.delete(id);
	}
	
	@Transactional
	public ExtParamValScenMetric update(ExtParamValScenMetric toUpdate) throws EntityNotFoundException {
		
		if(extParamValScenMetricRepository.findOne(toUpdate.getId()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public ExtParamValScenMetric findByID(Integer id) {
		return extParamValScenMetricRepository.findOne(id);
	}
	
}
