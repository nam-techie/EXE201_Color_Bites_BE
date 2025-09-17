package com.exe201.color_bites_be.service;

/**
 * Interface định nghĩa các phương thức quản lý subscription
 * Xử lý nâng cấp và hạ cấp gói dịch vụ
 */
public interface ISubscriptionService {
    
    /**
     * Nâng cấp lên Premium sau khi thanh toán thành công
     */
    void upgradeToPremium(String accountId);
    
    /**
     * Hạ cấp về Free (khi hết hạn hoặc hủy subscription)
     */
    void downgradeToFree(String accountId);
    
    /**
     * Kiểm tra subscription plan hiện tại
     */
    String getCurrentSubscriptionPlan(String accountId);
    
    /**
     * Kiểm tra có quyền Premium không
     */
    boolean hasPremiumAccess(String accountId);
}
