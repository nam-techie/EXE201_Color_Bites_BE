package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.response.ListFriendResponse;
import com.exe201.color_bites_be.entity.Friendship;

import java.util.List;

public interface IFriendShipService {
    void sendFriendRequest(String targetUserId);
    void acceptFriendRequest(String requesterId);
    void rejectFriendRequest(String requesterId);
    void unfriend(String friendId);
    List<ListFriendResponse> getFriends(String userId);
    List<Friendship> getFriendsByUserId(String userId);
}
