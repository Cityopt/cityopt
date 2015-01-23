package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.Component;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Integer> {
	@Query("select c from Component c where Lower(c.name) like CONCAT('%',Lower(:name),'%')")
	List<Component> findByName(@Param("name") String name);
}
