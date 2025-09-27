package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    
    // Tìm nhà hàng theo ID và chưa bị xóa
    @Query("{'_id': ?0, 'isDeleted': {$ne: true}}")
    Optional<Restaurant> findByIdAndNotDeleted(String id);
    
    // Tìm tất cả nhà hàng chưa bị xóa
    @Query("{'isDeleted': {$ne: true}}")
    Page<Restaurant> findAllActiveRestaurants(Pageable pageable);
    
    // Tìm nhà hàng theo từ khóa trong tên hoặc mô tả
    @Query("{'$and': [" +
           "{'isDeleted': {$ne: true}}, " +
           "{'$or': [" +
           "{'name': {$regex: ?0, $options: 'i'}}, " +
           "{'description': {$regex: ?0, $options: 'i'}}" +
           "]}" +
           "]}")
    Page<Restaurant> findByKeywordAndNotDeleted(String keyword, Pageable pageable);
    
    // Tìm nhà hàng theo khu vực
    @Query("{'region': ?0, 'isDeleted': {$ne: true}}")
    Page<Restaurant> findByRegionAndNotDeleted(String region, Pageable pageable);
    
    // Tìm nhà hàng theo loại
    @Query("{'type': ?0, 'isDeleted': {$ne: true}}")
    Page<Restaurant> findByTypeAndNotDeleted(String type, Pageable pageable);
    
    // Tìm nhà hàng theo mood tags
    @Query("{'moodTags': {$in: [?0]}, 'isDeleted': {$ne: true}}")
    Page<Restaurant> findByMoodTagsContainingAndNotDeleted(String moodTag, Pageable pageable);
    
    // Tìm nhà hàng nổi bật
    @Query("{'featured': true, 'isDeleted': {$ne: true}}")
    Page<Restaurant> findFeaturedRestaurantsAndNotDeleted(Pageable pageable);
    
    // Đếm số nhà hàng theo trạng thái deleted
    long countByIsDeleted(Boolean isDeleted);
    
    // Tìm nhà hàng theo khoảng giá
    @Query("{'avgPrice': {$gte: ?0, $lte: ?1}, 'isDeleted': {$ne: true}}")
    Page<Restaurant> findByPriceRangeAndNotDeleted(Double minPrice, Double maxPrice, Pageable pageable);
    
    // Tìm nhà hàng theo rating tối thiểu
    @Query("{'rating': {$gte: ?0}, 'isDeleted': {$ne: true}}")
    Page<Restaurant> findByMinRatingAndNotDeleted(Double minRating, Pageable pageable);
    
    // Tìm nhà hàng được tạo bởi user
    @Query("{'createdBy': ?0, 'isDeleted': {$ne: true}}")
    Page<Restaurant> findByCreatedByAndNotDeleted(String accountId, Pageable pageable);
    
    // Đếm số nhà hàng theo khu vực
    @Query(value = "{'region': ?0, 'isDeleted': {$ne: true}}", count = true)
    long countByRegionAndNotDeleted(String region);
    
    // Kiểm tra nhà hàng có tồn tại và chưa bị xóa
    @Query(value = "{'_id': ?0, 'isDeleted': {$ne: true}}", exists = true)
    boolean existsByIdAndNotDeleted(String id);
    
    // Tìm nhà hàng gần vị trí (GeoSpatial query)
    @Query("{'coordinates': {$near: {$geometry: {type: 'Point', coordinates: [?0, ?1]}, $maxDistance: ?2}}, 'isDeleted': {$ne: true}}")
    List<Restaurant> findNearbyRestaurants(double longitude, double latitude, double maxDistanceInMeters);
}
