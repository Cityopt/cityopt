package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.ScenarioMetrics;

@Repository
public interface ScenarioMetricsRepository extends JpaRepository<ScenarioMetrics, Integer> {

}
