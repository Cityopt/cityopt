package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.Type;

@Repository
public interface TypeRepository extends JpaRepository<Type, Integer> {

}
