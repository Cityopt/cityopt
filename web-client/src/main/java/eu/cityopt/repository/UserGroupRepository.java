package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.UserGroup;

@Repository("userGroupRepository")
public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {
	@Query("select u from UserGroup u where Lower(u.name) like CONCAT('%',Lower(:userGroupName),'%')")
	List<UserGroup> findByGroupNameContaining(@Param("userGroupName") String userGroupName);
}
