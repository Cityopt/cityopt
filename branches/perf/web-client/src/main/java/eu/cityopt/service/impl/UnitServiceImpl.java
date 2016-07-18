package eu.cityopt.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.Type;
import eu.cityopt.model.Unit;
import eu.cityopt.repository.UnitRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.UnitService;

@Service("UnitService")
@Transactional
public class UnitServiceImpl implements UnitService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UnitRepository unitRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional(readOnly=true)
	public List<UnitDTO> findAll() {
		return modelMapper.map(unitRepository.findAll(), 
				new TypeToken<List<UnitDTO>>() {}.getType());
	}

	@Transactional
	public UnitDTO save(UnitDTO u) {
		Unit unit = modelMapper.map(u, Unit.class);
		unit = em.merge(unit);
		return modelMapper.map(unit, UnitDTO.class);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(unitRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		unitRepository.delete(id);
	}
	
	@Transactional
	public UnitDTO update(UnitDTO toUpdate) throws EntityNotFoundException {
		
		if(unitRepository.findOne(toUpdate.getUnitid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	public UnitDTO findByID(int id) throws EntityNotFoundException {
		
		if(unitRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(unitRepository.findOne(id), UnitDTO.class);
	}

	@Override
	public UnitDTO findByName(String name) throws EntityNotFoundException {
		if(unitRepository.findByName(name)==null)
		{
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(unitRepository.findByName(name), UnitDTO.class);
	}
	
}
