package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Integer>{
	@Query("select p from Project p where Lower(p.name) like CONCAT('%',Lower(:prjName),'%')")
	List<Project> findByName(@Param("prjName") String prjname);

}