package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.OptimizationSet;

@Repository
public interface OptimizationSetRepository extends JpaRepository<OptimizationSet, Integer> {

	List<OptimizationSet> findByName(String name);
	
	OptimizationSet findByNameAndProject_prjid(String name,int prjid);
}

