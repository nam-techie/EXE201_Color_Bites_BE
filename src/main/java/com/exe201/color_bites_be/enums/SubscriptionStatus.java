package com.exe201.color_bites_be.enums;

/**
 * Enum for subscription status values
 * Used in Subscription entity
 */
public enum SubscriptionStatus {
    ACTIVE,    // Subscription is active and valid
    EXPIRED,   // Subscription has expired
    CANCELED   // Subscription was canceled by user
}
