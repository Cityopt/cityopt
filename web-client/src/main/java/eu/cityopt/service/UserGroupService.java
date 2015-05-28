package eu.cityopt.service;

import java.util.List;

import eu.cityopt.DTO.UserGroupDTO;

public interface UserGroupService extends CityOptService<UserGroupDTO> {

	UserGroupDTO update(UserGroupDTO toUpdate) throws EntityNotFoundException;

	UserGroupDTO save(UserGroupDTO u);

	List<UserGroupDTO> findByGroupNameContaining(String userGroupName);

}