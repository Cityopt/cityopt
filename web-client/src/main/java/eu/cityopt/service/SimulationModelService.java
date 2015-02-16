package eu.cityopt.service;

import java.util.List;

import javax.transaction.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.SimulationModelDTO;
import eu.cityopt.model.SimulationModel;

public interface SimulationModelService extends CityOptService<SimulationModelDTO>{
	//void deleteAll();

	public SimulationModelDTO save(SimulationModel model);

	public SimulationModelDTO update(SimulationModel toUpdate) throws EntityNotFoundException;
	
}
