package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    
    // Tìm favorite theo account và restaurant
    @Query("{'accountId': ?0, 'restaurantId': ?1}")
    Optional<Favorite> findByAccountIdAndRestaurantId(String accountId, String restaurantId);

    // Tìm tất cả favorite của user
    @Query("{'accountId': ?0}")
    Page<Favorite> findByAccountId(String accountId, Pageable pageable);

    // Tìm tất cả favorite của restaurant
    @Query("{'restaurantId': ?0}")
    List<Favorite> findByRestaurantId(String restaurantId);

    // Đếm số favorite của restaurant
    @Query(value = "{'restaurantId': ?0}", count = true)
    long countByRestaurantId(String restaurantId);

    // Đếm số favorite của user
    @Query(value = "{'accountId': ?0}", count = true)
    long countByAccountId(String accountId);

    // Kiểm tra user đã favorite restaurant chưa
    @Query(value = "{'accountId': ?0, 'restaurantId': ?1}", exists = true)
    boolean existsByAccountIdAndRestaurantId(String accountId, String restaurantId);

    // Xóa favorite theo account và restaurant
    @Query(value = "{'accountId': ?0, 'restaurantId': ?1}")
    void deleteByAccountIdAndRestaurantId(String accountId, String restaurantId);

    // Xóa tất cả favorite của restaurant
    @Query(value = "{'restaurantId': ?0}")
    void deleteByRestaurantId(String restaurantId);

    // Lấy danh sách restaurant ID mà user đã favorite
    @Query(value = "{'accountId': ?0}", fields = "{'restaurantId': 1}")
    List<Favorite> findRestaurantIdsByAccountId(String accountId);
    
    // Lấy top restaurant được favorite nhiều nhất
    @Query(value = "{}", sort = "{'createdAt': -1}")
    Page<Favorite> findRecentFavorites(Pageable pageable);
}
