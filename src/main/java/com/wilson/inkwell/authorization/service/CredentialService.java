package com.wilson.inkwell.authorization.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wilson.inkwell.authorization.dto.AccessAndRefreshTokenResponse;
import com.wilson.inkwell.authorization.dto.CredentialCreationEvent;
import com.wilson.inkwell.authorization.dto.CredentialForm;
import com.wilson.inkwell.authorization.dto.JwtTokenResponse;
import com.wilson.inkwell.authorization.entity.Credential;
import com.wilson.inkwell.authorization.entity.CredentialRole;
import com.wilson.inkwell.authorization.entity.RefreshToken;
import com.wilson.inkwell.authorization.entity.Role;
import com.wilson.inkwell.authorization.enums.RefreshTokenStatusEnum;
import com.wilson.inkwell.authorization.enums.RoleEnum;
import com.wilson.inkwell.authorization.exception.AuthenticationException;
import com.wilson.inkwell.authorization.exception.CredentialAlreadyCreatedException;
import com.wilson.inkwell.authorization.exception.RoleNotFoundException;
import com.wilson.inkwell.authorization.repository.CredentialRepository;
import com.wilson.inkwell.authorization.repository.RefreshTokenRepository;
import com.wilson.inkwell.authorization.repository.RoleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final RabbitMqEventEmitterService rabbitMqService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    @Value("${refreshtoken.duration.days}")
    private int refreshTokenDurationDays;

    @Transactional
    public void registerNewCredentials(CredentialForm form) {

        Credential savedEntity; // used to generate a amq message

        Optional<Credential> credentialQuery = credentialRepository.getCredentialsByEmail(form.email());

        if (credentialQuery.isPresent()) {
            throw new CredentialAlreadyCreatedException(form.email());
        } else {

            Credential newCredential = new Credential();
            newCredential.setEmail(form.email());
            newCredential.setPassword(passwordEncoder.encode(form.password()));
            newCredential.setCreatedAt(Instant.now());

            RoleEnum roleName = RoleEnum.ROLE_CUSTOMER;
            Optional<Role> roleQuery = roleRepository.getRoleByName(roleName);

            if (roleQuery.isEmpty()) {
                throw new RoleNotFoundException(roleName.toString());
            }

            CredentialRole credentialRole = new CredentialRole();
            credentialRole.setCredential(newCredential);
            credentialRole.setRole(roleQuery.get());
            newCredential.getCredentialRoles().add(credentialRole);

            try {
                savedEntity = credentialRepository.save(newCredential);
            } catch (DataIntegrityViolationException e) {
                throw new CredentialAlreadyCreatedException(form.email());
            }

            // send the message after a successful persistance operation
            rabbitMqService.sendCredentialCreationMessage(
                new CredentialCreationEvent(savedEntity.getEmail(), savedEntity.getId())
            );

        }
    }

    @Transactional
    public AccessAndRefreshTokenResponse processLoginAttemp(CredentialForm form) {

        Optional<Credential> credentialQuery = credentialRepository.getCredentialsByEmail(form.email());
        Credential credentialEntity;

        if (credentialQuery.isEmpty()) {
            throw new AuthenticationException("Credential for email: " + form.email() + " was not found.");
        } else {
            credentialEntity = credentialQuery.get();
        }

        if (!passwordEncoder.matches(form.password(), credentialEntity.getPassword())) {
            throw new AuthenticationException("The password provided doesn't match the stored password.");
        }

        // setting up the claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", credentialEntity.getId());
        Set<String> roles = new HashSet<>();
        credentialEntity.getCredentialRoles().forEach(e -> roles.add(e.getRole().getName().toString()));
        claims.put("roles", roles);

        // generate a refresh token 
        String refreshToken = refreshTokenService.generateOpaqueRefreshToken();
        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setStatus(RefreshTokenStatusEnum.VALID);
        tokenEntity.setTokenHash(refreshTokenService.hashOpaqueToken(refreshToken));
        tokenEntity.setIssuedAt(Instant.now());
        tokenEntity.setExpiresAt(Instant.now().plus(refreshTokenDurationDays, ChronoUnit.DAYS));
        tokenEntity.setCredential(credentialEntity);

        // persist the refresh token
        refreshTokenRepository.save(tokenEntity);

        return new AccessAndRefreshTokenResponse(
            jwtService.generateJwt(claims),
            refreshToken
        );

    }

}
