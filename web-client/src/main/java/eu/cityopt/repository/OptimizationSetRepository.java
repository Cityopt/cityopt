package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.OptimizationSet;

@Repository
public interface OptimizationSetRepository extends JpaRepository<OptimizationSet, Integer> {

}

