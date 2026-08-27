package com.wilson.inkwell.authorization.controller;

import org.springframework.web.bind.annotation.RestController;

import com.wilson.inkwell.authorization.dto.AccessAndRefreshTokenResponse;
import com.wilson.inkwell.authorization.dto.CredentialForm;
import com.wilson.inkwell.authorization.dto.JwtTokenResponse;
import com.wilson.inkwell.authorization.service.CredentialService;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final CredentialService credentialService;
    
    @Value("${refreshtoken.duration.days}")
    private int refreshTokenDurationDays;

    @PostMapping("create-account")
    public ResponseEntity<JwtTokenResponse> newCredentialRequest(@Valid @RequestBody CredentialForm form) {

        AccessAndRefreshTokenResponse tokens = credentialService.registerNewCredentials(form);
        
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, generateRefreshTokenCookie(tokens.refreshToken()).toString())
                             .body(new JwtTokenResponse(tokens.accessToken()));
    }

    @PostMapping("login")
    public ResponseEntity<JwtTokenResponse> loginRequest(@Valid @RequestBody CredentialForm form) {
        AccessAndRefreshTokenResponse tokens = credentialService.processLoginAttemp(form);

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, generateRefreshTokenCookie(tokens.refreshToken()).toString())
                             .body(new JwtTokenResponse(tokens.accessToken()));
    }

    @GetMapping("public-hello")
    public String getMethodName() {
        return "Hello from public endpoint";
    }
    
    @PostMapping("refresh")
    public ResponseEntity<JwtTokenResponse> refreshAccessToken(@CookieValue("refresh_token") String refreshToken) {
        System.out.println("the token is: " + refreshToken);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                credentialService.processRefreshAccessToken(refreshToken));
    }
    
    private ResponseCookie generateRefreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                             .httpOnly(true)
                             // TODO change these values for production environments using https
                             .secure(false)
                             .sameSite("Lax")
                             .path("/api/auth")
                             .maxAge(Duration.ofDays(refreshTokenDurationDays))
                             .build();
    }
}
