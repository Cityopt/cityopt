package eu.cityopt.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.repository.ExtParamValRepository;

@Service("ExtParamValService")
public class ExtParamValServiceImpl implements ExtParamValService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ExtParamValRepository extParamValRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional(readOnly=true)
	public List<ExtParamValDTO> findAll() {
		return modelMapper.map(extParamValRepository.findAll(), 
				new TypeToken<List<ExtParamValDTO>>() {}.getType());
	}

	@Transactional
	public ExtParamValDTO save(ExtParamValDTO u) {
		ExtParamVal eparam = modelMapper.map(u, ExtParamVal.class);
		eparam = em.merge(eparam);
		return modelMapper.map(eparam, ExtParamValDTO.class);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(extParamValRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		extParamValRepository.delete(id);
	}
	
	@Transactional
	public ExtParamValDTO update(ExtParamValDTO toUpdate) throws EntityNotFoundException {
		
		if(extParamValRepository.findOne(toUpdate.getExtparamvalid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly = true)
	public ExtParamValDTO findByID(int id) throws EntityNotFoundException {
		ExtParamVal eparam = extParamValRepository.findOne(id);
		
		if(eparam == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(eparam, ExtParamValDTO.class);
	}
	
}
