package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Mood;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MoodRepository extends MongoRepository<Mood, String> {
    
    // Tìm mood theo ID
    Optional<Mood> findById(String id);
    
    // Tìm mood theo tên (không phân biệt hoa thường)
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    Optional<Mood> findByNameIgnoreCase(String name);
    
    // Tìm mood theo tên chính xác
    Optional<Mood> findByName(String name);
    
    // Tìm kiếm mood theo từ khóa trong tên
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    Page<Mood> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    
    // Lấy tất cả mood có sắp xếp theo tên
    @Query(value = "{}", sort = "{'name': 1}")
    Page<Mood> findAllOrderByName(Pageable pageable);
    
    // Kiểm tra mood có tồn tại theo tên
    boolean existsByNameIgnoreCase(String name);
    
    // Đếm tổng số mood
    long count();
    
    // Tìm mood được sử dụng nhiều nhất (sẽ join với Post collection)
    @Query(value = "{}", sort = "{'createdAt': -1}")
    Page<Mood> findAllOrderByCreatedAtDesc(Pageable pageable);
}
