package eu.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {

}
