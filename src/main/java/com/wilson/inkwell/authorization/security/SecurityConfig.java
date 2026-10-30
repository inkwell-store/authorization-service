package com.wilson.inkwell.authorization.security;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.wilson.inkwell.authorization.enums.RoleEnum;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        String BASE_URL = "/api/auth/";

        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, BASE_URL + "me/email").hasAuthority(RoleEnum.ROLE_CUSTOMER.toString())
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.POST, BASE_URL + "create-account").permitAll()
                .requestMatchers(HttpMethod.POST, BASE_URL + "login").permitAll()
                .requestMatchers(HttpMethod.POST, BASE_URL + "refresh").permitAll()
                .requestMatchers(HttpMethod.POST, BASE_URL + "logout").permitAll()
                .requestMatchers(HttpMethod.GET, BASE_URL + "public-hello").permitAll()
                .requestMatchers(HttpMethod.GET, "/.well-known/jwks.json").permitAll()
                .anyRequest().authenticated())

                // .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .oauth2ResourceServer(oauth -> oauth
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())) // registering the converter
                );

        return httpSecurity.build();
    }

    @Bean
    SecureRandom secureRandom() {
        return new SecureRandom();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        // used to map the claim "roles" from my custom jwt to something
        // spring can work with
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix(""); // remove the "scope" prefix

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return authenticationConverter;
    }
}
