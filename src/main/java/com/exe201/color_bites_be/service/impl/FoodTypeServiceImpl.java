package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.CreateFoodTypeRequest;
import com.exe201.color_bites_be.dto.request.UpdateFoodTypeRequest;
import com.exe201.color_bites_be.dto.response.FoodTypeResponse;
import com.exe201.color_bites_be.entity.FoodType;
import com.exe201.color_bites_be.entity.RestaurantFoodType;
import com.exe201.color_bites_be.exception.BadRequestException;
import com.exe201.color_bites_be.exception.DuplicateEntity;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.FoodTypeRepository;
import com.exe201.color_bites_be.repository.RestaurantFoodTypeRepository;
import com.exe201.color_bites_be.service.IFoodTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodTypeServiceImpl implements IFoodTypeService {

    @Autowired
    private FoodTypeRepository foodTypeRepository;

    @Autowired
    private RestaurantFoodTypeRepository restaurantFoodTypeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public FoodTypeResponse createFoodType(CreateFoodTypeRequest request) {
        try {
            // Check if food type already exists
            if (foodTypeRepository.existsByName(request.getName())) {
                throw new DuplicateEntity("Loại món ăn đã tồn tại");
            }

            FoodType foodType = modelMapper.map(request, FoodType.class);
            FoodType savedFoodType = foodTypeRepository.save(foodType);

            return buildFoodTypeResponse(savedFoodType);

        } catch (DuplicateEntity e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi tạo loại món ăn: " + e.getMessage());
        }
    }

    @Override
    public FoodTypeResponse readFoodTypeById(String foodTypeId) {
        FoodType foodType = foodTypeRepository.findById(foodTypeId)
                .orElseThrow(() -> new NotFoundException("Loại món ăn không tồn tại"));

        return buildFoodTypeResponse(foodType);
    }

    @Override
    public List<FoodTypeResponse> readAllFoodTypes() {
        List<FoodType> foodTypes = foodTypeRepository.findAll();
        return foodTypes.stream()
                .map(this::buildFoodTypeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FoodTypeResponse updateFoodType(String foodTypeId, UpdateFoodTypeRequest request) {
        FoodType foodType = foodTypeRepository.findById(foodTypeId)
                .orElseThrow(() -> new NotFoundException("Loại món ăn không tồn tại"));

        try {
            // Check if new name already exists (excluding current food type)
            if (!foodType.getName().equals(request.getName()) && 
                foodTypeRepository.existsByName(request.getName())) {
                throw new DuplicateEntity("Tên loại món ăn đã tồn tại");
            }

            foodType.setName(request.getName());
            FoodType updatedFoodType = foodTypeRepository.save(foodType);

            return buildFoodTypeResponse(updatedFoodType);

        } catch (DuplicateEntity e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi cập nhật loại món ăn: " + e.getMessage());
        }
    }

    @Override
    public void deleteFoodType(String foodTypeId) {
        FoodType foodType = foodTypeRepository.findById(foodTypeId)
                .orElseThrow(() -> new NotFoundException("Loại món ăn không tồn tại"));

        try {
            // Check if food type is being used by any restaurants
            List<RestaurantFoodType> restaurantFoodTypes = restaurantFoodTypeRepository.findByFoodTypeId(foodTypeId);
            if (!restaurantFoodTypes.isEmpty()) {
                throw new BadRequestException("Không thể xóa loại món ăn đang được sử dụng bởi nhà hàng");
            }

            foodTypeRepository.delete(foodType);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi xóa loại món ăn: " + e.getMessage());
        }
    }

    @Override
    public List<FoodTypeResponse> getFoodTypesByRestaurant(String restaurantId) {
        List<RestaurantFoodType> restaurantFoodTypes = restaurantFoodTypeRepository.findByRestaurantId(restaurantId);
        
        return restaurantFoodTypes.stream()
                .map(rft -> {
                    FoodType foodType = foodTypeRepository.findById(rft.getFoodTypeId()).orElse(null);
                    return foodType != null ? buildFoodTypeResponse(foodType) : null;
                })
                .filter(ftr -> ftr != null)
                .collect(Collectors.toList());
    }

    @Override
    public void assignFoodTypesToRestaurant(String restaurantId, List<String> foodTypeIds) {
        try {
            for (String foodTypeId : foodTypeIds) {
                // Check if food type exists
                if (!foodTypeRepository.existsById(foodTypeId)) {
                    throw new NotFoundException("Loại món ăn không tồn tại: " + foodTypeId);
                }

                // Check if assignment already exists
                if (!restaurantFoodTypeRepository.existsByRestaurantIdAndFoodTypeId(restaurantId, foodTypeId)) {
                    RestaurantFoodType restaurantFoodType = new RestaurantFoodType();
                    restaurantFoodType.setRestaurantId(restaurantId);
                    restaurantFoodType.setFoodTypeId(foodTypeId);
                    restaurantFoodTypeRepository.save(restaurantFoodType);
                }
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi gán loại món ăn cho nhà hàng: " + e.getMessage());
        }
    }

    @Override
    public void removeFoodTypesFromRestaurant(String restaurantId, List<String> foodTypeIds) {
        try {
            for (String foodTypeId : foodTypeIds) {
                restaurantFoodTypeRepository.findByRestaurantId(restaurantId).stream()
                        .filter(rft -> rft.getFoodTypeId().equals(foodTypeId))
                        .forEach(restaurantFoodTypeRepository::delete);
            }
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi xóa loại món ăn khỏi nhà hàng: " + e.getMessage());
        }
    }

    private FoodTypeResponse buildFoodTypeResponse(FoodType foodType) {
        FoodTypeResponse response = modelMapper.map(foodType, FoodTypeResponse.class);
        
        // Calculate usage count
        long usageCount = restaurantFoodTypeRepository.findByFoodTypeId(foodType.getId()).size();
        response.setUsageCount(usageCount);
        
        return response;
    }
}
