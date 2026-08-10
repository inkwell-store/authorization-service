package com.wilson.inkwell.authorization.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wilson.inkwell.authorization.entity.Role;
import com.wilson.inkwell.authorization.enums.RoleEnum;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    @Query("SELECT r FROM Role r WHERE r.name = :name")
    Optional<Role> getRoleByName(@Param("name") RoleEnum name);

}
