package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.DecisionVariableResult;

@Repository
public interface DecisionVariableResultRepository extends JpaRepository<DecisionVariableResult, Integer> {

}
