package com.example.IndiChessBackend.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameStateDTO {
    private String type; // "MOVE", "GAME_OVER", "ERROR"
    private String fen;
    private String pgn;
    private String currentTurn; // "WHITE", "BLACK"
    private String lastMove; // "e4", "Nf3"
    private Long whiteTimeLeft;
    private Long blackTimeLeft;
    private String status;
    private String message;
}
