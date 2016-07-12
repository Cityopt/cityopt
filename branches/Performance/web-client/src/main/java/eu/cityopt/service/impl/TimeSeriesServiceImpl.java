package eu.cityopt.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.DTO.TimeSeriesDTOX;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.repository.TypeRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.TimeSeriesService;

@Service("TimeSeriesService")
@Transactional
public class TimeSeriesServiceImpl implements TimeSeriesService {
	
	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private TimeSeriesRepository timeSeriesRepository;
	
	@Autowired
	private TimeSeriesValRepository timeSeriesValRepository;

	@Transactional(readOnly=true)
	public List<TimeSeries> findAll() {
		return timeSeriesRepository.findAll();
	}

	@Transactional
	public TimeSeries save(TimeSeries u) {
		return timeSeriesRepository.save(u);
	}

	@Override
	@Transactional
	public TimeSeries save(TimeSeriesDTOX tsd) {
        TimeSeries timeSeries = new TimeSeries();
    	timeSeries.setTseriesid(tsd.getTseriesid());
    	//timeSeries.setVersion(tsd.getVersion());
    	timeSeries.setType((tsd.getType() == null) ? null
    			: typeRepository.findOne(tsd.getType().getTypeid()));
        int n = tsd.getTimes().length;
        if (n != tsd.getValues().length) {
        	throw new IllegalArgumentException();
        }
        for (int i = 0; i < n; ++i) {
            TimeSeriesVal timeSeriesVal = new TimeSeriesVal();
            timeSeriesVal.setTime(tsd.getTimes()[i]);
            timeSeriesVal.setValue(Double.toString(tsd.getValues()[i]));

            timeSeriesVal.setTimeseries(timeSeries);
            timeSeries.getTimeseriesvals().add(timeSeriesVal);
        }
        timeSeriesValRepository.save(timeSeries.getTimeseriesvals());
        return timeSeriesRepository.save(timeSeries);
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
