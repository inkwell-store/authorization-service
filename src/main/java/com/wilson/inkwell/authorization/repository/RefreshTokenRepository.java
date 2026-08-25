package com.wilson.inkwell.authorization.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wilson.inkwell.authorization.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
}
