package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.Project;

@Repository
public interface ObjectiveFunctionRepository extends JpaRepository<ObjectiveFunction, Integer> {
	 
	@Query("select o from ObjectiveFunction o where o.project.prjid = :prjID and o.name=:name")
	public ObjectiveFunction findByName(@Param("prjID") Integer prjID,@Param("name") String name);
	
}

