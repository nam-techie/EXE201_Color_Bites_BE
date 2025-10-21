package com.exe201.color_bites_be.scheduler;

import com.exe201.color_bites_be.service.impl.SubscriptionServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler để xử lý các tác vụ liên quan đến subscription
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduler {
    
    private final SubscriptionServiceImpl subscriptionService;
    
    /**
     * Chạy hằng ngày lúc 00:30 để expire subscriptions hết hạn
     * Cron: "0 30 0 * * ?" = 0 giây, 30 phút, 0 giờ, mọi ngày, mọi tháng, mọi năm
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void expireSubscriptionsDaily() {
        log.info("Starting daily subscription expiration job at 00:30");
        try {
            subscriptionService.expireSubscriptions();
            log.info("Completed daily subscription expiration job successfully");
        } catch (Exception e) {
            log.error("Error during daily subscription expiration job: ", e);
        }
    }
    
    /**
     * Chạy mỗi 4 giờ để kiểm tra subscriptions (backup job)
     * Cron: "0 0 0/4 * * ?" = mỗi 4 giờ
     */
    @Scheduled(cron = "0 0 0/4 * * ?")
    public void checkSubscriptionsBackup() {
        log.info("Starting backup subscription check job every 4 hours");
        try {
            subscriptionService.expireSubscriptions();
            log.info("Completed backup subscription check job successfully");
        } catch (Exception e) {
            log.error("Error during backup subscription check: ", e);
        }
    }
    
    /**
     * Chạy mỗi 30 phút để kiểm tra subscriptions sắp hết hạn (warning job)
     * Cron: "0 0/30 * * * ?" = mỗi 30 phút
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void checkExpiringSubscriptions() {
        log.info("Starting expiring subscription check job every 30 minutes");
        try {
            // Có thể thêm logic gửi thông báo cho user sắp hết hạn
            // subscriptionService.notifyExpiringSubscriptions();
            log.info("Completed expiring subscription check job successfully");
        } catch (Exception e) {
            log.error("Error during expiring subscription check: ", e);
        }
    }
}