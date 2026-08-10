package com.wilson.inkwell.authorization.controller;

import org.springframework.web.bind.annotation.RestController;

import com.wilson.inkwell.authorization.dto.CredentialCreationForm;
import com.wilson.inkwell.authorization.service.CredentialQueryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class AuthController {

    private final CredentialQueryService credentialQueryService;

    public AuthController(CredentialQueryService credentialQueryService) {
        this.credentialQueryService = credentialQueryService;
    }

    @PostMapping("/api/create-account")
    public ResponseEntity<Void> processNewCredentialRequest(@RequestBody CredentialCreationForm form) {
        credentialQueryService.registerNewCredentials(form);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/public-hello")
    public String getMethodName() {
        return "Hello from public endpoint";
    }
    
}
