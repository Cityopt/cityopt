package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.Metric;
import eu.cityopt.repository.MetricRepository;

@Service("MetricService")
public class MetricServiceImpl implements MetricService {
	
	@Autowired
	private MetricRepository metricRepository;
	
	public List<Metric> findAll() {
		return metricRepository.findAll();
	}

	@Transactional
	public Metric save(Metric u) {
		return metricRepository.save(u);
	}

	@Transactional
	public void delete(Metric u) throws EntityNotFoundException {
		
		if(metricRepository.findOne(u.getMetid()) == null) {
			throw new EntityNotFoundException();
		}
		
		metricRepository.delete(u);
	}
	
	@Transactional
	public Metric update(Metric toUpdate) throws EntityNotFoundException {
		
		if(metricRepository.findOne(toUpdate.getMetid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public Metric findByID(Integer id) {
		return metricRepository.findOne(id);
	}
	
}
