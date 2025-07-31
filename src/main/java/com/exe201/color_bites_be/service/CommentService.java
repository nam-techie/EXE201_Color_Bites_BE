package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateCommentRequest;
import com.exe201.color_bites_be.dto.response.CommentResponse;
import com.exe201.color_bites_be.entity.Comment;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.CommentRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserInformationRepository userInformationRepository;
    
    @Autowired
    private PostService postService;
    
    /**
     * Tạo comment mới
     */
    @Transactional
    public CommentResponse createComment(String postId, String accountId, CreateCommentRequest request) {
        // Kiểm tra bài viết có tồn tại không
        postService.getPostById(postId, accountId);
        
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAccountId(accountId);
        comment.setContent(request.getContent());
        comment.setIsDeleted(false);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        // Xử lý parent comment và depth
        if (request.getParentCommentId() != null && !request.getParentCommentId().isEmpty()) {
            Comment parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new NotFoundException("Comment cha không tồn tại"));
            
            comment.setParentComment(request.getParentCommentId());
            comment.setDepth(parentComment.getDepth() + 1);
        } else {
            comment.setParentComment(null);
            comment.setDepth(0);
        }
        
        Comment savedComment = commentRepository.save(comment);
        
        // Cập nhật số lượng comment của bài viết
        postService.updateCommentCount(postId);
        
        return convertToCommentResponse(savedComment);
    }
    
    /**
     * Lấy danh sách comment của một bài viết
     */
    public Page<CommentResponse> getCommentsByPost(String postId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByPostIdAndParentCommentIsNullAndIsDeletedFalseOrderByCreatedAtDesc(postId, pageable);
        return comments.map(this::convertToCommentResponse);
    }
    
    /**
     * Lấy reply của một comment
     */
    public List<CommentResponse> getRepliesByComment(String commentId) {
        List<Comment> replies = commentRepository.findByParentCommentAndIsDeletedFalseOrderByCreatedAtAsc(commentId);
        return replies.stream()
                .map(this::convertToCommentResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy comment theo ID
     */
    public CommentResponse getCommentById(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment không tồn tại"));
        
        if (comment.getIsDeleted()) {
            throw new NotFoundException("Comment đã bị xóa");
        }
        
        return convertToCommentResponse(comment);
    }
    
    /**
     * Lấy comment của một user
     */
    public Page<CommentResponse> getCommentsByUser(String userId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByAccountIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, pageable);
        return comments.map(this::convertToCommentResponse);
    }
    
    /**
     * Cập nhật comment
     */
    @Transactional
    public CommentResponse updateComment(String commentId, String accountId, CreateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment không tồn tại"));
        
        if (!comment.getAccountId().equals(accountId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa comment này");
        }
        
        if (comment.getIsDeleted()) {
            throw new NotFoundException("Comment đã bị xóa");
        }
        
        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        
        Comment updatedComment = commentRepository.save(comment);
        return convertToCommentResponse(updatedComment);
    }
    
    /**
     * Xóa comment (soft delete)
     */
    @Transactional
    public void deleteComment(String commentId, String accountId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment không tồn tại"));
        
        if (!comment.getAccountId().equals(accountId)) {
            throw new RuntimeException("Bạn không có quyền xóa comment này");
        }
        
        comment.setIsDeleted(true);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        
        // Cập nhật số lượng comment của bài viết
        postService.updateCommentCount(comment.getPostId());
    }
    
    /**
     * Chuyển đổi Comment thành CommentResponse
     */
    private CommentResponse convertToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPostId());
        response.setAccountId(comment.getAccountId());
        response.setParentCommentId(comment.getParentComment());
        response.setDepth(comment.getDepth());
        response.setContent(comment.getContent());
        response.setIsDeleted(comment.getIsDeleted());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        
        // Lấy thông tin user
        userInformationRepository.findByAccountId(comment.getAccountId()).ifPresent(userInfo -> {
            response.setUsername(userInfo.getUsername());
            response.setUserAvatar(userInfo.getAvatar());
        });
        
        // Lấy số lượng reply
        long replyCount = commentRepository.countByPostIdAndIsDeletedFalse(comment.getId());
        response.setReplyCount((int) replyCount);
        
        // Lấy danh sách reply (chỉ cho comment gốc)
        if (comment.getParentComment() == null) {
            List<CommentResponse> replies = getRepliesByComment(comment.getId());
            response.setReplies(replies);
        }
        
        return response;
    }
    
    /**
     * Đếm số comment của một bài viết
     */
    public long countCommentsByPost(String postId) {
        return commentRepository.countByPostIdAndIsDeletedFalse(postId);
    }
    
    /**
     * Đếm số comment của một user
     */
    public long countCommentsByUser(String userId) {
        return commentRepository.countByAccountIdAndIsDeletedFalse(userId);
    }
} 