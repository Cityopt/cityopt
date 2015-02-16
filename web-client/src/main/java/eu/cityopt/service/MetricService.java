package eu.cityopt.service;

import eu.cityopt.DTO.MetricDTO;

public interface MetricService extends CityOptService<MetricDTO> {
	public MetricDTO save(MetricDTO u);
	
	public MetricDTO update(MetricDTO toUpdate) throws EntityNotFoundException;
	
	public MetricDTO findByID(int id) throws EntityNotFoundException;
	
	public void setProject(int metId, int prjid);
}