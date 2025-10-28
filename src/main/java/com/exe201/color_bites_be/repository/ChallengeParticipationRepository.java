package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.ChallengeParticipation;
import com.exe201.color_bites_be.enums.ParticipationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeParticipationRepository extends MongoRepository<ChallengeParticipation, String> {
    
    @Query("{'account_id': ?0}")
    List<ChallengeParticipation> findByAccountId(String accountId);

    Long countChallengeParticipationByChallengeId(String challengeId);
    
    @Query("{'account_id': ?0}")
    Page<ChallengeParticipation> findByAccountId(String accountId, Pageable pageable);
    
    @Query("{'challenge_id': ?0}")
    List<ChallengeParticipation> findByChallengeId(String challengeId);
    
    @Query("{'account_id': ?0, 'challenge_id': ?1}")
    Optional<ChallengeParticipation> findByAccountIdAndChallengeId(String accountId, String challengeId);
    
    @Query("{'status': ?0}")
    List<ChallengeParticipation> findByStatus(ParticipationStatus status);
    
    @Query("{'account_id': ?0, 'status': ?1}")
    List<ChallengeParticipation> findByAccountIdAndStatus(String accountId, ParticipationStatus status);
}






