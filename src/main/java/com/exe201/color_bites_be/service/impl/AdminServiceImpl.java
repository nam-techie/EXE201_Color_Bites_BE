package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.response.ListAccountResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import com.exe201.color_bites_be.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation của IAdminService
 * Xử lý logic quản trị hệ thống cho admin
 */
@Service
public class AdminServiceImpl implements IAdminService {
    
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserInformationRepository userInformationRepository;

    @Override
    public List<ListAccountResponse> getAllUserByAdmin() {
        List<Account> accounts = accountRepository.findAll();
        List<UserInformation> userInformations = userInformationRepository.findAll();
        List<ListAccountResponse> listAccountResponses = new ArrayList<>();
        
        for (Account account : accounts) {
            for (UserInformation userInformation : userInformations) {
                if (account.getId().equals(userInformation.getAccount().getId())) {
                    ListAccountResponse dto = new ListAccountResponse();
                    dto.setId(userInformation.getAccount().getId());
                    dto.setUsername(account.getUserName());
                    dto.setRole(account.getRole().name());
                    dto.setFullName(userInformation.getFullName());
                    dto.setAvatarUrl(userInformation.getAvatarUrl());
                    dto.setCreated(account.getCreatedAt());
                    dto.setUpdated(account.getUpdatedAt());
                    dto.setActive(account.getIsActive());
                    listAccountResponses.add(dto);
                }
            }
        }
        return listAccountResponses;
    }

    @Override
    public void blockUser(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        account.setIsActive(false);
        accountRepository.save(account);
    }

    @Override
    public void activeUser(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        account.setIsActive(true);
        accountRepository.save(account);
    }
}
