package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
}
