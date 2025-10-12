package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.Friendship;
import com.exe201.color_bites_be.enums.FriendStatus;
import com.exe201.color_bites_be.repository.FriendShipRepository;
import com.exe201.color_bites_be.service.IFriendShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FriendShipServiceImpl implements IFriendShipService {

    private String[] normalizePair(String me, String other) {
        return me.compareTo(other) < 0
                ? new String[]{me, other}
                : new String[]{other, me};
    }

    @Autowired
    FriendShipRepository friendShipRepository;

    @Override
    public void sendFriendRequest(String targetUserId) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String[] pair = normalizePair(account.getId(), targetUserId);

        // Check đã tồn tại chưa
        Optional<Friendship> existing = friendShipRepository.findByUserAAndUserB(pair[0], pair[1]);
        if (existing.isPresent()) {
            Friendship fs = existing.get();
            if (fs.getStatus() == FriendStatus.ACCEPTED) {
                throw new RuntimeException("Hai người đã là bạn bè");
            }
            if (fs.getStatus() == FriendStatus.PENDING) {
                throw new RuntimeException("Yêu cầu kết bạn đã tồn tại");
            }
        }

        Friendship fs = new Friendship();
        fs.setUserA(pair[0]);
        fs.setUserB(pair[1]);
        fs.setRequestedBy(account.getId());
        fs.setStatus(FriendStatus.PENDING);
        fs.setCreatedAt(LocalDateTime.now());
        fs.setUpdatedAt(LocalDateTime.now());

        friendShipRepository.save(fs);
    }

    @Override
    public void acceptFriendRequest(String requesterId) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String[] pair = normalizePair(account.getId(), requesterId);

        Friendship fs = friendShipRepository.findByUserAAndUserB(pair[0], pair[1])
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lời mời kết bạn"));

        if (!fs.getStatus().equals(FriendStatus.PENDING)) {
            throw new RuntimeException("Lời mời không còn hợp lệ");
        }

        // chỉ cho phép người nhận chấp nhận
        if (!fs.getRequestedBy().equals(requesterId)) {
            throw new RuntimeException("Bạn không thể tự chấp nhận yêu cầu của mình");
        }

        fs.setStatus(FriendStatus.ACCEPTED);
        fs.setUpdatedAt(LocalDateTime.now());
        friendShipRepository.save(fs);
    }

    @Override
    public void rejectFriendRequest(String requesterId) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String[] pair = normalizePair(account.getId(), requesterId);

        Friendship fs = friendShipRepository.findByUserAAndUserB(pair[0], pair[1])
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lời mời kết bạn"));

        if (fs.getStatus() == FriendStatus.PENDING) {
            friendShipRepository.delete(fs);
        } else {
            throw new RuntimeException("Không thể từ chối khi đã là bạn bè");
        }
    }

    @Override
    public void unfriend(String friendId) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String[] pair = normalizePair(account.getId(), friendId);
        Friendship fs = friendShipRepository.findByUserAAndUserB(pair[0], pair[1])
                .orElseThrow(() -> new RuntimeException("Không tồn tại quan hệ bạn bè"));

        if (fs.getStatus() == FriendStatus.ACCEPTED) {
            friendShipRepository.delete(fs);
        } else {
            throw new RuntimeException("Không phải bạn bè");
        }
    }

    @Override
    public List<Friendship> getFriends(String userId) {
        return friendShipRepository.findByUserAOrUserBAndStatus(userId, userId, FriendStatus.ACCEPTED);
    }
}
