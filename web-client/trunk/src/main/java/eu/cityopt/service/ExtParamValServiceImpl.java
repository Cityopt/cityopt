package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.ExtParamVal;
import eu.cityopt.repository.ExtParamValRepository;

@Service("ExtParamValService")
public class ExtParamValServiceImpl implements ExtParamValService {
	
	@Autowired
	private ExtParamValRepository extParamValRepository;
	
	public List<ExtParamVal> findAll() {
		return extParamValRepository.findAll();
	}

	@Transactional
	public ExtParamVal save(ExtParamVal u) {
		return extParamValRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(extParamValRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		extParamValRepository.delete(id);
	}
	
	@Transactional
	public ExtParamVal update(ExtParamVal toUpdate) throws EntityNotFoundException {
		
		if(extParamValRepository.findOne(toUpdate.getExtparamvalid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public ExtParamVal findByID(int id) {
		return extParamValRepository.findOne(id);
	}
	
}
