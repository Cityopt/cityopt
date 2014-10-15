package com.ait.repository;

import java.util.List;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ait.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Integer>{
	@Query("select p from Project p where Lower(p.prjname) like CONCAT('%',Lower(:prjName),'%')")
	List<Project> findByName(@Param("prjName") String prjname);

}
