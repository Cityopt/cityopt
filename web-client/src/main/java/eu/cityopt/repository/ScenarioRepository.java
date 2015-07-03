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
	@Query("select s from Scenario s where Lower(s.name) like CONCAT('%',Lower(:scenName),'%') order by s.name")
	List<Scenario> findByNameContaining(@Param("scenName") String scenName);
	
	Scenario findByName(String scenName);
	
	@Query("select s from Scenario s where Lower(s.name) like Lower(:scenName)"
			+ " and s.project.prjid = :prjid")
	Scenario findByNamePrjid(@Param("scenName") String scenName, @Param("prjid") int prjid);
	
	@Query("select s from Scenario s where s.createdon between :dateLower and :dateUpper order by s.name")
	List<Scenario> findByCreationDate(@Param("dateLower") Date dateLower, @Param("dateUpper") Date dateUpper);
}
