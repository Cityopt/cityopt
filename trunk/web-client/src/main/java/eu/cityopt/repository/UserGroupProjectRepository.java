package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.DTO.UserGroupProjectDTO;
import eu.cityopt.model.AppUser;
import eu.cityopt.model.UserGroupProject;

@Repository("userGroupProjectRepository")
public interface UserGroupProjectRepository extends JpaRepository<UserGroupProject, Integer> {

	@Query("select u from UserGroupProject u "
			+ " where u.usergroup.usergroupid = :groupId")
	List<UserGroupProject> findByGroup(@Param("groupId") int groupId);

	@Query("select u from UserGroupProject u "
			+ " where u.project.prjid = :prjId")
	List<UserGroupProject> findByProject(@Param("prjId") int prjId);

	@Query("select u from UserGroupProject u "
			+ " where u.appuser.userid = :userId")
	List<UserGroupProject> findByUser(@Param("userId") int userId);
	
	@Query("select u from UserGroupProject u "
			+ " where u.appuser.name = :userName")
	List<UserGroupProject> findByUser(@Param("userName") String userName);
	
	@Query("select u from UserGroupProject u "
			+ " where u.appuser.userid = :userId"
			+ " and  u.project.prjid = :prjId")
	UserGroupProject findByUserAndProject(@Param("userId") int userId, @Param("prjId") int prjId);
	
	@Query("select u from UserGroupProject u "
			+ " where u.appuser.userid = :userId"
			+ " and  u.project.prjid is null")
	UserGroupProject findByUserAndProjectIsNull(@Param("userId") int userId);
	
	 
	@Query("select u.appuser from UserGroupProject u where u.project.prjid= :prjId")
	List<AppUser> findAppUserOfProject(@Param("prjId") int prjId);
	
}
