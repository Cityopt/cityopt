package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.Component;
import eu.cityopt.model.Metric;

@Repository
public interface MetricRepository extends JpaRepository<Metric, Integer> {
	@Query("select m from Metric m where Lower(m.name) = Lower(:name) "
			+ " and prjid = :prjid order by m.name")
	Metric findByNameAndProject(@Param("prjid") int prjid, @Param("name") String name);
}
