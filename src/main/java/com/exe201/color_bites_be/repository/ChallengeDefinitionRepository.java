package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.ChallengeDefinition;
import com.exe201.color_bites_be.enums.ChallengeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeDefinitionRepository extends MongoRepository<ChallengeDefinition, String> {
    Optional<ChallengeDefinition> findById(String challegenId);
    
    @Query("{'is_active': true}")
    List<ChallengeDefinition> findByIsActive();
    
    @Query("{'is_active': true}")
    Page<ChallengeDefinition> findActiveChallenges(Pageable pageable);
    
    @Query("{'restaurant_id': ?0, 'is_active': true}")
    List<ChallengeDefinition> findByRestaurantId(String restaurantId);
    
    @Query("{'type_obj.key': ?0, 'is_active': true}")
    List<ChallengeDefinition> findByTypeObjKey(String typeKey);
    
    @Query("{'challenge_type': ?0, 'is_active': true}")
    List<ChallengeDefinition> findByChallengeType(ChallengeType challengeType);
    
    @Query("{'start_date': {$lte: ?0}, 'end_date': {$gte: ?0}, 'is_active': true}")
    List<ChallengeDefinition> findByDateRange(LocalDateTime date);
    
    @Query("{'created_by': ?0, 'is_active': true}")
    Page<ChallengeDefinition> findByCreatedBy(String createdBy, Pageable pageable);
}

