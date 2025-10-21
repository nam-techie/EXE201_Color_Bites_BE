package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.UserInformationRequest;
import com.exe201.color_bites_be.dto.response.CloudinaryResponse;
import com.exe201.color_bites_be.dto.response.UserInformationResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.entity.Subscription;
import com.exe201.color_bites_be.entity.Subscription.SubscriptionStatus;
import com.exe201.color_bites_be.enums.SubcriptionPlan;
import com.exe201.color_bites_be.enums.Gender;
import com.exe201.color_bites_be.exception.DuplicateEntity;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import com.exe201.color_bites_be.repository.SubscriptionRepository;
import com.exe201.color_bites_be.service.ICloudinaryService;
import com.exe201.color_bites_be.service.IUserInformationService;
import com.exe201.color_bites_be.util.FileUpLoadUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Implementation của IUserInformationService
 * Xử lý logic quản lý thông tin người dùng
 */
@Service
public class UserInformationServiceImpl implements IUserInformationService {

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    ICloudinaryService cloudinaryService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserInformationResponse getUserInformation() {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInformation userInformation = userInformationRepository.findByAccountId(account.getId());
        if (userInformation == null) {
            throw new NotFoundException("Thông tin người dùng không tồn tại");
        }

        UserInformationResponse response = modelMapper.map(userInformation, UserInformationResponse.class);
        response.setAccountId(account.getId());
        response.setUsername(account.getUserName());

        // Bổ sung thông tin subscription cho FE hiển thị
        subscriptionRepository.findByAccountIdAndStatus(account.getId(), SubscriptionStatus.ACTIVE)
                .ifPresentOrElse(sub -> {
                    response.setSubscriptionStatus(SubscriptionStatus.ACTIVE.name());
                    response.setSubscriptionStartsAt(sub.getStartsAt());
                    response.setSubscriptionExpiresAt(sub.getExpiresAt());
                    long remaining = 0;
                    if (sub.getExpiresAt() != null) {
                        remaining = Duration.between(LocalDateTime.now(), sub.getExpiresAt()).toDays();
                        if (remaining < 0) remaining = 0;
                    }
                    response.setSubscriptionRemainingDays((int) remaining);
                }, () -> {
                    response.setSubscriptionStatus(SubscriptionStatus.EXPIRED.name());
                    response.setSubscriptionStartsAt(null);
                    response.setSubscriptionExpiresAt(null);
                    response.setSubscriptionRemainingDays(0);
                });
        return response;
    }

    @Override
    @Transactional
    public UserInformationResponse updateUserInformation(UserInformationRequest request) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInformation userInformation = userInformationRepository.findByAccountId(account.getId());
        if (userInformation == null) {
            throw new NotFoundException("Thông tin người dùng không tồn tại");
        }

        if (request.getGender() != null) {
            userInformation.setGender(Gender.valueOf(request.getGender()));
        }

        if (request.getBio() != null) {
            userInformation.setBio(request.getBio());
        }

        userInformation.setUpdatedAt(LocalDateTime.now());

        UserInformation updatedUserInfo = userInformationRepository.save(userInformation);

        UserInformationResponse response = modelMapper.map(updatedUserInfo, UserInformationResponse.class);
        response.setAccountId(account.getId());
        response.setUsername(account.getUserName());

        // Bổ sung lại thông tin subscription để FE luôn có dữ liệu mới
        subscriptionRepository.findByAccountIdAndStatus(account.getId(), SubscriptionStatus.ACTIVE)
                .ifPresentOrElse(sub -> {
                    response.setSubscriptionStatus(SubscriptionStatus.ACTIVE.name());
                    response.setSubscriptionStartsAt(sub.getStartsAt());
                    response.setSubscriptionExpiresAt(sub.getExpiresAt());
                    long remaining = 0;
                    if (sub.getExpiresAt() != null) {
                        remaining = Duration.between(LocalDateTime.now(), sub.getExpiresAt()).toDays();
                        if (remaining < 0) remaining = 0;
                    }
                    response.setSubscriptionRemainingDays((int) remaining);
                }, () -> {
                    response.setSubscriptionStatus(SubscriptionStatus.EXPIRED.name());
                    response.setSubscriptionStartsAt(null);
                    response.setSubscriptionExpiresAt(null);
                    response.setSubscriptionRemainingDays(0);
                });
        return response;
    }

    @Override
    @Transactional
    public void upgradeSubscriptionPlan(String accountId, SubcriptionPlan newPlan) {
        UserInformation userInformation = userInformationRepository.findByAccountId(accountId);
        if (userInformation == null) {
            throw new NotFoundException("Thông tin người dùng không tồn tại");
        }

        userInformation.setSubscriptionPlan(newPlan);
        userInformation.setUpdatedAt(LocalDateTime.now());
        userInformationRepository.save(userInformation);
    }

    @Override
    @Transactional
    public void downgradeToFree(String accountId) {
        upgradeSubscriptionPlan(accountId, SubcriptionPlan.FREE);
    }

    @Override
    @Transactional
    public String uploadAvatar(String id, final MultipartFile file) {
        final UserInformation userInformation = userInformationRepository.findByAccountId(id);
        if (userInformation == null) {
            throw new NotFoundException("Người dùng không tồn tại.");
        }
        FileUpLoadUtil.assertAllowed(file, FileUpLoadUtil.IMAGE_PATTERN);
        final String fileName = FileUpLoadUtil.getFileName(file.getOriginalFilename());
        final CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(file, fileName);
        userInformation.setAvatarUrl(cloudinaryResponse.getUrl());
        userInformationRepository.save(userInformation);
        return userInformation.getAvatarUrl();
    }
}
