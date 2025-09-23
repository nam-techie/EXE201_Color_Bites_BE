package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.CreatePostRequest;
import com.exe201.color_bites_be.dto.request.UpdatePostRequest;
import com.exe201.color_bites_be.dto.response.AuthorResponsePost;
import com.exe201.color_bites_be.dto.response.PostResponse;
import com.exe201.color_bites_be.dto.response.TagResponse;
import com.exe201.color_bites_be.entity.*;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.repository.*;
import com.exe201.color_bites_be.service.IPostService;
import com.exe201.color_bites_be.service.IReactionService;
import com.exe201.color_bites_be.util.HashtagExtractor;
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

@Service
public class PostServiceImpl implements IPostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostTagRepository postTagRepository;


    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IReactionService reactionService;

    @Autowired
    private MoodRepository moodRepository;

    @Autowired
    private PostImagesRepository postImagesRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Tạo post entity
        Post post = new Post();
        post.setAccountId(account.getId());
        post.setContent(request.getContent());
        post.setMoodId(request.getMoodId());
        post.setReactionCount(0);
        post.setCommentCount(0);
        post.setIsDeleted(false);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        // Lưu post
        Post savedPost = postRepository.save(post);

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<PostImages> imgs = request.getImageUrls().stream().map(u -> {
                PostImages pi = new PostImages();
                pi.setPostId(savedPost.getId());
                pi.setUrl(u);
                pi.setCreatedAt(LocalDateTime.now());
                return pi;
            }).toList();
            postImagesRepository.saveAll(imgs);
        }

        // Trích xuất hashtags từ content
        List<String> extractedHashtags = HashtagExtractor.extractHashtags(request.getContent());
        
//        // Combine manual tags với extracted hashtags
//        List<String> allTags = HashtagExtractor.combineAndNormalizeTags(request.getTagNames(), extractedHashtags);
        
        // Xử lý tags
        List<Tag> tags = new ArrayList<>();
        if (!extractedHashtags.isEmpty()) {
            tags = processPostTags(savedPost.getId(), extractedHashtags);
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
    public Page<PostResponse> readPostsByUser(int page, int size) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByAccountIdAndNotDeleted(account.getId(), pageable);

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
    public Page<PostResponse> readPostsByMood(String moodId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByMoodIdAndNotDeleted(moodId, pageable);

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

        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getMood() != null) {
            post.setMoodId(request.getMood());
        }
        // ImageUrls will be handled by PostImages entity
        if (request.getVideoUrl() != null) {
            post.setVideoUrl(request.getVideoUrl());
        }

        post.setUpdatedAt(LocalDateTime.now());

        // Lưu post
        Post updatedPost = postRepository.save(post);

        // Xử lý tags - auto-extract hashtags từ content và combine với manual tags
        List<Tag> tags = new ArrayList<>();
        
        // Trích xuất hashtags từ content nếu content được cập nhật
        List<String> extractedHashtags = new ArrayList<>();
        if (request.getContent() != null) {
            extractedHashtags = HashtagExtractor.extractHashtags(request.getContent());
        }
        
        // Combine manual tags với extracted hashtags
        List<String> allTags = HashtagExtractor.combineAndNormalizeTags(request.getTagNames(), extractedHashtags);
        
        if (!allTags.isEmpty() || request.getContent() != null) {
            // Xóa tags cũ nếu có thay đổi tags hoặc content
            postTagRepository.deleteByPostId(postId);
            
            // Thêm tags mới (bao gồm cả hashtags từ content)
            if (!allTags.isEmpty()) {
                tags = processPostTags(postId, allTags);
            }
        } else {
            // Giữ nguyên tags cũ nếu không có thay đổi gì
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
        reactionService.deleteAllReactionsByPost(postId);
        
        // Xóa tất cả comments liên quan (soft delete)
        deleteAllCommentsByPost(postId);
    }

    @Override
    public long countPostsByUser() {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return postRepository.countByAccountIdAndNotDeleted(account.getId());
    }

    @Override
    @Transactional
    public void toggleReaction(String postId, String reactionType) {
        // Delegate to ReactionService for cleaner separation of concerns
        reactionService.toggleReaction(postId);
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

    private AuthorResponsePost setAuthorResponse(String accountId){
        Account authorAccount = accountRepository.findAccountById(accountId);
        String avatarUrl = userInformationRepository.findByAccountId(accountId).getAvatarUrl();
        AuthorResponsePost authorResponsePost = new AuthorResponsePost();
        authorResponsePost.setAccountId(authorAccount.getId());
        authorResponsePost.setAuthorName(authorAccount.getUserName());
        authorResponsePost.setAuthorAvatar(avatarUrl);
        return  authorResponsePost;
    }

    private PostResponse buildPostResponse(Post post, List<Tag> tags) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostResponse response = modelMapper.map(post, PostResponse.class);
        if (post.getMoodId() != null) {
            Optional<Mood> mood = moodRepository.findById(post.getMoodId());
            if (mood.isPresent()) {
                response.setMoodName(mood.get().getName());
                response.setMoodEmoji(mood.get().getEmoji());
            }
        }
        // Lấy thông tin tác giả
        response.setAuthor(setAuthorResponse(post.getAccountId()));

        List<TagResponse> tagResponses = tags.stream()
                .map(tag -> modelMapper.map(tag, TagResponse.class))
                .collect(Collectors.toList());
        response.setTags(tagResponses);


        response.setIsOwner(post.getAccountId().equals(account.getId()));

        response.setImageUrls(postImagesRepository.findUrlsByPostId(post.getId()));
        return response;
    }

    private void deleteAllCommentsByPost(String postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        for (Comment comment : comments) {
            comment.setIsDeleted(true);
            comment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(comment);
        }
    }
}
