package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateChallengeDefinitionRequest;
import com.exe201.color_bites_be.dto.request.UpdateChallengeDefinitionRequest;
import com.exe201.color_bites_be.dto.response.ChallengeDefinitionResponse;
import com.exe201.color_bites_be.dto.response.ChallengeDetailResponse;
import com.exe201.color_bites_be.enums.ChallengeType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IChallengeDefinitionService {
    
    ChallengeDefinitionResponse createChallengeDefinition(CreateChallengeDefinitionRequest request);
    
    ChallengeDefinitionResponse readChallengeDefinitionById(String challengeId);
    
    List<ChallengeDetailResponse> readActiveChallenges();
    
    List<ChallengeDefinitionResponse> readChallengesByType(ChallengeType challengeType);
    
    List<ChallengeDefinitionResponse> readChallengesByRestaurant(String restaurantId);
    
    List<ChallengeDefinitionResponse> readChallengesByTypeObj(String typeKey);
    
    ChallengeDefinitionResponse updateChallengeDefinition(String challengeId, UpdateChallengeDefinitionRequest request);
    
    void deleteChallengeDefinition(String challengeId);
    
    void activateChallenge(String challengeId);
    
    void deactivateChallenge(String challengeId);
}

