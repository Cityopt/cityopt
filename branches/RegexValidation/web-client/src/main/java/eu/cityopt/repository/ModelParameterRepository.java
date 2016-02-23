package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ModelParameter;

@Repository
public interface ModelParameterRepository extends JpaRepository<ModelParameter, Integer> {

}
