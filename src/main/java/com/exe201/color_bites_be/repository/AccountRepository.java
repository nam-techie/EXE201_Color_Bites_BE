package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
    Optional<Account> findByUserName(String userName);

    Optional<Account> findByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    Account findAccountById(String id);

    // Để tương thích với code cũ
    default Account findAccountByUserName(String userName) {
        return findByUserName(userName).orElse(null);
    }
    
    // Đếm số account theo trạng thái active
    long countByIsActive(Boolean isActive);
}
