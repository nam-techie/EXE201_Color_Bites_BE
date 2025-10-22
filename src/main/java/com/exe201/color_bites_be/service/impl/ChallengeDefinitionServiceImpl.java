package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.CreateChallengeDefinitionRequest;
import com.exe201.color_bites_be.dto.request.UpdateChallengeDefinitionRequest;
import com.exe201.color_bites_be.dto.response.ChallengeDefinitionResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.ChallengeDefinition;
import com.exe201.color_bites_be.enums.ChallengeType;
import com.exe201.color_bites_be.exception.BadRequestException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.ChallengeDefinitionRepository;
import com.exe201.color_bites_be.service.IChallengeDefinitionService;
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
public class ChallengeDefinitionServiceImpl implements IChallengeDefinitionService {

    @Autowired
    private ChallengeDefinitionRepository challengeDefinitionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ChallengeDefinitionResponse createChallengeDefinition(CreateChallengeDefinitionRequest request) {
        try {
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            
            // Validate challenge type specific requirements
            validateChallengeDefinitionRequest(request);

            ChallengeDefinition challengeDefinition = modelMapper.map(request, ChallengeDefinition.class);
            challengeDefinition.setCreatedBy(account.getId());
            challengeDefinition.setCreatedAt(LocalDateTime.now());
            challengeDefinition.setIsActive(true);

            ChallengeDefinition savedChallenge = challengeDefinitionRepository.save(challengeDefinition);
            return buildChallengeDefinitionResponse(savedChallenge);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi tạo thử thách: " + e.getMessage());
        }
    }

    @Override
    public ChallengeDefinitionResponse readChallengeDefinitionById(String challengeId) {
        ChallengeDefinition challenge = challengeDefinitionRepository.findById(challengeId)
                .orElseThrow(() -> new NotFoundException("Thử thách không tồn tại"));

        return buildChallengeDefinitionResponse(challenge);
    }

    @Override
    public Page<ChallengeDefinitionResponse> readActiveChallenges(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChallengeDefinition> challenges = challengeDefinitionRepository.findActiveChallenges(pageable);
        
        return challenges.map(this::buildChallengeDefinitionResponse);
    }

    @Override
    public List<ChallengeDefinitionResponse> readChallengesByType(ChallengeType challengeType) {
        List<ChallengeDefinition> challenges = challengeDefinitionRepository.findByChallengeType(challengeType);
        return challenges.stream()
                .map(this::buildChallengeDefinitionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChallengeDefinitionResponse> readChallengesByRestaurant(String restaurantId) {
        List<ChallengeDefinition> challenges = challengeDefinitionRepository.findByRestaurantId(restaurantId);
        return challenges.stream()
                .map(this::buildChallengeDefinitionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChallengeDefinitionResponse> readChallengesByFoodType(String foodTypeId) {
        List<ChallengeDefinition> challenges = challengeDefinitionRepository.findByFoodTypeId(foodTypeId);
        return challenges.stream()
                .map(this::buildChallengeDefinitionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChallengeDefinitionResponse updateChallengeDefinition(String challengeId, UpdateChallengeDefinitionRequest request) {
        ChallengeDefinition challenge = challengeDefinitionRepository.findById(challengeId)
                .orElseThrow(() -> new NotFoundException("Thử thách không tồn tại"));

        try {
            // Update fields if provided
            if (request.getTitle() != null) {
                challenge.setTitle(request.getTitle());
            }
            if (request.getDescription() != null) {
                challenge.setDescription(request.getDescription());
            }
            if (request.getChallengeType() != null) {
                challenge.setChallengeType(request.getChallengeType());
            }
            if (request.getRestaurantId() != null) {
                challenge.setRestaurantId(request.getRestaurantId());
            }
            if (request.getFoodTypeId() != null) {
                challenge.setFoodTypeId(request.getFoodTypeId());
            }
            if (request.getTargetCount() != null) {
                challenge.setTargetCount(request.getTargetCount());
            }
            if (request.getStartDate() != null) {
                challenge.setStartDate(request.getStartDate());
            }
            if (request.getEndDate() != null) {
                challenge.setEndDate(request.getEndDate());
            }
            if (request.getRewardDescription() != null) {
                challenge.setRewardDescription(request.getRewardDescription());
            }
            if (request.getIsActive() != null) {
                challenge.setIsActive(request.getIsActive());
            }

            ChallengeDefinition updatedChallenge = challengeDefinitionRepository.save(challenge);
            return buildChallengeDefinitionResponse(updatedChallenge);

        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi cập nhật thử thách: " + e.getMessage());
        }
    }

    @Override
    public void deleteChallengeDefinition(String challengeId) {
        ChallengeDefinition challenge = challengeDefinitionRepository.findById(challengeId)
                .orElseThrow(() -> new NotFoundException("Thử thách không tồn tại"));

        try {
            challengeDefinitionRepository.delete(challenge);
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi xóa thử thách: " + e.getMessage());
        }
    }

    @Override
    public void activateChallenge(String challengeId) {
        ChallengeDefinition challenge = challengeDefinitionRepository.findById(challengeId)
                .orElseThrow(() -> new NotFoundException("Thử thách không tồn tại"));

        challenge.setIsActive(true);
        challengeDefinitionRepository.save(challenge);
    }

    @Override
    public void deactivateChallenge(String challengeId) {
        ChallengeDefinition challenge = challengeDefinitionRepository.findById(challengeId)
                .orElseThrow(() -> new NotFoundException("Thử thách không tồn tại"));

        challenge.setIsActive(false);
        challengeDefinitionRepository.save(challenge);
    }

    private void validateChallengeDefinitionRequest(CreateChallengeDefinitionRequest request) {
        if (request.getChallengeType() == ChallengeType.PARTNER_LOCATION && 
            (request.getRestaurantId() == null || request.getRestaurantId().trim().isEmpty())) {
            throw new BadRequestException("Thử thách PARTNER_LOCATION cần có restaurantId");
        }
        
        if (request.getChallengeType() == ChallengeType.THEME_COUNT && 
            (request.getFoodTypeId() == null || request.getFoodTypeId().trim().isEmpty())) {
            throw new BadRequestException("Thử thách THEME_COUNT cần có foodTypeId");
        }
        
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Ngày kết thúc phải sau ngày bắt đầu");
        }
    }

    private ChallengeDefinitionResponse buildChallengeDefinitionResponse(ChallengeDefinition challenge) {
        ChallengeDefinitionResponse response = modelMapper.map(challenge, ChallengeDefinitionResponse.class);
        
        // TODO: Add additional fields like restaurantName, foodTypeName, participantCount
        // These would require additional repository calls or joins
        
        return response;
    }
}
