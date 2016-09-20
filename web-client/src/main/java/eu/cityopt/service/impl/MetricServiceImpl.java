package eu.cityopt.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.MetricValDTO;
import eu.cityopt.model.Metric;
import eu.cityopt.model.MetricVal;
import eu.cityopt.model.Project;
import eu.cityopt.repository.MetricRepository;
import eu.cityopt.repository.MetricValRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.MetricService;

@Service("MetricService")
public class MetricServiceImpl implements MetricService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private MetricRepository metricRepository;
	
	@Autowired
	private MetricValRepository metricValRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Transactional(readOnly = true)
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
	
	@Transactional(readOnly = true)
	public MetricDTO findByID(int id) throws EntityNotFoundException {
		
		if(metricRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(metricRepository.findOne(id), MetricDTO.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<MetricValDTO> getMetricVals(int metricId) throws EntityNotFoundException {
		Metric m = metricRepository.findOne(metricId);
		
		if(m == null) {
			throw new EntityNotFoundException();
		}		
		
		//m.getMetricvals() == null ? new HashSet<MetricValDTO>() :
		return modelMapper.map(m.getMetricvals(), 
				new TypeToken<Set<MetricValDTO>>() {}.getType());
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<MetricValDTO> getMetricValsByEParamSet(int metricId, int epvsId)
			throws EntityNotFoundException {
		List<MetricVal> res = metricValRepository.findByMetricAndEParamSet(metricId, epvsId);
		
		if(res == null) {
			throw new EntityNotFoundException();
		}		
		
		//m.getMetricvals() == null ? new HashSet<MetricValDTO>() :
		return modelMapper.map(res, 
				new TypeToken<List<MetricValDTO>>() {}.getType());
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<MetricValDTO> getMetricVals(int metricId, int scenId) throws EntityNotFoundException {
		List<MetricVal> res = metricValRepository.findByMetricAndScen(metricId, scenId);
		
		if(res == null) {
			throw new EntityNotFoundException();
		}		
		
		//m.getMetricvals() == null ? new HashSet<MetricValDTO>() :
		return modelMapper.map(res, 
				new TypeToken<List<MetricValDTO>>() {}.getType());
	}
	
    @Override
    @Transactional(readOnly = true)
    public List<MetricValDTO> getMetricValsByProject(int projectId) throws EntityNotFoundException {
        List<MetricVal> res = metricValRepository.findByProject(projectId);
        if(res == null) {
            throw new EntityNotFoundException();
        }       
        return modelMapper.map(res, 
                new TypeToken<List<MetricValDTO>>() {}.getType());
    }
    
	@Transactional
	public void setProject(int metId, int prjid){
		Metric met = metricRepository.findOne(metId);
		Project p = projectRepository.findOne(prjid);
		met.setProject(p);
		metricRepository.save(met);
	}

	
	
}
