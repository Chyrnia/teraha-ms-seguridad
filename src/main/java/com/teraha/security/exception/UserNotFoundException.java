package com.teraha.security.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }

	public UserNotFoundException(String username){
		super("User with username '" + username + "' not found");
	}
}
