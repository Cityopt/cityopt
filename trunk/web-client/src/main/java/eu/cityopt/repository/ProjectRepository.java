package eu.cityopt.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Integer>,
		JpaSpecificationExecutor<Project> {
	@Query("select distinct p from Project p where Lower(p.name) like CONCAT('%',Lower(:prjName),'%')")
	List<Project> findByNameContaining(@Param("prjName") String prjname);
	
	Project findByName(String prjname);
	
	List<Project> findByNameLikeIgnoreCase(String prjname);
	
	List<Project> findByCreatedonBetween(Date from, Date to);

	List<Project> findByNameContainingOrDescriptionContaining(String sc1, String sc2);
	
//	@Query("select distinct  p from Project p LEFT JOIN FETCH p.components comps where p.id = :prjID")
//	public Project findOne(@Param("prjID") Integer prjID);
//	
//	@Query("select distinct p from Project p LEFT JOIN FETCH p.components comps")	
//	public List<Project> findAll();
	
	@Query("select distinct p from Project p LEFT JOIN FETCH p.scenarios scens")    
    public List<Project> findAllWithScenarios();
	
}
