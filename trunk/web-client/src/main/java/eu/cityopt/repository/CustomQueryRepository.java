package eu.cityopt.repository;

import java.sql.SQLException;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.DTO.ComponentInputParamDTO;
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

}