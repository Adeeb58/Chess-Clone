package com.example.IndiChessBackend.controller;

import com.example.IndiChessBackend.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for chat functionality
 */
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle chat messages sent to /app/chat
     * Broadcasts to all players in the game
     */
    @MessageMapping("/chat")
    public void sendMessage(
            @Payload ChatMessage chatMessage,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Set sender from authenticated user
        if (userDetails != null) {
            chatMessage.setSender(userDetails.getUsername());
        }

        // Set timestamp
        chatMessage.setTimestamp(System.currentTimeMillis());

        // Set type to CHAT if not specified
        if (chatMessage.getType() == null) {
            chatMessage.setType(ChatMessage.MessageType.CHAT);
        }

        // Broadcast to all subscribers of this game
        messagingTemplate.convertAndSend(
                "/topic/game/" + chatMessage.getGameId() + "/chat",
                chatMessage);

        System.out.println("💬 Chat message in game " + chatMessage.getGameId() +
                " from " + chatMessage.getSender() + ": " + chatMessage.getMessage());
    }

    /**
     * Send system message to a game
     */
    public void sendSystemMessage(Long gameId, String message) {
        ChatMessage systemMessage = new ChatMessage(
                gameId,
                "System",
                message,
                ChatMessage.MessageType.SYSTEM);

        messagingTemplate.convertAndSend(
                "/topic/game/" + gameId + "/chat",
                systemMessage);
    }

    /**
     * Send game event message to a game
     */
    public void sendGameEvent(Long gameId, String event) {
        ChatMessage eventMessage = new ChatMessage(
                gameId,
                "Game",
                event,
                ChatMessage.MessageType.GAME_EVENT);

        messagingTemplate.convertAndSend(
                "/topic/game/" + gameId + "/chat",
                eventMessage);
    }
}
