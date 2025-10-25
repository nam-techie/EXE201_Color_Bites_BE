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
    
    // Tìm tag theo tên chính xác
    Optional<Tag> findByName(String name);
    
    // Tìm tag theo tên (không phân biệt hoa thường)
    @Query("{'name': {$regex: '^?0$', $options: 'i'}}")
    Optional<Tag> findByNameIgnoreCase(String name);
    
    // Tìm nhiều tag theo danh sách tên
    @Query("{'name': {$in: ?0}}")
    List<Tag> findByNameIn(List<String> names);
    
    // Tìm tag phổ biến nhất (theo usage count)
    @Query(value = "{}", sort = "{'usageCount': -1}")
    Page<Tag> findMostPopularTags(Pageable pageable);
    
    // Tìm tag theo từ khóa
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    Page<Tag> findByNameContaining(String keyword, Pageable pageable);
    
    // Kiểm tra tag có tồn tại không
    boolean existsByName(String name);
    
    // Kiểm tra tag có tồn tại không (không phân biệt hoa thường)
    @Query(value = "{'name': {$regex: '^?0$', $options: 'i'}}", exists = true)
    boolean existsByNameIgnoreCase(String name);
    
    // Thêm method cho admin
    long countByIsDeleted(boolean isDeleted);
}
