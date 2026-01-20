package com.example.IndiChessBackend.controller;

import com.example.IndiChessBackend.model.DTO.GameStateDTO;
import com.example.IndiChessBackend.model.DTO.MoveRequest;
import com.example.IndiChessBackend.model.Game;
import com.example.IndiChessBackend.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class SocketController {

    private final GameService gameService;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/move")
    // @SendTo removed, doing manual send
    public void handleMove(@Payload MoveRequest moveRequest, Principal principal) {
        // Principal might be null in basic socket setup if not fully integrated with
        // SecurityContext
        // For now trusting client username or passing it in DTO would be safer if Auth
        // not ready
        // But let's assume Auth works (JWT)

        String username = principal != null ? principal.getName() : "anonymous";

        try {
            Game game = gameService.makeMove(
                    moveRequest.getGameId(),
                    username,
                    moveRequest.getFrom(),
                    moveRequest.getTo(),
                    moveRequest.getPromotion());

            GameStateDTO gameState = new GameStateDTO(
                    "MOVE",
                    game.getFen(),
                    game.getPgn(),
                    game.getCurrentTurn(),
                    null, // lastMove
                    game.getWhiteTimeRemaining(),
                    game.getBlackTimeRemaining(),
                    game.getStatus().toString(),
                    game.getStatusMessage());

            // Send to specific game topic
            messagingTemplate.convertAndSend("/topic/game/" + moveRequest.getGameId(), gameState);

        } catch (Exception e) {
            e.printStackTrace();
            GameStateDTO errorState = new GameStateDTO("ERROR", null, null, null, null, 0L, 0L, "ERROR",
                    e.getMessage());
            // Attempt to send error back to same topic (or user specific queue ideally)
            messagingTemplate.convertAndSend("/topic/game/" + moveRequest.getGameId(), errorState);
        }
    }
}
