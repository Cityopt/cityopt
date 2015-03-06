package eu.cityopt.sim.service;

import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.opt.OptimisationResults;

/** SimulationStorage bound to a specific project and external parameter values. */
public interface DbSimulationStorageI extends SimulationStorage {
    void initialize(int projectId, ExternalParameters externals, 
            Integer userId, Integer scenGenId);

    void saveScenarioGeneratorStatus(int scenGenId,
            OptimisationResults results, String messages);
}
