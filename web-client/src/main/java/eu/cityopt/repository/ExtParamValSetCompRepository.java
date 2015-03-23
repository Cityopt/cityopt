package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.ExtParamValSetComp;

@Repository
public interface ExtParamValSetCompRepository  extends JpaRepository<ExtParamValSetComp, Integer>{
	@Query("select epv from ExtParamValSetComp e JOIN e.extparamval as epv where extParamValSetID = :epvsid")
	List<ExtParamVal> findByExtParamValSetId(@Param("epvsid") int epvsId);
}
