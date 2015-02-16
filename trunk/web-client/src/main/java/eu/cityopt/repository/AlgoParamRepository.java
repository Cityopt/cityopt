package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.AlgoParam;

@Repository
public interface AlgoParamRepository extends JpaRepository<AlgoParam, Integer>{

}
