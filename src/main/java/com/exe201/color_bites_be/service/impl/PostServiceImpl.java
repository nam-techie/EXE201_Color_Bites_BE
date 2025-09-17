package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.CreatePostRequest;
import com.exe201.color_bites_be.dto.request.UpdatePostRequest;
import com.exe201.color_bites_be.dto.response.PostResponse;
import com.exe201.color_bites_be.dto.response.TagResponse;
import com.exe201.color_bites_be.entity.*;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.*;
import com.exe201.color_bites_be.service.IPostService;
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
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements IPostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Tạo post entity
        Post post = new Post();
        post.setAccountId(account.getId());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setMood(request.getMood());
        post.setImageUrls(request.getImageUrls());
        post.setVideoUrl(request.getVideoUrl());
        post.setReactionCount(0);
        post.setCommentCount(0);
        post.setIsDeleted(false);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        // Lưu post
        Post savedPost = postRepository.save(post);

        // Xử lý tags
        List<Tag> tags = new ArrayList<>();
        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            tags = processPostTags(savedPost.getId(), request.getTagNames());
        }

        // Tạo response
        return buildPostResponse(savedPost, tags);
    }

    @Override
    public PostResponse readPostById(String postId) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        // Lấy tags của bài viết
        List<Tag> tags = getPostTags(postId);

        return buildPostResponse(post, tags);
    }

    @Override
    public Page<PostResponse> readAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findAllActivePosts(pageable);

        return posts.map(post -> {
            List<Tag> tags = getPostTags(post.getId());
            return buildPostResponse(post, tags);
        });
    }

    @Override
    public Page<PostResponse> readPostsByUser(String accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByAccountIdAndNotDeleted(accountId, pageable);

        return posts.map(post -> {
            List<Tag> tags = getPostTags(post.getId());
            return buildPostResponse(post, tags);
        });
    }

    @Override
    public Page<PostResponse> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByKeywordAndNotDeleted(keyword, pageable);

        return posts.map(post -> {
            List<Tag> tags = getPostTags(post.getId());
            return buildPostResponse(post, tags);
        });
    }

    @Override
    public Page<PostResponse> readPostsByMood(String mood, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByMoodAndNotDeleted(mood, pageable);

        return posts.map(post -> {
            List<Tag> tags = getPostTags(post.getId());
            return buildPostResponse(post,tags);
        });
    }

    @Override
    @Transactional
    public PostResponse editPost(String postId, UpdatePostRequest request) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        // Kiểm tra quyền sở hữu
        if (!post.getAccountId().equals(account.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa bài viết này");
        }

        // Cập nhật các field nếu có
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getMood() != null) {
            post.setMood(request.getMood());
        }
        if (request.getImageUrls() != null) {
            post.setImageUrls(request.getImageUrls());
        }
        if (request.getVideoUrl() != null) {
            post.setVideoUrl(request.getVideoUrl());
        }

        post.setUpdatedAt(LocalDateTime.now());

        // Lưu post
        Post updatedPost = postRepository.save(post);

        // Xử lý tags nếu có thay đổi
        List<Tag> tags = new ArrayList<>();
        if (request.getTagNames() != null) {
            // Xóa tags cũ
            postTagRepository.deleteByPostId(postId);
            // Thêm tags mới
            tags = processPostTags(postId, request.getTagNames());
        } else {
            // Giữ nguyên tags cũ
            tags = getPostTags(postId);
        }

        return buildPostResponse(updatedPost, tags);
    }

    @Override
    @Transactional
    public void deletePost(String postId) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        // Kiểm tra quyền sở hữu
        if (!post.getAccountId().equals(account.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa bài viết này");
        }

        // Soft delete
        post.setIsDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        // Xóa tags liên quan
        postTagRepository.deleteByPostId(postId);
        
        // Xóa reactions liên quan
        reactionRepository.deleteByPostId(postId);
        
        // Xóa tất cả comments liên quan (soft delete)
        deleteAllCommentsByPost(postId);
    }

    @Override
    public long countPostsByUser(String accountId) {return postRepository.countByAccountIdAndNotDeleted(accountId);}

    @Override
    @Transactional
    public void toggleReaction(String postId, String reactionType) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Kiểm tra bài viết có tồn tại không
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new NotFoundException("Bài viết không tồn tại"));

        // Kiểm tra user đã react chưa
        reactionRepository.findByPostIdAndAccountId(postId, account.getId())
                .ifPresentOrElse(
                        existingReaction -> {
                            if (existingReaction.getReactionType().equals(reactionType)) {
                                // Unreact - xóa reaction
                                reactionRepository.delete(existingReaction);
                                post.setReactionCount(Math.max(0, post.getReactionCount() - 1));
                            } else {
                                // Thay đổi loại reaction
                                existingReaction.setReactionType(reactionType);
                                reactionRepository.save(existingReaction);
                            }
                        },
                        () -> {
                            // Tạo reaction mới
                            Reaction reaction = new Reaction();
                            reaction.setPostId(postId);
                            reaction.setAccountId(account.getId());
                            reaction.setReactionType(reactionType);
                            reaction.setCreatedAt(LocalDateTime.now());
                            reactionRepository.save(reaction);
                            post.setReactionCount(post.getReactionCount() + 1);
                        }
                );

        // Cập nhật reaction count
        postRepository.save(post);
    }

    /**
     * Xử lý tags cho bài viết
     */
    private List<Tag> processPostTags(String postId, List<String> tagNames) {
        List<Tag> tags = new ArrayList<>();

        for (String tagName : tagNames) {
            if (tagName == null || tagName.trim().isEmpty()) continue;

            String normalizedTagName = tagName.trim().toLowerCase();

            // Tìm hoặc tạo tag
            Tag tag = tagRepository.findByNameIgnoreCase(normalizedTagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(normalizedTagName);
                        newTag.setUsageCount(0);
                        newTag.setCreatedAt(LocalDateTime.now());
                        return tagRepository.save(newTag);
                    });

            // Tăng usage count
            tag.setUsageCount(tag.getUsageCount() + 1);
            tag = tagRepository.save(tag);
            tags.add(tag);

            // Tạo liên kết PostTag nếu chưa có
            if (!postTagRepository.existsByPostIdAndTagId(postId, tag.getId())) {
                PostTag postTag = new PostTag();
                postTag.setPostId(postId);
                postTag.setTagId(tag.getId());
                postTagRepository.save(postTag);
            }
        }

        return tags;
    }

    /**
     * Lấy danh sách tags của bài viết
     */
    private List<Tag> getPostTags(String postId) {
        List<PostTag> postTags = postTagRepository.findByPostId(postId);
        List<String> tagIds = postTags.stream()
                .map(PostTag::getTagId)
                .collect(Collectors.toList());

        if (tagIds.isEmpty()) {
            return new ArrayList<>();
        }

        return tagRepository.findAllById(tagIds);
    }

    /**
     * Xây dựng PostResponse từ Post entity
     */
    private PostResponse buildPostResponse(Post post, List<Tag> tags) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostResponse response = modelMapper.map(post, PostResponse.class);

        // Lấy thông tin tác giả
        UserInformation userInfo = userInformationRepository.findByAccountId(post.getAccountId());
        if (userInfo != null) {
            response.setAuthorName(userInfo.getFullName());
            response.setAuthorAvatar(userInfo.getAvatarUrl());
        }

        // Set tags
        List<TagResponse> tagResponses = tags.stream()
                .map(tag -> modelMapper.map(tag, TagResponse.class))
                .collect(Collectors.toList());
        response.setTags(tagResponses);

        // Kiểm tra quyền sở hữu
        response.setIsOwner(post.getAccountId().equals(account.getId()));

        // Kiểm tra user đã react chưa
        if (account.getId() != null) {
            reactionRepository.findByPostIdAndAccountId(post.getId(), account.getId())
                    .ifPresentOrElse(
                            reaction -> {
                                response.setHasReacted(true);
                                response.setUserReactionType(reaction.getReactionType());
                            },
                            () -> {
                                response.setHasReacted(false);
                                response.setUserReactionType(null);
                            }
                    );
        } else {
            response.setHasReacted(false);
            response.setUserReactionType(null);
        }

        return response;
    }

    /**
     * Xóa tất cả comment của bài viết khi bài viết bị xóa
     */
    private void deleteAllCommentsByPost(String postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        for (Comment comment : comments) {
            comment.setIsDeleted(true);
            comment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(comment);
        }
    }
}
