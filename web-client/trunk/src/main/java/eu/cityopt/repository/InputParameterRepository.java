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
	List<InputParameter> findByName(@Param("name") String name);
}
