package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.OptConstraint;

@Repository
public interface OptConstraintRepository extends JpaRepository<OptConstraint, Integer> {
	OptConstraint findByNameAndProject_prjid(@Param("name") String name,@Param("prjid") int prjid);
	
	@Query("SELECT optconstraint.optconstid, optconstraint.prjid, optconstraint.name, optconstraint.expression, optconstraint.lowerbound, optconstraint.upperbound, optconstraint.version FROM optsearchconst INNER JOIN optconstraint ON optsearchconst.optconstid = optconstraint.optconstid INNER JOIN optimizationset ON optsearchconst.optid = optimizationset.optid WHERE optconstraint.name = :name AND optimizationset.optid =:optID")
	OptConstraint findByNameAndOptSet(@Param("name") String name, @Param("optID") int optID);
	
	@Query("SELECT optconstraint.optconstid, optconstraint.prjid, optconstraint.name, optconstraint.expression, optconstraint.lowerbound, optconstraint.upperbound, optconstraint.version FROM scengenoptconstraint INNER JOIN optconstraint ON scengenoptconstraint.optconstid = optconstraint.optconstid INNER JOIN scenariogenerator ON scengenoptconstraint.scengenid = scenariogenerator.scengenid WHERE optconstraint.name = :name AND scenariogenerator.scengenid = :scengenid")
	OptConstraint findByNameAndScenGen(@Param("name") String name, @Param("scengenid") int scengenid);
	
	
}

