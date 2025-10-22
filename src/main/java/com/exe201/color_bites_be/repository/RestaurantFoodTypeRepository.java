package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.RestaurantFoodType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantFoodTypeRepository extends MongoRepository<RestaurantFoodType, String> {
    
    List<RestaurantFoodType> findByRestaurantId(String restaurantId);
    
    List<RestaurantFoodType> findByFoodTypeId(String foodTypeId);
    
    void deleteByRestaurantId(String restaurantId);
    
    boolean existsByRestaurantIdAndFoodTypeId(String restaurantId, String foodTypeId);
}
