package com.wilson.inkwell.authorization.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.wilson.inkwell.authorization.exception.InternalServerErrorException;

@Service
public class RefreshTokenService {
    
    private final SecureRandom secureRandom;

    public RefreshTokenService(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    /**
     * Generates a cryptographic safe random string using {@link SecureRandom} 
     * implementation.
     * 
     * @return the token as a base64 string
     */
    public String generateOpaqueRefreshToken() {

        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                     .withoutPadding()
                     .encodeToString(bytes);
    }

    /**
     * Receives an opaque token and hashes it using a cryptographic hash function,
     * then it returns it as a base64 string.
     * 
     * @param token the opake token
     * @return a base64 hash string
     */
    public String hashOpaqueToken(String token) {
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder()
                         .withoutPadding()
                         .encodeToString(encodedHash);

        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }


}
