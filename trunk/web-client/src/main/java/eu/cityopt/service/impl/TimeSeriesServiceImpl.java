package eu.cityopt.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.TimeSeries;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.TimeSeriesService;

@Service("TimeSeriesService")
@Transactional
public class TimeSeriesServiceImpl implements TimeSeriesService {
	
	@Autowired
	private TimeSeriesRepository timeSeriesRepository;
	
	@Transactional(readOnly=true)
	public List<TimeSeries> findAll() {
		return timeSeriesRepository.findAll();
	}

	@Transactional
	public TimeSeries save(TimeSeries u) {
		return timeSeriesRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(timeSeriesRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		timeSeriesRepository.delete(id);
	}
	
	@Transactional
	public TimeSeries update(TimeSeries toUpdate) throws EntityNotFoundException {
		
		if(timeSeriesRepository.findOne(toUpdate.getTseriesid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	public TimeSeries findByID(int id) {
		return timeSeriesRepository.findOne(id);
	}
	
}
