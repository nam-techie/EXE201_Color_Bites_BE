package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.JoinChallengeRequest;
import com.exe201.color_bites_be.dto.response.ChallengeParticipationResponse;
import com.exe201.color_bites_be.dto.response.ChallengeProgressResponse;
import com.exe201.color_bites_be.enums.ParticipationStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IChallengeParticipationService {
    
    ChallengeParticipationResponse joinChallenge(String challengeId);
    
    ChallengeParticipationResponse readParticipationById(String participationId);
    
    List<ChallengeParticipationResponse> readUserParticipations();
    
    Page<ChallengeParticipationResponse> readUserParticipations(int page, int size);
    
    List<ChallengeParticipationResponse> readParticipationsByChallenge(String challengeId);
    
    List<ChallengeParticipationResponse> readParticipationsByStatus(ParticipationStatus status);
    
    ChallengeProgressResponse getParticipationProgress(String participationId);
    
    void updateParticipationProgress(String participationId, int progressCount);
    
    void completeParticipation(String participationId);
    
    void failParticipation(String participationId);
}






