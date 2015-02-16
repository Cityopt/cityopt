package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.SimulationModel;
import eu.cityopt.model.TimeSeriesVal;

@Repository
public interface TimeSeriesValRepository extends JpaRepository<TimeSeriesVal, Integer> {
	@Query("select t from TimeSeriesVal t where t.timeseries.tseriesid = :tSeriesId order by t.time asc")
	public List<TimeSeriesVal> findTimeSeriesValOrderedByTime(@Param("tSeriesId") int timeSeriesId);
}
