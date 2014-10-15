package com.ait.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ait.model.Usergroupproject;

@Repository("userGroupProjectRepository")
public interface UserGroupProjectRepository extends JpaRepository<Usergroupproject, Integer> {

}
