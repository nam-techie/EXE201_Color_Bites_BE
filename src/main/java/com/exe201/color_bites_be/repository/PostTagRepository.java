package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.PostTag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepository extends MongoRepository<PostTag, String> {
    
    // Tìm tất cả tag của một bài viết
    List<PostTag> findByPostId(String postId);
    
    // Tìm tất cả bài viết của một tag
    List<PostTag> findByTagId(String tagId);
    
    // Xóa tất cả tag của một bài viết
    void deleteByPostId(String postId);
    
    // Xóa một tag cụ thể của một bài viết
    void deleteByPostIdAndTagId(String postId, String tagId);
    
    // Kiểm tra tag đã được gán cho bài viết chưa
    boolean existsByPostIdAndTagId(String postId, String tagId);
    
    // Tìm theo danh sách postId
    List<PostTag> findByPostIdIn(List<String> postIds);
} 