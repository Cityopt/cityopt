package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.Metric;

public interface ExtParamValSetRepository extends JpaRepository<ExtParamValSet, Integer> {
	@Query("select epvs from ExtParamValSet epvs "
			+ " JOIN epvs.extparamvalsetcomps epvsc "
			+ " JOIN epvsc.extparamval epv "
			+ " JOIN epv.extparam ep "
			+ " where Lower(epvs.name) = Lower(:name) "
			+ " and ep.project.prjid = :prjid")
	ExtParamValSet findByNameAndProject(@Param("prjid") int prjid, @Param("name") String name);
	
	@Query("select distinct epvs from ExtParamValSet epvs"
			+ " JOIN epvs.extparamvalsetcomps epvsc "
			+ " JOIN epvsc.extparamval epv "
			+ " JOIN epv.extparam ep "
			+ " where ep.project.prjid = :prjid")
	List<ExtParamValSet> findByProject(@Param("prjid") int prjid);
}
