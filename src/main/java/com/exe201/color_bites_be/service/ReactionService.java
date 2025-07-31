package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.ReactionRequest;
import com.exe201.color_bites_be.entity.Reaction;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReactionService {
    
    @Autowired
    private ReactionRepository reactionRepository;
    
    /**
     * Tạo hoặc cập nhật reaction
     */
    @Transactional
    public void addOrUpdateReaction(String postId, String accountId, ReactionRequest request) {
        // Kiểm tra reaction hiện tại
        Optional<Reaction> existingReaction = reactionRepository.findByPostIdAndAccountId(postId, accountId);
        
        if (existingReaction.isPresent()) {
            // Cập nhật reaction hiện tại
            Reaction reaction = existingReaction.get();
            reaction.setReactionType(request.getReactionType());
            reaction.setCreatedAt(LocalDateTime.now());
            reactionRepository.save(reaction);
        } else {
            // Tạo reaction mới
            Reaction reaction = new Reaction();
            reaction.setPostId(postId);
            reaction.setAccountId(accountId);
            reaction.setReactionType(request.getReactionType());
            reaction.setCreatedAt(LocalDateTime.now());
            reactionRepository.save(reaction);
        }
    }
    
    /**
     * Xóa reaction
     */
    @Transactional
    public void removeReaction(String postId, String accountId) {
        reactionRepository.deleteByPostIdAndAccountId(postId, accountId);
    }
    
    /**
     * Lấy reaction của một user cho một bài viết
     */
    public String getUserReactionType(String postId, String accountId) {
        Optional<Reaction> reaction = reactionRepository.findByPostIdAndAccountId(postId, accountId);
        return reaction.map(Reaction::getReactionType).orElse(null);
    }
    
    /**
     * Kiểm tra user đã reaction chưa
     */
    public boolean hasUserReacted(String postId, String accountId) {
        return reactionRepository.existsByPostIdAndAccountId(postId, accountId);
    }
    
    /**
     * Lấy thống kê reaction của một bài viết
     */
    public Map<String, Long> getReactionStats(String postId) {
        Map<String, Long> stats = new HashMap<>();
        
        // Đếm tổng số reaction
        long totalReactions = reactionRepository.countByPostId(postId);
        stats.put("total", totalReactions);
        
        // Đếm theo từng loại reaction
        String[] reactionTypes = {"like", "love", "haha", "wow", "sad", "angry"};
        for (String type : reactionTypes) {
            long count = reactionRepository.countByPostIdAndReactionType(postId, type);
            stats.put(type, count);
        }
        
        return stats;
    }
    
    /**
     * Lấy danh sách reaction của một bài viết
     */
    public List<Reaction> getReactionsByPost(String postId) {
        return reactionRepository.findByPostId(postId);
    }
    
    /**
     * Lấy danh sách reaction của một user
     */
    public List<Reaction> getReactionsByUser(String accountId) {
        return reactionRepository.findByAccountId(accountId);
    }
    
    /**
     * Đếm số reaction của một bài viết
     */
    public long countReactionsByPost(String postId) {
        return reactionRepository.countByPostId(postId);
    }
    
    /**
     * Đếm số reaction theo loại của một bài viết
     */
    public long countReactionsByType(String postId, String reactionType) {
        return reactionRepository.countByPostIdAndReactionType(postId, reactionType);
    }
} 