package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.Component;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Integer> {

}
