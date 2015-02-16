package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.OptSearchConst;

@Repository
public interface OptSearchConstRepository extends JpaRepository<OptSearchConst, Integer> {

}

