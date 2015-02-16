package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ScenarioGenerator;

@Repository
public interface ScenarioGeneratorRepository extends JpaRepository<ScenarioGenerator, Integer> {

}

