package eu.cityopt.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.SimulationResultDTO;
import eu.cityopt.DTO.TimeSeriesValDTO;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.repository.SimulationResultRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.SimulationResultService;

@Service("SimulationResultService")
@Transactional
public class SimulationResultServiceImpl implements SimulationResultService {
	private static final int PAGE_SIZE = 50;
	
	@Autowired
	private SimulationResultRepository simulationResultRepository;
	
	@Autowired
	private TimeSeriesValRepository timeSeriesValRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Transactional(readOnly=true)
	public List<SimulationResultDTO> findAll() {
		return modelMapper.map(simulationResultRepository.findAll(),
				new TypeToken<List<SimulationResultDTO>>() {}.getType());
	}

//	@Transactional
//	public SimulationResult save(SimulationResult u) {
//		return simulationResultRepository.save(u);
//	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(simulationResultRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		simulationResultRepository.delete(id);
	}
	
//	@Transactional
//	public SimulationResult update(SimulationResult toUpdate) throws EntityNotFoundException {
//		
//		if(simulationResultRepository.findOne(toUpdate.getScenresid()) == null) {
//			throw new EntityNotFoundException();
//		}
//		
//		return save(toUpdate);
//	}
	
	@Transactional(readOnly=true)
	public SimulationResultDTO findByID(int id) throws EntityNotFoundException {
		SimulationResult simRes = simulationResultRepository.findOne(id);
		if(simRes == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(simRes, SimulationResultDTO.class);
	}

	@Override
	@Transactional(readOnly=true)
	public List<TimeSeriesValDTO> getTimeSeriesValsOrderedByTime(int scenResId) throws EntityNotFoundException {
		SimulationResult simRes = simulationResultRepository.findOne(scenResId);
		
		if(simRes == null) {
			throw new EntityNotFoundException();
		}
		if(simRes.getTimeseries() == null){
			throw new EntityNotFoundException("no TimeSeries found on "
					+ "SimulationResult with id: "+ scenResId);
		}
		
		List<TimeSeriesVal> tsValues = timeSeriesValRepository.findTimeSeriesValOrderedByTime(
				simRes.getTimeseries().getTseriesid());
		
		return modelMapper.map(tsValues, new TypeToken<List<TimeSeriesValDTO>>(){}.getType());
	}
	
	
	
	@Override
	@Transactional(readOnly=true)
	public SimulationResultDTO findByOutVarIdScenId(int outVarId, int scenarioID) throws EntityNotFoundException {
		SimulationResult simRes = simulationResultRepository.findByScenAndOutvar(scenarioID, outVarId);
		if(simRes == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(simRes, SimulationResultDTO.class);
	}
	
	@Override
	public List<SimulationResultDTO> findAll(int pageIndex) {
		/*
		PageRequest request =
	            new PageRequest(pageIndex,PAGE_SIZE,new Sort(Sort.Direction.ASC,"simresid"));
	            */
		PageRequest request =
	            new PageRequest(pageIndex,PAGE_SIZE);
		return modelMapper.map(simulationResultRepository.findAll(request),
				new TypeToken<List<SimulationResultDTO>>() {}.getType());		
	}
	
}
