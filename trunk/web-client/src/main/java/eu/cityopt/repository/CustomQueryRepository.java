package eu.cityopt.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.DTO.ComponentInputParamDTO;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.TimeSeriesVal;

public interface CustomQueryRepository {

	public List<ComponentInputParamDTO> findComponentsWithInputParams(
			int prjid, int scenid);

	/** ugly function to update the sequence values when running unit tests with dbunit testdata
	 * (because db unit doesn't even use the sequences when ids are omitted)
	 * @throws SQLException
	 */
	public void updateSequences() throws SQLException;

	public List<ComponentInputParamDTO> findComponentsWithInputParamsByCompId(
			int componentId);

	public void deleteTimeSeriesValues(int tseriesid);

	public boolean insertTimeSeriesBatch(List<TimeSeriesVal> tsvalues);
	
	public List<TimeSeriesVal> findTimeSeriesValByTimeSeriesID(int tid);
	
	public TimeSeries insertTimeSeries(TimeSeries timeseries);

	/** ids of scenarios that are Pareto-optimal in any scenario generator run */
	public Set<Integer> findParetoOptimalScenarios(int projectId);

	/** ids of scenarios that are Pareto-optimal in one scenario generator run */
    public Set<Integer> findParetoOptimalScenarios(int projectId, int scenGenId);
}