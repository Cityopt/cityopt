package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ExtParamValScenGen;

@Repository
public interface ExtParamValScenGenRepository extends JpaRepository<ExtParamValScenGen, Integer> {

}
