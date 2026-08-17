package com.wilson.inkwell.authorization.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.wilson.inkwell.authorization.exception.InternalServerErrorException;

@Configuration
public class JwtConfig {

    @Bean
    JwtEncoder generateJwtEnconder() {

        JWK jwk = new RSAKey.Builder(getPublicKey())
                            .privateKey(getPrivateKey())
                            .build();

        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }


    @Bean
    JWKSet jwkSet() {
        RSAKey.Builder builder = new RSAKey.Builder(getPublicKey())
        .keyUse(KeyUse.SIGNATURE)
        .algorithm(JWSAlgorithm.RS256)
        .keyID("inkwell-key1"); // TODO this is temporary until I implement a rotation key service
        return new JWKSet(builder.build());
    }

    private RSAPublicKey getPublicKey() {

        try {

            String key = new String(
                    new ClassPathResource("/keys/inkwell_public.pem").getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);

            key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                     .replace("-----END PUBLIC KEY-----", "")
                     .replaceAll("\\s", "");
            
            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);

            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            throw new InternalServerErrorException(e.getMessage());
        }

    }

    private RSAPrivateKey getPrivateKey() {

        try {

            String key = new String(
                    new ClassPathResource("/keys/inkwell_private.pem").getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);

            key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                     .replace("-----END PRIVATE KEY-----", "")
                     .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);

            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            throw new InternalServerErrorException(e.getMessage());
        }

    }

}
