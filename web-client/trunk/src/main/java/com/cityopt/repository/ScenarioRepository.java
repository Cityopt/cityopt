package com.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cityopt.model.Scenario;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario,Integer>{
	@Query("select s from Scenario s where Lower(s.name) like CONCAT('%',Lower(:scenName),'%')")
	List<Scenario> findByName(@Param("scenName") String scenName);
}
