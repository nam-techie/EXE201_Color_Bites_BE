package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.UserInformationRequest;
import com.exe201.color_bites_be.dto.response.UserInformationResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.enums.SubcriptionPlan;
import com.exe201.color_bites_be.enums.Gender;
import com.exe201.color_bites_be.exception.DuplicateEntity;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import com.exe201.color_bites_be.service.IUserInformationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation của IUserInformationService
 * Xử lý logic quản lý thông tin người dùng
 */
@Service
public class UserInformationServiceImpl implements IUserInformationService {

    @Autowired
    private UserInformationRepository userInformationRepository;


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
}
