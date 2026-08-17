package com.wilson.inkwell.authorization.controller;

import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWKSet;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class JwksController {

    private final JWKSet jwkSet;

    public JwksController(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getMethodName() {
        return jwkSet.toJSONObject();
    }

}
