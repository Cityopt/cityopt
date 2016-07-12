package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.ScenGenObjectiveFunction;
import eu.cityopt.model.ScenGenOptConstraint;

@Repository
public interface ScenGenObjectiveFunctionRepository extends JpaRepository<ScenGenObjectiveFunction, Integer> {
	@Query("select obf from ScenGenObjectiveFunction o join o.objectivefunction obf "
			+ " where o.scenariogenerator.scengenid = :scengenid")
	List<ObjectiveFunction> findObjectiveFunctionsforScenGen(@Param("scengenid") int scengenId);
	
	@Query("select o from ScenGenObjectiveFunction o "
			+ " where o.scenariogenerator.scengenid = :scengenid"
			+ " and o.objectivefunction.obtfunctionid = :ofid ")
	ScenGenObjectiveFunction findByScenGenIdAndOptFunctionId(@Param("scengenid") int scengenId, @Param("ofid") int objectiveFunctionId);
	
}
