package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    // Tìm tất cả comment của một bài viết (không bị xóa)
    Page<Comment> findByPostIdAndIsDeletedFalseOrderByCreatedAtDesc(String postId, Pageable pageable);
    
    // Tìm comment gốc (parentComment = null) của một bài viết
    Page<Comment> findByPostIdAndParentCommentIsNullAndIsDeletedFalseOrderByCreatedAtDesc(String postId, Pageable pageable);
    
    // Tìm reply của một comment
    List<Comment> findByParentCommentAndIsDeletedFalseOrderByCreatedAtAsc(String parentCommentId);
    
    // Tìm tất cả comment của một user
    Page<Comment> findByAccountIdAndIsDeletedFalseOrderByCreatedAtDesc(String accountId, Pageable pageable);
    
    // Đếm số comment của một bài viết
    long countByPostIdAndIsDeletedFalse(String postId);
    
    // Đếm số comment của một user
    long countByAccountIdAndIsDeletedFalse(String accountId);
    
    // Tìm comment theo depth
    @Query("{'postId': ?0, 'depth': ?1, 'isDeleted': false}")
    List<Comment> findByPostIdAndDepthAndIsDeletedFalse(String postId, Integer depth);
} 