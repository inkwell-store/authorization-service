package com.wilson.inkwell.authorization.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A record that models a Credential Form, with an email
 * and password fields.
 * 
 * @param email is the user email
 * @param password is the user password
 */
public record CredentialForm(
        
    @NotBlank(message = "email is required")
    @Email(message = "email must be properly formatted")
    String email, 
    
    @NotBlank(message = "password is required")
    @Size(min = 6, max = 100, message = "password must have between 6 and 100 characters inclusive" )
    String password
) {
    
}
