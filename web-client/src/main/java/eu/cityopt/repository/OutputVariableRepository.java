package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.Component;
import eu.cityopt.model.OutputVariable;

@Repository
public interface OutputVariableRepository extends JpaRepository<OutputVariable, Integer> {
	@Query("select o from OutputVariable o where Lower(o.name) LIKE Lower(:name) "
			+ " and o.component.componentid = :componentId")
	OutputVariable findByComponentAndName(@Param("componentId") int componentid, @Param("name") String name);
}

