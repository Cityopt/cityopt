package eu.cityopt.DTO;

import org.modelmapper.PropertyMap;

import eu.cityopt.model.Scenario;

public class ScenarioMap extends PropertyMap<Scenario, ScenarioDTO> {

	@Override
	protected void configure() {
		// TODO Auto-generated method stub
//		map().setPrjid(source.getProject().getPrjid());
	} 
	
}