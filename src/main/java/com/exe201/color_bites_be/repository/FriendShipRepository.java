package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Friendship;
import com.exe201.color_bites_be.enums.FriendStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FriendShipRepository extends MongoRepository<Friendship, String> {
    Optional<Friendship> findByUserAAndUserB(String userA, String userB);

    List<Friendship> findByUserAOrUserBAndStatus(String userA, String userB, FriendStatus status);

}
