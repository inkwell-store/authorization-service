package com.wilson.inkwell.authorization.exception;

public class CredentialAlreadyCreatedException extends RuntimeException {

    public CredentialAlreadyCreatedException(String email) {
        super("The email " + email + " is already registered.");
    }

}
