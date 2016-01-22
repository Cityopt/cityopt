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
}

