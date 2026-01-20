package com.example.IndiChessBackend.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDTO {
    private int gamesPlayed;
    private int wins;
    private int losses;
    private int draws;
}
