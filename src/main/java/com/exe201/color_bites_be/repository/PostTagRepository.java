package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.PostTag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostTagRepository extends MongoRepository<PostTag, String> {
    
    // Tìm tất cả tag của một bài viết
    List<PostTag> findByPostId(String postId);
    
    // Tìm tất cả bài viết có tag
    List<PostTag> findByTagId(String tagId);
    
    // Xóa tất cả tag của một bài viết
    void deleteByPostId(String postId);
    
    // Xóa một tag cụ thể của bài viết
    void deleteByPostIdAndTagId(String postId, String tagId);
    
    // Xóa tất cả liên kết của một tag
    void deleteByTagId(String tagId);
    
    // Kiểm tra bài viết có tag không
    boolean existsByPostIdAndTagId(String postId, String tagId);
    
    // Đếm số bài viết có tag
    long countByTagId(String tagId);
    
    // Lấy danh sách postId theo tagId
    @Query(value = "{'tagId': ?0}", fields = "{'postId': 1}")
    List<PostTag> findPostIdsByTagId(String tagId);
}
