package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.Component;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Integer> {
	@Query("select c from Component c where Lower(c.name) like CONCAT('%',Lower(:name),'%') order by c.name")
	List<Component> findByNameContaining(@Param("name") String name);
	
	@Query("select c from Component c where prjid = :prjid order by c.name")
	List<Component> findByProject(@Param("prjid") int prjid);
	
	@Query("select c from Component c where prjid = :prjid order by c.name")
	Page<Component> findByProject(@Param("prjid") int prjid,Pageable page);
	
	@Query("select c from Component c where Lower(c.name) = Lower(:name) "
			+ " and prjid = :prjid")
	Component findByNameAndProject(@Param("prjid") int prjid, @Param("name") String name);
}
