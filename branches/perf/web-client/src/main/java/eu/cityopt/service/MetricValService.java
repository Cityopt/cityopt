package eu.cityopt.service;

import eu.cityopt.DTO.MetricValDTO;

public interface MetricValService extends CityOptService<MetricValDTO> {
	public MetricValDTO save(MetricValDTO u, int metId);
	
	public MetricValDTO update(MetricValDTO toUpdate, int metId) throws EntityNotFoundException;
	
	public MetricValDTO findByID(int id) throws EntityNotFoundException;

}
