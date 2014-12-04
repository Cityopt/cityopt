package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.DataReliability;
import eu.cityopt.repository.DataReliabilityRepository;

@Service("DataReliabilityService")
public class DataReliabilityServiceImpl implements DataReliabilityService {
	
	@Autowired
	private DataReliabilityRepository dataReliabilityRepository;
	
	public List<DataReliability> findAll() {
		return dataReliabilityRepository.findAll();
	}

	@Transactional
	public DataReliability save(DataReliability u) {
		return dataReliabilityRepository.save(u);
	}

	@Transactional
	public void delete(DataReliability u) throws EntityNotFoundException {
		
		if(dataReliabilityRepository.findOne(u.getDatarelid()) == null) {
			throw new EntityNotFoundException();
		}
		
		dataReliabilityRepository.delete(u);
	}
	
	@Transactional
	public DataReliability update(DataReliability toUpdate) throws EntityNotFoundException {
		
		if(dataReliabilityRepository.findOne(toUpdate.getDatarelid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public DataReliability findByID(Integer id) {
		return dataReliabilityRepository.findOne(id);
	}
	
}
