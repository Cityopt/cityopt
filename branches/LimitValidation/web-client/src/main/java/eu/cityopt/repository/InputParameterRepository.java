package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.InputParameter;

@Repository
public interface InputParameterRepository extends JpaRepository<InputParameter,Integer> { 
	@Query("select i from InputParameter i where Lower(i.name) like CONCAT('%',Lower(:name),'%')")
	List<InputParameter> findByNameContaining(@Param("name") String name);
	
	/*@Query("select i from InputParameter i where Lower(i.name) like Lower(:name)"
			+ " and i.component.componentid = :compId")*/
	@Query("select i from InputParameter i where i.name=:name and i.component.componentid = :compId")
	InputParameter findByNameAndCompId(@Param("name") String name, @Param("compId") int compId);
	
	InputParameter findByNameAndComponent_componentid(@Param("name") String name, @Param("compId") int componentid);
}
