package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.response.ListAccountResponse;
import com.exe201.color_bites_be.dto.response.AdminPostResponse;
import com.exe201.color_bites_be.dto.response.AdminRestaurantResponse;
import com.exe201.color_bites_be.dto.response.AdminTransactionResponse;
import com.exe201.color_bites_be.dto.response.AdminCommentResponse;
import com.exe201.color_bites_be.dto.response.AdminTagResponse;
import com.exe201.color_bites_be.dto.response.StatisticsResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.entity.Post;
import com.exe201.color_bites_be.entity.Restaurant;
import com.exe201.color_bites_be.entity.Transaction;
import com.exe201.color_bites_be.entity.Mood;
import com.exe201.color_bites_be.entity.Comment;
import com.exe201.color_bites_be.entity.Tag;
import com.exe201.color_bites_be.enums.TransactionEnums;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import com.exe201.color_bites_be.repository.PostRepository;
import com.exe201.color_bites_be.repository.RestaurantRepository;
import com.exe201.color_bites_be.repository.TransactionRepository;
import com.exe201.color_bites_be.repository.MoodRepository;
import com.exe201.color_bites_be.repository.CommentRepository;
import com.exe201.color_bites_be.repository.TagRepository;
import com.exe201.color_bites_be.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Implementation của IAdminService
 * Xử lý logic quản trị hệ thống cho admin
 */
@Service
public class AdminServiceImpl implements IAdminService {
    
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserInformationRepository userInformationRepository;
    
    @Autowired
    PostRepository postRepository;
    
    @Autowired
    RestaurantRepository restaurantRepository;
    
    @Autowired
    TransactionRepository transactionRepository;
    
    @Autowired
    MoodRepository moodRepository;
    
    @Autowired
    CommentRepository commentRepository;
    
    @Autowired
    TagRepository tagRepository;

    @Override
    public List<ListAccountResponse> getAllUserByAdmin() {
        List<Account> accounts = accountRepository.findAll();
        List<UserInformation> userInformations = userInformationRepository.findAll();
        List<ListAccountResponse> listAccountResponses = new ArrayList<>();
        
        for (Account account : accounts) {
            for (UserInformation userInformation : userInformations) {
                if (account.getId().equals(userInformation.getAccount().getId())) {
                    ListAccountResponse dto = new ListAccountResponse();
                    dto.setId(userInformation.getAccount().getId());
                    dto.setUsername(account.getUserName());
                    dto.setRole(account.getRole().name());
                    dto.setAvatarUrl(userInformation.getAvatarUrl());
                    dto.setCreated(account.getCreatedAt());
                    dto.setUpdated(account.getUpdatedAt());
                    dto.setActive(account.getIsActive());
                    listAccountResponses.add(dto);
                }
            }
        }
        return listAccountResponses;
    }

    @Override
    public void blockUser(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        account.setIsActive(false);
        accountRepository.save(account);
    }

    @Override
    public void activeUser(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        account.setIsActive(true);
        accountRepository.save(account);
    }

    // ========== POST MANAGEMENT ==========

    @Override
    public Page<AdminPostResponse> getAllPostsByAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAll(pageable);
        
