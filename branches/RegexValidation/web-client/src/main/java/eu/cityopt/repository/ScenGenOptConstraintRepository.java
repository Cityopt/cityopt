package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.OptSearchConst;
import eu.cityopt.model.ScenGenOptConstraint;

@Repository
public interface ScenGenOptConstraintRepository extends JpaRepository<ScenGenOptConstraint, Integer> {
	@Query("select oc from ScenGenOptConstraint o join o.optconstraint oc "
			+ " where scenGenID = :scengenid")
	List<OptConstraint> findOptConstraintsforScenGen(@Param("scengenid") int scengenId);
	
	@Query("select o from ScenGenOptConstraint o "
			+ " where scenGenID = :scengenid"
			+ " and optConstID = :ocid ")
	ScenGenOptConstraint findByScenGenIdAndOptConstId(@Param("scengenid") int scengenId, @Param("ocid") int ocId);
	
}
