package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.ExtParamValScenGen;

@Repository
public interface ExtParamValScenGenRepository extends JpaRepository<ExtParamValScenGen, Integer> {

}
