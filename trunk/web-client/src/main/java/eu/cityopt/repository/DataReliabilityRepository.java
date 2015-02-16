package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.DataReliability;

@Repository
public interface DataReliabilityRepository extends JpaRepository<DataReliability, Integer>{

}
