package com.wilson.inkwell.authorization.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {
    
    @Bean
    PasswordEncoder generatePasswordEnconder() {
        // There are many ways to improve security for the hashing, but they become
        // exponentially expensive, so here I'm using the default configurations.  
        return new BCryptPasswordEncoder();
    }
}
