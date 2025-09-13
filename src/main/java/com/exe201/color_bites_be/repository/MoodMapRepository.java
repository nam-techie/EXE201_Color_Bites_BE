package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.MoodMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MoodMapRepository extends MongoRepository<MoodMap, String> {
    
    // Tìm mood map theo account ID và chưa bị xóa
    @Query("{'accountId': ?0, 'isDeleted': {$ne: true}}")
    Page<MoodMap> findByAccountIdAndNotDeleted(String accountId, Pageable pageable);

    // Tìm mood map theo ID và chưa bị xóa
    @Query("{'_id': ?0, 'isDeleted': {$ne: true}}")
    Optional<MoodMap> findByIdAndNotDeleted(String id);

    // Tìm mood map public
    @Query("{'visibility': 'public', 'isDeleted': {$ne: true}}")
    Page<MoodMap> findPublicMoodMaps(Pageable pageable);

    // Tìm mood map theo title
    @Query("{'accountId': ?0, 'title': {$regex: ?1, $options: 'i'}, 'isDeleted': {$ne: true}}")
    Page<MoodMap> findByAccountIdAndTitleContaining(String accountId, String title, Pageable pageable);

    // Tìm mood map đã export
    @Query("{'accountId': ?0, 'exported': true, 'isDeleted': {$ne: true}}")
    List<MoodMap> findExportedByAccountId(String accountId);

    // Đếm số mood map của user
    @Query(value = "{'accountId': ?0, 'isDeleted': {$ne: true}}", count = true)
    long countByAccountIdAndNotDeleted(String accountId);

    // Tìm mood map theo visibility
    @Query("{'accountId': ?0, 'visibility': ?1, 'isDeleted': {$ne: true}}")
    Page<MoodMap> findByAccountIdAndVisibilityAndNotDeleted(String accountId, String visibility, Pageable pageable);

    // Kiểm tra mood map có tồn tại và chưa bị xóa
    @Query(value = "{'_id': ?0, 'isDeleted': {$ne: true}}", exists = true)
    boolean existsByIdAndNotDeleted(String id);

    // Tìm mood map mới nhất của user
    @Query(value = "{'accountId': ?0, 'isDeleted': {$ne: true}}", sort = "{'createdAt': -1}")
    Optional<MoodMap> findLatestByAccountId(String accountId);
}
