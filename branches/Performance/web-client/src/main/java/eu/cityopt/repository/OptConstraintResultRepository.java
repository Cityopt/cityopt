package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.OptConstraintResult;

@Repository
public interface OptConstraintResultRepository extends JpaRepository<OptConstraintResult, Integer>{

}
