package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {

}
