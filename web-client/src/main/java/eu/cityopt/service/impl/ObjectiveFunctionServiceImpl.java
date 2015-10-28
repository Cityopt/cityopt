package eu.cityopt.service.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ObjResultsToObjResultsDTOMap;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.ObjectiveFunctionResultDTO;
import eu.cityopt.DTO.OptSetToOpenOptimizationSetDTOMap;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.ScenarioGeneratorToOpenOptimizationSetDTOMap;
import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.ObjectiveFunctionResult;
import eu.cityopt.repository.ObjectiveFunctionRepository;
import eu.cityopt.repository.ObjectiveFunctionResultRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ObjectiveFunctionService;

@Service("ObjectiveFunctionService")
public class ObjectiveFunctionServiceImpl implements ObjectiveFunctionService {
	
	@Autowired
	private ObjectiveFunctionRepository objectiveFunctionRepository;
	
	@Autowired
	private ObjectiveFunctionResultRepository objectiveFunctionResultRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	public ObjectiveFunctionServiceImpl(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
		modelMapper.addMappings(new ObjResultsToObjResultsDTOMap());
		
	}
	
	
	@Transactional(readOnly=true)
	@Override
	public List<ObjectiveFunctionDTO> findAll() {
		return modelMapper.map(objectiveFunctionRepository.findAll(), 
				new TypeToken<List<ObjectiveFunctionDTO>>() {}.getType());
	}

	@Transactional
	@Override
	public ObjectiveFunctionDTO save(ObjectiveFunctionDTO u) {
		ObjectiveFunction of = modelMapper.map(u, ObjectiveFunction.class);
		of = em.merge(of);
		return modelMapper.map(of, ObjectiveFunctionDTO.class);
	}

	@Transactional
	@Override
	public void delete(int id) throws EntityNotFoundException {
		
		if(objectiveFunctionRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		objectiveFunctionRepository.delete(id);
	}
	
	@Transactional
	@Override
	public ObjectiveFunctionDTO update(ObjectiveFunctionDTO toUpdate) throws EntityNotFoundException {
		
		if(objectiveFunctionRepository.findOne(toUpdate.getObtfunctionid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	@Override
	public ObjectiveFunctionDTO findByID(int id) throws EntityNotFoundException {
		ObjectiveFunction of = objectiveFunctionRepository.findOne(id);
		
		if(of == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(of, ObjectiveFunctionDTO.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public Set<OptimizationSetDTO> getOptimizationSets(int objectiveFunctionId) throws EntityNotFoundException {
		ObjectiveFunction of = objectiveFunctionRepository.findOne(objectiveFunctionId);
		
		if(of == null) {
			throw new EntityNotFoundException();
		}

		return modelMapper.map(of.getOptimizationsets(), 
				new TypeToken<Set<OptimizationSetDTO>>(){}.getType());
	}

	@Transactional(readOnly=true)
	@Override
	public ObjectiveFunctionDTO findByName(int prjID, String name) throws EntityNotFoundException {
		ObjectiveFunction of = objectiveFunctionRepository.findByName(prjID, name);
		if(of == null) {
			throw new EntityNotFoundException();
		}

		return modelMapper.map(of, ObjectiveFunctionDTO.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public Boolean existsByName(int prjID, String name)  {
		ObjectiveFunction of = objectiveFunctionRepository.findByName(prjID, name);
		if(of == null) {
			return false;
		}

		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectiveFunctionResultDTO> findResultsByScenarioGenerator(int scenGenID,
			int objectiveFunctionId) {
		
		List<ObjectiveFunctionResult> results = objectiveFunctionResultRepository.findByScenGenAndObjFunction(scenGenID, objectiveFunctionId);
		if(results == null) {
			return null;
		}

		return modelMapper.map(results, 
				new TypeToken<List<ObjectiveFunctionResultDTO>>(){}.getType());
	}
	
}
