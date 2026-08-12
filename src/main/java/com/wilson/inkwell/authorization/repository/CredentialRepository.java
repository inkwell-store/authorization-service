package com.wilson.inkwell.authorization.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wilson.inkwell.authorization.entity.Credential;

public interface CredentialRepository extends JpaRepository<Credential, UUID>{
    
    @Query("SELECT c FROM Credential c WHERE c.email = :email")
    Optional<Credential> getCredentialsByEmail(@Param("email") String email);

}
