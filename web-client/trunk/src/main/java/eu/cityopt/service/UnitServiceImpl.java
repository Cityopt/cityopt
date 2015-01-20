package eu.cityopt.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.model.Unit;
import eu.cityopt.repository.UnitRepository;

@Service("UnitService")
public class UnitServiceImpl implements UnitService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UnitRepository unitRepository;
	
	public List<UnitDTO> findAll() {
		return modelMapper.map(unitRepository.findAll(), 
				new TypeToken<List<UnitDTO>>() {}.getType());
	}

	@Transactional
	public UnitDTO save(UnitDTO u) {
		Unit unit = modelMapper.map(u, Unit.class);
		unit = unitRepository.save(unit);
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
	
	public UnitDTO findByID(int id) throws EntityNotFoundException {
		
		if(unitRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(unitRepository.findOne(id), UnitDTO.class);
	}
	
}
