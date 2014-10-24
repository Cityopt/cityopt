package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.Usergroupproject;

@Repository("userGroupProjectRepository")
public interface UserGroupProjectRepository extends JpaRepository<Usergroupproject, Integer> {

}
