package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ExtParam;

@Repository
public interface ExtParamRepository extends JpaRepository<ExtParam, Integer>{
	@Query("select e from ExtParam e where e.name=:name")
	List<ExtParam> findByName(@Param("name") String name);
	
	@Query("select e from ExtParam e where e.name=:name and e.project.prjid=:prjid")
	ExtParam findByName(@Param("name") String name,@Param("prjid") int prjid);
}
