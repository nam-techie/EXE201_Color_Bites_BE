package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.CreateCommentRequest;
import com.exe201.color_bites_be.dto.request.UpdateCommentRequest;
import com.exe201.color_bites_be.dto.response.AuthorResponsePost;
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
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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


    @Override
    @Transactional
        public CommentResponse createComment(String postId, CreateCommentRequest request) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAccountId(account.getId());
        comment.setContent(request.getContent());
        comment.setIsDeleted(false);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        if(request.getParentCommentId() != null && !request.getParentCommentId().isBlank()){
            comment.setParentCommentId(request.getParentCommentId());
            Optional<Comment> depth = commentRepository.findDepthById(request.getParentCommentId());
            comment.setDepth(depth.get().getDepth() + 1);
        }else{
            comment.setParentCommentId(null);
            comment.setDepth(0);
        }

//        String parentId = request.getParentCommentId();
//        if (parentId != null && !parentId.isBlank()) {
//            Comment parent = commentRepository.findByIdAndNotDeleted(parentId)
//                    .orElseThrow(() -> new NotFoundException("Comment cha không tồn tại"));
//            comment.setParentCommentId(parent.getId());
//            comment.setDepth(parent.getDepth() + 1);
//        } else {
//            comment.setParentCommentId(null);
//            comment.setDepth(0);
//        }

        Comment savedComment = commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return buildCommentResponse(savedComment);
    }

    @Override
    public CommentResponse readCommentById(String commentId) {
        Comment comment = commentRepository.findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new NotFoundException("Comment không tồn tại"));

        return buildCommentResponse(comment);
    }

    @Override
    public Page<CommentResponse> readRootCommentsByPost(String postId, int page, int size) {
        postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Comment> comments = commentRepository.findRootCommentsByPostId(postId, pageable);

        return comments.map(comment -> buildCommentResponse(comment));
    }

    @Override
    public Page<CommentResponse> readAllCommentsByPost(String postId, int page, int size) {
        postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Comment> comments = commentRepository.findByPostIdAndNotDeleted(postId, pageable);

        return comments.map(comment -> buildCommentResponse(comment));
    }

    @Override
    public List<CommentResponse> readRepliesByComment(String parentCommentId) {
        commentRepository.findByIdAndNotDeleted(parentCommentId)
                .orElseThrow(() -> new NotFoundException("Comment cha không tồn tại"));

        List<Comment> replies = commentRepository.findRepliesByParentCommentId(parentCommentId);

        return replies.stream()
                .map(comment -> buildCommentResponse(comment))
                .collect(Collectors.toList());
    }

//    @Override
//    @Transactional
//    public CommentResponse editComment(String commentId, UpdateCommentRequest request) {
//        Comment comment = commentRepository.findByIdAndNotDeleted(commentId)
//                .orElseThrow(() -> new NotFoundException("Comment không tồn tại"));
//
//        // Kiểm tra quyền sở hữu
//        if (!comment.getAccountId().equals(accountId)) {
//            throw new RuntimeException("Bạn không có quyền chỉnh sửa comment này");
//        }
//
//        // Cập nhật content
//        comment.setContent(request.getContent());
//        // TODO: Add isEdited field to Comment entity
//        // comment.setIsEdited(true);
//        comment.setUpdatedAt(LocalDateTime.now());
//
//        Comment updatedComment = commentRepository.save(comment);
//
//        return buildCommentResponse(updatedComment, accountId);
//    }

    @Override
    @Transactional
    public void deleteComment(String commentId) {
        Comment comment = commentRepository.findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new NotFoundException("Comment không tồn tại"));

//        // Kiểm tra quyền sở hữu
//        if (!comment.getAccountId().equals(accountId)) {
//            throw new RuntimeException("Bạn không có quyền xóa comment này");
//        }

        // Soft delete comment
        comment.setIsDeleted(true);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        deleteAllReplies(commentId);

        Post post = postRepository.findByIdAndNotDeleted(comment.getPostId())
                .orElse(null);
        if (post != null) {
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postRepository.save(post);
        }
    }

    @Override
    public int countCommentsByPost(String postId) {
        return  commentRepository.findAllByPostId(postId).size();
    }

    @Override
    public long countRootCommentsByPost(String postId) {
        return commentRepository.countRootCommentsByPostId(postId);
    }

    @Override
    public List<CommentResponse> readCommentsByUser(String postId, String accountId) {
        List<Comment> comments = commentRepository.findByPostIdAndAccountIdAndNotDeleted(postId, accountId);

        return comments.stream()
                .map(comment -> buildCommentResponse(comment))
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

//    private int calculateCommentDepth(Comment comment) {
//        int depth = 0;
//        // TODO: Add parentCommentId field to Comment entity
//        // Temporary return 0 to avoid compilation error
//        /*
//        Comment current = comment;
//        while (current.getParentCommentId() != null && !current.getParentCommentId().isEmpty()) {
//            depth++;
//            current = commentRepository.findById(current.getParentCommentId())
//                    .orElse(null);
//            if (current == null) break;
//        }
//        */
//
//        return depth;
//    }

    private AuthorResponsePost setAuthorResponse(String accountId){
        Account authorAccount = accountRepository.findAccountById(accountId);
        String avatarUrl = userInformationRepository.findByAccountId(accountId).getAvatarUrl();
        AuthorResponsePost authorResponsePost = new AuthorResponsePost();
        authorResponsePost.setAccountId(authorAccount.getId());
        authorResponsePost.setAuthorName(authorAccount.getUserName());
        authorResponsePost.setAuthorAvatar(avatarUrl);
        return  authorResponsePost;
    }

    private CommentResponse buildCommentResponse(Comment comment) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommentResponse response = modelMapper.map(comment, CommentResponse.class);

        response.setAuthor(setAuthorResponse(comment.getAccountId()));

        response.setIsOwner(comment.getAccountId().equals(account.getId()));

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

    private void deleteAllReplies(String parentCommentId) {
        List<Comment> replies = commentRepository.findRepliesByParentCommentId(parentCommentId);
        
        for (Comment reply : replies) {
            deleteAllReplies(reply.getId());
            reply.setIsDeleted(true);
            reply.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(reply);
        }
    }
}
