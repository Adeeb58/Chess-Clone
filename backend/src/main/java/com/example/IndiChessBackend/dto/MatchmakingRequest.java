package com.example.IndiChessBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for matchmaking requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchmakingRequest {

    private String timeControl; // "STANDARD", "RAPID", "BLITZ"
}
