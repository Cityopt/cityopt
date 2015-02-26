package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.OptimizationSet;
import eu.cityopt.repository.OptimizationSetRepository;

@Service("OptimizationSetService")
public class OptimizationSetServiceImpl implements OptimizationSetService {
	
	@Autowired
	private OptimizationSetRepository optimizationSetRepository;
	
	@Transactional(readOnly=true)
	public List<OptimizationSet> findAll() {
		return optimizationSetRepository.findAll();
	}

	@Transactional
	public OptimizationSet save(OptimizationSet u) {
		return optimizationSetRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(optimizationSetRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		optimizationSetRepository.delete(id);
	}
	
	@Transactional
	public OptimizationSet update(OptimizationSet toUpdate) throws EntityNotFoundException {
		
		if(optimizationSetRepository.findOne(toUpdate.getOptid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	public OptimizationSet findByID(int id) {
		return optimizationSetRepository.findOne(id);
	}
	
}
