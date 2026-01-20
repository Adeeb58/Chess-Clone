package com.example.IndiChessBackend.exception;

/**
 * Exception thrown when attempting to create a user with a username or email
 * that already exists
 */
public class UserAlreadyExistsException extends CustomException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String field, String value) {
        super(String.format("User with %s '%s' already exists", field, value));
    }
}
