package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.MetricVal;

@Repository
public interface MetricValRepository extends JpaRepository<MetricVal, Integer> {
	@Query("select mv from MetricVal mv LEFT JOIN FETCH mv.scenariometrics scm "
			+ "where mv.metric.metid = :metId and scm.scenario.scenid = :scenId")
	public List<MetricVal> findByMetricAndScen(@Param("metId") Integer metId,
			@Param("scenId") Integer scenId);
	@Query("select mv from MetricVal mv LEFT JOIN FETCH mv.scenariometrics scm "
			+ "where scm.scenario.scenid = :scenId")
	public List<MetricVal> findByScenId(@Param("scenId") Integer scenId);
	@Query("select mv from MetricVal mv LEFT JOIN FETCH mv.scenariometrics scm "
			+ "where mv.metric.metid = :metId and scm.extparamvalset.extparamvalsetid = :extParamSetId")
	public List<MetricVal> findByMetricAndEParamSet(@Param("metId") Integer metId,
			@Param("extParamSetId") Integer epvsId);
}
