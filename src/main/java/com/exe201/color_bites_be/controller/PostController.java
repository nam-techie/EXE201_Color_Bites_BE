package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreatePostRequest;
import com.exe201.color_bites_be.dto.request.UpdatePostRequest;
import com.exe201.color_bites_be.dto.response.PostResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.model.UserPrincipal;
import com.exe201.color_bites_be.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * Tạo bài viết mới
     */
    @PostMapping
    public ResponseDto<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
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

            PostResponse response = postService.createPost(accountId, request);
            return new ResponseDto<>(HttpStatus.CREATED.value(), "Tạo bài viết thành công", response);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tạo bài viết: " + e.getMessage(), null);
        }
    }

    /**
     * Lấy bài viết theo ID
     */
    @GetMapping("/{postId}")
    public ResponseDto<PostResponse> getPostById(
            @PathVariable String postId,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            PostResponse response = postService.getPostById(postId, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thông tin bài viết thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy thông tin bài viết", null);
        }
    }

    /**
     * Lấy danh sách bài viết (có phân trang)
     */
    @GetMapping
    public ResponseDto<Page<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            Page<PostResponse> posts = postService.getAllPosts(page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy danh sách bài viết thành công", posts);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách bài viết", null);
        }
    }

    /**
     * Lấy bài viết của user
     */
    @GetMapping("/user/{accountId}")
    public ResponseDto<Page<PostResponse>> getPostsByUser(
            @PathVariable String accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            Page<PostResponse> posts = postService.getPostsByUser(accountId, page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy bài viết của người dùng thành công", posts);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy bài viết của người dùng", null);
        }
    }

    /**
     * Tìm kiếm bài viết
     */
    @GetMapping("/search")
    public ResponseDto<Page<PostResponse>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            Page<PostResponse> posts = postService.searchPosts(keyword, page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Tìm kiếm bài viết thành công", posts);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tìm kiếm bài viết", null);
        }
    }

    /**
     * Lấy bài viết theo mood
     */
    @GetMapping("/mood/{mood}")
    public ResponseDto<Page<PostResponse>> getPostsByMood(
            @PathVariable String mood,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            Page<PostResponse> posts = postService.getPostsByMood(mood, page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy bài viết theo mood thành công", posts);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy bài viết theo mood", null);
        }
    }

    /**
     * Cập nhật bài viết
     */
    @PutMapping("/{postId}")
    public ResponseDto<PostResponse> updatePost(
            @PathVariable String postId,
            @Valid @RequestBody UpdatePostRequest request,
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

            PostResponse response = postService.updatePost(postId, accountId, request);
            return new ResponseDto<>(HttpStatus.OK.value(), "Cập nhật bài viết thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (RuntimeException e) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi cập nhật bài viết", null);
        }
    }

    /**
     * Xóa bài viết
     */
    @DeleteMapping("/{postId}")
    public ResponseDto<String> deletePost(
            @PathVariable String postId,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            postService.deletePost(postId, accountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Xóa bài viết thành công", null);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (RuntimeException e) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi xóa bài viết", null);
        }
    }

    /**
     * React/Unreact bài viết
     */
    @PostMapping("/{postId}/react")
    public ResponseDto<String> toggleReaction(
            @PathVariable String postId,
            @RequestBody Map<String, String> requestBody,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();
            String reactionType = requestBody.get("reactionType");

            if (reactionType == null || reactionType.trim().isEmpty()) {
                return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "Loại reaction không được để trống", null);
            }

            postService.toggleReaction(postId, accountId, reactionType);
            return new ResponseDto<>(HttpStatus.OK.value(), "Cập nhật reaction thành công", null);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi cập nhật reaction", null);
        }
    }

    /**
     * Lấy số lượng bài viết của user
     */
    @GetMapping("/count/{accountId}")
    public ResponseDto<Long> getPostCountByUser(@PathVariable String accountId) {
        try {
            long count = postService.countPostsByUser(accountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy số lượng bài viết thành công", count);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy số lượng bài viết", null);
        }
    }
}
