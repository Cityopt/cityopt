package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ObjectiveFunction;

@Repository
public interface ObjectiveFunctionRepository extends JpaRepository<ObjectiveFunction, Integer> {

}

