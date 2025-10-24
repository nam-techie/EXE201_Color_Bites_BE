package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.SubmitChallengeEntryRequest;
import com.exe201.color_bites_be.dto.request.UpdateChallengeEntryRequest;
import com.exe201.color_bites_be.dto.response.ChallengeEntryResponse;
import com.exe201.color_bites_be.enums.EntryStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IChallengeEntryService {
    
    ChallengeEntryResponse submitEntry(String participationId, SubmitChallengeEntryRequest request);
    
    ChallengeEntryResponse readEntryById(String entryId);
    
    List<ChallengeEntryResponse> readEntriesByParticipation(String participationId);
    
    Page<ChallengeEntryResponse> readEntriesByParticipation(String participationId, int page, int size);
    
    List<ChallengeEntryResponse> readEntriesByRestaurant(String restaurantId);
    
    List<ChallengeEntryResponse> readEntriesByStatus(EntryStatus status);
    
    Page<ChallengeEntryResponse> readEntriesByStatus(EntryStatus status, int page, int size);
    
    ChallengeEntryResponse updateEntry(String entryId, UpdateChallengeEntryRequest request);
    
    ChallengeEntryResponse approveEntry(String entryId);
    
    ChallengeEntryResponse rejectEntry(String entryId);
    
    void deleteEntry(String entryId);
    
    long countEntriesByParticipation(String participationId);
    
    long countApprovedEntriesByParticipation(String participationId);
}



