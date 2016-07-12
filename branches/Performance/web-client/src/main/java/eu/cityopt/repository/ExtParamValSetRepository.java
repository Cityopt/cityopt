package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
	
	@Query(value="delete from extparamvalset where extparamvalsetid in (select distinct extparamvalset.extparamvalsetid from extparamvalset left join extparamvalsetcomp on extparamvalset.extparamvalsetid=extparamvalsetcomp.extparamvalsetid left join optimizationset on extparamvalset.extparamvalsetid=optimizationset.extparamvalsetid where (extparamvalsetcomp.extparamvalid is null and optimizationset.extparamvalsetid is null))",nativeQuery=true)
	@Modifying
	void cleanupExtParamValSets();
}
