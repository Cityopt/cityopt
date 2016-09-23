package eu.cityopt.DTO;

import org.modelmapper.PropertyMap;

import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.ScenarioGenerator;

public class ScenarioGeneratorToOpenOptimizationSetDTOMap 
		extends PropertyMap<ScenarioGenerator, OpenOptimizationSetDTO>{	
	
	@Override
	protected void configure() {
		map().setId(source.getScengenid());
		map().setName(source.getName());
		map().setDescription(null);
		map().setOptSetType(OptimizationSetType.GeneticAlgorithm);
	}
	
}
