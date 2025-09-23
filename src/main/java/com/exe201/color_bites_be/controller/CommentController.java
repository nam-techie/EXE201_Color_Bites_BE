package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreateCommentRequest;
import com.exe201.color_bites_be.dto.request.UpdateCommentRequest;
import com.exe201.color_bites_be.dto.response.CommentResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.model.UserPrincipal;
import com.exe201.color_bites_be.service.ICommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    /**
     * Tạo comment mới cho bài viết
     */
    @PostMapping("/create/posts/{postId}")
    public ResponseDto<CommentResponse> createComment(
            @PathVariable String postId,
            @RequestBody CreateCommentRequest request) {

        try {
            CommentResponse response = commentService.createComment(postId, request);
            return new ResponseDto<>(HttpStatus.CREATED.value(), "Comment đã được tạo thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (RuntimeException e) {
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tạo comment: " + e.getMessage(), null);
        }
    }

    /**
     * Lấy comment theo ID
     */
    @GetMapping("/read/{commentId}")
    public ResponseDto<CommentResponse> readCommentById(
            @PathVariable String commentId) {

        try {
            CommentResponse response = commentService.readCommentById(commentId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Thông tin comment đã được tải thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy thông tin comment", null);
        }
    }

    /**
     * Lấy danh sách comment gốc của bài viết (có phân trang)
     */
    @GetMapping("/read/posts/{postId}/root")
    public ResponseDto<Page<CommentResponse>> readRootCommentsByPost(
            @PathVariable String postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){

        try {

            Page<CommentResponse> comments = commentService.readRootCommentsByPost(postId, page, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách comment gốc đã được tải thành công", comments);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách comment", null);
        }
    }

    /**
     * Lấy tất cả comment của bài viết (bao gồm reply, có phân trang)
     */
    @GetMapping("/read/posts/{postId}/all")
    public ResponseDto<Page<CommentResponse>> readAllCommentsByPost(
            @PathVariable String postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size){

        try {
            Page<CommentResponse> comments = commentService.readAllCommentsByPost(postId, page, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Tất cả comment đã được tải thành công", comments);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách comment", null);
        }
    }

    /**
     * Lấy replies của comment
     */
    @GetMapping("/read/{commentId}/replies")
    public ResponseDto<List<CommentResponse>> readRepliesByComment(
            @PathVariable String commentId){

        try {
            List<CommentResponse> replies = commentService.readRepliesByComment(commentId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách reply đã được tải thành công", replies);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách reply", null);
        }
    }

//    /**
//     * Cập nhật comment
//     */
//    @PutMapping("/edit/{commentId}")
//    public ResponseDto<CommentResponse> editComment(
//            @PathVariable String commentId,
//            @Valid @RequestBody UpdateCommentRequest request){
//
//        try {
//            CommentResponse response = commentService.editComment(commentId, request);
//            return new ResponseDto<>(HttpStatus.OK.value(), "Comment đã được cập nhật thành công", response);
//        } catch (NotFoundException e) {
//            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
//        } catch (RuntimeException e) {
//            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
//        } catch (Exception e) {
//            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                    "Đã xảy ra lỗi khi cập nhật comment", null);
//        }
//    }

    /**
     * Xóa comment
     */
    @DeleteMapping("/delete/{commentId}")
    public ResponseDto<String> deleteComment(
            @PathVariable String commentId){

        try {
            commentService.deleteComment(commentId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Comment đã được xóa thành công", null);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (RuntimeException e) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi xóa comment", null);
        }
    }

    /**
     * Đếm số lượng comment của bài viết
     */
    @GetMapping("/count/posts/{postId}")
    public ResponseDto<Integer> countCommentsByPost(@PathVariable String postId) {
        try {
            int count = commentService.countCommentsByPost(postId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Số lượng comment đã được tải thành công", count);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy số lượng comment", null);
        }
    }

    /**
     * Đếm số lượng comment gốc của bài viết
     */
    @GetMapping("/count/posts/{postId}/root")
    public ResponseDto<Long> countRootCommentsByPost(@PathVariable String postId) {
        try {
            long count = commentService.countRootCommentsByPost(postId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Số lượng comment gốc đã được tải thành công", count);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy số lượng comment gốc", null);
        }
    }

    /**
     * Lấy comment của user trong bài viết
     */
    @GetMapping("/read/posts/{postId}/user/{accountId}")
    public ResponseDto<List<CommentResponse>> readCommentsByUser(
            @PathVariable String postId,
            @PathVariable String accountId) {

        try {
            List<CommentResponse> comments = commentService.readCommentsByUser(postId, accountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Comment của người dùng đã được tải thành công", comments);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy comment của người dùng", null);
        }
    }
}
