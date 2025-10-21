package com.exe201.color_bites_be.service;

/**
 * Interface for rate limiting service
 * Tracks and enforces request limits per client (by IP address)
 */
public interface IRateLimitService {
    
    /**
     * Check if a request from the given identifier is allowed
     * @param identifier Client identifier (e.g., IP address)
     * @return true if request is allowed, false if rate limit exceeded
     */
    boolean isAllowed(String identifier);
    
    /**
     * Get the number of remaining requests for the identifier
     * @param identifier Client identifier
     * @return number of remaining requests
     */
    int getRemainingRequests(String identifier);
    
    /**
     * Get the reset time for the rate limit window
     * @param identifier Client identifier
     * @return epoch time in seconds when the window resets
     */
    long getResetTime(String identifier);
}

