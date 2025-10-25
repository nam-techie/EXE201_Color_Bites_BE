package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.CreateChallengeDefinitionRequest;
import com.exe201.color_bites_be.dto.request.UpdateChallengeDefinitionRequest;
import com.exe201.color_bites_be.dto.response.ChallengeDefinitionResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.ChallengeDefinition;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.enums.ChallengeType;
import com.exe201.color_bites_be.enums.SubcriptionPlan;
import com.exe201.color_bites_be.exception.BadRequestException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.ChallengeDefinitionRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
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
    @Autowired
    private UserInformationRepository userInformationRepository;

    @Override
    public ChallengeDefinitionResponse createChallengeDefinition(CreateChallengeDefinitionRequest request) {
        try {
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            UserInformation userInformation = userInformationRepository.findByAccountId(account.getId());

            if(userInformation.getSubscriptionPlan() == SubcriptionPlan.FREE){
                throw new BadRequestException("Bạn cần nâng cấp gói để sử dụng tính năng này");
            }


            // Validate challenge type specific requirements
            validateChallengeDefinitionRequest(request);

            ChallengeDefinition challengeDefinition = new ChallengeDefinition();
            challengeDefinition.setTitle(request.getTitle());
            challengeDefinition.setDescription(request.getDescription());
            challengeDefinition.setChallengeType(request.getChallengeType());
            challengeDefinition.setRestaurantId(request.getRestaurantId());
            challengeDefinition.setTypeObjId(request.getTypeObjId()); // JSON embedded type object
            challengeDefinition.setImages(request.getImages()); // JSON embedded images
            challengeDefinition.setTargetCount(request.getTargetCount());
            challengeDefinition.setStartDate(request.getStartDate());
            challengeDefinition.setEndDate((request.getStartDate().plusDays(request.getEndDate())));
            challengeDefinition.setRewardDescription(request.getRewardDescription());
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
    public List<ChallengeDefinitionResponse> readChallengesByTypeObj(String typeKey) {
        List<ChallengeDefinition> challenges = challengeDefinitionRepository.findByTypeObjKey(typeKey);
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
            if (request.getTypeObjId() != null) {
                challenge.setTypeObjId(request.getTypeObjId()); // JSON embedded type object
            }
            if (request.getImages() != null) {
                challenge.setImages(request.getImages()); // JSON embedded images
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
            (request.getTypeObjId() == null)){
            throw new BadRequestException("Thử thách THEME_COUNT cần có typeObj với key hợp lệ");
        }
        
        if (request.getEndDate() < 0 && request.getEndDate() > 31) {
            throw new BadRequestException("Trên 1 ngày và dưới 1 tháng");
        }
    }

    private ChallengeDefinitionResponse buildChallengeDefinitionResponse(ChallengeDefinition challenge) {
        ChallengeDefinitionResponse response = new ChallengeDefinitionResponse();
        
        // Map basic fields
        response.setId(challenge.getId());
        response.setTitle(challenge.getTitle());
        response.setDescription(challenge.getDescription());
        response.setChallengeType(challenge.getChallengeType());
        response.setRestaurantId(challenge.getRestaurantId());
        response.setTypeObjId(challenge.getTypeObjId()); // JSON embedded type object
        response.setImages(challenge.getImages()); // JSON embedded images
        response.setTargetCount(challenge.getTargetCount());
        response.setStartDate(challenge.getStartDate());
        response.setEndDate(challenge.getEndDate());
        response.setRewardDescription(challenge.getRewardDescription());
        response.setCreatedBy(challenge.getCreatedBy());
        response.setCreatedAt(challenge.getCreatedAt());
        response.setIsActive(challenge.getIsActive());
        
        return response;
    }
}

