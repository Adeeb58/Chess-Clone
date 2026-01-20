package com.example.IndiChessBackend.service;

import com.example.IndiChessBackend.exception.GameStateException;
import com.example.IndiChessBackend.exception.InvalidMoveException;
import com.example.IndiChessBackend.exception.ResourceNotFoundException;
import com.example.IndiChessBackend.model.Game;
import com.example.IndiChessBackend.model.GameStatus;
import com.example.IndiChessBackend.model.User;
import com.example.IndiChessBackend.repo.GameRepo;
import com.example.IndiChessBackend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepo gameRepo;
    private final UserRepo userRepo;
    private final GameEngineService gameEngineService;

    public Game createGame(String username) {
        return createGame(username, com.example.IndiChessBackend.model.TimeControl.STANDARD);
    }

    public Game createGame(String username, com.example.IndiChessBackend.model.TimeControl timeControl) {
        User player = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        Game game = new Game();
        game.setWhitePlayer(player);
        game.setFen(gameEngineService.getInitialFen()); // Use Engine
        game.setStatus(GameStatus.WAITING);
        game.setCurrentTurn("WHITE");
        game.setTimeControl(timeControl);
        game.setWhiteTimeRemaining(timeControl.getInitialTimeSeconds());
        game.setBlackTimeRemaining(timeControl.getInitialTimeSeconds());
        return gameRepo.save(game);
    }

    public Game joinGame(Long gameId, String username) {
        Game game = gameRepo.findById(java.util.Objects.requireNonNull(gameId))
                .orElseThrow(() -> new ResourceNotFoundException("Game", gameId));
        User player = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));

        if (game.getStatus() != GameStatus.WAITING) {
            throw new GameStateException("Game is not available to join");
        }

        if (game.getWhitePlayer().getUsername().equals(username)) {
            throw new GameStateException("You are already in this game");
        }

        game.setBlackPlayer(player);
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setLastMoveTime(System.currentTimeMillis());
        return gameRepo.save(game);
    }

    public Game makeMove(Long gameId, String username, String from, String to, String promotion) {
        Game game = gameRepo.findById(java.util.Objects.requireNonNull(gameId))
                .orElseThrow(() -> new ResourceNotFoundException("Game", gameId));

        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new GameStateException("Game is not in progress");
        }

        // Validate turn
        boolean isWhite = game.getWhitePlayer().getUsername().equals(username);
        if (isWhite && !"WHITE".equals(game.getCurrentTurn())) {
            throw new GameStateException("Not your turn");
        }
        if (!isWhite && !"BLACK".equals(game.getCurrentTurn())) {
            throw new GameStateException("Not your turn");
        }

        // Apply move using Engine
        try {
            String newFen = gameEngineService.applyMove(game.getFen(), from, to, promotion);
            // Calculate time elapsed and apply increment
            long now = System.currentTimeMillis();
            if (game.getLastMoveTime() != null) {
                long elapsedSeconds = (now - game.getLastMoveTime()) / 1000;
                if (isWhite) {
                    long newTime = game.getWhiteTimeRemaining() - elapsedSeconds;
                    // Add increment after move (if not first move)
                    if (game.getTimeControl() != null) {
                        newTime += game.getTimeControl().getIncrementSeconds();
                    }
                    game.setWhiteTimeRemaining(Math.max(0, newTime));
                } else {
                    long newTime = game.getBlackTimeRemaining() - elapsedSeconds;
                    // Add increment after move (if not first move)
                    if (game.getTimeControl() != null) {
                        newTime += game.getTimeControl().getIncrementSeconds();
                    }
                    game.setBlackTimeRemaining(Math.max(0, newTime));
                }
            }
            game.setLastMoveTime(now);

            game.setPreviousFen(game.getFen()); // Save current state for undo
            game.setFen(newFen);

            // Check Game Over
            GameEngineService.GameResult result = gameEngineService.getGameResult(newFen);
            if (result != GameEngineService.GameResult.IN_PROGRESS) {
                game.setStatus(GameStatus.COMPLETED);
                if (result == GameEngineService.GameResult.WHITE_WINS) {
                    game.setStatusMessage("White Wins by Checkmate!");
                } else if (result == GameEngineService.GameResult.BLACK_WINS) {
                    game.setStatusMessage("Black Wins by Checkmate!");
                } else {
                    game.setStatusMessage("Draw / Stalemate");
                }
            } else if (game.getWhiteTimeRemaining() <= 0) {
                game.setStatus(GameStatus.COMPLETED);
                game.setStatusMessage("Black Wins by Timeout!");
            } else if (game.getBlackTimeRemaining() <= 0) {
                game.setStatus(GameStatus.COMPLETED);
                game.setStatusMessage("White Wins by Timeout!");
            }

            game.setCurrentTurn(isWhite ? "BLACK" : "WHITE");

            // Append move to PGN-like history (UCI format space separated)
            String moveStr = from + to + (promotion != null ? promotion : "");
            if (game.getPgn() == null || game.getPgn().isEmpty()) {
                game.setPgn(moveStr);
            } else {
                game.setPgn(game.getPgn() + " " + moveStr);
            }

            return gameRepo.save(game);
        } catch (IllegalArgumentException e) {
            throw new InvalidMoveException("Invalid move: " + e.getMessage());
        }
    }

    public Game getGame(Long gameId) {
        return gameRepo.findById(java.util.Objects.requireNonNull(gameId))
                .orElseThrow(() -> new ResourceNotFoundException("Game", gameId));
    }

    public Game resignGame(Long gameId, String username) {
        Game game = gameRepo.findById(java.util.Objects.requireNonNull(gameId))
                .orElseThrow(() -> new ResourceNotFoundException("Game", gameId));

        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new GameStateException("Game is not in progress");
        }

        User white = game.getWhitePlayer();
        User black = game.getBlackPlayer();

        if (white.getUsername().equals(username)) {
            game.setStatus(GameStatus.COMPLETED);
            game.setStatusMessage("Black wins by Resignation");
        } else if (black != null && black.getUsername().equals(username)) {
            game.setStatus(GameStatus.COMPLETED);
            game.setStatusMessage("White wins by Resignation");
        } else {
            throw new GameStateException("You are not part of this game");
        }

        return gameRepo.save(game);
    }

    public Game undoLastMove(Long gameId) {
        Game game = gameRepo.findById(java.util.Objects.requireNonNull(gameId))
                .orElseThrow(() -> new ResourceNotFoundException("Game", gameId));

        if (game.getPreviousFen() == null) {
            throw new GameStateException("No move to undo");
        }

        // Restore FEN and flip turn back
        game.setFen(game.getPreviousFen());
        game.setPreviousFen(null); // Limit to 1 undo for simplicity
        game.setStatus(GameStatus.IN_PROGRESS); // If it was checkmate, it's not anymore
        game.setStatusMessage(null);
        game.setCurrentTurn(game.getCurrentTurn().equals("WHITE") ? "BLACK" : "WHITE");

        return gameRepo.save(game);
    }

    public com.example.IndiChessBackend.model.DTO.UserStatsDTO getUserStats(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        // Fetch all games involving the user
        // Note: repo method usage assumes we pass the user for both arguments to get
        // games where they are white OR black
        java.util.List<Game> games = gameRepo.findByWhitePlayerOrBlackPlayer(user, user);

        int wins = 0;
        int losses = 0;
        int draws = 0;

        for (Game g : games) {
            // Only count finished games
            if (g.getStatus() == GameStatus.COMPLETED || g.getStatus() == GameStatus.FINISHED) {
                String msg = g.getStatusMessage() != null ? g.getStatusMessage().toLowerCase() : "";
                boolean isWhite = g.getWhitePlayer().equals(user);

                if (msg.contains("draw") || msg.contains("stalemate")) {
                    draws++;
                } else if (msg.contains("white wins")) {
                    if (isWhite)
                        wins++;
                    else
                        losses++;
                } else if (msg.contains("black wins")) {
                    if (!isWhite)
                        wins++;
                    else
                        losses++;
                }
            }
        }

        return new com.example.IndiChessBackend.model.DTO.UserStatsDTO(wins + losses + draws, wins, losses, draws);
    }
}
