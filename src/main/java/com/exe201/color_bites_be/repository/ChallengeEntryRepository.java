package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.ChallengeEntry;
import com.exe201.color_bites_be.enums.EntryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeEntryRepository extends MongoRepository<ChallengeEntry, String> {
    
    @Query("{'participation_id': ?0}")
    List<ChallengeEntry> findByParticipationId(String participationId);
    
    @Query("{'participation_id': ?0}")
    Page<ChallengeEntry> findByParticipationId(String participationId, Pageable pageable);
    
    @Query("{'restaurant_id': ?0}")
    List<ChallengeEntry> findByRestaurantId(String restaurantId);
    
    @Query("{'status': ?0}")
    List<ChallengeEntry> findByStatus(EntryStatus status);
    
    @Query("{'status': ?0}")
    Page<ChallengeEntry> findByStatus(EntryStatus status, Pageable pageable);
    
    @Query(value = "{'participation_id': ?0}", count = true)
    long countByParticipationId(String participationId);
    
    @Query("{'participation_id': ?0, 'status': ?1}")
    List<ChallengeEntry> findByParticipationIdAndStatus(String participationId, EntryStatus status);
}






