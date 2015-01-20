package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.Algorithm;
import eu.cityopt.repository.AlgorithmRepository;

@Service("AlgorithmService")
public class AlgorithmServiceImpl implements AlgorithmService{
	
	@Autowired
	private AlgorithmRepository algorithmRepository;
	
	public List<Algorithm> findAll() {
		return algorithmRepository.findAll();
	}

	@Transactional
	public Algorithm save(Algorithm u) {
		return algorithmRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException{
		
		if(algorithmRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		algorithmRepository.delete(id);
	}
	
	@Transactional
	public Algorithm update(Algorithm toUpdate) throws EntityNotFoundException {
		
		if(algorithmRepository.findOne(toUpdate.getAlgorithmid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public Algorithm findByID(int id) {
		return algorithmRepository.findOne(id);
	}
	
}
