package eu.cityopt.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.Scenario;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario,Integer>{
	@Query("select s from Scenario s where Lower(s.name) like CONCAT('%',Lower(:scenName),'%')")
	List<Scenario> findByName(@Param("scenName") String scenName);
	@Query("select s from Scenario s where s.createdon between :dateLower and :dateUpper")
	List<Scenario> findByCreationDate(@Param("dateLower") Date dateLower, @Param("dateUpper") Date dateUpper);
}