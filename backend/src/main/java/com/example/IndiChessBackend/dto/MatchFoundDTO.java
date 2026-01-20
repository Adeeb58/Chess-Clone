package com.example.IndiChessBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for match found notifications
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchFoundDTO {

    private Long gameId;
    private String opponentUsername;
    private String playerColor; // "WHITE" or "BLACK"
    private String timeControl;
    private String message;

    public MatchFoundDTO(Long gameId, String opponentUsername, String playerColor, String timeControl) {
        this.gameId = gameId;
        this.opponentUsername = opponentUsername;
        this.playerColor = playerColor;
        this.timeControl = timeControl;
        this.message = "Match found! Starting game...";
    }
}
