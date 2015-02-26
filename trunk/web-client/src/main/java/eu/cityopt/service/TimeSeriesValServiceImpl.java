package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.repository.TimeSeriesValRepository;

@Service("TimeSeriesValService")
@Transactional
public class TimeSeriesValServiceImpl implements TimeSeriesValService {
	
	@Autowired
	private TimeSeriesValRepository timeSeriesValRepository;
	
	@Transactional(readOnly=true)
	public List<TimeSeriesVal> findAll() {
		return timeSeriesValRepository.findAll();
	}

	@Transactional
	public TimeSeriesVal save(TimeSeriesVal u) {
		return timeSeriesValRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(timeSeriesValRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		timeSeriesValRepository.delete(id);
	}
	
	@Transactional
	public TimeSeriesVal update(TimeSeriesVal toUpdate) throws EntityNotFoundException {
		
		if(timeSeriesValRepository.findOne(toUpdate.getTseriesvalid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	public TimeSeriesVal findByID(int id) {
		return timeSeriesValRepository.findOne(id);
	}
	
}
