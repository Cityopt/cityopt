package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.AlgoParamVal;

@Repository
public interface AlgoParamValRepository extends JpaRepository<AlgoParamVal, Integer> {

}
