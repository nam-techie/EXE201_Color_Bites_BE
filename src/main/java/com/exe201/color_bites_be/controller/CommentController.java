package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreateCommentRequest;
import com.exe201.color_bites_be.dto.request.UpdateCommentRequest;
import com.exe201.color_bites_be.dto.response.CommentResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.model.UserPrincipal;
import com.exe201.color_bites_be.service.CommentService;
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
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * Tạo comment mới cho bài viết
     */
    @PostMapping("/posts/{postId}")
    public ResponseDto<CommentResponse> createComment(
            @PathVariable String postId,
            @Valid @RequestBody CreateCommentRequest request,
            BindingResult bindingResult,
            Authentication authentication) {

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append(". ");
            }
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", null);
        }

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            CommentResponse response = commentService.createComment(postId, accountId, request);
            return new ResponseDto<>(HttpStatus.CREATED.value(), "Tạo comment thành công", response);
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
    @GetMapping("/{commentId}")
    public ResponseDto<CommentResponse> getCommentById(
            @PathVariable String commentId,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            CommentResponse response = commentService.getCommentById(commentId, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thông tin comment thành công", response);
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
    @GetMapping("/posts/{postId}/root")
    public ResponseDto<Page<CommentResponse>> getRootCommentsByPost(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            Page<CommentResponse> comments = commentService.getRootCommentsByPost(postId, page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy danh sách comment gốc thành công", comments);
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
    @GetMapping("/posts/{postId}/all")
    public ResponseDto<Page<CommentResponse>> getAllCommentsByPost(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            Page<CommentResponse> comments = commentService.getAllCommentsByPost(postId, page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy tất cả comment thành công", comments);
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
    @GetMapping("/{commentId}/replies")
    public ResponseDto<List<CommentResponse>> getRepliesByComment(
            @PathVariable String commentId,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            List<CommentResponse> replies = commentService.getRepliesByComment(commentId, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy danh sách reply thành công", replies);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách reply", null);
        }
    }

    /**
     * Cập nhật comment
     */
    @PutMapping("/{commentId}")
    public ResponseDto<CommentResponse> updateComment(
            @PathVariable String commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            BindingResult bindingResult,
            Authentication authentication) {

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append(". ");
            }
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", null);
        }

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            CommentResponse response = commentService.updateComment(commentId, accountId, request);
            return new ResponseDto<>(HttpStatus.OK.value(), "Cập nhật comment thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (RuntimeException e) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi cập nhật comment", null);
        }
    }

    /**
     * Xóa comment
     */
    @DeleteMapping("/{commentId}")
    public ResponseDto<String> deleteComment(
            @PathVariable String commentId,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            commentService.deleteComment(commentId, accountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Xóa comment thành công", null);
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
    @GetMapping("/posts/{postId}/count")
    public ResponseDto<Long> getCommentCountByPost(@PathVariable String postId) {
        try {
            long count = commentService.countCommentsByPost(postId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy số lượng comment thành công", count);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy số lượng comment", null);
        }
    }

    /**
     * Đếm số lượng comment gốc của bài viết
     */
    @GetMapping("/posts/{postId}/count/root")
    public ResponseDto<Long> getRootCommentCountByPost(@PathVariable String postId) {
        try {
            long count = commentService.countRootCommentsByPost(postId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy số lượng comment gốc thành công", count);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy số lượng comment gốc", null);
        }
    }

    /**
     * Lấy comment của user trong bài viết
     */
    @GetMapping("/posts/{postId}/user/{accountId}")
    public ResponseDto<List<CommentResponse>> getCommentsByUser(
            @PathVariable String postId,
            @PathVariable String accountId,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            List<CommentResponse> comments = commentService.getCommentsByUser(postId, accountId, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy comment của người dùng thành công", comments);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy comment của người dùng", null);
        }
    }
}
