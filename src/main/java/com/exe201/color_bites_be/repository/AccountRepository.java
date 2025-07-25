package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
   Account findAccountById(Long id);
   Account findAccountByUserName(String username);
   Boolean existsByEmail(String email);
   Boolean existsByUserName(String username);
}
