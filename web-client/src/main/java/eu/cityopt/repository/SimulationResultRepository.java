package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.SimulationResult;

@Repository
public interface SimulationResultRepository extends JpaRepository<SimulationResult, Integer> {

}
