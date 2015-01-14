package eu.cityopt.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.model.AppUser;
import eu.cityopt.repository.AppUserRepository;

@Service("AppUserService")
public class AppUserServiceImpl implements AppUserService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private AppUserRepository appuserRepository;
	
	public List<AppUserDTO> findAll() {
		return modelMapper.map(appuserRepository.findAll(), 
				new TypeToken<List<AppUserDTO>>() {}.getType());
	}

	@Transactional
	public AppUserDTO save(AppUserDTO u) {
		AppUser user = modelMapper.map(u, AppUser.class);
		user = appuserRepository.save(user);
		return modelMapper.map(user, AppUserDTO.class);
	}

	@Transactional
	public void deleteAll() {
		appuserRepository.deleteAll();
	}
	
	@Transactional
	public void delete(Integer id) throws EntityNotFoundException {
		
		if(appuserRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		appuserRepository.delete(id);
	}
	
	@Transactional
	public AppUserDTO update(AppUserDTO toUpdate) throws EntityNotFoundException {
		
		if(appuserRepository.findOne(toUpdate.getUserid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public AppUserDTO findByID(Integer id) throws EntityNotFoundException {
		if(appuserRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(appuserRepository.findOne(id), AppUserDTO.class);
	}	
	
}
