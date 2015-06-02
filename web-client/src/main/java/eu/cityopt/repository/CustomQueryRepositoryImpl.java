package eu.cityopt.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.DTO.ComponentInputParamDTO;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.service.impl.ProjectServiceImpl;

@Repository
public class CustomQueryRepositoryImpl implements CustomQueryRepository {	

	private DataSource dataSource;	
	private JdbcTemplate template;
	
	static Logger log = Logger.getLogger(ProjectServiceImpl.class);
	
	@Autowired //constructor injection - so spring knows that the datasource is required
	public CustomQueryRepositoryImpl(DataSource dataSource) {
		this.dataSource = dataSource;
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
				+ "	WHERE component.prjid=?";
		
//		String [] args = new String[] {String.valueOf(prjid), String.valueOf(scenid) };
//		Integer [] argsi = new Integer [] {prjid, scenid };
		Object [] argso = new Object [] { prjid, scenid };
		
		List<ComponentInputParamDTO> components = template.query(sql, argso,
				new BeanPropertyRowMapper<ComponentInputParamDTO>(ComponentInputParamDTO.class));
		
		return components;
	}

	/** ugly function to update the sequence values when running unit tests with dbunit testdata
	 * (because db unit doesn't even use the sequences when ids are omitted)
	 * @throws SQLException
	 */
	@Transactional
	public void updateSequences() throws SQLException{
		Connection conn = dataSource.getConnection();
		
	    try {	
	        // Select all sequence names	        
	        Statement seqStmt = conn.createStatement();
	        ResultSet rs = seqStmt.executeQuery("SELECT c.relname FROM pg_class c WHERE c.relkind = 'S';");

	        Integer test = template.queryForObject("select count(*) from timeseriesval;", Integer.class);
	        Integer test2 = template.queryForObject("SELECT MAX(tseriesvalid) FROM timeseriesval;", Integer.class);
	
	        // ... and update the sequence to match max(id)+1.
	        while (rs.next()) {
	            String sequence = rs.getString("relname");
	            String table = sequence.split("_")[0];
	            String idColumn = sequence.split("_")[1];
//	            Statement updStmt = conn.createStatement();
	            template.execute("SELECT SETVAL('" + sequence + "', (SELECT MAX(" + idColumn + ")+1 FROM " + table + "));");
	        }
//	        template.execute("select SETVAL('timeseriesval_tseriesvalid_seq', (SELECT MAX(tseriesvalid) FROM timeseriesval));");
	        Integer test3 = template.queryForObject("select nextval('timeseriesval_tseriesvalid_seq');", Integer.class);
	        System.err.println("tseriesvalid sequence val: " + test3);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            conn.close();
	        } catch (SQLException e) {
	        }
	    }
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
				+ "	WHERE component.componentId=?";
		
//		String [] args = new String[] {String.valueOf(prjid), String.valueOf(scenid) };
//		Integer [] argsi = new Integer [] {prjid, scenid };
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
				ps.setString(2, value.getValue());
				ps.setInt(3, value.getTimeseries().getTseriesid());
			}
 
			@Override
			public int getBatchSize() {
				return tsvalues.size();
			}
		});
	}
}
