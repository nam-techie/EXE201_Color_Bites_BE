package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.UserInformation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInformationRepository extends MongoRepository<UserInformation, String> {
    UserInformation findByAccountId(String accountId);

}
