package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.OptConstraint;

@Repository
public interface OptConstraintRepository extends JpaRepository<OptConstraint, Integer> {

}

