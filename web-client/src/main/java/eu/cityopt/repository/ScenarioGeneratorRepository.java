package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.MetricVal;
import eu.cityopt.model.Project;
import eu.cityopt.model.ScenarioGenerator;

@Repository
public interface ScenarioGeneratorRepository extends JpaRepository<ScenarioGenerator, Integer> {
	@Modifying
	@Query("update ScenarioGenerator set scengenid = :newId "
			+ " where scengenid = :oldId ")
	public void updateId(@Param("oldId") Integer oldId,
			@Param("newId") Integer newId);
	
	ScenarioGenerator findByName(String name);
	List<ScenarioGenerator> findByNameAndProject_prjid(String name, int prjid);
}

