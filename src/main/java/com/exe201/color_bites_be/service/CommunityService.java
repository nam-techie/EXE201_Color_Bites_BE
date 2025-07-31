package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateCommentRequest;
import com.exe201.color_bites_be.dto.request.CreatePostRequest;
import com.exe201.color_bites_be.dto.request.ReactionRequest;
import com.exe201.color_bites_be.dto.response.CommentResponse;
import com.exe201.color_bites_be.dto.response.PostResponse;
import com.exe201.color_bites_be.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommunityService {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private ReactionService reactionService;
    
    @Autowired
    private TagService tagService;
    
    // ==================== POST OPERATIONS ====================
    
    /**
     * Tạo bài viết mới
     */
    public PostResponse createPost(String accountId, CreatePostRequest request) {
        return postService.createPost(accountId, request);
    }
    
    /**
     * Lấy feed bài viết
     */
    public Page<PostResponse> getFeed(Pageable pageable, String currentUserId) {
        return postService.getPosts(pageable, currentUserId);
    }
    
    /**
     * Lấy bài viết theo ID
     */
    public PostResponse getPost(String postId, String currentUserId) {
        return postService.getPostById(postId, currentUserId);
    }
    
    /**
     * Lấy bài viết của user
     */
    public Page<PostResponse> getUserPosts(String userId, Pageable pageable, String currentUserId) {
        return postService.getPostsByUser(userId, pageable, currentUserId);
    }
    
    /**
     * Tìm kiếm bài viết
     */
    public Page<PostResponse> searchPosts(String keyword, Pageable pageable, String currentUserId) {
        return postService.searchPosts(keyword, pageable, currentUserId);
    }
    
    /**
     * Lấy bài viết theo mood
     */
    public Page<PostResponse> getPostsByMood(String mood, Pageable pageable, String currentUserId) {
        return postService.getPostsByMood(mood, pageable, currentUserId);
    }
    
    /**
     * Cập nhật bài viết
     */
    public PostResponse updatePost(String postId, String accountId, CreatePostRequest request) {
        return postService.updatePost(postId, accountId, request);
    }
    
    /**
     * Xóa bài viết
     */
    public void deletePost(String postId, String accountId) {
        postService.deletePost(postId, accountId);
    }
    
    // ==================== COMMENT OPERATIONS ====================
    
    /**
     * Tạo comment
     */
    public CommentResponse createComment(String postId, String accountId, CreateCommentRequest request) {
        return commentService.createComment(postId, accountId, request);
    }
    
    /**
     * Lấy comments của bài viết
     */
    public Page<CommentResponse> getPostComments(String postId, Pageable pageable) {
        return commentService.getCommentsByPost(postId, pageable);
    }
    
    /**
     * Lấy replies của comment
     */
    public List<CommentResponse> getCommentReplies(String commentId) {
        return commentService.getRepliesByComment(commentId);
    }
    
    /**
     * Cập nhật comment
     */
    public CommentResponse updateComment(String commentId, String accountId, CreateCommentRequest request) {
        return commentService.updateComment(commentId, accountId, request);
    }
    
    /**
     * Xóa comment
     */
    public void deleteComment(String commentId, String accountId) {
        commentService.deleteComment(commentId, accountId);
    }
    
    // ==================== REACTION OPERATIONS ====================
    
    /**
     * Thêm/ cập nhật reaction
     */
    public void addReaction(String postId, String accountId, ReactionRequest request) {
        reactionService.addOrUpdateReaction(postId, accountId, request);
    }
    
    /**
     * Xóa reaction
     */
    public void removeReaction(String postId, String accountId) {
        reactionService.removeReaction(postId, accountId);
    }
    
    /**
     * Lấy reaction của user
     */
    public String getUserReaction(String postId, String accountId) {
        return reactionService.getUserReactionType(postId, accountId);
    }
    
    /**
     * Lấy thống kê reaction
     */
    public Map<String, Long> getReactionStats(String postId) {
        return reactionService.getReactionStats(postId);
    }
    
    // ==================== TAG OPERATIONS ====================
    
    /**
     * Lấy tags phổ biến
     */
    public Page<Tag> getPopularTags(Pageable pageable) {
        return tagService.getPopularTags(pageable);
    }
    
    /**
     * Tìm kiếm tags
     */
    public Page<Tag> searchTags(String keyword, Pageable pageable) {
        return tagService.searchTags(keyword, pageable);
    }
    
    /**
     * Tạo tag mới
     */
    public Tag createTag(String name) {
        return tagService.createTag(name);
    }
    
    // ==================== STATISTICS ====================
    
    /**
     * Lấy thống kê tổng quan
     */
    public Map<String, Object> getCommunityStats(String userId) {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        // Thống kê bài viết
        long postCount = postService.countCommentsByPost(userId);
        stats.put("postCount", postCount);
        
        // Thống kê comment
        long commentCount = commentService.countCommentsByUser(userId);
        stats.put("commentCount", commentCount);
        
        // Thống kê reaction
        long reactionCount = reactionService.countReactionsByUser(userId).size();
        stats.put("reactionCount", reactionCount);
        
        return stats;
    }
    
    /**
     * Lấy thống kê bài viết
     */
    public Map<String, Object> getPostStats(String postId) {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        // Số lượng comment
        long commentCount = commentService.countCommentsByPost(postId);
        stats.put("commentCount", commentCount);
        
        // Thống kê reaction
        Map<String, Long> reactionStats = reactionService.getReactionStats(postId);
        stats.put("reactionStats", reactionStats);
        
        return stats;
    }
} 