package eu.cityopt.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.DTO.ComponentInputParamDTO;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.model.Type;
import eu.cityopt.service.impl.ProjectServiceImpl;

@Repository
public class CustomQueryRepositoryImpl implements CustomQueryRepository {	

	private JdbcTemplate template;
	
	static Logger log = Logger.getLogger(ProjectServiceImpl.class);
	
	@Autowired //constructor injection - so spring knows that the datasource is required
	public CustomQueryRepositoryImpl(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
	}
	
	
	@Transactional(readOnly=true)
	public List<ComponentInputParamDTO> findComponentsWithInputParams(int prjid, int scenid){
		String sql = "SELECT inputparameter.componentid,"
				+ " component.\"name\" componentName,"
				+ " inputparameter.inputid,"
				+ " inputparameter.\"name\" inputParameterName,"
				+ " inputparameter.defaultvalue,"
				+ " inputparamval.inputparamvalid,"
				+ " inputparamval.\"value\","
				+ " inputparamval.scenid scenarioID,"
				+ " component.prjid"
				+ " FROM inputparameter"
				+ " INNER JOIN component ON component.componentid=inputparameter.componentid"
				+ " LEFT JOIN"
				+ " (SELECT *"
				+ " FROM inputparamval"
				+ " WHERE scenid=?) inputparamval ON inputparameter.inputid=inputparamval.inputid"
				+ "	WHERE component.prjid=? order by component.name, inputparameter.name";

		Object [] argso = new Object [] { prjid, scenid };
		
		List<ComponentInputParamDTO> components = template.query(sql, argso,
				new BeanPropertyRowMapper<ComponentInputParamDTO>(ComponentInputParamDTO.class));
		
		return components;
	}
	
	@Transactional
	public TimeSeries insertTimeSeries(TimeSeries timeseries)
	{
		 
		Integer tseriesID = template.queryForObject("select nextval('timeseries_tseriesid_seq');", Integer.class);
		
		template.update(new PreparedStatementCreator() {           
		
		                @Override
		                public PreparedStatement createPreparedStatement(Connection connection)
		                        throws SQLException {
		                    PreparedStatement ps = connection.prepareStatement("insert into timeseries (tseriesid, typeid) values (?,?)", Statement.RETURN_GENERATED_KEYS);
		                    ps.setInt(1, tseriesID);		                    
		                    ps.setInt(2, timeseries.getType().getTypeid());
		                    return ps;
		                }
		            });
		
		timeseries.setTseriesid(tseriesID);
		insertBatch(timeseries.getTimeseriesvals());
		
		return timeseries;
		//return findTimeSeriesByTimeSeriesID(timeSeriesID);
		
	}
	
	@Transactional(readOnly=true)
	public List<TimeSeriesVal> findTimeSeriesValByTimeSeriesID(int tid)
	{
		String sql = "SELECT value,time,tseriesvalid,tseriesid from timeseriesval where timeseriesval.tseriesid=? order by time";
		Object [] argso = new Object [] { tid };
		
		List<TimeSeriesVal> tseriesVal = template.query(sql, argso,
				new BeanPropertyRowMapper<TimeSeriesVal>(TimeSeriesVal.class));
		
		return tseriesVal;		
	}
	
	@Transactional(readOnly=true)
	public TimeSeries findTimeSeriesByTimeSeriesID(int tid)
	{
		String sql = "SELECT * from timeseries where tseriesid=?";
		Object [] argso = new Object [] { tid };
		
		List<TimeSeries>  timeSeriesList =template.query(sql, argso,new BeanPropertyRowMapper<TimeSeries>(TimeSeries.class));
		
		if(timeSeriesList.size()>0)
			return timeSeriesList.get(0);
		else return null;
	}

