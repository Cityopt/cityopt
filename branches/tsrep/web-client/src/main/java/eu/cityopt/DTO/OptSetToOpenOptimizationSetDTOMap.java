package eu.cityopt.DTO;

import org.modelmapper.PropertyMap;

import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.Project;

public class OptSetToOpenOptimizationSetDTOMap extends PropertyMap<OptimizationSet, OpenOptimizationSetDTO>{	
	@Override
	protected void configure() {
		map().setId(source.getOptid());
		map().setName(source.getName());
		map().setDescription(null);
		map().setOptSetType(OptimizationSetType.DatabaseSearch);
	}
}
