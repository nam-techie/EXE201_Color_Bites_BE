package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.service.IRateLimitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory rate limiter using sliding window algorithm
 * Tracks requests per IP address within a configurable time window
 */
@Service
public class RateLimitServiceImpl implements IRateLimitService {

    @Value("${app.rate-limit.max-requests:100}")
    private int maxRequests;

    @Value("${app.rate-limit.time-window-seconds:60}")
    private int timeWindowSeconds;

    @Value("${app.rate-limit.enabled:true}")
    private boolean enabled;

    // Map: identifier -> deque of request timestamps
    private final Map<String, Deque<Long>> requestTimestamps = new ConcurrentHashMap<>();

    @Override
    public boolean isAllowed(String identifier) {
        if (!enabled) {
            return true;
        }

        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - (timeWindowSeconds * 1000L);

        // Get or create deque for this identifier
        Deque<Long> timestamps = requestTimestamps.computeIfAbsent(identifier, k -> new LinkedList<>());

        synchronized (timestamps) {
            // Remove timestamps outside the current window
            while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                timestamps.pollFirst();
            }

            // Check if limit exceeded
            if (timestamps.size() >= maxRequests) {
                return false;
            }

            // Add current request timestamp
            timestamps.addLast(currentTime);
            return true;
        }
    }

    @Override
    public int getRemainingRequests(String identifier) {
        if (!enabled) {
            return maxRequests;
        }

        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - (timeWindowSeconds * 1000L);

        Deque<Long> timestamps = requestTimestamps.get(identifier);
        if (timestamps == null) {
            return maxRequests;
        }

        synchronized (timestamps) {
            // Count valid timestamps in current window
            long validCount = timestamps.stream()
                    .filter(ts -> ts >= windowStart)
                    .count();
            return Math.max(0, maxRequests - (int) validCount);
        }
    }

    @Override
    public long getResetTime(String identifier) {
        Deque<Long> timestamps = requestTimestamps.get(identifier);
        if (timestamps == null || timestamps.isEmpty()) {
            return System.currentTimeMillis() / 1000 + timeWindowSeconds;
        }

        synchronized (timestamps) {
            if (timestamps.isEmpty()) {
                return System.currentTimeMillis() / 1000 + timeWindowSeconds;
            }
            // Reset time is when the oldest timestamp expires
            long oldestTimestamp = timestamps.peekFirst();
            return (oldestTimestamp / 1000) + timeWindowSeconds;
        }
    }

    /**
     * Cleanup expired entries every 5 minutes to prevent memory leak
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void cleanupExpiredEntries() {
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - (timeWindowSeconds * 1000L);

        requestTimestamps.entrySet().removeIf(entry -> {
            Deque<Long> timestamps = entry.getValue();
            synchronized (timestamps) {
                // Remove old timestamps
                while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                    timestamps.pollFirst();
                }
                // Remove entry if no recent requests
                return timestamps.isEmpty();
            }
        });
    }
}