	/** ugly function to update the sequence values when running unit tests with dbunit testdata
	 * (because db unit doesn't even use the sequences when ids are omitted)
	 * @throws SQLException
	 */
	@Transactional
	public void updateSequences() throws SQLException {
		
    	//get all sequences
    	List<Map<String, Object>> rows = template.queryForList("SELECT c.relname FROM pg_class c WHERE c.relkind = 'S';");
    	for (Map<String, Object> row : rows) {
    		String sequence = (String)row.get("relname");
    		String table = sequence.split("_")[0];
            String idColumn = sequence.split("_")[1];
            //update all sequences using the highest value of id column + 1
            template.execute("SELECT SETVAL('" + sequence + "', (SELECT MAX(" + idColumn + ")+1 FROM " + table + "));");
    	}

        Integer test3 = template.queryForObject("select nextval('timeseriesval_tseriesvalid_seq');", Integer.class);
        System.err.println("tseriesvalid sequence val: " + test3);

	}
	
	@Transactional(readOnly=true)
	public List<ComponentInputParamDTO> findComponentsWithInputParamsByCompId(
			int componentId) {
		String sql = "SELECT inputparameter.componentid,"
				+ " component.\"name\" componentName,"
				+ " inputparameter.inputid,"
				+ " inputparameter.\"name\" inputParameterName,"
				+ " inputparameter.defaultvalue,"
				+ " inputparamval.inputparamvalid,"
				+ " inputparamval.\"value\","
				+ " inputparamval.scenid scenarioID,"
				+ " component.prjid"
				+ " FROM inputparameter"
				+ " INNER JOIN component ON component.componentid=inputparameter.componentid"
				+ " LEFT JOIN"
				+ " (SELECT *"
				+ " FROM inputparamval"
				+ " ) inputparamval ON inputparameter.inputid=inputparamval.inputid"
				+ "	WHERE component.componentId=? order by inputparameter.name";
		
		Object [] argso = new Object [] { componentId };
		
		List<ComponentInputParamDTO> components = template.query(sql, argso,
				new BeanPropertyRowMapper<ComponentInputParamDTO>(ComponentInputParamDTO.class));
		
		return components;
	}
	

	@Transactional
	public void deleteTimeSeriesValues(int tseriesid) {
		String sql = "delete from timeseriesval "
				+ "where tseriesid = ?";
		
		Object [] argso = new Object [] { tseriesid };
		
		template.update(sql, argso);
	}
	
	@Transactional
	public boolean insertTimeSeriesBatch(final List<TimeSeriesVal> tsvalues){
		try { 
			insertBatch(tsvalues); 
			return true;
		} catch (Exception e) { 
			log.error("Error on inserting batch", e);
			return false;
		} 
	}
	
	//insert batch
	private void insertBatch(final List<TimeSeriesVal> tsvalues){
	 
		String sql = "INSERT INTO TIMESERIESVAL"
				+ "(TIME, VALUE, TSERIESID) VALUES"
				+ "(?,?,?)";
		template.batchUpdate(sql, new BatchPreparedStatementSetter() {
		
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				TimeSeriesVal value = tsvalues.get(i);
				ps.setTimestamp(1, new java.sql.Timestamp(value.getTime().getTime()));
				ps.setDouble(2, value.getValue());
				ps.setInt(3, value.getTimeseries().getTseriesid());
			}
 
			@Override
			public int getBatchSize() {
				return tsvalues.size();
			}
		});
	}

    @Transactional(readOnly=true)
    @Override
    public Set<Integer> findParetoOptimalScenarios(int projectId) {
        String sql = "SELECT DISTINCT scenID FROM ScenGenResult JOIN Scenario USING (scenID)"
                + " WHERE prjID = ? AND paretoOptimal = TRUE;";
        Object [] argso = new Object [] { projectId };
        List<Integer> scenarioList =
                template.queryForList(sql, argso, Integer.class);
        return new HashSet<Integer>(scenarioList);
    }

    @Transactional(readOnly=true)
    @Override
    public Set<Integer> findParetoOptimalScenarios(int projectId, int scenGenId) {
        String sql = "SELECT scenID FROM ScenGenResult JOIN Scenario USING (scenID)"
                + " WHERE prjID = ? AND ScenGenResult.scenGenId = ?"
                + " AND paretoOptimal = TRUE;";
        Object [] argso = new Object [] { projectId, scenGenId };
        List<Integer> scenarioList =
                template.queryForList(sql, argso, Integer.class);
        return new HashSet<Integer>(scenarioList);
    }
}
