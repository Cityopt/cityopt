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
     * @see #updateExtParamValInSet(int, ExtParamValDTO, TimeSeriesDTOX, boolean)
     */
    public ExtParamValSetDTO updateExtParamValInSet(int extParamValSetId,
            ExtParamValDTO extParamVal, TimeSeriesDTOX timeSeries,
            boolean cloneToKeepOldMetrics) throws EntityNotFoundException;

    /**
     * Updates a single ExtParamVal in all sets of a project.  Removes any old
     * metric values (i.e. ScenarioMetrics) referencing the set.
     * @param extParamValSetId identifies the set
     * @param extParamVal the new value data for an external parameter.
     *   Must contain ExtParamDTO with correct id.
     *   Leave the string value null when using a time series value.
     * @param timeSeries the time series data to be saved, in case the external
     *   parameter is set to a time series.  Leave this null when using a 
     *   plain string value inside ExtParamValDTO.
     * @throws EntityNotFoundException 
     */
    public default void updateExtParamValInProject(int extParamValSetId,
            ExtParamValDTO extParamVal, TimeSeriesDTOX timeSeries)
                    throws EntityNotFoundException {
        updateExtParamValInProject(extParamValSetId, extParamVal, timeSeries, false);
    }

    /**
     * Updates a single ExtParamVal in all sets of a project.  Removes any old
     * metric values (i.e. ScenarioMetrics) referencing the set - or can optionally
     * clone the sets instead, so that the old metric values remain in the database.
     * @see #updateExtParamValInProject(int, ExtParamValDTO, TimeSeriesDTOX)
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
