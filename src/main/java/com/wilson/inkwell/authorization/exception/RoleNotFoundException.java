package com.wilson.inkwell.authorization.exception;

public class RoleNotFoundException extends RuntimeException {
    
    public RoleNotFoundException(String roleName) {
        super("Role " + roleName + " doesn't exist in the database.");
    }

}
