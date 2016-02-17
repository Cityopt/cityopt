package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.DecisionVariable;
import eu.cityopt.model.OptConstraint;

@Repository
public interface DecisionVariableRepository extends JpaRepository<DecisionVariable, Integer> {
	
	@Query(value="SELECT decisionvariable.decisionvarid, decisionvariable.scengenid, decisionvariable.name, decisionvariable.lowerbound, decisionvariable.upperbound, decisionvariable.typeid, decisionvariable.inputid, decisionvariable.version, scenariogenerator.scengenid FROM scenariogenerator INNER JOIN decisionvariable ON decisionvariable.scengenid = scenariogenerator.scengenid WHERE scenariogenerator.scengenid = :scengen AND scenariogenerator.scengenid = :scengen AND decisionvariable.name = :name",nativeQuery=true)
	DecisionVariable findByNameAndScenGen(@Param("name") String name, @Param("scengenid") int scengenid);

}
