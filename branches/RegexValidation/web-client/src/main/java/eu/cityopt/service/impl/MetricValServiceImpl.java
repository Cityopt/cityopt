package eu.cityopt.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.MetricValDTO;
import eu.cityopt.model.MetricVal;
import eu.cityopt.repository.MetricRepository;
import eu.cityopt.repository.MetricValRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.MetricValService;

@Service("MetricValService")
public class MetricValServiceImpl implements MetricValService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private MetricRepository metricRepository;
	
	@Autowired
	private MetricValRepository metricValRepository;
	
	@Override
	@Transactional(readOnly=true)
	public List<MetricValDTO> findAll() {
		return modelMapper.map(metricValRepository.findAll(), 
				new TypeToken<List<MetricValDTO>>() {}.getType());
	}

	@Override
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(metricRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		metricValRepository.delete(id);		
	}

	@Override
	@Transactional
	public MetricValDTO save(MetricValDTO u, int metId) {
		MetricVal metric = modelMapper.map(u, MetricVal.class);
		metric.setMetric(metricRepository.findOne(metId));
		metric = metricValRepository.save(metric);
		return modelMapper.map(metric, MetricValDTO.class);		
	}

	@Override
	@Transactional
	public MetricValDTO update(MetricValDTO toUpdate, int metId) throws EntityNotFoundException{

		if(metricRepository.findOne(toUpdate.getMetricvalid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate, metId);
	}

	@Override
	@Transactional(readOnly=true)
	public MetricValDTO findByID(int id) throws EntityNotFoundException {
		if(metricRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(metricValRepository.findOne(id), MetricValDTO.class);
	}

}
