package eu.cityopt.sim.service;

import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.SimulationStorage;

/** SimulationStorage bound to a specific project and external parameter values. */
public interface DbSimulationStorageI extends SimulationStorage {
    void initialize(int projectId, ExternalParameters externals, 
            Integer userId, Integer scenGenId);
}
