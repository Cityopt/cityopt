package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.InputParameter;

@Repository
public interface InputParameterRepository extends JpaRepository<InputParameter,Integer> { 

}
