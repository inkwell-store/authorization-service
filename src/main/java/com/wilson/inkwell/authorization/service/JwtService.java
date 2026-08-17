package com.wilson.inkwell.authorization.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
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

    @Value("${jwt.issuer}")
    private String issuer;

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
                                                   .issuer(issuer); 

        claims.forEach((key, value) -> builder.claim(key, value));
        JwtClaimsSet claimSet = builder.build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256)
                                    .keyId("inkwell-key-1")
                                    .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claimSet)).getTokenValue();
    }

}
