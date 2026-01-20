package com.example.IndiChessBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "white_player_id")
    private User whitePlayer;

    @ManyToOne
    @JoinColumn(name = "black_player_id")
    private User blackPlayer;

    private String fen; // Board state

    @Lob
    private String pgn; // Move history

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    private String currentTurn; // "WHITE" or "BLACK"

    @Enumerated(EnumType.STRING)
    @Column(name = "time_control")
    private TimeControl timeControl; // STANDARD, RAPID, BLITZ

    private Long whiteTimeRemaining; // Seconds
    private Long blackTimeRemaining; // Seconds
    private Long lastMoveTime; // Timestamp in millis

    private String statusMessage; // e.g. "White Wins by Checkmate"
    private String previousFen; // For single-step Undo
}
