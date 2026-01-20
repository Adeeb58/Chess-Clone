package com.example.IndiChessBackend.service;

import com.example.IndiChessBackend.dto.MatchFoundDTO;
import com.example.IndiChessBackend.dto.QueueStatusDTO;
import com.example.IndiChessBackend.model.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Service for managing matchmaking queue and pairing players
 */
@Service
@RequiredArgsConstructor
public class MatchmakingService {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    // Queue: timeControl -> (username -> queueEntry)
    private final Map<String, ConcurrentHashMap<String, QueueEntry>> queues = new ConcurrentHashMap<>();

    // Scheduled executor for timeout handling
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * Queue entry containing user info and queue time
     */
    private static class QueueEntry {
        long queuedAt;
        ScheduledFuture<?> timeoutTask;

        QueueEntry(long queuedAt, ScheduledFuture<?> timeoutTask) {
            this.queuedAt = queuedAt;
            this.timeoutTask = timeoutTask;
        }
    }

    /**
     * Add a player to the matchmaking queue
     */
    public QueueStatusDTO joinQueue(String username, String timeControl) {
        // Normalize time control (make final for lambda)
        final String finalTimeControl = timeControl != null ? timeControl.toUpperCase() : "STANDARD";

        // Check if user is already in any queue
        if (isUserInAnyQueue(username)) {
            return new QueueStatusDTO(true, "Already in queue");
        }

        // Get or create queue for this time control
        ConcurrentHashMap<String, QueueEntry> queue = queues.computeIfAbsent(
                finalTimeControl,
                k -> new ConcurrentHashMap<>());

        // Try to find a match immediately
        String opponent = findOpponent(queue, username);

        if (opponent != null) {
            // Match found! Create game
            return createMatch(username, opponent, finalTimeControl, queue);
        }

        // No match found, add to queue
        long queuedAt = System.currentTimeMillis();

        // Schedule timeout task (90 seconds)
        ScheduledFuture<?> timeoutTask = scheduler.schedule(
                () -> handleTimeout(username, finalTimeControl),
                90,
                TimeUnit.SECONDS);

        QueueEntry entry = new QueueEntry(queuedAt, timeoutTask);
        queue.put(username, entry);

        System.out.println("✅ " + username + " joined " + finalTimeControl + " queue. Queue size: " + queue.size());

        return new QueueStatusDTO(
                true,
                finalTimeControl,
                queuedAt,
                90,
                "Searching for opponent...");
    }

    /**
     * Remove a player from the matchmaking queue
     */
    public QueueStatusDTO leaveQueue(String username) {
        for (Map.Entry<String, ConcurrentHashMap<String, QueueEntry>> queueEntry : queues.entrySet()) {
            ConcurrentHashMap<String, QueueEntry> queue = queueEntry.getValue();
            QueueEntry entry = queue.remove(username);

            if (entry != null) {
                // Cancel timeout task
                if (entry.timeoutTask != null) {
                    entry.timeoutTask.cancel(false);
                }

                System.out.println("❌ " + username + " left " + queueEntry.getKey() + " queue");
                return new QueueStatusDTO(false, "Left queue");
            }
        }

        return new QueueStatusDTO(false, "Not in queue");
    }

    /**
     * Get queue status for a user
     */
    public QueueStatusDTO getQueueStatus(String username) {
        for (Map.Entry<String, ConcurrentHashMap<String, QueueEntry>> queueEntry : queues.entrySet()) {
            QueueEntry entry = queueEntry.getValue().get(username);

            if (entry != null) {
                long waitTime = (System.currentTimeMillis() - entry.queuedAt) / 1000;
                int remainingTime = Math.max(0, 90 - (int) waitTime);

                return new QueueStatusDTO(
                        true,
                        queueEntry.getKey(),
                        entry.queuedAt,
                        remainingTime,
                        "Searching for opponent... (" + remainingTime + "s remaining)");
            }
        }

        return new QueueStatusDTO(false, "Not in queue");
    }

