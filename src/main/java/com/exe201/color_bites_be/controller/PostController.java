package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreatePostRequest;
import com.exe201.color_bites_be.dto.request.UpdatePostRequest;
import com.exe201.color_bites_be.dto.response.PostResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.model.UserPrincipal;
import com.exe201.color_bites_be.service.IPostImageService;
import com.exe201.color_bites_be.service.IPostService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private IPostService postService;

    @Autowired
    private IPostImageService postImageService;

    /**
     * Tạo bài viết mới
     */
    @PostMapping("/create")
    public ResponseDto<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request) {
        try {
            PostResponse response = postService.createPost(request);
            return new ResponseDto<>(HttpStatus.CREATED.value(), "Bài viết đã được tạo thành công", response);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tạo bài viết: " + e.getMessage(), null);
        }
    }

    @PostMapping("/uploadImage")
    public ResponseDto<List<String>> uploadPostImage(@RequestPart List<MultipartFile> files){
        List<String> urls = postImageService.uploadPostImages(files);
        return new ResponseDto<>(HttpStatus.OK.value(),  "Uploaded image successfully", urls);
    }

    /**
     * Lấy bài viết theo ID
     */
    @GetMapping("/read/{postId}")
    public ResponseDto<PostResponse> readPostById(
            @PathVariable String postId) {

        try {
            PostResponse response = postService.readPostById(postId);
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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<PostResponse> posts = postService.readAllPosts(page - 1, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách bài viết đã được tải thành công", posts);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách bài viết", null);
        }
    }

    /**
     * Lấy bài viết của user
     */
    @GetMapping("/read/user")
    public ResponseDto<Page<PostResponse>> readPostsByUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<PostResponse> posts = postService.readPostsByUser( page - 1, size);
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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {

            Page<PostResponse> posts = postService.searchPosts(keyword, page - 1, size);
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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {

            Page<PostResponse> posts = postService.readPostsByMood(mood, page - 1, size);
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
            @Valid @RequestBody UpdatePostRequest request) {

        try {
            PostResponse response = postService.editPost(postId, request);
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
            @PathVariable String postId) {

        try {
            postService.deletePost(postId);
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
            @RequestBody Map<String, String> requestBody) {

        try {
            String reactionType = requestBody.get("reactionType");

            if (reactionType == null || reactionType.trim().isEmpty()) {
                return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "Loại reaction không được để trống", null);
            }

            postService.toggleReaction(postId, reactionType);
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
    @GetMapping("/count/user")
    public ResponseDto<Long> countPostsByUser() {
        try {
            long count = postService.countPostsByUser();
            return new ResponseDto<>(HttpStatus.OK.value(), "Số lượng bài viết đã được tải thành công", count);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy số lượng bài viết", null);
        }
    }
}
