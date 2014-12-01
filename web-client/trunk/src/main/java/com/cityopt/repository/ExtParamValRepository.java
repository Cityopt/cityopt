package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.ExtParamVal;

@Repository
public interface ExtParamValRepository extends JpaRepository<ExtParamVal, Integer>{

}
