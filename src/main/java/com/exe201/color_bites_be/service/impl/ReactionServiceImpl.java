package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.response.ReactionResponse;
import com.exe201.color_bites_be.dto.response.ReactionSummaryResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.Post;
import com.exe201.color_bites_be.entity.Reaction;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.enums.ReactionType;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.PostRepository;
import com.exe201.color_bites_be.repository.ReactionRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import com.exe201.color_bites_be.service.IReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation của ReactionService
 * Quản lý reaction system đơn giản như Instagram (chỉ LOVE)
 */
@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements IReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final UserInformationRepository userInformationRepository;
    private final AccountRepository accountRepository;



    @Override
    @Transactional
    public boolean toggleReaction(String postId) {
        // Lấy thông tin user hiện tại
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        Optional<Reaction> existingReaction = reactionRepository.findByPostIdAndAccountId(postId, account.getId());
        
        if (existingReaction.isPresent()) {
            reactionRepository.delete(existingReaction.get());

            post.setReactionCount(Math.max(0, post.getReactionCount() - 1));
            postRepository.save(post);
            
            return false; // Đã unlike
        } else {
            Reaction reaction = new Reaction();
            reaction.setPostId(postId);
            reaction.setAccountId(account.getId());
            reaction.setReaction(ReactionType.LOVE);
            reaction.setCreatedAt(LocalDateTime.now());
            
            reactionRepository.save(reaction);

            post.setReactionCount(post.getReactionCount() + 1);
            postRepository.save(post);
            
            return true;
        }
    }

    @Override
    public boolean hasUserReacted(String postId, String accountId) {
        // Nếu không truyền accountId, lấy user hiện tại
        if (accountId == null) {
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            accountId = account.getId();
        }
        
        return reactionRepository.existsByPostIdAndAccountId(postId, accountId);
    }

    @Override
    public long getReactionCount(String postId) {
        return reactionRepository.countByPostId(postId);
    }

    @Override
    public Page<ReactionResponse> getReactionsByPost(String postId, Pageable pageable) {
        // Kiểm tra bài viết có tồn tại không
        postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        List<Reaction> reactions = reactionRepository.findByPostId(postId);

        List<ReactionResponse> reactionResponses = reactions.stream()
                .map(this::convertToReactionResponse)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), reactionResponses.size());
        
        List<ReactionResponse> pagedReactions = reactionResponses.subList(start, end);
        
        return new PageImpl<>(pagedReactions, pageable, reactionResponses.size());
    }

    @Override
    public ReactionSummaryResponse getReactionSummary(String postId) {
        postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        ReactionSummaryResponse summary = new ReactionSummaryResponse();
        summary.setPostId(postId);
        summary.setTotalReactions(getReactionCount(postId));
        summary.setReactionType("LOVE");

        try {
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            summary.setHasUserReacted(hasUserReacted(postId, account.getId()));
        } catch (Exception e) {
            summary.setHasUserReacted(false);
        }

        List<Reaction> recentReactions = reactionRepository.findByPostId(postId)
                .stream()
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .collect(Collectors.toList());
        
        List<ReactionSummaryResponse.RecentReactor> recentReactors = recentReactions.stream()
                .map(this::convertToRecentReactor)
                .collect(Collectors.toList());
        
        summary.setRecentReactors(recentReactors);
        
        return summary;
    }

    @Override
    public Page<String> getPostsLikedByUser(String accountId, Pageable pageable) {
        // Nếu không truyền accountId, lấy user hiện tại
        final String finalAccountId;
        if (accountId == null) {
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            finalAccountId = account.getId();
        } else {
            finalAccountId = accountId;
        }
        
        // Lấy tất cả reactions của user (có thể tối ưu với repository query)
        List<Reaction> userReactions = reactionRepository.findAll()
                .stream()
                .filter(reaction -> reaction.getAccountId().equals(finalAccountId))
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .collect(Collectors.toList());
        
        // Extract postIds
        List<String> postIds = userReactions.stream()
                .map(Reaction::getPostId)
                .collect(Collectors.toList());
        
        // Áp dụng phân trang
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), postIds.size());
        
        List<String> pagedPostIds = postIds.subList(start, end);
        
        return new PageImpl<>(pagedPostIds, pageable, postIds.size());
    }

    @Override
    @Transactional
    public void deleteAllReactionsByPost(String postId) {
        reactionRepository.deleteByPostId(postId);
    }

    @Override
    @Transactional
    public long updatePostReactionCount(String postId) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));
        
        long actualCount = getReactionCount(postId);
        post.setReactionCount((int) actualCount);
        postRepository.save(post);
        
        return actualCount;
    }

    /**
     * Helper method: Convert Reaction entity sang ReactionResponse DTO
     */
    private ReactionResponse convertToReactionResponse(Reaction reaction) {
        ReactionResponse response = new ReactionResponse();
        response.setId(reaction.getId());
        response.setPostId(reaction.getPostId());
        response.setAccountId(reaction.getAccountId());
        response.setReactionType(reaction.getReaction().name());
        response.setCreatedAt(reaction.getCreatedAt());
        
        // Lấy thông tin user
        UserInformation userInfo = userInformationRepository.findByAccountId(reaction.getAccountId());
        Account account = accountRepository.findAccountById(reaction.getAccountId());
        if (userInfo != null) {
            response.setAuthorName(account.getUserName());
            response.setAuthorAvatar(userInfo.getAvatarUrl());
        } else {
            response.setAuthorName("Unknown User");
            response.setAuthorAvatar(null);
        }
        
        return response;
    }

    /**
     * Helper method: Convert Reaction entity sang RecentReactor DTO
     */
    private ReactionSummaryResponse.RecentReactor convertToRecentReactor(Reaction reaction) {
        ReactionSummaryResponse.RecentReactor reactor = new ReactionSummaryResponse.RecentReactor();
        reactor.setAccountId(reaction.getAccountId());
        
        // Lấy thông tin user
        UserInformation userInfo = userInformationRepository.findByAccountId(reaction.getAccountId());
        Account account = accountRepository.findAccountById(reaction.getAccountId());
        if (userInfo != null) {
            reactor.setAuthorName(account.getUserName());
            reactor.setAuthorAvatar(userInfo.getAvatarUrl());
        } else {
            reactor.setAuthorName("Unknown User");
            reactor.setAuthorAvatar(null);
        }
        
        return reactor;
    }
}
