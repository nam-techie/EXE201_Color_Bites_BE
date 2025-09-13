package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, String> {
    
    // Tìm quiz theo account ID
    @Query("{'accountId': ?0}")
    Page<Quiz> findByAccountId(String accountId, Pageable pageable);
    
    // Tìm quiz mới nhất của user
    @Query(value = "{'accountId': ?0}", sort = "{'createdAt': -1}")
    Optional<Quiz> findLatestByAccountId(String accountId);
    
    // Tìm quiz theo mood result
    @Query("{'moodResult': ?0}")
    Page<Quiz> findByMoodResult(String moodResult, Pageable pageable);
    
    // Đếm số quiz của user
    @Query(value = "{'accountId': ?0}", count = true)
    long countByAccountId(String accountId);
    
    // Tìm quiz trong khoảng thời gian
    @Query("{'accountId': ?0, 'createdAt': {$gte: ?1, $lte: ?2}}")
    List<Quiz> findByAccountIdAndCreatedAtBetween(String accountId, 
                                                  java.time.LocalDateTime startDate, 
                                                  java.time.LocalDateTime endDate);
    
    // Tìm quiz có recommended restaurants
    @Query("{'recommendedRestaurants': {$exists: true, $ne: []}}")
    Page<Quiz> findQuizzesWithRecommendations(Pageable pageable);
    
    // Tìm quiz theo recommended restaurant
    @Query("{'recommendedRestaurants': {$in: [?0]}}")
    List<Quiz> findByRecommendedRestaurantsContaining(String restaurantId);
    
    // Thống kê mood result phổ biến
    @Query(value = "{}", sort = "{'createdAt': -1}")
    Page<Quiz> findAllOrderByCreatedAtDesc(Pageable pageable);
}
