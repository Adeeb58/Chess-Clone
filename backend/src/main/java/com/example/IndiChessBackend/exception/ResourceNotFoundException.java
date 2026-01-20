package com.example.IndiChessBackend.exception;

/**
 * Exception thrown when a requested resource (User, Game, etc.) is not found
 */
public class ResourceNotFoundException extends CustomException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s not found with identifier: %s", resourceType, identifier));
    }

    public ResourceNotFoundException(String resourceType, Long id) {
        super(String.format("%s not found with id: %d", resourceType, id));
    }
}
