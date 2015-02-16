package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.TimeSeries;

@Repository
public interface TimeSeriesRepository extends JpaRepository<TimeSeries, Integer> {

}
