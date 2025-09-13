package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.response.ListAccountResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserInformationRepository userInformationRepository;

    public List<ListAccountResponse> getAllUserByAdmin(){
        List<Account> accounts = accountRepository.findAll();
        List<UserInformation> userInformations = userInformationRepository.findAll();
        List<ListAccountResponse> listAccountResponses = new ArrayList<>();
        for(Account account:accounts){
            for(UserInformation userInformation:userInformations){
                if(account.getId().equals(userInformation.getAccount().getId())){
                    ListAccountResponse dto = new ListAccountResponse();
                    dto.setId(userInformation.getAccount().getId());
                    dto.setUsername(account.getUserName());
                    dto.setRole(account.getRole());
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

    public void blockUser(String id){
        Account account = accountRepository.findById(id).orElse(null);
        account.setIsActive(false);
        accountRepository.save(account);
    }

    public void activeUser(String id){
        Account account = accountRepository.findById(id).orElse(null);
        account.setIsActive(true);
        accountRepository.save(account);
    }

}
