package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ScenGenOptConstraint;

@Repository
public interface ScenGenOptConstraintRepository extends JpaRepository<ScenGenOptConstraint, Integer> {

}
