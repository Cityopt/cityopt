package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.Algorithm;

@Repository
public interface AlgorithmRepository extends JpaRepository<Algorithm, Integer>{

}
