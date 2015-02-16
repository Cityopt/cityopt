package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.AppUser;

@Repository("userRepository")
public interface AppUserRepository extends JpaRepository<AppUser,Integer> {
	@Query("select u from AppUser u where Lower(u.name) like CONCAT('%',Lower(:userName),'%')")
	List<AppUser> findByUserName(@Param("userName") String userName);
	
	@Query("select u from AppUser u where u.name = :userName and u.password = :password")
	AppUser authenticateUser(@Param("userName") String userName,
			@Param("password") String password);
	
}
