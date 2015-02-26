package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.OptSearchConst;
import eu.cityopt.repository.OptSearchConstRepository;

@Service("OptSearchConstService")
public class OptSearchConstServiceImpl implements OptSearchConstService {
	
	@Autowired
	private OptSearchConstRepository optSearchConstRepository;
	
	@Transactional(readOnly=true)
	public List<OptSearchConst> findAll() {
		return optSearchConstRepository.findAll();
	}

	@Transactional
	public OptSearchConst save(OptSearchConst u) {
		return optSearchConstRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(optSearchConstRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		optSearchConstRepository.delete(id);
	}
	
	@Transactional
	public OptSearchConst update(OptSearchConst toUpdate) throws EntityNotFoundException {
		
		if(optSearchConstRepository.findOne(toUpdate.getOptsearchconstid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	public OptSearchConst findByID(int id) {
		return optSearchConstRepository.findOne(id);
	}
	
}
