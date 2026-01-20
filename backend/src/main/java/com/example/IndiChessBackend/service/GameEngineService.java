package com.example.IndiChessBackend.service;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import org.springframework.stereotype.Service;

@Service
public class GameEngineService {

    public boolean isValidMove(String fen, String from, String to) {
        Board board = new Board();
        board.loadFromFen(fen);

        Square fromSquare = Square.fromValue(from.toUpperCase());
        Square toSquare = Square.fromValue(to.toUpperCase());

        try {
            // Generate legal moves
            for (Move move : MoveGenerator.generateLegalMoves(board)) {
                if (move.getFrom() == fromSquare && move.getTo() == toSquare) {
                    return true;
                }
            }
        } catch (MoveGeneratorException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public String makeMove(String fen, String from, String to) {
        Board board = new Board();
        board.loadFromFen(fen);

        Square fromSquare = Square.fromValue(from.toUpperCase());
        Square toSquare = Square.fromValue(to.toUpperCase());
        Move move = new Move(fromSquare, toSquare); // Simplification, need to handle promotion

        board.doMove(move);
        return board.getFen();
    }

    // Improved implementation with proper robust Move creation
    public String applyMove(String fen, String from, String to, String promotion) {
        Board board = new Board();
        board.loadFromFen(fen);

        // Construct move string like "e2e4" or "a7a8q"
        String moveStr = from.toLowerCase() + to.toLowerCase();
        if (promotion != null && !promotion.isEmpty()) {
            moveStr += promotion.toLowerCase();
        }

        // Find matching legal move
        try {
            for (Move legal : MoveGenerator.generateLegalMoves(board)) {
                if (legal.toString().equals(moveStr)) {
                    board.doMove(legal);
                    return board.getFen();
                }
            }
        } catch (MoveGeneratorException e) {
            throw new IllegalArgumentException("Error generating moves: " + e.getMessage());
        }

        throw new IllegalArgumentException("Illegal move: " + moveStr);
    }

    public String getInitialFen() {
        return new Board().getFen();
    }

    public enum GameResult {
        IN_PROGRESS,
        WHITE_WINS, // Checkmate
        BLACK_WINS, // Checkmate
        DRAW, // Stalemate, Insufficient Material, etc.
    }

    public GameResult getGameResult(String fen) {
        // Robust check for missing Kings in FEN string to avoid library parsing errors
        // where loadFromFen might throw exception
        if (fen != null) {
            String piecePlacement = fen.split(" ")[0];
            if (!piecePlacement.contains("K"))
                return GameResult.BLACK_WINS;
            if (!piecePlacement.contains("k"))
                return GameResult.WHITE_WINS;
        }

        Board board = new Board();
        try {
            board.loadFromFen(fen);
        } catch (Exception e) {
            // Check failsafe again just in case, though string check should cover it
            return GameResult.IN_PROGRESS;
        }

        // Standard checks using library logic
        if (board.getKingSquare(
                com.github.bhlangonijr.chesslib.Side.WHITE) == com.github.bhlangonijr.chesslib.Square.NONE) {
            return GameResult.BLACK_WINS;
        }
        if (board.getKingSquare(
                com.github.bhlangonijr.chesslib.Side.BLACK) == com.github.bhlangonijr.chesslib.Square.NONE) {
            return GameResult.WHITE_WINS;
        }

        if (board.isMated()) {
            return board.getSideToMove() == com.github.bhlangonijr.chesslib.Side.WHITE ? GameResult.BLACK_WINS
                    : GameResult.WHITE_WINS;
        } else if (board.isStaleMate() || board.isDraw() || board.isInsufficientMaterial()) {
            return GameResult.DRAW;
        }
        return GameResult.IN_PROGRESS;
    }

    public boolean isGameOver(String fen) {
        return getGameResult(fen) != GameResult.IN_PROGRESS;
    }
}