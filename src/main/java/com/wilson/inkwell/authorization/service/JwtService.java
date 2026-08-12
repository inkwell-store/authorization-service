package com.wilson.inkwell.authorization.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    
    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    /**
     * Method that receives a map of claims and packs them together into a
     * jwt string.
     */
    public String generateJwt(Map<String, Object> claims) {

        Instant issuedAt = Instant.now();
        long EXPIRATION_TIME_SECONDS = 3600; 

        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                                                   .issuedAt(issuedAt)
                                                   .expiresAt(issuedAt.plusSeconds(EXPIRATION_TIME_SECONDS))
                                                   .issuer("http://auth-service:8081");

        claims.forEach((key, value) -> builder.claim(key, value));

        JwtClaimsSet claimSet = builder.build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claimSet)).getTokenValue();
    }

}
