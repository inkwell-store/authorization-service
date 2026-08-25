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
import com.wilson.inkwell.authorization.exception.InvalidRefreshTokenException;
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
    public AccessAndRefreshTokenResponse registerNewCredentials(CredentialForm form) {

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

            // now I can generate both tokens
            return new AccessAndRefreshTokenResponse(
                generateJwtAccessTokenForCredential(savedEntity),
                generateRefreshTokenForCredential(savedEntity)
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

        return new AccessAndRefreshTokenResponse(
            generateJwtAccessTokenForCredential(credentialEntity),
            generateRefreshTokenForCredential(credentialEntity)
        );

    }

    /**
     * Returns a new access token using the refresh token passed as parameter.
     * 
     * @param refreshToken a refresh token
     * @return a jwt access token
     */
    @Transactional
    public JwtTokenResponse processRefreshAccessToken(String refreshToken) {

        // In this case just by querying the entity I already establish they
        // should be equal in the first place.
        Optional<RefreshToken> query = refreshTokenRepository.getRefreshTokenEntityByHash(
                refreshTokenService.hashOpaqueToken(refreshToken));

        if (query.isEmpty()) {
            throw new InvalidRefreshTokenException("No RefreshToken entity was found");
        }

        RefreshToken entity = query.get();

        if (entity.getStatus() != RefreshTokenStatusEnum.VALID) {
            throw new InvalidRefreshTokenException("Refresh token is invalid");
        }


        // FIXME this is not necessarily a bug, but it's bad implemented:
        //
        // I need to check if the refresh token is expired. So, I'm saying here the 
        // refresh token is set as expired inside the server ONLY when a request for an
        // access token is made, if no request is performed a refresh token will expire
        // but this change won't be reflected here. I see two solutions:
        // 
        // 1. Use a Task and schedule the removal server-side. This is a problem if the
        // server goes down and I have to reinitiate once it goes up again.
        // 2. Use a a database routine that performs the expiration check, which seems
        // the better alternative.
        //
        // I'll leave it like this for now though and worry about this once I implement 
        // a proper dbms. Also I should see to add token rotation, as of right now a 
        // single refresh token can be used for any number of refresh access token attempts.
        if (entity.getExpiresAt().isBefore(Instant.now())) {

            // set as expired before throwing the exception
            if (entity.getStatus() == RefreshTokenStatusEnum.VALID) {
                entity.setStatus(RefreshTokenStatusEnum.EXPIRED);
                refreshTokenRepository.save(entity);
            }

            throw new InvalidRefreshTokenException("Refresh token is expired");
        }

        return new JwtTokenResponse(generateJwtAccessTokenForCredential(entity.getCredential()));

    }

    /**
     * Generates a new Jwt-encoded AccessToken for the Credential passed as parameter
     * and returns it as a base64 string 
     * 
     * @param credential is the Credential Entity
     * @return the jwt access token as string
     */
    private String generateJwtAccessTokenForCredential(Credential credential) {
        
        // setting up the claims
        Map<String, Object> claims = new HashMap<>();
        Set<String> roles = new HashSet<>();
        credential.getCredentialRoles().forEach(e -> roles.add(e.getRole().getName().toString()));
        claims.put("roles", roles);
        claims.put("sub", credential.getId());

        return jwtService.generateJwt(claims);
    }

    /**
     * Generates a new RefreshToken for the Crendetial passed as parameter, saves it
     * as an <code>RefreshToken</code> entity inside the database and returns it as a base64 string.
     * 
     * @param credential is the Credential entity
     * @return the refresh token as string
     */
    private String generateRefreshTokenForCredential(Credential credential) {
        
        // generate a refresh token 
        String refreshToken = refreshTokenService.generateOpaqueRefreshToken();
        
        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setStatus(RefreshTokenStatusEnum.VALID);
        tokenEntity.setTokenHash(refreshTokenService.hashOpaqueToken(refreshToken));
        tokenEntity.setIssuedAt(Instant.now());
        tokenEntity.setExpiresAt(Instant.now().plus(refreshTokenDurationDays, ChronoUnit.DAYS));
        tokenEntity.setCredential(credential);

        // persist the refresh token
        refreshTokenRepository.save(tokenEntity);

        return refreshToken;
    }

    

}
