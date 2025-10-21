package com.exe201.color_bites_be.service;

/**
 * Interface định nghĩa các phương thức gửi thông báo
 * Hỗ trợ Email, SMS, Push notification
 */
public interface INotificationService {
    
    /**
     * Gửi thông báo khi có đơn hàng mới
     * TODO: Define Order entity when implementing
     */
    // void sendOrderCreated(Order order);
    
    /**
     * Gửi thông báo khi có comment mới
     * TODO: Define Comment entity reference
     */
    // void sendNewComment(Comment comment);
    
    /**
     * Gửi thông báo khi có reaction mới
     * TODO: Define Reaction entity reference
     */
    // void sendNewReaction(Reaction reaction);
    
    /**
     * Gửi email reset mật khẩu
     */
    void sendPasswordReset(String email, String resetLink);
    
    /**
     * Gửi email xác nhận tài khoản
     */
    void sendAccountVerification(String email, String verificationLink);
    
    /**
     * Gửi thông báo khuyến mãi
     */
    void sendPromotionNotification(String accountId, String promotionInfo);
    
    /**
     * Gửi thông báo hết hạn subscription
     */
    void sendSubscriptionExpiry(String accountId, String expiryDate);
}
