package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreatePostRequest;
import com.exe201.color_bites_be.dto.request.UpdatePostRequest;
import com.exe201.color_bites_be.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface định nghĩa các phương thức quản lý bài viết
 * Bao gồm CRUD, tìm kiếm, phân trang và tương tác với bài viết
 */
public interface IPostService {
    

    PostResponse createPost(CreatePostRequest request, List<MultipartFile> files);
    

    PostResponse readPostById(String postId);
    

    Page<PostResponse> readAllPosts(int page, int size);
    

    Page<PostResponse> readPostsByUser( int page, int size);
    

    Page<PostResponse> searchPosts(String keyword, int page, int size);
    

    Page<PostResponse> readPostsByMood(String mood, int page, int size);
    

    PostResponse editPost(String postId, UpdatePostRequest request);
    

    void deletePost(String postId);
    

    long countPostsByUser();
    

    void toggleReaction(String postId, String reactionType);
}
