package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
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

    // Tìm theo keyword (chứa, không phân biệt hoa/thường) và chưa bị xóa mềm
    @Query(value = "{ 'is_deleted': { $ne: true }, $or: [ " +
            "{ 'name':    { $regex: ?0, $options: 'i' } }, " +
            "{ 'address': { $regex: ?0, $options: 'i' } }, " +
            "{ 'district':{ $regex: ?0, $options: 'i' } }, " +
            "{ 'type':    { $regex: ?0, $options: 'i' } }, " +
            "{ 'price':   { $regex: ?0, $options: 'i' } } " +
            "] }")
    Page<Restaurant> findByKeywordAndNotDeleted(String keyword, Pageable pageable);

    // Tìm theo keyword và district (optional filter)
    @Query(value = "{ 'is_deleted': { $ne: true }, " +
            "'district': { $regex: ?1, $options: 'i' }, " +
            "$or: [ " +
            "{ 'name':    { $regex: ?0, $options: 'i' } }, " +
            "{ 'address': { $regex: ?0, $options: 'i' } }, " +
            "{ 'type':    { $regex: ?0, $options: 'i' } }, " +
            "{ 'price':   { $regex: ?0, $options: 'i' } } " +
            "] }")
    Page<Restaurant> findByKeywordAndDistrictAndNotDeleted(String keyword, String district, Pageable pageable);


    @Query(value = "{ 'district': { $regex: ?0, $options: 'i' }, 'is_deleted': { $ne: true } }")
    Page<Restaurant> searchByDistrict(String district, Pageable pageable);


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
    
    // Tìm nhà hàng gần vị trí (GeoSpatial query) - OLD METHOD (keep for backward compatibility)
    @Query("{'coordinates': {$near: {$geometry: {type: 'Point', coordinates: [?0, ?1]}, $maxDistance: ?2}}, 'isDeleted': {$ne: true}}")
    List<Restaurant> findNearbyRestaurants(double longitude, double latitude, double maxDistanceInMeters);

    // Tìm nhà hàng gần vị trí sử dụng location field (NEW - với 2dsphere index)
    @Query(value = "{ 'is_deleted': { $ne: true }, 'location': { $near: { $geometry: ?0, $maxDistance: ?1 } } }")
    List<Restaurant> findNearbyByLocation(GeoJsonPoint location, double maxDistanceInMeters);

    // Tìm nhà hàng trong bounding box
    @Query(value = "{ 'location': { $geoWithin: { $box: [ [?0, ?1], [?2, ?3] ] } }, 'is_deleted': { $ne: true } }")
    List<Restaurant> findInBounds(double minLon, double minLat, double maxLon, double maxLat);
}
