package com.example.IndiChessBackend.model;

/**
 * Enum for different time control types in chess
 */
public enum TimeControl {
    STANDARD(0, 0, "Standard - No time limit"),
    RAPID(600, 0, "Rapid - 10 minutes"),
    BLITZ(180, 1, "Blitz - 3 minutes + 1 second increment");

    private final long initialTimeSeconds;
    private final long incrementSeconds;
    private final String description;

    TimeControl(long initialTimeSeconds, long incrementSeconds, String description) {
        this.initialTimeSeconds = initialTimeSeconds;
        this.incrementSeconds = incrementSeconds;
        this.description = description;
    }

    public long getInitialTimeSeconds() {
        return initialTimeSeconds;
    }

    public long getIncrementSeconds() {
        return incrementSeconds;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get TimeControl from string (case-insensitive)
     */
    public static TimeControl fromString(String value) {
        if (value == null) {
            return STANDARD;
        }

        try {
            return TimeControl.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return STANDARD;
        }
    }
}
