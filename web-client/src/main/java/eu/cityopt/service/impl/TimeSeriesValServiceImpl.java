package eu.cityopt.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.TimeSeriesValDTO;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.TimeSeriesValService;

@Service("TimeSeriesValService")
@Transactional
public class TimeSeriesValServiceImpl implements TimeSeriesValService {
	private static final int PAGE_SIZE = 50;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private TimeSeriesValRepository timeSeriesValRepository;
	
	@Autowired
	private TimeSeriesRepository timeSeriesRepository;
	
	@Transactional(readOnly=true)
	public List<TimeSeriesValDTO> findAll() {
		return modelMapper.map(timeSeriesValRepository.findAll(), 
				new TypeToken<List<TimeSeriesValDTO>>(){}.getType());
	}

//	@Transactional
//	public TimeSeriesVal save(TimeSeriesVal u) {
//		return timeSeriesValRepository.save(u);
//	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(timeSeriesValRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		timeSeriesValRepository.delete(id);
	}
	
//	@Transactional
//	public TimeSeriesVal update(TimeSeriesVal toUpdate) throws EntityNotFoundException {
//		
//		if(timeSeriesValRepository.findOne(toUpdate.getTseriesvalid()) == null) {
//			throw new EntityNotFoundException();
//		}
//		
//		return save(toUpdate);
//	}
	
	@Transactional(readOnly=true)
	public TimeSeriesValDTO findByID(int id) throws EntityNotFoundException {
		TimeSeriesVal val = timeSeriesValRepository.findOne(id);
		
		if(val == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(val, TimeSeriesValDTO.class) ;
	}
	
	@Transactional(readOnly=true)
	private List<TimeSeriesValDTO> findByTimeSeriesIdOrderedByTimeOld(int timeSeriesId) throws EntityNotFoundException {
		
		List<TimeSeriesVal> tsValues = timeSeriesValRepository.findTimeSeriesValOrderedByTime(timeSeriesId);
		
		if(tsValues == null){
			throw new EntityNotFoundException("no TimeSeries found on "
					+ "SimulationResult with id: "+ timeSeriesId);
		}
		
		return modelMapper.map(tsValues, new TypeToken<List<TimeSeriesValDTO>>(){}.getType());
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<TimeSeriesValDTO> findByTimeSeriesIdOrderedByTime(int timeSeriesId) throws EntityNotFoundException {
		
		List<TimeSeriesVal> tsValues = timeSeriesValRepository.findTimeSeriesValOrderedByTime(timeSeriesId);
		
		if(tsValues == null){
			throw new EntityNotFoundException("no TimeSeries found on "
					+ "SimulationResult with id: "+ timeSeriesId);
		}

		return convertCollection(tsValues);
	}
	
	private List<TimeSeriesValDTO> convertCollection(List<TimeSeriesVal> tsVals){
		List<TimeSeriesValDTO> tsValsD = new ArrayList<TimeSeriesValDTO>(); 
		for(int i = 0; i < tsVals.size(); i++){
			tsValsD.add(modelMapper.map(tsVals.get(i), TimeSeriesValDTO.class));
		}
		return tsValsD;
	}

	@Override
	public Page<TimeSeriesValDTO> findByTimeSeriesIdOrderedByTime(
			int timeSeriesId, int pageIndex) throws EntityNotFoundException {
		
		PageRequest request =
	            new PageRequest(pageIndex,PAGE_SIZE);
		
		Page<TimeSeriesVal> tsValues =  timeSeriesValRepository.findTimeSeriesValOrderedByTime(timeSeriesId,request);
		
		if(tsValues == null){
			throw new EntityNotFoundException("no TimeSeries found on "
					+ "SimulationResult with id: "+ timeSeriesId);
		}
		
		return modelMapper.map(tsValues, new TypeToken<Page<TimeSeriesValDTO>>() {}.getType());		
		
	}
	
}
