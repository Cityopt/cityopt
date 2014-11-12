package com.cityopt.repository;

import java.util.List;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;





import com.cityopt.model.Project;
import com.cityopt.model.Simulationmodel;

@Repository
public interface SimulationModelRepository extends JpaRepository<Simulationmodel,Integer>{
	
	@Query("select s from Simulationmodel s where Lower(s.description) like CONCAT('%',Lower(:modelDesc),'%')")
	List<Simulationmodel> findByDescription(@Param("modelDesc") String modelDescription);
}
