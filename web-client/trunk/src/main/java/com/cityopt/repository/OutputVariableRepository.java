package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.OutputVariable;

@Repository
public interface OutputVariableRepository extends JpaRepository<OutputVariable, Integer> {

}

