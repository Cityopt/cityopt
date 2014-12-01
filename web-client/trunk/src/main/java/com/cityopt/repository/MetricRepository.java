package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.Metric;

@Repository
public interface MetricRepository extends JpaRepository<Metric, Integer> {

}
