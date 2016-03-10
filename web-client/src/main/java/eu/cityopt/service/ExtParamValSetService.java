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
     * Updates a single ExtParamVal in a set.  Removes any old metric values
     * (i.e. ScenarioMetrics) referencing the set - or can optionally clone the set
     * instead, so that the old metric values remain in the database.
     * @param extParamValSetId identifies the set
     * @param extParamVal the new value data for an external parameter.
     *   Must contain ExtParamDTO with correct id.
     *   Leave the string value null when using a time series value.
     * @param timeSeries the time series data to be saved, in case the external
     *   parameter is set to a time series.  Leave this null when using a 
     *   plain string value inside ExtParamValDTO.
     * @param cloneToKeepOldMetrics whether to clone the set containing the old
     *   value in order to keep old ScenarioMetrics intact
     * @throws EntityNotFoundException
     * @return ExtParamValSetDTO the updated set (may be a new clone) 
     */
    public ExtParamValSetDTO updateExtParamValInSet(int extParamValSetId,
            ExtParamValDTO extParamVal, TimeSeriesDTOX timeSeries,
            boolean cloneToKeepOldMetrics) throws EntityNotFoundException;

    /**
     * Updates a single ExtParamVal in all sets of a project.  Removes any old
     * metric values (i.e. ScenarioMetrics) referencing the set - or can optionally
     * clone the sets instead, so that the old metric values remain in the database.
     * @param extParamValSet contains the id of the set, and its new name.
     * @param extParamVals list of /all/ external parameter values to be included
     *   in the set.  For time series valued parameters, the value must be null.
     * @param timeSeriesByParamId map from extParamId to time series data.
     *   Must contain a TimeSeriesDTOX for every external parameter whose value
     *   should be a time series.  Will be saved in the database.
     * @param cloneToKeepOldMetrics whether to clone the set containing the old
     *   value in order to keep old ScenarioMetrics intact
     */
    public void updateExtParamValInProject(int extParamValSetId,
            ExtParamValDTO extParamVal, TimeSeriesDTOX timeSeries,
            boolean cloneToKeepOldMetrics) throws EntityNotFoundException;

    /**
     * Replaces all the values in a set in one go.  Removes any old metric values
     * (i.e. ScenarioMetrics) referencing the set - or can optionally clone the set
     * instead, so that the old metric values remain in the database.
     * @see #update(ExtParamValSetDTO, List, Map)
     */
    public ExtParamValSetDTO update(
            ExtParamValSetDTO extParamValSet, List<ExtParamValDTO> extParamVals,
            Map<Integer, TimeSeriesDTOX> timeSeriesByParamId,
            boolean cloneToKeepOldMetrics) throws EntityNotFoundException;
}
