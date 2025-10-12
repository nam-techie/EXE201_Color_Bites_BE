package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.entity.Friendship;
import com.exe201.color_bites_be.service.IFriendShipService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends/")
@RequiredArgsConstructor
public class FriendShipController {
    @Autowired
    IFriendShipService friendShipService;

    @PostMapping("/request/{targetUserId}")
    public ResponseDto<Void> sendRequest(@PathVariable String targetUserId) {
        friendShipService.sendFriendRequest(targetUserId);
        return new ResponseDto<>(HttpStatus.CREATED.value(), "Đã gửi yêu cầu kết bạn", null);
    }

    @PostMapping("/accept/{requesterId}")
    public ResponseDto<Void> accept(@PathVariable String requesterId) {
        friendShipService.acceptFriendRequest(requesterId);
        return new ResponseDto<>(HttpStatus.OK.value(), "Đã chấp nhận lời mời", null);
    }

    @PostMapping("/reject/{requesterId}")
    public ResponseDto<Void> reject(@PathVariable String requesterId) {
        friendShipService.rejectFriendRequest(requesterId);
        return new ResponseDto<>(HttpStatus.OK.value(), "Đã từ chối lời mời", null);
    }

    @DeleteMapping("/unfriend/{friendId}")
    public ResponseDto<Void> unfriend(@PathVariable String friendId) {
        friendShipService.unfriend(friendId);
        return new ResponseDto<>(HttpStatus.OK.value(), "Đã hủy kết bạn", null);
    }

    @GetMapping("/{userId}")
    public ResponseDto<List<Friendship>> getFriends(@PathVariable String userId) {
        List<Friendship> friends = friendShipService.getFriends(userId);
        return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách bạn bè", friends);
    }

}
