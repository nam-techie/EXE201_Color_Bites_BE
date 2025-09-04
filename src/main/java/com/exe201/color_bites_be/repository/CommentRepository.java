package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    // Tìm comment theo ID và chưa bị xóa
    @Query("{'_id': ?0, 'isDeleted': {$ne: true}}")
    Optional<Comment> findByIdAndNotDeleted(String id);
    
    // Tìm tất cả comment gốc của bài viết (parentComment = null) và chưa bị xóa
    @Query("{'postId': ?0, 'parentComment': null, 'isDeleted': {$ne: true}}")
    Page<Comment> findRootCommentsByPostId(String postId, Pageable pageable);
    
    // Tìm tất cả reply của comment cha và chưa bị xóa
    @Query("{'parentComment': ?0, 'isDeleted': {$ne: true}}")
    List<Comment> findRepliesByParentCommentId(String parentCommentId);
    
    // Tìm tất cả comment của bài viết (bao gồm cả reply) và chưa bị xóa
    @Query("{'postId': ?0, 'isDeleted': {$ne: true}}")
    Page<Comment> findByPostIdAndNotDeleted(String postId, Pageable pageable);
    
    // Tìm comment của user trong bài viết
    @Query("{'postId': ?0, 'accountId': ?1, 'isDeleted': {$ne: true}}")
    List<Comment> findByPostIdAndAccountIdAndNotDeleted(String postId, String accountId);
    
    // Đếm số comment gốc của bài viết
    @Query(value = "{'postId': ?0, 'parentComment': null, 'isDeleted': {$ne: true}}", count = true)
    long countRootCommentsByPostId(String postId);
    
    // Đếm tổng số comment của bài viết (bao gồm reply)
    @Query(value = "{'postId': ?0, 'isDeleted': {$ne: true}}", count = true)
    long countAllCommentsByPostId(String postId);
    
    // Đếm số reply của comment
    @Query(value = "{'parentComment': ?0, 'isDeleted': {$ne: true}}", count = true)
    long countRepliesByParentCommentId(String parentCommentId);
    
    // Kiểm tra comment có tồn tại và chưa bị xóa
    @Query(value = "{'_id': ?0, 'isDeleted': {$ne: true}}", exists = true)
    boolean existsByIdAndNotDeleted(String id);
    
    // Tìm comment theo depth (để giới hạn độ sâu nesting)
    @Query("{'postId': ?0, 'depth': ?1, 'isDeleted': {$ne: true}}")
    List<Comment> findByPostIdAndDepth(String postId, Integer depth);
    
    // Xóa tất cả comment của bài viết (soft delete)
    @Query("{'postId': ?0}")
    List<Comment> findAllByPostId(String postId);
}
