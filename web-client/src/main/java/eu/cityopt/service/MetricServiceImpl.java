package eu.cityopt.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.model.Metric;
import eu.cityopt.model.Project;
import eu.cityopt.repository.MetricRepository;
import eu.cityopt.repository.ProjectRepository;

@Service("MetricService")
public class MetricServiceImpl implements MetricService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private MetricRepository metricRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	public List<MetricDTO> findAll() {
		return modelMapper.map(metricRepository.findAll(), 
				new TypeToken<List<MetricDTO>>() {}.getType());
	}

	@Transactional
	public MetricDTO save(MetricDTO u) {
		Metric metric = modelMapper.map(u, Metric.class);
		metric = metricRepository.save(metric);
		return modelMapper.map(metric, MetricDTO.class);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(metricRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		metricRepository.delete(id);
	}
	
	@Transactional
	public MetricDTO update(MetricDTO toUpdate) throws EntityNotFoundException {
		
		if(metricRepository.findOne(toUpdate.getMetid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public MetricDTO findByID(int id) throws EntityNotFoundException {
		
		if(metricRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(metricRepository.findOne(id), MetricDTO.class);
	}
	
	public void setProject(int metId, int prjid){
		Metric met = metricRepository.findOne(metId);
		Project p = projectRepository.findOne(prjid);
		met.setProject(p);
		metricRepository.save(met);
	}
	
}
