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
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * Tạo bài viết mới
     */
    @PostMapping("/create")
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
            return new ResponseDto<>(HttpStatus.CREATED.value(), "Bài viết đã được tạo thành công", response);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tạo bài viết: " + e.getMessage(), null);
        }
    }

    /**
     * Lấy bài viết theo ID
     */
    @GetMapping("/read/{postId}")
    public ResponseDto<PostResponse> readPostById(
            @PathVariable String postId,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            PostResponse response = postService.readPostById(postId, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Thông tin bài viết đã được tải thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy thông tin bài viết", null);
        }
    }

    /**
     * Lấy danh sách tất cả bài viết (có phân trang)
     */
    @GetMapping("/list")
    public ResponseDto<Page<PostResponse>> readAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            Page<PostResponse> posts = postService.readAllPosts(page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách bài viết đã được tải thành công", posts);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách bài viết", null);
        }
    }

    /**
     * Lấy bài viết của user
     */
    @GetMapping("/read/user/{accountId}")
    public ResponseDto<Page<PostResponse>> readPostsByUser(
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

            Page<PostResponse> posts = postService.readPostsByUser(accountId, page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Bài viết của người dùng đã được tải thành công", posts);
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
            return new ResponseDto<>(HttpStatus.OK.value(), "Kết quả tìm kiếm bài viết đã được tải thành công", posts);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tìm kiếm bài viết", null);
        }
    }

    /**
     * Lấy bài viết theo mood
     */
    @GetMapping("/read/mood/{mood}")
    public ResponseDto<Page<PostResponse>> readPostsByMood(
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

            Page<PostResponse> posts = postService.readPostsByMood(mood, page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Bài viết theo mood đã được tải thành công", posts);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy bài viết theo mood", null);
        }
    }

    /**
     * Cập nhật bài viết
     */
    @PutMapping("/edit/{postId}")
    public ResponseDto<PostResponse> editPost(
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

            PostResponse response = postService.editPost(postId, accountId, request);
            return new ResponseDto<>(HttpStatus.OK.value(), "Bài viết đã được cập nhật thành công", response);
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
    @DeleteMapping("/delete/{postId}")
    public ResponseDto<String> deletePost(
            @PathVariable String postId,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            postService.deletePost(postId, accountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Bài viết đã được xóa thành công", null);
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
    @PutMapping("/react/{postId}")
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
            return new ResponseDto<>(HttpStatus.OK.value(), "Reaction đã được cập nhật thành công", null);
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
    @GetMapping("/count/user/{accountId}")
    public ResponseDto<Long> countPostsByUser(@PathVariable String accountId) {
        try {
            long count = postService.countPostsByUser(accountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Số lượng bài viết đã được tải thành công", count);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy số lượng bài viết", null);
        }
    }
}
