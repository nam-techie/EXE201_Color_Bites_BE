package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.entity.Subscription;
import com.exe201.color_bites_be.entity.Subscription.SubscriptionStatus;
import com.exe201.color_bites_be.enums.SubcriptionPlan;
import com.exe201.color_bites_be.repository.SubscriptionRepository;
import com.exe201.color_bites_be.service.ISubscriptionService;
import com.exe201.color_bites_be.service.IUserInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation của ISubscriptionService
 * Xử lý logic quản lý subscription plan theo flow hệ thống
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements ISubscriptionService {

    private final IUserInformationService userInformationService;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional
    public void upgradeToPremium(String accountId) {
        log.info("Upgrading account {} to PREMIUM", accountId);
        
        // Hủy subscription hiện tại nếu có
        Optional<Subscription> currentSubscription = subscriptionRepository
            .findByAccountIdAndStatus(accountId, SubscriptionStatus.ACTIVE);
        
        if (currentSubscription.isPresent()) {
            Subscription existing = currentSubscription.get();
            existing.setStatus(SubscriptionStatus.CANCELED);
            existing.setUpdatedAt(LocalDateTime.now());
            subscriptionRepository.save(existing);
        }
        
        // Tạo subscription mới với PREMIUM
        LocalDateTime now = LocalDateTime.now();
        Subscription newSubscription = Subscription.builder()
            .accountId(accountId)
            .plan(SubcriptionPlan.PREMIUM)
            .status(SubscriptionStatus.ACTIVE)
            .startsAt(now)
            .expiresAt(now.plusDays(30)) // 30 ngày
            .createdAt(now)
            .updatedAt(now)
            .build();
        
        subscriptionRepository.save(newSubscription);
        
        // Cập nhật UserInformation
        userInformationService.upgradeSubscriptionPlan(accountId, SubcriptionPlan.PREMIUM);
        
        log.info("Successfully upgraded account {} to PREMIUM, expires at {}", 
            accountId, newSubscription.getExpiresAt());
    }

    @Override
    @Transactional
    public void downgradeToFree(String accountId) {
        log.info("Downgrading account {} to FREE", accountId);
        
        // Tìm và expire subscription hiện tại
        Optional<Subscription> currentSubscription = subscriptionRepository
            .findByAccountIdAndStatus(accountId, SubscriptionStatus.ACTIVE);
        
        if (currentSubscription.isPresent()) {
            Subscription existing = currentSubscription.get();
            existing.setStatus(SubscriptionStatus.EXPIRED);
            existing.setUpdatedAt(LocalDateTime.now());
            subscriptionRepository.save(existing);
        }
        
        // Cập nhật UserInformation về FREE
        userInformationService.downgradeToFree(accountId);
        
        log.info("Successfully downgraded account {} to FREE", accountId);
    }

    @Override
    public String getCurrentSubscriptionPlan(String accountId) {
        try {
            // Kiểm tra subscription active
            Optional<Subscription> activeSubscription = subscriptionRepository
                .findByAccountIdAndStatus(accountId, SubscriptionStatus.ACTIVE);
            
            if (activeSubscription.isPresent()) {
                Subscription subscription = activeSubscription.get();
                
                // Kiểm tra có hết hạn chưa
                if (subscription.getExpiresAt().isBefore(LocalDateTime.now())) {
                    // Tự động expire
                    downgradeToFree(accountId);
                    return SubcriptionPlan.FREE.name();
                }
                
                return subscription.getPlan().name();
            }
            
            return SubcriptionPlan.FREE.name();
        } catch (Exception e) {
            log.error("Error getting subscription plan for account {}: ", accountId, e);
            return SubcriptionPlan.FREE.name(); // Default fallback
        }
    }

    @Override
    public boolean hasPremiumAccess(String accountId) {
        String currentPlan = getCurrentSubscriptionPlan(accountId);
        return SubcriptionPlan.PREMIUM.name().equals(currentPlan);
    }
    
    /**
     * Phương thức để scheduler gọi - expire subscriptions hết hạn
     */
    @Transactional
    public void expireSubscriptions() {
        log.info("Running subscription expiration job");
        
        List<Subscription> expiredSubscriptions = subscriptionRepository
            .findExpiredActiveSubscriptions(LocalDateTime.now());
        
        for (Subscription subscription : expiredSubscriptions) {
            log.info("Expiring subscription {} for account {}", 
                subscription.getId(), subscription.getAccountId());
            
            // Update subscription status
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscription.setUpdatedAt(LocalDateTime.now());
            subscriptionRepository.save(subscription);
            
            // Downgrade user to FREE
            userInformationService.downgradeToFree(subscription.getAccountId());
        }
        
        log.info("Expired {} subscriptions", expiredSubscriptions.size());
    }
}
