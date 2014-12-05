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
	
	public List<OptSearchConst> findAll() {
		return optSearchConstRepository.findAll();
	}

	@Transactional
	public OptSearchConst save(OptSearchConst u) {
		return optSearchConstRepository.save(u);
	}

	@Transactional
	public void delete(OptSearchConst u) throws EntityNotFoundException {
		
		if(optSearchConstRepository.findOne(u.getOptsearchconstid()) == null) {
			throw new EntityNotFoundException();
		}
		
		optSearchConstRepository.delete(u);
	}
	
	@Transactional
	public OptSearchConst update(OptSearchConst toUpdate) throws EntityNotFoundException {
		
		if(optSearchConstRepository.findOne(toUpdate.getOptsearchconstid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public OptSearchConst findByID(Integer id) {
		return optSearchConstRepository.findOne(id);
	}
	
}
