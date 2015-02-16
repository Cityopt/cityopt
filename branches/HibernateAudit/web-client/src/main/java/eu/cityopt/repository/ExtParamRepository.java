package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.ExtParam;

@Repository
public interface ExtParamRepository extends JpaRepository<ExtParam, Integer>{

}
