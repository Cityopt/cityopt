package com.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cityopt.model.Usergroup;

@Repository("userGroupRepository")
public interface UserGroupRepository extends JpaRepository<Usergroup, Integer> {
	@Query("select u from Usergroup u where Lower(u.name) like CONCAT('%',Lower(:userGroupName),'%')")
	List<Usergroup> findByGroupName(@Param("userGroupName") String userGroupName);
}
