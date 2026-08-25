package com.wilson.inkwell.authorization.controller;

import org.springframework.web.bind.annotation.RestController;

import com.wilson.inkwell.authorization.dto.AccessAndRefreshTokenResponse;
import com.wilson.inkwell.authorization.dto.CredentialForm;
import com.wilson.inkwell.authorization.dto.JwtTokenResponse;
import com.wilson.inkwell.authorization.service.CredentialService;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final CredentialService credentialService;
    
    @Value("${refreshtoken.duration.days}")
    private int refreshTokenDurationDays;

    @PostMapping("create-account")
    public ResponseEntity<Void> processNewCredentialRequest(@RequestBody CredentialForm form) {
        credentialService.registerNewCredentials(form);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("login")
    public ResponseEntity<JwtTokenResponse> processLoginRequest(@RequestBody CredentialForm form) {
        AccessAndRefreshTokenResponse tokens = credentialService.processLoginAttemp(form);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", tokens.refreshToken())
                                              .httpOnly(true)
                                              // TODO change these values for production environments using https
                                              .secure(false)
                                              .sameSite("Lax")
                                              .path("/api/auth")
                                              .maxAge(Duration.ofDays(refreshTokenDurationDays))
                                              .build();
        
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, cookie.toString())
                             .body(new JwtTokenResponse(tokens.accessToken()));
    }

    @GetMapping("public-hello")
    public String getMethodName() {
        return "Hello from public endpoint";
    }
    
}
