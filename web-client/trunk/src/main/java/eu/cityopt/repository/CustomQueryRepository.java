package eu.cityopt.repository;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import eu.cityopt.DTO.ComponentInputParamDTO;

@Repository
public class CustomQueryRepository {	
	
//	private DataSource dataSource;
	
	private JdbcTemplate template;
	
	@Autowired //constructor injection - so spring knows that the datasource is required
	public CustomQueryRepository(DataSource dataSource) {
//		this.dataSource = dataSource;
		template = new JdbcTemplate(dataSource);
	}
	
	public List<ComponentInputParamDTO> findComponentsWithInputParams(int prjid, int scenid){
		String sql = "SELECT inputparameter.componentid,"
				+ " component.\"name\" componentName,"
				+ " inputparameter.inputid,"
				+ " inputparameter.\"name\" inputParameterName,"
				+ " inputparameter.defaultvalue,"
				+ " inputparamval.scendefinitionid,"
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
}
