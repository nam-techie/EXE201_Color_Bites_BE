package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
    // Tìm bài viết chưa bị xóa
    @Query("{'isDeleted':false}")
    Page<Post> findAllActivePosts(Pageable pageable);


    
    // Tìm bài viết của user chưa bị xóa
    @Query("{'accountId': ?0, 'isDeleted': {$ne: true}}")
    Page<Post> findByAccountIdAndNotDeleted(String accountId, Pageable pageable);
    
    // Tìm bài viết theo ID và chưa bị xóa
    @Query("{'_id': ?0, 'isDeleted': {$ne: true}}")
    Optional<Post> findByIdAndNotDeleted(String id);
    
    // Tìm bài viết theo từ khóa trong title hoặc content
    @Query("{'$and': [" +
           "{'isDeleted': {$ne: true}}, " +
           "{'$or': [" +
           "{'title': {$regex: ?0, $options: 'i'}}, " +
           "{'content': {$regex: ?0, $options: 'i'}}" +
           "]}" +
           "]}")
    Page<Post> findByKeywordAndNotDeleted(String keyword, Pageable pageable);
    
    // Tìm bài viết theo mood
    @Query("{'moodId': ?0, 'isDeleted': {$ne: true}}")
    Page<Post> findByMoodIdAndNotDeleted(String moodId, Pageable pageable);
    
    // Đếm số bài viết của user
    @Query(value = "{'accountId': ?0, 'isDeleted': {$ne: true}}", count = true)
    long countByAccountIdAndNotDeleted(String accountId);
}
