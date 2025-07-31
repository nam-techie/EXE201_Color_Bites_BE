package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends MongoRepository<Reaction, String> {
    
    // Tìm reaction của một user cho một bài viết
    Optional<Reaction> findByPostIdAndAccountId(String postId, String accountId);
    
    // Tìm tất cả reaction của một bài viết
    List<Reaction> findByPostId(String postId);
    
    // Tìm tất cả reaction của một user
    List<Reaction> findByAccountId(String accountId);
    
    // Đếm số reaction của một bài viết
    long countByPostId(String postId);
    
    // Đếm số reaction theo loại của một bài viết
    long countByPostIdAndReactionType(String postId, String reactionType);
    
    // Kiểm tra user đã reaction chưa
    boolean existsByPostIdAndAccountId(String postId, String accountId);
    
    // Xóa reaction của một user cho một bài viết
    void deleteByPostIdAndAccountId(String postId, String accountId);
} 