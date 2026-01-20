package com.example.IndiChessBackend.controller;

import com.example.IndiChessBackend.model.Game;
import com.example.IndiChessBackend.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(gameService.createGame(userDetails.getUsername()));
    }

    @PostMapping("/join/{gameId}")
    public ResponseEntity<Game> joinGame(@PathVariable Long gameId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(gameService.joinGame(gameId, userDetails.getUsername()));
    }

    @PostMapping("/move/{gameId}")
    public ResponseEntity<Game> makeMove(@PathVariable Long gameId,
            @RequestBody Map<String, String> moveData,
            @AuthenticationPrincipal UserDetails userDetails) {
        String from = moveData.get("from");
        String to = moveData.get("to");
        String promotion = moveData.get("promotion");

        return ResponseEntity.ok(gameService.makeMove(gameId, userDetails.getUsername(), from, to, promotion));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGame(gameId));
    }

    @PostMapping("/{gameId}/resign")
    public ResponseEntity<Game> resign(@PathVariable Long gameId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(gameService.resignGame(gameId, userDetails.getUsername()));
    }

    @PostMapping("/{gameId}/undo")
    public ResponseEntity<Game> undo(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.undoLastMove(gameId));
    }
}