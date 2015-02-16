package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.SimulationModel;

@Repository
public interface SimulationModelRepository extends JpaRepository<SimulationModel,Integer>{
	
	@Query("select s from SimulationModel s where Lower(s.description) like CONCAT('%',Lower(:modelDesc),'%')")
	List<SimulationModel> findByDescription(@Param("modelDesc") String modelDescription);
}
