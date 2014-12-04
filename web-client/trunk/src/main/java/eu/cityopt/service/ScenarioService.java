package eu.cityopt.service;

import java.util.Date;
import java.util.List;

import eu.cityopt.model.Scenario;

public interface ScenarioService extends CityOptService<Scenario>{
	
	List<Scenario> findByCreationDate(Date dateLower, Date dateUpper);

}