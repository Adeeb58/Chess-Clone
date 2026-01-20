package com.example.IndiChessBackend.controller;

import com.example.IndiChessBackend.dto.MatchmakingRequest;
import com.example.IndiChessBackend.dto.QueueStatusDTO;
import com.example.IndiChessBackend.service.MatchmakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for matchmaking endpoints
 */
@RestController
@RequestMapping("/matchmaking")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    /**
     * Join the matchmaking queue
     */
    @PostMapping("/queue")
    public ResponseEntity<QueueStatusDTO> joinQueue(
            @RequestBody MatchmakingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        String timeControl = request.getTimeControl();

        QueueStatusDTO status = matchmakingService.joinQueue(username, timeControl);
        return ResponseEntity.ok(status);
    }

    /**
     * Leave the matchmaking queue
     */
    @DeleteMapping("/queue")
    public ResponseEntity<QueueStatusDTO> leaveQueue(
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        QueueStatusDTO status = matchmakingService.leaveQueue(username);
        return ResponseEntity.ok(status);
    }

    /**
     * Get current queue status
     */
    @GetMapping("/status")
    public ResponseEntity<QueueStatusDTO> getQueueStatus(
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        QueueStatusDTO status = matchmakingService.getQueueStatus(username);
        return ResponseEntity.ok(status);
    }

    /**
     * Get total players in queue (for monitoring/debugging)
     */
    @GetMapping("/total")
    public ResponseEntity<Integer> getTotalPlayersInQueue() {
        int total = matchmakingService.getTotalPlayersInQueue();
        return ResponseEntity.ok(total);
    }
}
