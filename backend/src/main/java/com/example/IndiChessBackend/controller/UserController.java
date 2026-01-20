package com.example.IndiChessBackend.controller;

import com.example.IndiChessBackend.model.DTO.UserStatsDTO;
import com.example.IndiChessBackend.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final GameService gameService;

    @GetMapping("/stats")
    public ResponseEntity<UserStatsDTO> getUserStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(gameService.getUserStats(username));
    }
}
