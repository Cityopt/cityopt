package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;

@Repository
public interface InputParamValRepository extends JpaRepository<InputParamVal,Integer> {

	@Query("select ipv from InputParamVal ipv "
			+ " join ipv.inputparameter ip"
			+ " join ipv.scenario s"
			+ " where ip.inputid = :inputid "
			+ " and s.scenid = :scenid ")
	InputParamVal findByInputIdAndScenId(@Param("inputid") int inputid, @Param("scenid") int scenid);

	@Query("select ipv from InputParamVal ipv "
			+ " join ipv.inputparameter ip"
			+ " join ip.component c"
			+ " join ipv.scenario s"
			+ " where c.componentid = :componentid"
			+ " and s.scenid = :scenid ")
	List<InputParamVal> findByComponentAndScenario(@Param("componentid") int componentid, 
			@Param("scenid") int scenid);
	
}