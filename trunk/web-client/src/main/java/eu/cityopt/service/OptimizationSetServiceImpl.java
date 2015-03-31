package eu.cityopt.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.repository.OptSearchConstRepository;
import eu.cityopt.repository.OptimizationSetRepository;

@Service("OptimizationSetService")
public class OptimizationSetServiceImpl implements OptimizationSetService {
	
	@Autowired
	private OptimizationSetRepository optimizationSetRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private OptSearchConstRepository optSearchConstRepository;
	
	@Transactional(readOnly=true)
	@Override
	public List<OptimizationSetDTO> findAll() {
		return modelMapper.map(optimizationSetRepository.findAll(), 
				new TypeToken<List<OptimizationSetDTO>>() {}.getType());
	}

	@Transactional
	@Override
	public OptimizationSetDTO save(OptimizationSetDTO u) {
		OptimizationSet os = modelMapper.map(u, OptimizationSet.class);
//		os.getProject().setName("prj name before merge");
//		os = em.merge(os);
//		os.getProject().setName("prj name after merge");
		os = optimizationSetRepository.save(os);
		return modelMapper.map(os, OptimizationSetDTO.class);
	}

	@Transactional
	@Override
	public void delete(int id) throws EntityNotFoundException {
		
		if(optimizationSetRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		optimizationSetRepository.delete(id);
	}
	
	@Transactional
	@Override
	public OptimizationSetDTO update(OptimizationSetDTO toUpdate) throws EntityNotFoundException {
		
		if(optimizationSetRepository.findOne(toUpdate.getOptid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	@Override
	public OptimizationSetDTO findByID(int id) throws EntityNotFoundException {
		OptimizationSet os = optimizationSetRepository.findOne(id);
		
		if(os == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(os, OptimizationSetDTO.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<OptConstraintDTO> getSearchConstraints(int optimizationSetId) throws EntityNotFoundException {
		OptimizationSet os = optimizationSetRepository.findOne(optimizationSetId);
		
		if(os == null) {
			throw new EntityNotFoundException();
		}
		
		List<OptConstraint> osList = optSearchConstRepository.findOptConstraintsforOptSet(os.getOptid());
		
		return modelMapper.map(osList, new TypeToken<List<OptConstraintDTO>>() {}.getType());
	}
	
}
