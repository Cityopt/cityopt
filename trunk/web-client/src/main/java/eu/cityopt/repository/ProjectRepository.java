package eu.cityopt.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Integer>,
		JpaSpecificationExecutor<Project> {

	List<Project> findByNameContainingIgnoreCase(String prjname,Sort sort);
	
	List<Project> findByNameContainingIgnoreCase(String prjname);
	
	Project findByName(String prjname);
		
	List<Project> findByNameLikeIgnoreCase(String prjname,Sort sort);
	
	List<Project> findByCreatedonBetween(Date from, Date to,Sort sort);

	List<Project> findByNameContainingOrDescriptionContaining(String sc1, String sc2,Sort sort);
	
//	@Query("select distinct  p from Project p LEFT JOIN FETCH p.components comps where p.id = :prjID")
//	public Project findOne(@Param("prjID") Integer prjID);
//	
//	@Query("select distinct p from Project p LEFT JOIN FETCH p.components comps")	
//	public List<Project> findAll();
	
	@Query("select distinct p from Project p LEFT JOIN FETCH p.scenarios scens order by p.name, scens.name")    
    public List<Project> findAllWithScenarios();
	
}
