package com.example.IndiChessBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for matchmaking queue status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueStatusDTO {

    private boolean inQueue;
    private String timeControl;
    private long queuedAt; // Timestamp in milliseconds
    private int estimatedWaitTime; // Seconds
    private String message;

    public QueueStatusDTO(boolean inQueue, String message) {
        this.inQueue = inQueue;
        this.message = message;
    }
}
