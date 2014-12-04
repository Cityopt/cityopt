package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.DecisionVariable;

@Repository
public interface DecisionVariableRepository extends JpaRepository<DecisionVariable, Integer> {

}
