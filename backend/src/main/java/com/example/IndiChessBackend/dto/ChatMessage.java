package com.example.IndiChessBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for chat messages in games
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private Long gameId;
    private String sender;
    private String message;
    private long timestamp;
    private MessageType type;

    public enum MessageType {
        CHAT, // Regular chat message
        SYSTEM, // System message (e.g., "Player joined")
        GAME_EVENT // Game event (e.g., "Checkmate!")
    }

    public ChatMessage(Long gameId, String sender, String message, MessageType type) {
        this.gameId = gameId;
        this.sender = sender;
        this.message = message;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }
}
