package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.ModelParameter;
import eu.cityopt.repository.ModelParameterRepository;

@Service("ModelParameterService")
public class ModelParameterServiceImpl implements ModelParameterService {
	
	@Autowired
	private ModelParameterRepository modelParameterRepository;
	
	@Transactional(readOnly=true)
	public List<ModelParameter> findAll() {
		return modelParameterRepository.findAll();
	}

	@Transactional
	public ModelParameter save(ModelParameter u) {
		return modelParameterRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(modelParameterRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		modelParameterRepository.delete(id);
	}
	
	@Transactional
	public ModelParameter update(ModelParameter toUpdate) throws EntityNotFoundException {
		
		if(modelParameterRepository.findOne(toUpdate.getModelparamid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	public ModelParameter findByID(int id) {
		return modelParameterRepository.findOne(id);
	}
	
}
