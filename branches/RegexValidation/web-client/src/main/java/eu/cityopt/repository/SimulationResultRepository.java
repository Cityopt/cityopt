package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.SimulationResult;

@Repository
public interface SimulationResultRepository extends JpaRepository<SimulationResult, Integer> {
//	@Query("select s from SimulationResult s where s.scenario.scenid = :scenId and"
//			+ " s.outputvariable.outvarid = :outId order by s.time asc")
//	List<SimulationResult> findByScenAndOutvar(@Param("scenId") int scenId, @Param("outId") int outId);
	@Query("select s from SimulationResult s where s.scenario.scenid = :scenId and"
	+ " s.outputvariable.outvarid = :outId")
	SimulationResult findByScenAndOutvar(@Param("scenId") int scenId, @Param("outId") int outId);
	
	@Query("select s from SimulationResult s where s.scenario.scenid = :scenId")
	List<SimulationResult> findByScenId(@Param("scenId") int scenId);
}
