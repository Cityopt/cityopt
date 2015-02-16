package eu.cityopt.service;

import java.util.List;

import eu.cityopt.DTO.AppUserDTO;

public interface AppUserService extends CityOptService<AppUserDTO>{
	AppUserDTO save(AppUserDTO u);
	
	AppUserDTO update(AppUserDTO toUpdate)  throws EntityNotFoundException;
	
}