package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ScenGenResult;

@Repository
public interface ScenGenResultRepository extends JpaRepository<ScenGenResult, Integer>{
	
}
