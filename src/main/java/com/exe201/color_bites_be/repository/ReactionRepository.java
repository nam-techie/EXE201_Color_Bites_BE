package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends MongoRepository<Reaction, String> {
    
    // Tìm reaction của user cho bài viết
    Optional<Reaction> findByPostIdAndAccountId(String postId, String accountId);
    
    // Tìm tất cả reaction của bài viết
    List<Reaction> findByPostId(String postId);
    
    // Đếm số reaction của bài viết
    long countByPostId(String postId);
    
    // Đếm số reaction theo loại của bài viết
    long countByPostIdAndReaction(String postId, com.exe201.color_bites_be.enums.ReactionType reaction);
    
    // Kiểm tra user đã react bài viết chưa
    boolean existsByPostIdAndAccountId(String postId, String accountId);
    
    // Xóa reaction của user cho bài viết
    void deleteByPostIdAndAccountId(String postId, String accountId);
    
    // Xóa tất cả reaction của bài viết
    void deleteByPostId(String postId);
}
