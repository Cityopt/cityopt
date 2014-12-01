package com.cityopt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cityopt.model.SearchConstraint;

@Repository
public interface SearchConstraintRepository extends JpaRepository<SearchConstraint, Integer> {

}
