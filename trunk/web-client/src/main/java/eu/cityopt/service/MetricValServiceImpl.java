package eu.cityopt.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.MetricValDTO;
import eu.cityopt.model.MetricVal;
import eu.cityopt.repository.MetricRepository;
import eu.cityopt.repository.MetricValRepository;

@Service("MetricValService")
public class MetricValServiceImpl implements MetricValService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private MetricRepository metricRepository;
	
	@Autowired
	private MetricValRepository metricValRepository;
	
	@Override
	public List<MetricValDTO> findAll() {
		return modelMapper.map(metricValRepository.findAll(), 
				new TypeToken<List<MetricValDTO>>() {}.getType());
	}

	@Override
	public void delete(int id) throws EntityNotFoundException {
		
		if(metricRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		metricValRepository.delete(id);		
	}

	@Override
	public MetricValDTO save(MetricValDTO u, int metId) {
		MetricVal metric = modelMapper.map(u, MetricVal.class);
		metric.setMetric(metricRepository.findOne(metId));
		metric = metricValRepository.save(metric);
		return modelMapper.map(metric, MetricValDTO.class);		
	}

	@Override
	public MetricValDTO update(MetricValDTO toUpdate, int metId) throws EntityNotFoundException{

		if(metricRepository.findOne(toUpdate.getMetricvalid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate, metId);
	}

	@Override
	public MetricValDTO findByID(int id) throws EntityNotFoundException {
		if(metricRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(metricValRepository.findOne(id), MetricValDTO.class);
	}

}
