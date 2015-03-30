package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.OptSearchConst;

@Repository
public interface OptSearchConstRepository extends JpaRepository<OptSearchConst, Integer> {

	@Query("select oc from OptSearchConst o JOIN o.optconstraint as oc "
			+ "where optID = :optid")
	List<OptConstraint> findOptConstraintsforOptSet(@Param("optid") int optId);
	
}

