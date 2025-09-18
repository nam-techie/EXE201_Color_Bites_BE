package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.CreateCommentRequest;
import com.exe201.color_bites_be.dto.request.UpdateCommentRequest;
import com.exe201.color_bites_be.dto.response.CommentResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.Comment;
import com.exe201.color_bites_be.entity.Post;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.CommentRepository;
import com.exe201.color_bites_be.repository.PostRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import com.exe201.color_bites_be.service.ICommentService;
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

/**
 * Implementation của ICommentService
 * Xử lý logic quản lý comment, nested comments và replies
 */
@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AccountRepository accountRepository;

    private static final int MAX_COMMENT_DEPTH = 3; // Giới hạn độ sâu nesting

    @Override
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

        // Xử lý parent comment nếu có
        if (request.getParentCommentId() != null && !request.getParentCommentId().isEmpty()) {
            Comment parentComment = commentRepository.findByIdAndNotDeleted(request.getParentCommentId())
                    .orElseThrow(() -> new NotFoundException("Comment cha không tồn tại"));

            // Kiểm tra độ sâu nesting
            int depth = calculateCommentDepth(parentComment);
            if (depth >= MAX_COMMENT_DEPTH) {
                throw new RuntimeException("Không thể tạo comment quá " + MAX_COMMENT_DEPTH + " cấp");
            }

            // TODO: Add parentCommentId field to Comment entity
            // comment.setParentCommentId(request.getParentCommentId());
        }

        // Lưu comment
        Comment savedComment = commentRepository.save(comment);

        // Cập nhật comment count của bài viết
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return buildCommentResponse(savedComment, accountId);
    }

    @Override
    public CommentResponse readCommentById(String commentId, String currentAccountId) {
        Comment comment = commentRepository.findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new NotFoundException("Comment không tồn tại"));

        return buildCommentResponse(comment, currentAccountId);
    }

    @Override
    public Page<CommentResponse> readRootCommentsByPost(String postId, int page, int size, String currentAccountId) {
        // Kiểm tra bài viết có tồn tại không
        postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Comment> comments = commentRepository.findRootCommentsByPostId(postId, pageable);

        return comments.map(comment -> buildCommentResponse(comment, currentAccountId));
    }

    @Override
    public Page<CommentResponse> readAllCommentsByPost(String postId, int page, int size, String currentAccountId) {
        // Kiểm tra bài viết có tồn tại không
        postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        // TODO: Add findByPostId(String, Pageable) method to CommentRepository
        // Temporary workaround - return empty page to avoid compilation error
        Page<Comment> comments = Page.empty(pageable);

        return comments.map(comment -> buildCommentResponse(comment, currentAccountId));
    }

    @Override
    public List<CommentResponse> readRepliesByComment(String parentCommentId, String currentAccountId) {
        // Kiểm tra comment cha có tồn tại không
        commentRepository.findByIdAndNotDeleted(parentCommentId)
                .orElseThrow(() -> new NotFoundException("Comment cha không tồn tại"));

        List<Comment> replies = commentRepository.findRepliesByParentCommentId(parentCommentId);

        return replies.stream()
                .map(comment -> buildCommentResponse(comment, currentAccountId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponse editComment(String commentId, String accountId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new NotFoundException("Comment không tồn tại"));

        // Kiểm tra quyền sở hữu
        if (!comment.getAccountId().equals(accountId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa comment này");
        }

        // Cập nhật content
        comment.setContent(request.getContent());
        // TODO: Add isEdited field to Comment entity
        // comment.setIsEdited(true);
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);

        return buildCommentResponse(updatedComment, accountId);
    }

    @Override
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
        deleteAllReplies(commentId);

        // Cập nhật comment count của bài viết
        Post post = postRepository.findByIdAndNotDeleted(comment.getPostId())
                .orElse(null);
        if (post != null) {
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postRepository.save(post);
        }
    }

    @Override
    public long countCommentsByPost(String postId) {
        // TODO: Add countByPostId method to CommentRepository
        // Temporary workaround - return 0
        return 0;
    }

    @Override
    public long countRootCommentsByPost(String postId) {
        return commentRepository.countRootCommentsByPostId(postId);
    }

    @Override
    public List<CommentResponse> readCommentsByUser(String postId, String accountId, String currentAccountId) {
        List<Comment> comments = commentRepository.findByPostIdAndAccountIdAndNotDeleted(postId, accountId);

        return comments.stream()
                .map(comment -> buildCommentResponse(comment, currentAccountId))
                .collect(Collectors.toList());
    }

    @Override
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
     * Tính độ sâu của comment (để giới hạn nesting)
     */
    private int calculateCommentDepth(Comment comment) {
        int depth = 0;
        // TODO: Add parentCommentId field to Comment entity
        // Temporary return 0 to avoid compilation error
        /*
        Comment current = comment;
        while (current.getParentCommentId() != null && !current.getParentCommentId().isEmpty()) {
            depth++;
            current = commentRepository.findById(current.getParentCommentId())
                    .orElse(null);
            if (current == null) break;
        }
        */

        return depth;
    }

    /**
     * Xây dựng CommentResponse từ Comment entity
     */
    private CommentResponse buildCommentResponse(Comment comment, String currentAccountId) {
        CommentResponse response = modelMapper.map(comment, CommentResponse.class);

        // Lấy thông tin tác giả
        UserInformation userInfo = userInformationRepository.findByAccountId(comment.getAccountId());
        Account account = accountRepository.findAccountById(comment.getAccountId());
        if (userInfo != null) {
            response.setAuthorName(account.getUserName());
            response.setAuthorAvatar(userInfo.getAvatarUrl());
        }

        // Kiểm tra quyền sở hữu
        response.setIsOwner(comment.getAccountId().equals(currentAccountId));

        // Đếm số replies
        long replyCount = commentRepository.countRepliesByParentCommentId(comment.getId());
        response.setReplyCount((int) replyCount);

        // TODO: Add findFirstRepliesByParentCommentId method to CommentRepository
        /*
        List<Comment> firstReplies = commentRepository.findFirstRepliesByParentCommentId(comment.getId(), 3);
        List<CommentResponse> replyResponses = firstReplies.stream()
                .map(reply -> buildCommentResponse(reply, currentAccountId))
                .collect(Collectors.toList());
        response.setReplies(replyResponses);
        */

        return response;
    }

    /**
     * Xóa tất cả replies của một comment (recursive)
     */
    private void deleteAllReplies(String parentCommentId) {
        List<Comment> replies = commentRepository.findRepliesByParentCommentId(parentCommentId);
        
        for (Comment reply : replies) {
            // Xóa replies của reply này (recursive)
            deleteAllReplies(reply.getId());
            
            // Xóa reply
            reply.setIsDeleted(true);
            reply.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(reply);
        }
    }
}
