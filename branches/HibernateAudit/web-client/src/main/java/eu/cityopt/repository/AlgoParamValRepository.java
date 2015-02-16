package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.AlgoParamVal;

@Repository
public interface AlgoParamValRepository extends JpaRepository<AlgoParamVal, Integer> {

}
