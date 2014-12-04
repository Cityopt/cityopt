package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.InputParamVal;

@Repository
public interface InputParamValRepository extends JpaRepository<InputParamVal,Integer> {

}
