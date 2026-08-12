package com.wilson.inkwell.authorization.controller;

import org.springframework.web.bind.annotation.RestController;

import com.wilson.inkwell.authorization.dto.CredentialForm;
import com.wilson.inkwell.authorization.dto.JwtTokenResponse;
import com.wilson.inkwell.authorization.service.CredentialService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/auth/")
public class AuthController {

    private final CredentialService credentialService;

    public AuthController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @PostMapping("create-account")
    public ResponseEntity<Void> processNewCredentialRequest(@RequestBody CredentialForm form) {
        credentialService.registerNewCredentials(form);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("login")
    public ResponseEntity<JwtTokenResponse> processLoginRequest(@RequestBody CredentialForm form) {
        return ResponseEntity.status(HttpStatus.OK).body(credentialService.generateJwtTokenForUser(form));
    }

    @GetMapping("public-hello")
    public String getMethodName() {
        return "Hello from public endpoint";
    }
    
}
