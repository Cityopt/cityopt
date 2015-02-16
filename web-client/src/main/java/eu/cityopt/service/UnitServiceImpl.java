package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.Unit;
import eu.cityopt.repository.UnitRepository;

@Service("UnitService")
public class UnitServiceImpl implements UnitService {
	
	@Autowired
	private UnitRepository unitRepository;
	
	public List<Unit> findAll() {
		return unitRepository.findAll();
	}

	@Transactional
	public Unit save(Unit u) {
		return unitRepository.save(u);
	}

	@Transactional
	public void delete(Unit u) throws EntityNotFoundException {
		
		if(unitRepository.findOne(u.getUnitid()) == null) {
			throw new EntityNotFoundException();
		}
		
		unitRepository.delete(u);
	}
	
	@Transactional
	public Unit update(Unit toUpdate) throws EntityNotFoundException {
		
		if(unitRepository.findOne(toUpdate.getUnitid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public Unit findByID(Integer id) {
		return unitRepository.findOne(id);
	}
	
}
