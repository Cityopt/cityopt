package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ObjectiveFunctionResult;

@Repository
public interface ObjectiveFunctionResultRepository extends JpaRepository<ObjectiveFunctionResult, Integer>{

	@Query("select res from ObjectiveFunctionResult res where res.objectivefunction.obtfunctionid=:objectiveFunctionId and res.scengenresult.scenariogenerator.scengenid=:scenGenID order by res.objectivefunctionresultid")
	public List<ObjectiveFunctionResult> findByScenGenAndObjFunction(@Param(value="scenGenID") Integer scenGenID,@Param(value="objectiveFunctionId") Integer objectiveFunctionId);
}
