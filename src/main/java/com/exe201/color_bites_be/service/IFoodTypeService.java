package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateFoodTypeRequest;
import com.exe201.color_bites_be.dto.request.UpdateFoodTypeRequest;
import com.exe201.color_bites_be.dto.response.FoodTypeResponse;

import java.util.List;

public interface IFoodTypeService {
    
    FoodTypeResponse createFoodType(CreateFoodTypeRequest request);
    
    FoodTypeResponse readFoodTypeById(String foodTypeId);
    
    List<FoodTypeResponse> readAllFoodTypes();
    
    FoodTypeResponse updateFoodType(String foodTypeId, UpdateFoodTypeRequest request);
    
    void deleteFoodType(String foodTypeId);
    
    List<FoodTypeResponse> getFoodTypesByRestaurant(String restaurantId);
    
    void assignFoodTypesToRestaurant(String restaurantId, List<String> foodTypeIds);
    
    void removeFoodTypesFromRestaurant(String restaurantId, List<String> foodTypeIds);
}
