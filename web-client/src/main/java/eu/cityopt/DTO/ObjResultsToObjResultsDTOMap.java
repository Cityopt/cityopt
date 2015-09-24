package eu.cityopt.DTO;

import org.modelmapper.PropertyMap;

import eu.cityopt.model.ObjectiveFunctionResult;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.Project;

public class ObjResultsToObjResultsDTOMap extends PropertyMap<ObjectiveFunctionResult, ObjectiveFunctionResultDTO>{	
	@Override
	protected void configure() {
		map().setObjectivefunctionresultid(source.getObjectivefunctionresultid());
		map().setObtfunctionid(source.getObjectivefunction().getObtfunctionid());
		map().setScengenid(source.getScengenresult().getScengenresultid());
		map().setValue(source.getValue());
		map().setVersion(source.getVersion());		
	}
}
