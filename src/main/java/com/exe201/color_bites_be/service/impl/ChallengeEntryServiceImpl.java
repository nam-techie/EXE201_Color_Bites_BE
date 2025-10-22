package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.SubmitChallengeEntryRequest;
import com.exe201.color_bites_be.dto.request.UpdateChallengeEntryRequest;
import com.exe201.color_bites_be.dto.response.ChallengeEntryResponse;
import com.exe201.color_bites_be.entity.ChallengeEntry;
import com.exe201.color_bites_be.entity.ChallengeParticipation;
import com.exe201.color_bites_be.enums.EntryStatus;
import com.exe201.color_bites_be.exception.BadRequestException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.ChallengeEntryRepository;
import com.exe201.color_bites_be.repository.ChallengeParticipationRepository;
import com.exe201.color_bites_be.service.IChallengeEntryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChallengeEntryServiceImpl implements IChallengeEntryService {

    @Autowired
    private ChallengeEntryRepository entryRepository;

    @Autowired
    private ChallengeParticipationRepository participationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ChallengeEntryResponse submitEntry(String participationId, SubmitChallengeEntryRequest request) {
        try {
            // Check if participation exists
            ChallengeParticipation participation = participationRepository.findById(participationId)
                    .orElseThrow(() -> new NotFoundException("Tham gia thử thách không tồn tại"));

            ChallengeEntry entry = modelMapper.map(request, ChallengeEntry.class);
            entry.setParticipationId(participationId);
            entry.setStatus(EntryStatus.PENDING);
            entry.setCreatedAt(LocalDateTime.now());

            ChallengeEntry savedEntry = entryRepository.save(entry);
            return buildEntryResponse(savedEntry);

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi nộp bài tham gia: " + e.getMessage());
        }
    }

    @Override
    public ChallengeEntryResponse readEntryById(String entryId) {
        ChallengeEntry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new NotFoundException("Bài tham gia không tồn tại"));

        return buildEntryResponse(entry);
    }

    @Override
    public List<ChallengeEntryResponse> readEntriesByParticipation(String participationId) {
        List<ChallengeEntry> entries = entryRepository.findByParticipationId(participationId);
        return entries.stream()
                .map(this::buildEntryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ChallengeEntryResponse> readEntriesByParticipation(String participationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChallengeEntry> entries = entryRepository.findByParticipationId(participationId, pageable);
        
        return entries.map(this::buildEntryResponse);
    }

    @Override
    public List<ChallengeEntryResponse> readEntriesByRestaurant(String restaurantId) {
        List<ChallengeEntry> entries = entryRepository.findByRestaurantId(restaurantId);
        return entries.stream()
                .map(this::buildEntryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChallengeEntryResponse> readEntriesByStatus(EntryStatus status) {
        List<ChallengeEntry> entries = entryRepository.findByStatus(status);
        return entries.stream()
                .map(this::buildEntryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ChallengeEntryResponse> readEntriesByStatus(EntryStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChallengeEntry> entries = entryRepository.findByStatus(status, pageable);
        
        return entries.map(this::buildEntryResponse);
    }

    @Override
    public ChallengeEntryResponse updateEntry(String entryId, UpdateChallengeEntryRequest request) {
        ChallengeEntry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new NotFoundException("Bài tham gia không tồn tại"));

        try {
            if (request.getRestaurantId() != null) {
                entry.setRestaurantId(request.getRestaurantId());
            }
            if (request.getPhotoUrl() != null) {
                entry.setPhotoUrl(request.getPhotoUrl());
            }
            if (request.getLatitude() != null) {
                entry.setLatitude(request.getLatitude());
            }
            if (request.getLongitude() != null) {
                entry.setLongitude(request.getLongitude());
            }
            if (request.getNotes() != null) {
                entry.setNotes(request.getNotes());
            }

            ChallengeEntry updatedEntry = entryRepository.save(entry);
            return buildEntryResponse(updatedEntry);

        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi cập nhật bài tham gia: " + e.getMessage());
        }
    }

    @Override
    public ChallengeEntryResponse approveEntry(String entryId) {
        ChallengeEntry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new NotFoundException("Bài tham gia không tồn tại"));

        entry.setStatus(EntryStatus.APPROVED);
        ChallengeEntry updatedEntry = entryRepository.save(entry);
        
        return buildEntryResponse(updatedEntry);
    }

    @Override
    public ChallengeEntryResponse rejectEntry(String entryId) {
        ChallengeEntry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new NotFoundException("Bài tham gia không tồn tại"));

        entry.setStatus(EntryStatus.REJECTED);
        ChallengeEntry updatedEntry = entryRepository.save(entry);
        
        return buildEntryResponse(updatedEntry);
    }

    @Override
    public void deleteEntry(String entryId) {
        ChallengeEntry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new NotFoundException("Bài tham gia không tồn tại"));

        try {
            entryRepository.delete(entry);
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi xóa bài tham gia: " + e.getMessage());
        }
    }

    @Override
    public long countEntriesByParticipation(String participationId) {
        return entryRepository.countByParticipationId(participationId);
    }

    @Override
    public long countApprovedEntriesByParticipation(String participationId) {
        List<ChallengeEntry> approvedEntries = entryRepository.findByParticipationIdAndStatus(participationId, EntryStatus.APPROVED);
        return approvedEntries.size();
    }

    private ChallengeEntryResponse buildEntryResponse(ChallengeEntry entry) {
        ChallengeEntryResponse response = modelMapper.map(entry, ChallengeEntryResponse.class);
        
        // TODO: Add additional fields like restaurantName, challengeTitle, accountId
        
        return response;
    }
}
