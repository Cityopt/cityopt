package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.MetricValDTO;

public interface MetricService extends CityOptService<MetricDTO> {
	public MetricDTO save(MetricDTO u);
	
	public MetricDTO update(MetricDTO toUpdate) throws EntityNotFoundException;
	
	public MetricDTO findByID(int id) throws EntityNotFoundException;
	
	public void setProject(int metId, int prjid);

	Set<MetricValDTO> getMetricVals(int id) throws EntityNotFoundException;
	
	List<MetricValDTO> getMetricVals(int metricId, int scenId) throws EntityNotFoundException;
	
	List<MetricValDTO> getMetricValsByEParamSet(int metricId, int epvsId) throws EntityNotFoundException;

	List<MetricValDTO> getMetricValsByProject(int projectId) throws EntityNotFoundException;

	List<MetricValDTO> getMetricValsByProjectScen(int projectId, int scenId) throws EntityNotFoundException;
}