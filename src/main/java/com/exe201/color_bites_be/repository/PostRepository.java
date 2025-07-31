package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
    // Tìm tất cả bài viết của một user
    Page<Post> findByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);
    
    // Tìm tất cả bài viết (không bị xóa)
    Page<Post> findByIsDeletedFalse(Pageable pageable);
    
    // Tìm bài viết theo mood
    Page<Post> findByMoodAndIsDeletedFalse(String mood, Pageable pageable);
    
    // Tìm bài viết có chứa từ khóa trong title hoặc content
    @Query("{'$and': [{'isDeleted': false}, {'$or': [{'title': {$regex: ?0, $options: 'i'}}, {'content': {$regex: ?0, $options: 'i'}}]}]}")
    Page<Post> findByTitleOrContentContainingIgnoreCase(String keyword, Pageable pageable);
    
    // Đếm số bài viết của một user
    long countByAccountIdAndIsDeletedFalse(String accountId);
    
    // Tìm bài viết theo danh sách ID
    List<Post> findByIdInAndIsDeletedFalse(List<String> ids);
} 