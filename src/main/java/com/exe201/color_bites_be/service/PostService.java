package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreatePostRequest;
import com.exe201.color_bites_be.dto.response.PostResponse;
import com.exe201.color_bites_be.entity.Post;
import com.exe201.color_bites_be.entity.Tag;
import com.exe201.color_bites_be.entity.PostTag;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.PostRepository;
import com.exe201.color_bites_be.repository.TagRepository;
import com.exe201.color_bites_be.repository.PostTagRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private TagRepository tagRepository;
    
    @Autowired
    private PostTagRepository postTagRepository;
    
    @Autowired
    private UserInformationRepository userInformationRepository;
    
    @Autowired
    private ReactionService reactionService;
    
    /**
     * Tạo bài viết mới
     */
    @Transactional
    public PostResponse createPost(String accountId, CreatePostRequest request) {
        // Tạo bài viết
        Post post = new Post();
        post.setAccountId(accountId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setMood(request.getMood());
        post.setImageUrls(request.getImageUrls() != null ? request.getImageUrls() : new ArrayList<>());
        post.setVideoUrl(request.getVideoUrl());
        post.setReactionCount(0);
        post.setCommentCount(0);
        post.setIsDeleted(false);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        
        Post savedPost = postRepository.save(post);
        
        // Xử lý tags
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            processTags(savedPost.getId(), request.getTags());
        }
        
        return convertToPostResponse(savedPost, accountId);
    }
    
    /**
     * Lấy danh sách bài viết (feed)
     */
    public Page<PostResponse> getPosts(Pageable pageable, String currentUserId) {
        Page<Post> posts = postRepository.findByIsDeletedFalse(pageable);
        return posts.map(post -> convertToPostResponse(post, currentUserId));
    }
    
    /**
     * Lấy bài viết theo ID
     */
    public PostResponse getPostById(String postId, String currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));
        
        if (post.getIsDeleted()) {
            throw new NotFoundException("Bài viết đã bị xóa");
        }
        
        return convertToPostResponse(post, currentUserId);
    }
    
    /**
     * Lấy bài viết của một user
     */
    public Page<PostResponse> getPostsByUser(String userId, Pageable pageable, String currentUserId) {
        Page<Post> posts = postRepository.findByAccountIdAndIsDeletedFalse(userId, pageable);
        return posts.map(post -> convertToPostResponse(post, currentUserId));
    }
    
    /**
     * Tìm kiếm bài viết
     */
    public Page<PostResponse> searchPosts(String keyword, Pageable pageable, String currentUserId) {
        Page<Post> posts = postRepository.findByTitleOrContentContainingIgnoreCase(keyword, pageable);
        return posts.map(post -> convertToPostResponse(post, currentUserId));
    }
    
    /**
     * Lấy bài viết theo mood
     */
    public Page<PostResponse> getPostsByMood(String mood, Pageable pageable, String currentUserId) {
        Page<Post> posts = postRepository.findByMoodAndIsDeletedFalse(mood, pageable);
        return posts.map(post -> convertToPostResponse(post, currentUserId));
    }
    
    /**
     * Cập nhật bài viết
     */
    @Transactional
    public PostResponse updatePost(String postId, String accountId, CreatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));
        
        if (!post.getAccountId().equals(accountId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa bài viết này");
        }
        
        if (post.getIsDeleted()) {
            throw new NotFoundException("Bài viết đã bị xóa");
        }
        
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setMood(request.getMood());
        post.setImageUrls(request.getImageUrls() != null ? request.getImageUrls() : new ArrayList<>());
        post.setVideoUrl(request.getVideoUrl());
        post.setUpdatedAt(LocalDateTime.now());
        
        Post updatedPost = postRepository.save(post);
        
        // Cập nhật tags
        if (request.getTags() != null) {
            // Xóa tags cũ
            postTagRepository.deleteByPostId(postId);
            // Thêm tags mới
            if (!request.getTags().isEmpty()) {
                processTags(postId, request.getTags());
            }
        }
        
        return convertToPostResponse(updatedPost, accountId);
    }
    
    /**
     * Xóa bài viết (soft delete)
     */
    @Transactional
    public void deletePost(String postId, String accountId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));
        
        if (!post.getAccountId().equals(accountId)) {
            throw new RuntimeException("Bạn không có quyền xóa bài viết này");
        }
        
        post.setIsDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }
    
    /**
     * Xử lý tags cho bài viết
     */
    private void processTags(String postId, List<String> tagNames) {
        for (String tagName : tagNames) {
            // Tìm hoặc tạo tag
            Tag tag = tagRepository.findByNameIgnoreCase(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName.toLowerCase());
                        newTag.setUsageCount(0);
                        newTag.setCreatedAt(LocalDateTime.now());
                        return tagRepository.save(newTag);
                    });
            
            // Tăng usage count
            tag.setUsageCount(tag.getUsageCount() + 1);
            tagRepository.save(tag);
            
            // Tạo PostTag
            PostTag postTag = new PostTag();
            postTag.setPostId(postId);
            postTag.setTagId(tag.getId());
            postTagRepository.save(postTag);
        }
    }
    
    /**
     * Chuyển đổi Post thành PostResponse
     */
    private PostResponse convertToPostResponse(Post post, String currentUserId) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setAccountId(post.getAccountId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setMood(post.getMood());
        response.setImageUrls(post.getImageUrls());
        response.setVideoUrl(post.getVideoUrl());
        response.setReactionCount(post.getReactionCount());
        response.setCommentCount(post.getCommentCount());
        response.setIsDeleted(post.getIsDeleted());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        
        // Lấy thông tin user
        userInformationRepository.findByAccountId(post.getAccountId()).ifPresent(userInfo -> {
            response.setUsername(userInfo.getUsername());
            response.setUserAvatar(userInfo.getAvatar());
        });
        
        // Lấy reaction của user hiện tại
        if (currentUserId != null) {
            response.setCurrentUserReaction(reactionService.getUserReactionType(post.getId(), currentUserId));
        }
        
        // Lấy tags
        List<PostTag> postTags = postTagRepository.findByPostId(post.getId());
        List<String> tagNames = postTags.stream()
                .map(postTag -> tagRepository.findById(postTag.getTagId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Tag::getName)
                .collect(Collectors.toList());
        response.setTags(tagNames);
        
        // Lấy thống kê reaction
        response.setReactionStats(reactionService.getReactionStats(post.getId()));
        
        return response;
    }
    
    /**
     * Cập nhật số lượng comment
     */
    @Transactional
    public void updateCommentCount(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));
        
        // Đếm số comment không bị xóa
        long commentCount = postRepository.countByAccountIdAndIsDeletedFalse(postId);
        post.setCommentCount((int) commentCount);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }
} 