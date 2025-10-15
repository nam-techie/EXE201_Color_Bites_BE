package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.exception.ServiceUnavailableException;
import com.exe201.color_bites_be.service.IGeocodingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Geocoding service implementation using Nominatim (OpenStreetMap)
 * Includes in-memory caching with TTL
 */
@Service
public class GeocodingServiceImpl implements IGeocodingService {

    @Value("${app.geocoding.nominatim-url:https://nominatim.openstreetmap.org}")
    private String nominatimUrl;

    @Value("${app.geocoding.cache-ttl-minutes:60}")
    private long cacheTtlMinutes;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Cache: key = "lat:lon" (rounded to 6 decimals), value = CacheEntry
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    @Override
    public Map<String, Object> reverseGeocode(double latitude, double longitude) {
        // Round to 6 decimals for cache key
        String cacheKey = String.format("%.6f:%.6f", latitude, longitude);
        
        // Check cache
        CacheEntry cached = cache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached.data;
        }

        // Call Nominatim API
        try {
            Map<String, Object> result = callNominatim(latitude, longitude);
            
            // Cache the result
            cache.put(cacheKey, new CacheEntry(result, cacheTtlMinutes));
            
            return result;
        } catch (Exception e) {
            throw new ServiceUnavailableException("Dịch vụ geocoding tạm thời không khả dụng: " + e.getMessage());
        }
    }

    private Map<String, Object> callNominatim(double latitude, double longitude) {
        String url = String.format("%s/reverse?format=json&lat=%.6f&lon=%.6f&addressdetails=1",
                nominatimUrl, latitude, longitude);

        try {
            // Set User-Agent header (required by Nominatim)
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "ColorBites-App/1.0");
            
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
            
            org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    String.class
            );

            // Parse response
            JsonNode root = objectMapper.readTree(response.getBody());
            
            Map<String, Object> result = new HashMap<>();
            result.put("provider", "nominatim");
            result.put("formatted", root.path("display_name").asText());
            
            // Extract address components
            Map<String, String> components = new HashMap<>();
            JsonNode address = root.path("address");
            if (address != null && !address.isMissingNode()) {
                address.fields().forEachRemaining(entry -> {
                    components.put(entry.getKey(), entry.getValue().asText());
                });
            }
            result.put("components", components);
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Nominatim API", e);
        }
    }

    /**
     * Cache entry with expiration time
     */
    private static class CacheEntry {
        final Map<String, Object> data;
        final long expiresAt;

        CacheEntry(Map<String, Object> data, long ttlMinutes) {
            this.data = data;
            this.expiresAt = System.currentTimeMillis() + (ttlMinutes * 60 * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}

