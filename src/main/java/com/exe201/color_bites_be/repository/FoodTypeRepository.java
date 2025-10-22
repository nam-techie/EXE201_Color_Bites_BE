package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.FoodType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodTypeRepository extends MongoRepository<FoodType, String> {
    
    Optional<FoodType> findByName(String name);
    
    boolean existsByName(String name);
}
