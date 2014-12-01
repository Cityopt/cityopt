package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.AlgoParam;

@Repository
public interface AlgoParamRepository extends JpaRepository<AlgoParam, Integer>{

}
