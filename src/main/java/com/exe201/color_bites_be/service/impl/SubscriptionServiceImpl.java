package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.enums.SubcriptionPlan;
import com.exe201.color_bites_be.service.ISubscriptionService;
import com.exe201.color_bites_be.service.IUserInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation của ISubscriptionService
 * Xử lý logic quản lý subscription plan
 * Chỉ service này mới có quyền thay đổi subscription plan
 */
@Service
public class SubscriptionServiceImpl implements ISubscriptionService {

    @Autowired
    private IUserInformationService userInformationService;

    @Override
    @Transactional
    public void upgradeToPremium(String accountId) {
        userInformationService.upgradeSubscriptionPlan(accountId, SubcriptionPlan.PREMIUM);
    }

    @Override
    @Transactional
    public void downgradeToFree(String accountId) {
        userInformationService.downgradeToFree(accountId);
    }

    @Override
    public String getCurrentSubscriptionPlan(String accountId) {
        try {
            var userInfo = userInformationService.getUserInformation();
            return userInfo.getSubscriptionPlan();
        } catch (Exception e) {
            return SubcriptionPlan.FREE.name(); // Default fallback
        }
    }

    @Override
    public boolean hasPremiumAccess(String accountId) {
        String currentPlan = getCurrentSubscriptionPlan(accountId);
        return SubcriptionPlan.PREMIUM.name().equals(currentPlan);
    }
}
