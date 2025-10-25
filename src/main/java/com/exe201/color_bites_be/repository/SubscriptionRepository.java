package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Subscription;
import com.exe201.color_bites_be.enums.SubscriptionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    
    /**
     * Tìm subscription active hiện tại của user
     */
    Optional<Subscription> findByAccountIdAndStatus(String accountId, SubscriptionStatus status);
    
    /**
     * Tìm tất cả subscriptions của user
     */
    List<Subscription> findByAccountIdOrderByCreatedAtDesc(String accountId);
    
    /**
     * Tìm subscriptions đã hết hạn cần update status
     */
    @Query("{'expiresAt': {$lte: ?0}, 'status': 'ACTIVE'}")
    List<Subscription> findExpiredActiveSubscriptions(LocalDateTime now);
    
    /**
     * Kiểm tra user có subscription active không
     */
    boolean existsByAccountIdAndStatus(String accountId, SubscriptionStatus status);
}
