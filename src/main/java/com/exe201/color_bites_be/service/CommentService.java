package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateCommentRequest;
import com.exe201.color_bites_be.dto.request.UpdateCommentRequest;
import com.exe201.color_bites_be.dto.response.CommentResponse;
import com.exe201.color_bites_be.entity.Comment;
import com.exe201.color_bites_be.entity.Post;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.CommentRepository;
import com.exe201.color_bites_be.repository.PostRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final int MAX_COMMENT_DEPTH = 3; // Giới hạn độ sâu nesting

    /**
     * Tạo comment mới
     */
    @Transactional
    public CommentResponse createComment(String postId, String accountId, CreateCommentRequest request) {
        // Kiểm tra bài viết có tồn tại không
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAccountId(accountId);
        comment.setContent(request.getContent());
        comment.setIsDeleted(false);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        // Xử lý nested comment
        if (request.getParentCommentId() != null && !request.getParentCommentId().trim().isEmpty()) {
            Comment parentComment = commentRepository.findByIdAndNotDeleted(request.getParentCommentId())
                    .orElseThrow(() -> new NotFoundException("Comment cha không tồn tại"));

            // Kiểm tra comment cha có cùng bài viết không
            if (!parentComment.getPostId().equals(postId)) {
                throw new RuntimeException("Comment cha không thuộc bài viết này");
            }

            // Kiểm tra độ sâu
            if (parentComment.getDepth() >= MAX_COMMENT_DEPTH) {
                throw new RuntimeException("Đã đạt giới hạn độ sâu comment");
            }

            comment.setParentComment(request.getParentCommentId());
            comment.setDepth(parentComment.getDepth() + 1);
        } else {
            // Comment gốc
            comment.setParentComment(null);
            comment.setDepth(0);
        }

        // Lưu comment
        Comment savedComment = commentRepository.save(comment);

        // Cập nhật comment count cho bài viết
        updatePostCommentCount(postId);

        // Tạo response
        return buildCommentResponse(savedComment, accountId, false);
    }

    /**
     * Lấy comment theo ID
     */
    public CommentResponse readCommentById(String commentId, String currentAccountId) {
        Comment comment = commentRepository.findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new NotFoundException("Comment không tồn tại"));

        return buildCommentResponse(comment, currentAccountId, true);
    }

    /**
     * Lấy danh sách comment gốc của bài viết (có phân trang)
     */
    public Page<CommentResponse> readRootCommentsByPost(String postId, int page, int size, String currentAccountId) {
        // Kiểm tra bài viết có tồn tại không
        postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Comment> comments = commentRepository.findRootCommentsByPostId(postId, pageable);

        return comments.map(comment -> buildCommentResponse(comment, currentAccountId, true));
    }

    /**
     * Lấy tất cả comment của bài viết (bao gồm reply, có phân trang)
     */
    public Page<CommentResponse> readAllCommentsByPost(String postId, int page, int size, String currentAccountId) {
        // Kiểm tra bài viết có tồn tại không
        postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Comment> comments = commentRepository.findByPostIdAndNotDeleted(postId, pageable);

        return comments.map(comment -> buildCommentResponse(comment, currentAccountId, false));
    }

    /**
     * Lấy replies của comment
     */
    public List<CommentResponse> readRepliesByComment(String parentCommentId, String currentAccountId) {
        // Kiểm tra comment cha có tồn tại không
        commentRepository.findByIdAndNotDeleted(parentCommentId)
                .orElseThrow(() -> new NotFoundException("Comment cha không tồn tại"));

        List<Comment> replies = commentRepository.findRepliesByParentCommentId(parentCommentId);

        return replies.stream()
                .map(comment -> buildCommentResponse(comment, currentAccountId, true))
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật comment
     */
    @Transactional
    public CommentResponse editComment(String commentId, String accountId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new NotFoundException("Comment không tồn tại"));

        // Kiểm tra quyền sở hữu
        if (!comment.getAccountId().equals(accountId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa comment này");
        }

        // Cập nhật nội dung
        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);

        return buildCommentResponse(updatedComment, accountId, true);
    }

    /**
     * Xóa comment (soft delete)
     */
    @Transactional
    public void deleteComment(String commentId, String accountId) {
        Comment comment = commentRepository.findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new NotFoundException("Comment không tồn tại"));

        // Kiểm tra quyền sở hữu
        if (!comment.getAccountId().equals(accountId)) {
            throw new RuntimeException("Bạn không có quyền xóa comment này");
        }

        // Soft delete comment
        comment.setIsDeleted(true);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        // Soft delete tất cả replies
        deleteRepliesRecursively(commentId);

        // Cập nhật comment count cho bài viết
        updatePostCommentCount(comment.getPostId());
    }

    /**
     * Xóa tất cả replies của comment (đệ quy)
     */
    private void deleteRepliesRecursively(String parentCommentId) {
        List<Comment> replies = commentRepository.findRepliesByParentCommentId(parentCommentId);
        for (Comment reply : replies) {
            reply.setIsDeleted(true);
            reply.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(reply);

            // Đệ quy xóa replies của reply
            deleteRepliesRecursively(reply.getId());
        }
    }

    /**
     * Đếm số lượng comment của bài viết
     */
    public long countCommentsByPost(String postId) {
        return commentRepository.countAllCommentsByPostId(postId);
    }

    /**
     * Đếm số lượng comment gốc của bài viết
     */
    public long countRootCommentsByPost(String postId) {
        return commentRepository.countRootCommentsByPostId(postId);
    }

    /**
     * Cập nhật comment count cho bài viết
     */
    private void updatePostCommentCount(String postId) {
        Post post = postRepository.findByIdAndNotDeleted(postId).orElse(null);
        if (post != null) {
            long commentCount = commentRepository.countAllCommentsByPostId(postId);
            post.setCommentCount((int) commentCount);
            post.setUpdatedAt(LocalDateTime.now());
            postRepository.save(post);
        }
    }

    /**
     * Xây dựng CommentResponse từ Comment entity
     */
    private CommentResponse buildCommentResponse(Comment comment, String currentAccountId, boolean includeReplies) {
        CommentResponse response = modelMapper.map(comment, CommentResponse.class);

        // Lấy thông tin tác giả
        UserInformation userInfo = userInformationRepository.findByAccountId(comment.getAccountId());
        if (userInfo != null) {
            response.setAuthorName(userInfo.getFullName());
            response.setAuthorAvatar(userInfo.getAvatarUrl());
        }

        // Kiểm tra quyền sở hữu
        response.setIsOwner(comment.getAccountId().equals(currentAccountId));

        // Kiểm tra đã chỉnh sửa chưa
        response.setIsEdited(!comment.getCreatedAt().equals(comment.getUpdatedAt()));

        // Đếm số reply
        long replyCount = commentRepository.countRepliesByParentCommentId(comment.getId());
        response.setReplyCount((int) replyCount);

        // Lấy replies nếu cần
        if (includeReplies && replyCount > 0) {
            List<Comment> replies = commentRepository.findRepliesByParentCommentId(comment.getId());
            List<CommentResponse> replyResponses = replies.stream()
                    .map(reply -> buildCommentResponse(reply, currentAccountId, true))
                    .collect(Collectors.toList());
            response.setReplies(replyResponses);
        } else {
            response.setReplies(new ArrayList<>());
        }

        return response;
    }

    /**
     * Xóa tất cả comment của bài viết khi bài viết bị xóa
     */
    @Transactional
    public void deleteAllCommentsByPost(String postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        for (Comment comment : comments) {
            comment.setIsDeleted(true);
            comment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(comment);
        }
    }

    /**
     * Lấy comment theo user
     */
    public List<CommentResponse> readCommentsByUser(String postId, String accountId, String currentAccountId) {
        List<Comment> comments = commentRepository.findByPostIdAndAccountIdAndNotDeleted(postId, accountId);

        return comments.stream()
                .map(comment -> buildCommentResponse(comment, currentAccountId, false))
                .collect(Collectors.toList());
    }
}
