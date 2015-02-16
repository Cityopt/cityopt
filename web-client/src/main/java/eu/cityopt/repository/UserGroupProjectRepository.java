package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.UserGroupProject;

@Repository("userGroupProjectRepository")
public interface UserGroupProjectRepository extends JpaRepository<UserGroupProject, Integer> {

}
