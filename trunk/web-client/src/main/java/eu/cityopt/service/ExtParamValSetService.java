package eu.cityopt.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.TimeSeriesDTOX;

public interface ExtParamValSetService extends CityOptService<ExtParamValSetDTO>{
	
	public ExtParamValSetDTO save(ExtParamValSetDTO epvs);
	
	public List<ExtParamValDTO> getExtParamVals(int extParamValSetId) 
			throws EntityNotFoundException;
	
	public void addExtParamVals(int extParamValSetId, Set<ExtParamValDTO> epVals) 
			throws EntityNotFoundException;	
	
	public void removeExtParamValsFromSet(int extParamValSetId, Set<ExtParamValDTO> epVals) 
			throws EntityNotFoundException;
	
	public void cleanupExtParamValSets();

	/**
	 * Updates the values in the set, cloning it first if the set is used in
	 * historical scenario data (ScenarioMetrics).
	 * @param extParamValSet contains the id of the set, and its new name.
	 * @param extParamVals list of /all/ external parameter values to be included
	 *   in the set.  For time series valued parameters, the value is left null.
	 * @param timeSeriesByParamId map from extParamId to time series data.
	 *   Must contain a TimeSeriesDTOX for every external parameter whose value
	 *   should be a time series.
	 */
	public ExtParamValSetDTO updateOrClone(
			ExtParamValSetDTO extParamValSet, List<ExtParamValDTO> extParamVals,
			Map<Integer, TimeSeriesDTOX> timeSeriesByParamId) throws EntityNotFoundException;
}
