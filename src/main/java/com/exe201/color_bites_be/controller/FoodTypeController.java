package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreateFoodTypeRequest;
import com.exe201.color_bites_be.dto.request.UpdateFoodTypeRequest;
import com.exe201.color_bites_be.dto.response.FoodTypeResponse;
import com.exe201.color_bites_be.service.IFoodTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food-types")
@Tag(name = "Food Type Management", description = "APIs for managing food types")
public class FoodTypeController {

    @Autowired
    private IFoodTypeService foodTypeService;

    @GetMapping
    @Operation(summary = "Get all food types", description = "Retrieve all available food types")
    public ResponseEntity<List<FoodTypeResponse>> getAllFoodTypes() {
        List<FoodTypeResponse> foodTypes = foodTypeService.readAllFoodTypes();
        return ResponseEntity.ok(foodTypes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get food type by ID", description = "Retrieve a specific food type by its ID")
    public ResponseEntity<FoodTypeResponse> getFoodTypeById(@PathVariable String id) {
        FoodTypeResponse foodType = foodTypeService.readFoodTypeById(id);
        return ResponseEntity.ok(foodType);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create food type", description = "Create a new food type (Admin only)")
    public ResponseEntity<FoodTypeResponse> createFoodType(@Valid @RequestBody CreateFoodTypeRequest request) {
        FoodTypeResponse foodType = foodTypeService.createFoodType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(foodType);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update food type", description = "Update an existing food type (Admin only)")
    public ResponseEntity<FoodTypeResponse> updateFoodType(
            @PathVariable String id, 
            @Valid @RequestBody UpdateFoodTypeRequest request) {
        FoodTypeResponse foodType = foodTypeService.updateFoodType(id, request);
        return ResponseEntity.ok(foodType);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete food type", description = "Delete a food type (Admin only)")
    public ResponseEntity<Void> deleteFoodType(@PathVariable String id) {
        foodTypeService.deleteFoodType(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get food types by restaurant", description = "Get all food types for a specific restaurant")
    public ResponseEntity<List<FoodTypeResponse>> getFoodTypesByRestaurant(@PathVariable String restaurantId) {
        List<FoodTypeResponse> foodTypes = foodTypeService.getFoodTypesByRestaurant(restaurantId);
        return ResponseEntity.ok(foodTypes);
    }
}
