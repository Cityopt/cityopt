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
     * Updates a single ExtParamVal in a set, cloning the set first if the
     * set is used in historical scenario data (ScenarioMetrics).
     * @param extParamValSetId identifies the set
     * @param extParamVal the new value data for an external parameter.
     *   Must contain ExtParamDTO with correct id.
     *   Leave the string value null when using a time series value.
     * @param timeSeries the time series data to be saved, in case the external
     *   parameter is set to a time series.  Leave this null when using a 
     *   plain string value inside ExtParamValDTO. 
     * @throws EntityNotFoundException 
     */
    public void updateExtParamValInSetOrClone(int extParamValSetId,
            ExtParamValDTO extParamVal, TimeSeriesDTOX timeSeries)
                    throws EntityNotFoundException;

	/**
	 * Updates all the values in a set, cloning it first if the set is used in
	 * historical scenario data (ScenarioMetrics).
	 * @param extParamValSet contains the id of the set, and its new name.
	 * @param extParamVals list of /all/ external parameter values to be included
	 *   in the set.  For time series valued parameters, the value is left null.
	 * @param timeSeriesByParamId map from extParamId to time series data.
	 *   Must contain a TimeSeriesDTOX for every external parameter whose value
	 *   should be a time series.  Will be saved in the database.
	 */
	public ExtParamValSetDTO updateOrClone(
			ExtParamValSetDTO extParamValSet, List<ExtParamValDTO> extParamVals,
			Map<Integer, TimeSeriesDTOX> timeSeriesByParamId) throws EntityNotFoundException;

	/**
	 * Updates values in all sets of a project.  Sets that are used in historical
	 * scenario data (ScenarioMetrics) are cloned first.
     * @param projectId contains the id of the project
     * @param extParamVals list of /all/ external parameter values to be included
     *   in the set.  For time series valued parameters, the value is left null.
     * @param timeSeriesByParamId map from extParamId to time series data.
     *   Must contain a TimeSeriesDTOX for every external parameter whose value
     *   should be a time series.  Will be saved in the database.
	 */
    void updateOrCloneAllSets(int projectId, List<ExtParamValDTO> extParamVals,
            Map<Integer, TimeSeriesDTOX> timeSeriesByParamId)
            throws EntityNotFoundException;
}
