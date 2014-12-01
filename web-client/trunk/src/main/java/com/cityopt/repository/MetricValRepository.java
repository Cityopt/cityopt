package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.MetricVal;

@Repository
public interface MetricValRepository extends JpaRepository<MetricVal, Integer> {

}
