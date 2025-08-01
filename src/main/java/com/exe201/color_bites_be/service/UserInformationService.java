package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.UserInformationRequest;
import com.exe201.color_bites_be.dto.response.UserInformationResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.enums.SubcriptionPlan;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserInformationService {

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Lấy thông tin user theo accountId
     */
    public UserInformationResponse getUserInformation(String accountId) {
        UserInformation userInformation = userInformationRepository.findByAccountId(accountId);
        if (userInformation == null) {
            throw new NotFoundException("Thông tin người dùng không tồn tại");
        }

        UserInformationResponse response = modelMapper.map(userInformation, UserInformationResponse.class);
        response.setAccountId(accountId);
        return response;
    }

    /**
     * Tạo hoặc cập nhật thông tin user lần đầu
     * (Vì register đã tạo UserInformation cơ bản, method này sẽ cập nhật thêm thông tin)
     */
    @Transactional
    public UserInformationResponse createOrUpdateUserInformation(String accountId, UserInformationRequest request) {
        // Kiểm tra account có tồn tại không
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));

        // Kiểm tra đã có thông tin user chưa
        UserInformation userInformation = userInformationRepository.findByAccountId(accountId);

        if (userInformation == null) {
            // Trường hợp chưa có UserInformation (có thể xảy ra với data cũ)
            userInformation = new UserInformation();
            userInformation.setAccount(account);
            userInformation.setSubscriptionPlan(SubcriptionPlan.FREE.name());
            userInformation.setCreatedAt(LocalDateTime.now());
        }

        // Cập nhật thông tin từ request
        if (request.getFullName() != null) {
            userInformation.setFullName(request.getFullName());
        }
        if (request.getGender() != null) {
            userInformation.setGender(request.getGender());
        }
        if (request.getDob() != null) {
            userInformation.setDob(request.getDob());
        }
        if (request.getPhone() != null) {
            userInformation.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            userInformation.setAddress(request.getAddress());
        }
        if (request.getBio() != null) {
            userInformation.setBio(request.getBio());
        }

        userInformation.setUpdatedAt(LocalDateTime.now());

        UserInformation savedUserInfo = userInformationRepository.save(userInformation);

        UserInformationResponse response = modelMapper.map(savedUserInfo, UserInformationResponse.class);
        response.setAccountId(accountId);
        return response;
    }

    /**
     * Cập nhật thông tin user
     */
    @Transactional
    public UserInformationResponse updateUserInformation(String accountId, UserInformationRequest request) {
        UserInformation userInformation = userInformationRepository.findByAccountId(accountId);
        if (userInformation == null) {
            throw new NotFoundException("Thông tin người dùng không tồn tại");
        }

        // Cập nhật các field từ request (chỉ cập nhật field không null)
        if (request.getFullName() != null) {
            userInformation.setFullName(request.getFullName());
        }
        if (request.getGender() != null) {
            userInformation.setGender(request.getGender());
        }
        if (request.getDob() != null) {
            userInformation.setDob(request.getDob());
        }
        if (request.getPhone() != null) {
            userInformation.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            userInformation.setAddress(request.getAddress());
        }
        if (request.getBio() != null) {
            userInformation.setBio(request.getBio());
        }

        // Không cho phép user tự cập nhật subscription plan
        // Subscription plan chỉ được cập nhật thông qua hệ thống thanh toán

        userInformation.setUpdatedAt(LocalDateTime.now());

        UserInformation updatedUserInfo = userInformationRepository.save(userInformation);

        UserInformationResponse response = modelMapper.map(updatedUserInfo, UserInformationResponse.class);
        response.setAccountId(accountId);
        return response;
    }

    /**
     * Nâng cấp subscription plan (chỉ dành cho hệ thống thanh toán)
     * Method này không được expose qua API public
     */
    @Transactional
    public void upgradeSubscriptionPlan(String accountId, SubcriptionPlan newPlan) {
        UserInformation userInformation = userInformationRepository.findByAccountId(accountId);
        if (userInformation == null) {
            throw new NotFoundException("Thông tin người dùng không tồn tại");
        }

        userInformation.setSubscriptionPlan(newPlan.name());
        userInformation.setUpdatedAt(LocalDateTime.now());
        userInformationRepository.save(userInformation);
    }

    /**
     * Hạ cấp về FREE (khi hết hạn premium)
     */
    @Transactional
    public void downgradeToFree(String accountId) {
        upgradeSubscriptionPlan(accountId, SubcriptionPlan.FREE);
    }
}
