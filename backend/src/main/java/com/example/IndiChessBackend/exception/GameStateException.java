package com.example.IndiChessBackend.exception;

/**
 * Exception thrown when an invalid game state operation is attempted
 * (e.g., joining a game that's already in progress, making a move when it's not
 * your turn)
 */
public class GameStateException extends CustomException {

    public GameStateException(String message) {
        super(message);
    }
}