    /**
     * Find an opponent in the queue (excluding self)
     */
    private String findOpponent(ConcurrentHashMap<String, QueueEntry> queue, String username) {
        for (String potentialOpponent : queue.keySet()) {
            if (!potentialOpponent.equals(username)) {
                return potentialOpponent;
            }
        }
        return null;
    }

    /**
     * Create a match between two players
     */
    private QueueStatusDTO createMatch(
            String player1,
            String player2,
            String timeControl,
            ConcurrentHashMap<String, QueueEntry> queue) {
        try {
            // Remove both players from queue
            QueueEntry entry1 = queue.remove(player1);
            QueueEntry entry2 = queue.remove(player2);

            // Cancel timeout tasks
            if (entry1 != null && entry1.timeoutTask != null) {
                entry1.timeoutTask.cancel(false);
            }
            if (entry2 != null && entry2.timeoutTask != null) {
                entry2.timeoutTask.cancel(false);
            }

            // Randomly assign colors
            boolean player1IsWhite = Math.random() < 0.5;
            String whitePlayer = player1IsWhite ? player1 : player2;
            String blackPlayer = player1IsWhite ? player2 : player1;

            // Create game with time control
            com.example.IndiChessBackend.model.TimeControl tc = com.example.IndiChessBackend.model.TimeControl
                    .fromString(timeControl);
            Game game = gameService.createGame(whitePlayer, tc);
            game = gameService.joinGame(game.getId(), blackPlayer);

            System.out.println("🎮 Match created! Game ID: " + game.getId() +
                    " | White: " + whitePlayer + " | Black: " + blackPlayer);

            // Notify both players via WebSocket
            MatchFoundDTO player1Notification = new MatchFoundDTO(
                    game.getId(),
                    player2,
                    player1IsWhite ? "WHITE" : "BLACK",
                    timeControl);

            MatchFoundDTO player2Notification = new MatchFoundDTO(
                    game.getId(),
                    player1,
                    player1IsWhite ? "BLACK" : "WHITE",
                    timeControl);

            messagingTemplate.convertAndSendToUser(
                    player1,
                    "/queue/match-found",
                    player1Notification);

            messagingTemplate.convertAndSendToUser(
                    player2,
                    "/queue/match-found",
                    player2Notification);

            return new QueueStatusDTO(false, "Match found! Game ID: " + game.getId());

        } catch (Exception e) {
            System.err.println("❌ Error creating match: " + e.getMessage());
            e.printStackTrace();

            // Re-add players to queue if match creation failed
            long now = System.currentTimeMillis();
            queue.put(player1, new QueueEntry(now, null));
            queue.put(player2, new QueueEntry(now, null));

            return new QueueStatusDTO(true, "Match creation failed, back in queue");
        }
    }

    /**
     * Handle queue timeout (90 seconds elapsed)
     */
    private void handleTimeout(String username, String timeControl) {
        ConcurrentHashMap<String, QueueEntry> queue = queues.get(timeControl);

        if (queue != null) {
            QueueEntry entry = queue.remove(username);

            if (entry != null) {
                System.out.println("⏰ Timeout for " + username + " in " + timeControl + " queue");

                // Notify user via WebSocket
                messagingTemplate.convertAndSendToUser(
                        username,
                        "/queue/timeout",
                        new QueueStatusDTO(false, "No opponent found. Please try again."));
            }
        }
    }

    /**
     * Check if user is in any queue
     */
    private boolean isUserInAnyQueue(String username) {
        for (ConcurrentHashMap<String, QueueEntry> queue : queues.values()) {
            if (queue.containsKey(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get total players in all queues (for monitoring)
     */
    public int getTotalPlayersInQueue() {
        return queues.values().stream()
                .mapToInt(ConcurrentHashMap::size)
                .sum();
    }

    /**
     * Cleanup method (call on shutdown)
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
