package eu.cityopt.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.model.Type;
import eu.cityopt.repository.TypeRepository;

@Service("TypeService")
public class TypeServiceImpl implements TypeService {
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private TypeRepository typeRepository;
	
	@Transactional(readOnly=true)
	public List<TypeDTO> findAll() {
		return modelMapper.map(typeRepository.findAll(), 
				new TypeToken<List<TypeDTO>>() {}.getType());
	}

	@Transactional
	public TypeDTO save(TypeDTO u) {
		Type type = modelMapper.map(u, Type.class);
		type = typeRepository.save(type);
		return modelMapper.map(type, TypeDTO.class);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(typeRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		typeRepository.delete(id);
	}
	
	@Transactional
	public TypeDTO update(TypeDTO toUpdate) throws EntityNotFoundException {
		
		if(typeRepository.findOne(toUpdate.getTypeid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	public TypeDTO findByID(int id) throws EntityNotFoundException {
		Type t = typeRepository.findOne(id);
		
		if(t == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(t, TypeDTO.class);
	}
	
}
