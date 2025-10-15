package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateCommentRequest;
import com.exe201.color_bites_be.dto.request.UpdateCommentRequest;
import com.exe201.color_bites_be.dto.response.CommentResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Interface định nghĩa các phương thức quản lý comment
 * Hỗ trợ nested comments và các tương tác với comment
 */
public interface ICommentService {
    

    CommentResponse createComment(String postId, CreateCommentRequest request);

    CommentResponse readCommentById(String commentId);
    

    Page<CommentResponse> readRootCommentsByPost(String postId, int page, int size);

    Page<CommentResponse> readAllCommentsByPost(String postId, int page, int size);
    

    List<CommentResponse> readRepliesByComment(String parentCommentId);
    

//    CommentResponse editComment(String commentId, UpdateCommentRequest request);

    void deleteComment(String commentId);
    

    int countCommentsByPost(String postId);

    long countRootCommentsByPost(String postId);

    List<CommentResponse> readCommentsByUser(String postId, String accountId);

    void deleteAllCommentsByPost(String postId);

}
