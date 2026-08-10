package com.wilson.inkwell.authorization.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        
        httpSecurity.authorizeHttpRequests(auth -> auth
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/create-account").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/public-hello").permitAll()
            .anyRequest().authenticated()    
        )

        // .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return httpSecurity.build();
    }

    @Bean
    PasswordEncoder generatePasswordEnconder() {
        // There are many ways to improve security for the hashing, but they become
        // exponentially expensive, so here I'm using the default configurations.  
        return new BCryptPasswordEncoder();
    }

    
}
