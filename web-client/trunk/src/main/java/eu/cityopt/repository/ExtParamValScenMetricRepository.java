package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ExtParamValScenMetric;

@Repository
public interface ExtParamValScenMetricRepository extends JpaRepository<ExtParamValScenMetric, Integer> {

}
