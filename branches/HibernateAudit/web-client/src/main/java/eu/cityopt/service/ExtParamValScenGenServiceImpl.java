package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.ExtParamValScenGen;
import eu.cityopt.repository.ExtParamValScenGenRepository;

@Service("ExtParamValScenGenService")
public class ExtParamValScenGenServiceImpl implements ExtParamValScenGenService {
	
	@Autowired
	private ExtParamValScenGenRepository extParamValScenGenRepository;
	
	public List<ExtParamValScenGen> findAll() {
		return extParamValScenGenRepository.findAll();
	}

	@Transactional
	public ExtParamValScenGen save(ExtParamValScenGen u) {
		return extParamValScenGenRepository.save(u);
	}

	@Transactional
	public void delete(ExtParamValScenGen u) throws EntityNotFoundException {
		
		if(extParamValScenGenRepository.findOne(u.getId()) == null) {
			throw new EntityNotFoundException();
		}
		
		extParamValScenGenRepository.delete(u);
	}
	
	@Transactional
	public ExtParamValScenGen update(ExtParamValScenGen toUpdate) throws EntityNotFoundException {
		
		if(extParamValScenGenRepository.findOne(toUpdate.getId()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public ExtParamValScenGen findByID(Integer id) {
		return extParamValScenGenRepository.findOne(id);
	}
	
}