        return posts.map(this::convertToAdminPostResponse);
    }

    @Override
    public AdminPostResponse getPostByIdByAdmin(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        return convertToAdminPostResponse(post);
    }

    @Override
    public void deletePostByAdmin(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        post.setIsDeleted(true);
        postRepository.save(post);
    }

    @Override
    public void restorePostByAdmin(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        post.setIsDeleted(false);
        postRepository.save(post);
    }

    // ========== RESTAURANT MANAGEMENT ==========

    @Override
    public Page<AdminRestaurantResponse> getAllRestaurantsByAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Restaurant> restaurants = restaurantRepository.findAll(pageable);
        
        return restaurants.map(this::convertToAdminRestaurantResponse);
    }

    @Override
    public AdminRestaurantResponse getRestaurantByIdByAdmin(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà hàng"));
        return convertToAdminRestaurantResponse(restaurant);
    }

    @Override
    public void deleteRestaurantByAdmin(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà hàng"));
        restaurant.setIsDeleted(true);
        restaurantRepository.save(restaurant);
    }

    @Override
    public void restoreRestaurantByAdmin(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà hàng"));
        restaurant.setIsDeleted(false);
        restaurantRepository.save(restaurant);
    }

    // ========== TRANSACTION MANAGEMENT ==========

    @Override
    public Page<AdminTransactionResponse> getAllTransactionsByAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        
        return transactions.map(this::convertToAdminTransactionResponse);
    }

    @Override
    public AdminTransactionResponse getTransactionByIdByAdmin(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));
        return convertToAdminTransactionResponse(transaction);
    }

    @Override
    public Page<AdminTransactionResponse> getTransactionsByStatusByAdmin(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        try {
            TransactionEnums.TxnStatus txnStatus = TransactionEnums.TxnStatus.valueOf(status.toUpperCase());
            Page<Transaction> transactions = transactionRepository.findByStatus(txnStatus, pageable);
            return transactions.map(this::convertToAdminTransactionResponse);
        } catch (IllegalArgumentException e) {
            // Nếu status không hợp lệ, trả về tất cả transactions
            Page<Transaction> transactions = transactionRepository.findAll(pageable);
            return transactions.map(this::convertToAdminTransactionResponse);
        }
    }

    // ========== STATISTICS ==========

    @Override
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Đếm số lượng users
        long totalUsers = accountRepository.count();
        long activeUsers = accountRepository.countByIsActive(true);
        
        // Đếm số lượng posts
        long totalPosts = postRepository.count();
        long deletedPosts = postRepository.countByIsDeleted(true);
        
        // Đếm số lượng restaurants
        long totalRestaurants = restaurantRepository.count();
        long deletedRestaurants = restaurantRepository.countByIsDeleted(true);
        
        // Đếm số lượng transactions
        long totalTransactions = transactionRepository.count();
        
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("blockedUsers", totalUsers - activeUsers);
        stats.put("totalPosts", totalPosts);
        stats.put("deletedPosts", deletedPosts);
        stats.put("activePosts", totalPosts - deletedPosts);
        stats.put("totalRestaurants", totalRestaurants);
        stats.put("deletedRestaurants", deletedRestaurants);
        stats.put("activeRestaurants", totalRestaurants - deletedRestaurants);
        stats.put("totalTransactions", totalTransactions);
        
        return stats;
    }

    // ========== COMMENT MANAGEMENT ==========

    @Override
    public Page<AdminCommentResponse> getAllCommentsByAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findAll(pageable);
        
        return comments.map(this::convertToAdminCommentResponse);
    }

    @Override
    public AdminCommentResponse getCommentByIdByAdmin(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy comment"));
        return convertToAdminCommentResponse(comment);
    }

    @Override
    public void deleteCommentByAdmin(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy comment"));
        comment.setIsDeleted(true);
        commentRepository.save(comment);
    }

    @Override
    public void restoreCommentByAdmin(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy comment"));
        comment.setIsDeleted(false);
        commentRepository.save(comment);
    }

    @Override
    public Page<AdminCommentResponse> getCommentsByPostByAdmin(String postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);
        
        return comments.map(this::convertToAdminCommentResponse);
    }

    @Override
    public Map<String, Object> getCommentStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalComments = commentRepository.count();
        long deletedComments = commentRepository.countByIsDeleted(true);
        long activeComments = totalComments - deletedComments;
        
        stats.put("totalComments", totalComments);
        stats.put("activeComments", activeComments);
        stats.put("deletedComments", deletedComments);
        
        return stats;
    }

    // ========== TAG MANAGEMENT ==========

    @Override
    public Page<AdminTagResponse> getAllTagsByAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Tag> tags = tagRepository.findAll(pageable);
        
        return tags.map(this::convertToAdminTagResponse);
    }

    @Override
    public AdminTagResponse getTagByIdByAdmin(String tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tag"));
        return convertToAdminTagResponse(tag);
    }

    @Override
    public AdminTagResponse createTagByAdmin(String name, String description) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setDescription(description);
        tag.setUsageCount(0L);
        tag.setIsDeleted(false);
        
        Tag savedTag = tagRepository.save(tag);
        return convertToAdminTagResponse(savedTag);
    }

    @Override
    public AdminTagResponse updateTagByAdmin(String tagId, String name, String description) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tag"));
        
        tag.setName(name);
        tag.setDescription(description);
        
        Tag savedTag = tagRepository.save(tag);
        return convertToAdminTagResponse(savedTag);
    }

    @Override
    public void deleteTagByAdmin(String tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tag"));
        tag.setIsDeleted(true);
        tagRepository.save(tag);
    }

    @Override
    public Map<String, Object> getTagStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalTags = tagRepository.count();
        long deletedTags = tagRepository.countByIsDeleted(true);
        long activeTags = totalTags - deletedTags;
        
        stats.put("totalTags", totalTags);
        stats.put("activeTags", activeTags);
        stats.put("deletedTags", deletedTags);
        
        return stats;
    }

    // ========== ADVANCED STATISTICS ==========

    @Override
    public StatisticsResponse getUserStatistics() {
        StatisticsResponse response = new StatisticsResponse();
        
        long totalUsers = accountRepository.count();
        long activeUsers = accountRepository.countByIsActive(true);
        
        response.setTotalUsers(totalUsers);
        response.setActiveUsers(activeUsers);
        
        return response;
    }

    @Override
    public StatisticsResponse getPostStatistics() {
        StatisticsResponse response = new StatisticsResponse();
        
        long totalPosts = postRepository.count();
        long deletedPosts = postRepository.countByIsDeleted(true);
        long activePosts = totalPosts - deletedPosts;
        
        response.setTotalPosts(totalPosts);
        
        return response;
    }

    @Override
    public StatisticsResponse getRestaurantStatistics() {
        StatisticsResponse response = new StatisticsResponse();
        
        long totalRestaurants = restaurantRepository.count();
        long deletedRestaurants = restaurantRepository.countByIsDeleted(true);
        long activeRestaurants = totalRestaurants - deletedRestaurants;
        
        response.setTotalRestaurants(totalRestaurants);
        
        return response;
    }

    @Override
    public StatisticsResponse getRevenueStatistics() {
        StatisticsResponse response = new StatisticsResponse();
        
        long totalTransactions = transactionRepository.count();
        long successfulTransactions = transactionRepository.countByStatus(TransactionEnums.TxnStatus.SUCCESS);
        long failedTransactions = transactionRepository.countByStatus(TransactionEnums.TxnStatus.FAILED);
        long pendingTransactions = transactionRepository.countByStatus(TransactionEnums.TxnStatus.PENDING);
        
        response.setTotalTransactions(totalTransactions);
        response.setSuccessfulTransactions(successfulTransactions);
        response.setFailedTransactions(failedTransactions);
        response.setPendingTransactions(pendingTransactions);
        
        return response;
    }

    @Override
    public StatisticsResponse getEngagementStatistics() {
        StatisticsResponse response = new StatisticsResponse();
        
        long totalComments = commentRepository.count();
        response.setTotalComments(totalComments);
        
        return response;
    }

    @Override
    public StatisticsResponse getChallengeStatistics() {
        StatisticsResponse response = new StatisticsResponse();
        
        // TODO: Implement challenge statistics when ChallengeRepository is available
        response.setTotalChallenges(0L);
        
        return response;
    }

    // ========== HELPER METHODS ==========

    private AdminPostResponse convertToAdminPostResponse(Post post) {
        AdminPostResponse response = new AdminPostResponse();
        response.setId(post.getId());
        response.setAccountId(post.getAccountId());
        response.setContent(post.getContent());
        response.setMoodId(post.getMoodId());
        response.setReactionCount(post.getReactionCount());
        response.setCommentCount(post.getCommentCount());
        response.setIsDeleted(post.getIsDeleted());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        
        // Lấy thông tin account
        Optional<Account> accountOpt = accountRepository.findById(post.getAccountId());
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            response.setAccountName(account.getUserName());
            response.setAuthorEmail(account.getEmail());
            response.setAuthorIsActive(account.getIsActive());
            response.setAuthorRole(account.getRole().name());
        }
        
        // Lấy thông tin mood
        if (post.getMoodId() != null) {
            Optional<Mood> moodOpt = moodRepository.findById(post.getMoodId());
            if (moodOpt.isPresent()) {
                response.setMoodName(moodOpt.get().getName());
            }
        }
        
        return response;
    }

    private AdminRestaurantResponse convertToAdminRestaurantResponse(Restaurant restaurant) {
        AdminRestaurantResponse response = new AdminRestaurantResponse();
        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setAddress(restaurant.getAddress());
        response.setLongitude(restaurant.getLongitude() != null ? restaurant.getLongitude().doubleValue() : null);
        response.setLatitude(restaurant.getLatitude() != null ? restaurant.getLatitude().doubleValue() : null);
        response.setType(null); // Restaurant entity doesn't have type field
        response.setCreatedBy(restaurant.getCreatedBy());
        response.setCreatedAt(restaurant.getCreatedAt());
        response.setIsDeleted(restaurant.getIsDeleted());
        
        // Lấy thông tin creator
        if (restaurant.getCreatedBy() != null) {
            Optional<Account> accountOpt = accountRepository.findById(restaurant.getCreatedBy());
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                response.setCreatedByName(account.getUserName());
                response.setCreatorEmail(account.getEmail());
                response.setCreatorIsActive(account.getIsActive());
                response.setCreatorRole(account.getRole().name());
            }
        }
        
        return response;
    }

    private AdminTransactionResponse convertToAdminTransactionResponse(Transaction transaction) {
        AdminTransactionResponse response = new AdminTransactionResponse();
        response.setId(transaction.getId());
        response.setAccountId(transaction.getAccountId());
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency().name());
        response.setType(transaction.getType().name());
        response.setStatus(transaction.getStatus().name());
        response.setPlan(transaction.getPlan().name());
        response.setGateway(transaction.getGateway());
        response.setOrderCode(transaction.getOrderCode());
        response.setProviderTxnId(transaction.getProviderTxnId());
        response.setMetadata(transaction.getMetadata());
        response.setRawPayload(transaction.getRawPayload());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        
        // Lấy thông tin account
        Optional<Account> accountOpt = accountRepository.findById(transaction.getAccountId());
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            response.setAccountName(account.getUserName());
            response.setAccountEmail(account.getEmail());
            response.setAccountIsActive(account.getIsActive());
            response.setAccountRole(account.getRole().name());
        }
        
        return response;
    }

    private AdminCommentResponse convertToAdminCommentResponse(Comment comment) {
        AdminCommentResponse response = new AdminCommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPostId());
        response.setContent(comment.getContent());
        response.setAccountId(comment.getAccountId());
        response.setParentCommentId(comment.getParentCommentId());
        response.setReplyCount(comment.getReplyCount());
        response.setIsDeleted(comment.getIsDeleted());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        
        // Lấy thông tin account
        Optional<Account> accountOpt = accountRepository.findById(comment.getAccountId());
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            response.setAccountName(account.getUserName());
            response.setAuthorEmail(account.getEmail());
            response.setAuthorIsActive(account.getIsActive());
            response.setAuthorRole(account.getRole().name());
        }
        
        // Lấy thông tin post
        Optional<Post> postOpt = postRepository.findById(comment.getPostId());
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            response.setPostTitle(post.getContent().length() > 50 ? 
                post.getContent().substring(0, 50) + "..." : post.getContent());
            
            // Lấy thông tin tác giả post
            Optional<Account> postAuthorOpt = accountRepository.findById(post.getAccountId());
            if (postAuthorOpt.isPresent()) {
                Account postAuthor = postAuthorOpt.get();
                response.setPostAuthorName(postAuthor.getUserName());
                response.setPostAuthorEmail(postAuthor.getEmail());
            }
        }
        
        return response;
    }

    private AdminTagResponse convertToAdminTagResponse(Tag tag) {
        AdminTagResponse response = new AdminTagResponse();
        response.setId(tag.getId());
        response.setName(tag.getName());
        response.setDescription(tag.getDescription());
        response.setUsageCount(tag.getUsageCount());
        response.setIsDeleted(tag.getIsDeleted());
        response.setCreatedAt(tag.getCreatedAt());
        response.setUpdatedAt(tag.getUpdatedAt());
        
        // TODO: Implement createdBy information when available
        response.setCreatedBy("System");
        response.setCreatedByName("System");
        response.setCreatedByEmail("system@colorbites.com");
        
        // TODO: Implement postCount and restaurantCount when relationships are available
        response.setPostCount(0L);
        response.setRestaurantCount(0L);
        
        return response;
    }
}
