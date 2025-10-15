package com.exe201.color_bites_be.config;

import com.exe201.color_bites_be.exception.RateLimitException;
import com.exe201.color_bites_be.service.IRateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to apply rate limiting on specific endpoints
 * Sets custom headers for rate limit information
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private IRateLimitService rateLimitService;

    private static final String[] RATE_LIMITED_PATHS = {
            "/api/restaurants/nearby",
            "/api/restaurants/in-bounds",
            "/api/restaurants/reverse-geocode"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();

        // Check if this endpoint should be rate limited
        boolean shouldRateLimit = false;
        for (String path : RATE_LIMITED_PATHS) {
            if (requestURI.contains(path)) {
                shouldRateLimit = true;
                break;
            }
        }

        if (!shouldRateLimit) {
            return true;
        }

        // Get client identifier (IP address)
        String clientIp = getClientIp(request);

        // Check rate limit
        boolean allowed = rateLimitService.isAllowed(clientIp);
        int remaining = rateLimitService.getRemainingRequests(clientIp);
        long resetTime = rateLimitService.getResetTime(clientIp);

        // Set rate limit headers
        response.setHeader("X-RateLimit-Limit", "100");
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        response.setHeader("X-RateLimit-Reset", String.valueOf(resetTime));

        if (!allowed) {
            throw new RateLimitException("Quá nhiều yêu cầu. Vui lòng thử lại sau.");
        }

        return true;
    }

    /**
     * Get client IP address from request
     * Checks X-Forwarded-For header first (for proxied requests)
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

