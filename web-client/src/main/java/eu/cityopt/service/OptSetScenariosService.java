package eu.cityopt.service;

import eu.cityopt.DTO.OptSetScenariosDTO;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;

public interface OptSetScenariosService extends CityOptService<OptSetScenariosDTO> {

    OptSetScenariosDTO save(OptSetScenariosDTO u);

    OptSetScenariosDTO update(OptSetScenariosDTO toUpdate)
            throws EntityNotFoundException;

    void saveEvaluationResults(Integer optSetId, SearchOptimizationResults sor);

}
