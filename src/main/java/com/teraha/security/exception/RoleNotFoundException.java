package com.teraha.security.exception;

public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String name) {
        super("Role not found: " + name);
    }
}
