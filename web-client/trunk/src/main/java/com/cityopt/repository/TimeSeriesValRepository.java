package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.TimeSeriesVal;

@Repository
public interface TimeSeriesValRepository extends JpaRepository<TimeSeriesVal, Integer> {

}
