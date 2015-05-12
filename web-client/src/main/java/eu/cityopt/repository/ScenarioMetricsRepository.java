package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ScenarioMetrics;

@Repository
public interface ScenarioMetricsRepository extends JpaRepository<ScenarioMetrics, Integer> {
	@Query("select sm from ScenarioMetrics sm where "
			+ " sm.scenario.scenid = :scenid and sm.extparamvalset.id = :epvsid")
	public ScenarioMetrics findByScenidAndExtParamValSetid(@Param("scenid") int scenid, @Param("epvsid") int epvsid);
}
