package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.TimeSeries;

@Repository
public interface TimeSeriesRepository extends JpaRepository<TimeSeries, Integer> {

}
