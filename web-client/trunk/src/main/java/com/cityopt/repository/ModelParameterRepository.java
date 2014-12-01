package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.ModelParameter;

@Repository
public interface ModelParameterRepository extends JpaRepository<ModelParameter, Integer> {

}
