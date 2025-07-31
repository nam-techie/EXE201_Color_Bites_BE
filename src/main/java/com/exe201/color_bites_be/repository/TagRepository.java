package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends MongoRepository<Tag, String> {
    
    // Tìm tag theo tên
    Optional<Tag> findByName(String name);
    
    // Kiểm tra tag có tồn tại không
    boolean existsByName(String name);
    
    // Tìm tag theo tên (không phân biệt hoa thường)
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    Optional<Tag> findByNameIgnoreCase(String name);
    
    // Tìm tag có chứa từ khóa
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    Page<Tag> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    
    // Tìm tag theo usage count (sắp xếp giảm dần)
    Page<Tag> findAllByOrderByUsageCountDesc(Pageable pageable);
    
    // Tìm tag phổ biến (usage count > 0)
    Page<Tag> findByUsageCountGreaterThanOrderByUsageCountDesc(int minUsage, Pageable pageable);
    
    // Tìm tag theo danh sách tên
    List<Tag> findByNameIn(List<String> names);
} 