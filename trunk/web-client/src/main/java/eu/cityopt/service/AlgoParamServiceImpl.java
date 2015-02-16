package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.AlgoParam;
import eu.cityopt.repository.AlgoParamRepository;

@Service("AlgoParamService")
public class AlgoParamServiceImpl implements AlgoParamService{
	
	@Autowired
	private AlgoParamRepository algoParamRepository;
	
	public List<AlgoParam> findAll() {
		return algoParamRepository.findAll();
	}

	@Transactional
	public AlgoParam save(AlgoParam u) {
		return algoParamRepository.save(u);
	}
	
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(algoParamRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		algoParamRepository.delete(id);
	}
	
	@Transactional
	public AlgoParam update(AlgoParam toUpdate) throws EntityNotFoundException {
		
		if(algoParamRepository.findOne(toUpdate.getAparamsid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public AlgoParam findByID(int id) {
		return algoParamRepository.findOne(id);
	}
	
}
