package com.wilson.inkwell.authorization.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wilson.inkwell.authorization.dto.CredentialForm;
import com.wilson.inkwell.authorization.dto.JwtTokenResponse;
import com.wilson.inkwell.authorization.entity.Credential;
import com.wilson.inkwell.authorization.entity.CredentialRole;
import com.wilson.inkwell.authorization.entity.Role;
import com.wilson.inkwell.authorization.enums.RoleEnum;
import com.wilson.inkwell.authorization.exception.AuthenticationException;
import com.wilson.inkwell.authorization.exception.CredentialAlreadyCreatedException;
import com.wilson.inkwell.authorization.exception.RoleNotFoundException;
import com.wilson.inkwell.authorization.repository.CredentialRepository;
import com.wilson.inkwell.authorization.repository.RoleRepository;

import jakarta.transaction.Transactional;

@Service
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    public CredentialService(CredentialRepository credentialRepository, PasswordEncoder passwordEncoder,
            RoleRepository roleRepository, JwtService jwtService) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public void registerNewCredentials(CredentialForm form) {

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
                credentialRepository.save(newCredential);
            } catch (DataIntegrityViolationException e) {
                throw new CredentialAlreadyCreatedException(form.email());
            }
        }
    }

    public JwtTokenResponse generateJwtTokenForUser(CredentialForm form) {

        Optional<Credential> credentialQuery = credentialRepository.getCredentialsByEmail(form.email());

        if (credentialQuery.isEmpty()) {
            throw new AuthenticationException("Credential for email: " + form.email() + " was not found.");
        }

        if (!passwordEncoder.matches(form.password(), credentialQuery.get().getPassword())) {
            throw new AuthenticationException("The password provided doesn't match the stored password.");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", credentialQuery.get().getId());

        Set<String> roles = new HashSet<>();
        credentialQuery.get().getCredentialRoles().forEach(e -> roles.add(e.getRole().getName().toString()));
        claims.put("roles", roles);

        return new JwtTokenResponse(jwtService.generateJwt(claims));

    }

}
