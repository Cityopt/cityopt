package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ExtParam;

@Repository
public interface ExtParamRepository extends JpaRepository<ExtParam, Integer>{
	@Query("select e from ExtParam e where Lower(e.name) like CONCAT('%',Lower(:name),'%')")
	List<ExtParam> findByName(@Param("name") String name);
}