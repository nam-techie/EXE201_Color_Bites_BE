package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.enums.SubcriptionPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service xử lý subscription plan
 * Chỉ service này mới có quyền thay đổi subscription plan
 */
@Service
public class SubscriptionService {

    @Autowired
    private UserInformationService userInformationService;

    /**
     * Nâng cấp lên Premium sau khi thanh toán thành công
     */
    @Transactional
    public void upgradeToPremium(String accountId) {
        userInformationService.upgradeSubscriptionPlan(accountId, SubcriptionPlan.PREMIUM);
    }

    /**
     * Hạ cấp về Free (khi hết hạn hoặc hủy subscription)
     */
    @Transactional
    public void downgradeToFree(String accountId) {
        userInformationService.downgradeToFree(accountId);
    }

    /**
     * Kiểm tra subscription plan hiện tại
     */
    public String getCurrentSubscriptionPlan(String accountId) {
        try {
            var userInfo = userInformationService.getUserInformation(accountId);
            return userInfo.getSubscriptionPlan();
        } catch (Exception e) {
            // Nếu chưa có thông tin user, mặc định là FREE
            return SubcriptionPlan.FREE.name();
        }
    }

    /**
     * Kiểm tra user có phải Premium không
     */
    public boolean isPremiumUser(String accountId) {
        return SubcriptionPlan.PREMIUM.name().equals(getCurrentSubscriptionPlan(accountId));
    }
}
