package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.JoinChallengeRequest;
import com.exe201.color_bites_be.dto.response.ChallengeParticipationResponse;
import com.exe201.color_bites_be.dto.response.ChallengeProgressResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.ChallengeDefinition;
import com.exe201.color_bites_be.entity.ChallengeParticipation;
import com.exe201.color_bites_be.enums.ParticipationStatus;
import com.exe201.color_bites_be.exception.BadRequestException;
import com.exe201.color_bites_be.exception.DuplicateEntity;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.ChallengeDefinitionRepository;
import com.exe201.color_bites_be.repository.ChallengeParticipationRepository;
import com.exe201.color_bites_be.service.IChallengeParticipationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChallengeParticipationServiceImpl implements IChallengeParticipationService {

    @Autowired
    private ChallengeParticipationRepository participationRepository;

    @Autowired
    private ChallengeDefinitionRepository challengeDefinitionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ChallengeParticipationResponse joinChallenge(String challengeId, JoinChallengeRequest request) {
        try {
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            
            // Check if challenge exists and is active
            ChallengeDefinition challenge = challengeDefinitionRepository.findById(challengeId)
                    .orElseThrow(() -> new NotFoundException("Thử thách không tồn tại"));
            
            if (!challenge.getIsActive()) {
                throw new BadRequestException("Thử thách không còn hoạt động");
            }

            // Check if user already joined
            if (participationRepository.findByAccountIdAndChallengeId(account.getId(), challengeId).isPresent()) {
                throw new DuplicateEntity("Bạn đã tham gia thử thách này");
            }

            ChallengeParticipation participation = new ChallengeParticipation();
            participation.setAccountId(account.getId());
            participation.setChallengeId(challengeId);
            participation.setStatus(ParticipationStatus.IN_PROGRESS);
            participation.setProgressCount(0);
            participation.setCreatedAt(LocalDateTime.now());

            ChallengeParticipation savedParticipation = participationRepository.save(participation);
            return buildParticipationResponse(savedParticipation);

        } catch (NotFoundException | BadRequestException | DuplicateEntity e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi tham gia thử thách: " + e.getMessage());
        }
    }

    @Override
    public ChallengeParticipationResponse readParticipationById(String participationId) {
        ChallengeParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new NotFoundException("Tham gia thử thách không tồn tại"));

        return buildParticipationResponse(participation);
    }

    @Override
    public List<ChallengeParticipationResponse> readUserParticipations(String accountId) {
        List<ChallengeParticipation> participations = participationRepository.findByAccountId(accountId);
        return participations.stream()
                .map(this::buildParticipationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ChallengeParticipationResponse> readUserParticipations(String accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChallengeParticipation> participations = participationRepository.findByAccountId(accountId, pageable);
        
        return participations.map(this::buildParticipationResponse);
    }

    @Override
    public List<ChallengeParticipationResponse> readParticipationsByChallenge(String challengeId) {
        List<ChallengeParticipation> participations = participationRepository.findByChallengeId(challengeId);
        return participations.stream()
                .map(this::buildParticipationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChallengeParticipationResponse> readParticipationsByStatus(ParticipationStatus status) {
        List<ChallengeParticipation> participations = participationRepository.findByStatus(status);
        return participations.stream()
                .map(this::buildParticipationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChallengeProgressResponse getParticipationProgress(String participationId) {
        ChallengeParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new NotFoundException("Tham gia thử thách không tồn tại"));

        ChallengeDefinition challenge = challengeDefinitionRepository.findById(participation.getChallengeId())
                .orElseThrow(() -> new NotFoundException("Thử thách không tồn tại"));

        ChallengeProgressResponse response = new ChallengeProgressResponse();
        response.setParticipationId(participation.getId());
        response.setChallengeId(challenge.getId());
        response.setChallengeTitle(challenge.getTitle());
        response.setProgressCount(participation.getProgressCount());
        response.setTargetCount(challenge.getTargetCount());
        response.setProgressPercentage((double) participation.getProgressCount() / challenge.getTargetCount() * 100);
        response.setStartDate(challenge.getStartDate());
        response.setEndDate(challenge.getEndDate());
        response.setCompletedAt(participation.getCompletedAt());
        response.setStatus(participation.getStatus().toString());

        return response;
    }

    @Override
    public void updateParticipationProgress(String participationId, int progressCount) {
        ChallengeParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new NotFoundException("Tham gia thử thách không tồn tại"));

        participation.setProgressCount(progressCount);
        participationRepository.save(participation);
    }

    @Override
    public void completeParticipation(String participationId) {
        ChallengeParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new NotFoundException("Tham gia thử thách không tồn tại"));

        participation.setStatus(ParticipationStatus.COMPLETED);
        participation.setCompletedAt(LocalDateTime.now());
        participationRepository.save(participation);
    }

    @Override
    public void failParticipation(String participationId) {
        ChallengeParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new NotFoundException("Tham gia thử thách không tồn tại"));

        participation.setStatus(ParticipationStatus.FAILED);
        participationRepository.save(participation);
    }

    private ChallengeParticipationResponse buildParticipationResponse(ChallengeParticipation participation) {
        ChallengeParticipationResponse response = modelMapper.map(participation, ChallengeParticipationResponse.class);
        
        // TODO: Add additional fields like challengeTitle, targetCount, etc.
        
        return response;
    }
}





