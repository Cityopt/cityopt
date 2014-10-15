package com.ait.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ait.model.Appuser;
import com.ait.model.Simulationmodel;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<Appuser,Integer> {
	@Query("select u from Appuser u where Lower(u.username) like CONCAT('%',Lower(:userName),'%')")
	List<Appuser> findByUserName(@Param("userName") String userName);
	
}
