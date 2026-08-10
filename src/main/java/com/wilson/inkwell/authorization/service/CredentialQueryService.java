package com.wilson.inkwell.authorization.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wilson.inkwell.authorization.dto.CredentialCreationForm;
import com.wilson.inkwell.authorization.entity.Credential;
import com.wilson.inkwell.authorization.entity.CredentialRole;
import com.wilson.inkwell.authorization.entity.Role;
import com.wilson.inkwell.authorization.enums.RoleEnum;
import com.wilson.inkwell.authorization.exception.CredentialAlreadyCreatedException;
import com.wilson.inkwell.authorization.exception.RoleNotFoundException;
import com.wilson.inkwell.authorization.repository.CredentialRepository;
import com.wilson.inkwell.authorization.repository.RoleRepository;

import jakarta.transaction.Transactional;

@Service
public class CredentialQueryService {

    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public CredentialQueryService(CredentialRepository credentialRepository, PasswordEncoder passwordEncoder,
            RoleRepository roleRepository) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void registerNewCredentials(CredentialCreationForm form) {

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

            // Because 
            try {
                credentialRepository.save(newCredential);
            } catch (DataIntegrityViolationException e) {
                throw new CredentialAlreadyCreatedException(form.email());
            }
        }
    }

}
