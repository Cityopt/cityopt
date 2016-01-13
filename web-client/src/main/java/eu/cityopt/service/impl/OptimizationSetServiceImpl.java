package eu.cityopt.service.impl;

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
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.OptSearchConst;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.Scenario;
import eu.cityopt.repository.OptConstraintRepository;
import eu.cityopt.repository.OptSearchConstRepository;
import eu.cityopt.repository.OptimizationSetRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.OptimizationSetService;

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
	
	@Autowired
	private OptConstraintRepository optConstraintRepository;
	
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
	
	@Override
	@Transactional(readOnly = true)
	public List<OptimizationSetDTO> findByName(String name) {
		List<OptimizationSet> os = optimizationSetRepository.findByName(name);
		if(os != null)
			return modelMapper.map(os, new TypeToken<List<OptimizationSetDTO>>() {}.getType());
		return null;
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<OptConstraintDTO> getOptConstraints(int optimizationSetId) throws EntityNotFoundException {
		OptimizationSet os = optimizationSetRepository.findOne(optimizationSetId);
		
		if(os == null) {
			throw new EntityNotFoundException();
		}
		
		List<OptConstraint> osList = optSearchConstRepository.findOptConstraintsforOptSet(os.getOptid());
		
		return modelMapper.map(osList, new TypeToken<List<OptConstraintDTO>>() {}.getType());
	}
	
	@Transactional
	@Override
	public OptConstraintDTO addOptConstraint(int optSetId, OptConstraintDTO ocDTO) 
			throws EntityNotFoundException {
		
		OptimizationSet optSet = optimizationSetRepository.findOne(optSetId);
		if(optSet == null) {
			throw new EntityNotFoundException();
		}
		
		OptConstraint oc = modelMapper.map(ocDTO, OptConstraint.class);
		oc = optConstraintRepository.save(oc);
		OptSearchConst osc = new OptSearchConst();
		osc.setOptconstraint(oc);
		osc.setOptimizationset(optSet);
		optSet.getOptsearchconsts().add(osc);
		
		optSearchConstRepository.save(osc);
		optimizationSetRepository.save(optSet);
		
		return modelMapper.map(oc, OptConstraintDTO.class);
	}
	
	@Transactional
	@Override
	public void removeOptConstraint(int optSetId, int optConstraintId) 
			throws EntityNotFoundException {
		
		OptSearchConst optSearchConst = optSearchConstRepository.findByOptIdAndOptConstId(optSetId, optConstraintId);
		if(optSearchConst == null) {
			throw new EntityNotFoundException();
		}
		
		optSearchConstRepository.delete(optSearchConst);
	}

	@Transactional(readOnly=true)
	@Override
	public OptimizationSetDTO findByName(String name, int prjid)  {
		OptimizationSet os = optimizationSetRepository.findByNameAndProject_prjid(name,prjid);
		
		if(os == null) {
			return null;
		}
		
		return modelMapper.map(os, OptimizationSetDTO.class);
	}
	
}
