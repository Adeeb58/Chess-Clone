package com.example.IndiChessBackend.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoveRequest {
    private String from;
    private String to;
    private String promotion; // "q", "r", "b", "n"
    private Long gameId;
}
