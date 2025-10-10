package com.exe201.color_bites_be.service;

import java.util.Map;

/**
 * Interface for geocoding services (reverse geocoding)
 * Converts coordinates to human-readable addresses
 */
public interface IGeocodingService {
    
    /**
     * Reverse geocode coordinates to address
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @return Map containing: provider, formatted, components
     */
    Map<String, Object> reverseGeocode(double latitude, double longitude);
}

