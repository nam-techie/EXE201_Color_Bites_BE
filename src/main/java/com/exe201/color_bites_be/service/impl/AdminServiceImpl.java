package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.response.ListAccountResponse;
import com.exe201.color_bites_be.dto.response.AdminPostResponse;
import com.exe201.color_bites_be.dto.response.AdminRestaurantResponse;
import com.exe201.color_bites_be.dto.response.AdminTransactionResponse;
import com.exe201.color_bites_be.dto.response.AdminCommentResponse;
import com.exe201.color_bites_be.dto.response.AdminTagResponse;
import com.exe201.color_bites_be.dto.response.UserStatisticsResponse;
import com.exe201.color_bites_be.dto.response.PostStatisticsResponse;
import com.exe201.color_bites_be.dto.response.RestaurantStatisticsResponse;
import com.exe201.color_bites_be.dto.response.RevenueStatisticsResponse;
import com.exe201.color_bites_be.dto.response.EngagementStatisticsResponse;
import com.exe201.color_bites_be.dto.response.ChallengeStatisticsResponse;
import com.exe201.color_bites_be.dto.response.AdminMoodResponse;
import com.exe201.color_bites_be.dto.response.ChallengeDefinitionResponse;
import com.exe201.color_bites_be.dto.response.RevenueReportResponse;
import com.exe201.color_bites_be.dto.response.UserInformationResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.entity.Post;
import com.exe201.color_bites_be.entity.Restaurant;
import com.exe201.color_bites_be.entity.Transaction;
import com.exe201.color_bites_be.entity.Comment;
import com.exe201.color_bites_be.entity.Tag;
import com.exe201.color_bites_be.entity.Mood;
import com.exe201.color_bites_be.entity.ChallengeDefinition;
import com.exe201.color_bites_be.enums.TransactionEnums;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import com.exe201.color_bites_be.repository.PostRepository;
import com.exe201.color_bites_be.repository.RestaurantRepository;
import com.exe201.color_bites_be.repository.TransactionRepository;
import com.exe201.color_bites_be.repository.MoodRepository;
import com.exe201.color_bites_be.repository.CommentRepository;
import com.exe201.color_bites_be.repository.TagRepository;
import com.exe201.color_bites_be.repository.ReactionRepository;
import com.exe201.color_bites_be.repository.FavoriteRepository;
import com.exe201.color_bites_be.repository.MoodMapRepository;
import com.exe201.color_bites_be.repository.QuizRepository;
import com.exe201.color_bites_be.repository.SubscriptionRepository;
import com.exe201.color_bites_be.repository.ChallengeDefinitionRepository;
import com.exe201.color_bites_be.repository.ChallengeParticipationRepository;
import com.exe201.color_bites_be.service.IAdminService;
import com.exe201.color_bites_be.enums.SubscriptionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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
    
    @Autowired
    ReactionRepository reactionRepository;
    
    @Autowired
    FavoriteRepository favoriteRepository;
    
    @Autowired
    MoodMapRepository moodMapRepository;
    
    @Autowired
    QuizRepository quizRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;
    
    @Autowired
    ChallengeDefinitionRepository challengeDefinitionRepository;
    
    @Autowired
    ChallengeParticipationRepository challengeParticipationRepository;

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

    @Override
    public UserInformationResponse getUserInformation(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        UserInformation userInformation = userInformationRepository.findByAccountId(accountId);
        if (userInformation == null) {
            throw new RuntimeException("Thông tin người dùng không tồn tại");
        }
        UserInformationResponse response = new UserInformationResponse();
        response.setAccountId(account.getId());
        response.setUsername(account.getUserName());
        response.setAvatarUrl(userInformation.getAvatarUrl());
        response.setBio(userInformation.getBio());
        response.setGender(userInformation.getGender() != null ? userInformation.getGender().name() : null);
        response.setSubscriptionPlan(userInformation.getSubscriptionPlan() != null ? userInformation.getSubscriptionPlan().name() : null);
        response.setCreatedAt(userInformation.getCreatedAt());
        response.setUpdatedAt(userInformation.getUpdatedAt());

        subscriptionRepository.findByAccountIdAndStatus(account.getId(), SubscriptionStatus.ACTIVE)
                .ifPresentOrElse(sub -> {
                    response.setSubscriptionStatus(SubscriptionStatus.ACTIVE.name());
                    response.setSubscriptionStartsAt(sub.getStartsAt());
                    response.setSubscriptionExpiresAt(sub.getExpiresAt());
                    long remaining = 0;
                    if (sub.getExpiresAt() != null) {
                        remaining = Duration.between(LocalDateTime.now(), sub.getExpiresAt()).toDays();
                        if (remaining < 0) remaining = 0;
                    }
                    response.setSubscriptionRemainingDays((int) remaining);
                }, () -> {
                    response.setSubscriptionStatus(SubscriptionStatus.EXPIRED.name());
                    response.setSubscriptionStartsAt(null);
                    response.setSubscriptionExpiresAt(null);
                    response.setSubscriptionRemainingDays(0);
                });
        return response;
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

    @Override
    public List<AdminTransactionResponse> getAllTransactionsListByAdmin() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(this::convertToAdminTransactionResponse)
                .collect(Collectors.toList());
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
        tag.setUsageCount(0);
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
    public UserStatisticsResponse getUserStatistics() {
        UserStatisticsResponse response = new UserStatisticsResponse();
        
        long totalUsers = accountRepository.count();
        long activeUsers = accountRepository.countByIsActive(true);
        
        response.setTotalUsers(totalUsers);
        response.setActiveUsers(activeUsers);
        response.setLastUpdated(LocalDateTime.now());
        response.setSystemStatus("ACTIVE");
        
        return response;
    }

    @Override
    public PostStatisticsResponse getPostStatistics() {
        PostStatisticsResponse response = new PostStatisticsResponse();
        
        long totalPosts = postRepository.count();
        
        response.setTotalPosts(totalPosts);
        response.setLastUpdated(LocalDateTime.now());
        response.setSystemStatus("ACTIVE");
        
        return response;
    }

    @Override
    public RestaurantStatisticsResponse getRestaurantStatistics() {
        RestaurantStatisticsResponse response = new RestaurantStatisticsResponse();
        
        long totalRestaurants = restaurantRepository.count();
        
        response.setTotalRestaurants(totalRestaurants);
        response.setLastUpdated(LocalDateTime.now());
        response.setSystemStatus("ACTIVE");
        
        return response;
    }

    @Override
    public RevenueStatisticsResponse getRevenueStatistics() {
        RevenueStatisticsResponse response = new RevenueStatisticsResponse();
        
        long totalTransactions = transactionRepository.count();
        long successfulTransactions = transactionRepository.countByStatus(TransactionEnums.TxnStatus.SUCCESS);
        long failedTransactions = transactionRepository.countByStatus(TransactionEnums.TxnStatus.FAILED);
        long pendingTransactions = transactionRepository.countByStatus(TransactionEnums.TxnStatus.PENDING);
        
        // Tính tổng doanh thu từ các transaction thành công
        List<Transaction> successfulTxnList = transactionRepository.findAll()
                .stream()
                .filter(txn -> txn.getStatus() == TransactionEnums.TxnStatus.SUCCESS && txn.getAmount() != null)
                .collect(Collectors.toList());
        
        double totalRevenue = successfulTxnList.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        // Tính doanh thu tháng này
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        double monthlyRevenue = successfulTxnList.stream()
                .filter(txn -> txn.getCreatedAt() != null && txn.getCreatedAt().isAfter(startOfMonth))
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        // Tính doanh thu hôm nay
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        double dailyRevenue = successfulTxnList.stream()
                .filter(txn -> txn.getCreatedAt() != null && txn.getCreatedAt().isAfter(startOfDay))
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        response.setTotalTransactions(totalTransactions);
        response.setSuccessfulTransactions(successfulTransactions);
        response.setFailedTransactions(failedTransactions);
        response.setPendingTransactions(pendingTransactions);
        response.setTotalRevenue(totalRevenue);
        response.setMonthlyRevenue(monthlyRevenue);
        response.setDailyRevenue(dailyRevenue);
        response.setLastUpdated(LocalDateTime.now());
        response.setSystemStatus("ACTIVE");
        
        return response;
    }

    @Override
    public EngagementStatisticsResponse getEngagementStatistics() {
        EngagementStatisticsResponse response = new EngagementStatisticsResponse();
        
        long totalComments = commentRepository.count();
        long totalReactions = reactionRepository.count();
        long totalFavorites = favoriteRepository.count();
        long totalMoodMaps = moodMapRepository.count();
        long totalQuizzes = quizRepository.count();
        
        // Tính average rating từ restaurants
        List<Restaurant> restaurants = restaurantRepository.findAll();
        double averageRating = restaurants.stream()
                .filter(r -> r.getRating() != null)
                .mapToDouble(Restaurant::getRating)
                .average()
                .orElse(0.0);
        
        response.setTotalComments(totalComments);
        response.setTotalReactions(totalReactions);
        response.setTotalFavorites(totalFavorites);
        response.setTotalMoodMaps(totalMoodMaps);
        response.setTotalQuizzes(totalQuizzes);
        response.setAverageRating(averageRating);
        response.setLastUpdated(LocalDateTime.now());
        response.setSystemStatus("ACTIVE");
        
        return response;
    }

    @Override
    public ChallengeStatisticsResponse getChallengeStatistics() {
        ChallengeStatisticsResponse response = new ChallengeStatisticsResponse();
        
        long totalChallenges = challengeDefinitionRepository.count();
        
        response.setTotalChallenges(totalChallenges);
        response.setLastUpdated(LocalDateTime.now());
        response.setSystemStatus("ACTIVE");
        
        return response;
    }

    // ========== MOOD MANAGEMENT ==========

    @Override
    public List<AdminMoodResponse> getAllMoodsByAdmin() {
        List<Mood> moods = moodRepository.findAll();
        return moods.stream()
                .map(this::convertToAdminMoodResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdminMoodResponse getMoodByIdByAdmin(String moodId) {
        Mood mood = moodRepository.findById(moodId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mood"));
        return convertToAdminMoodResponse(mood);
    }

    // ========== CHALLENGE MANAGEMENT ==========

    @Override
    public List<ChallengeDefinitionResponse> getAllChallengesByAdmin() {
        List<ChallengeDefinition> challenges = challengeDefinitionRepository.findAll();
        return challenges.stream()
                .map(this::convertToChallengeDefinitionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChallengeDefinitionResponse getChallengeByIdByAdmin(String challengeId) {
        ChallengeDefinition challenge = challengeDefinitionRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy challenge"));
        return convertToChallengeDefinitionResponse(challenge);
    }

    // ========== REVENUE REPORT ==========

    @Override
    public RevenueReportResponse getRevenueReport() {
        RevenueReportResponse response = new RevenueReportResponse();
        
        // Lấy tất cả transactions thành công
        List<Transaction> allTransactions = transactionRepository.findAll();
        List<Transaction> successfulTransactions = allTransactions.stream()
                .filter(txn -> txn.getStatus() == TransactionEnums.TxnStatus.SUCCESS && txn.getAmount() != null)
                .collect(Collectors.toList());
        
        // Tính tổng doanh thu
        double totalRevenue = successfulTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        // Tính doanh thu tháng này
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        double monthlyRevenue = successfulTransactions.stream()
                .filter(txn -> txn.getCreatedAt() != null && txn.getCreatedAt().isAfter(startOfMonth))
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        // Tính doanh thu hôm nay
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        double dailyRevenue = successfulTransactions.stream()
                .filter(txn -> txn.getCreatedAt() != null && txn.getCreatedAt().isAfter(startOfDay))
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        // Tính doanh thu theo ngày (30 ngày gần nhất)
        Map<String, Double> dailyRevenueMap = new HashMap<>();
        Map<String, Long> dailyCountMap = new HashMap<>();
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        
        for (Transaction txn : successfulTransactions) {
            if (txn.getCreatedAt() != null) {
                LocalDate txnDate = txn.getCreatedAt().toLocalDate();
                if (txnDate.isAfter(thirtyDaysAgo) || txnDate.isEqual(thirtyDaysAgo)) {
                    String dateKey = txnDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    dailyRevenueMap.put(dateKey, dailyRevenueMap.getOrDefault(dateKey, 0.0) + txn.getAmount());
                    dailyCountMap.put(dateKey, dailyCountMap.getOrDefault(dateKey, 0L) + 1);
                }
            }
        }
        
        List<RevenueReportResponse.DailyRevenue> dailyRevenues = dailyRevenueMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    RevenueReportResponse.DailyRevenue daily = new RevenueReportResponse.DailyRevenue();
                    daily.setDate(entry.getKey());
                    daily.setRevenue(entry.getValue());
                    daily.setTransactionCount(dailyCountMap.getOrDefault(entry.getKey(), 0L));
                    return daily;
                })
                .collect(Collectors.toList());
        
        // Tính doanh thu theo tháng (12 tháng gần nhất)
        Map<String, Double> monthlyRevenueMap = new HashMap<>();
        Map<String, Long> monthlyCountMap = new HashMap<>();
        LocalDate twelveMonthsAgo = LocalDate.now().minusMonths(12);
        
        for (Transaction txn : successfulTransactions) {
            if (txn.getCreatedAt() != null) {
                LocalDate txnDate = txn.getCreatedAt().toLocalDate();
                if (txnDate.isAfter(twelveMonthsAgo) || txnDate.isEqual(twelveMonthsAgo)) {
                    String monthKey = txnDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                    monthlyRevenueMap.put(monthKey, monthlyRevenueMap.getOrDefault(monthKey, 0.0) + txn.getAmount());
                    monthlyCountMap.put(monthKey, monthlyCountMap.getOrDefault(monthKey, 0L) + 1);
                }
            }
        }
        
        List<RevenueReportResponse.MonthlyRevenue> monthlyRevenues = monthlyRevenueMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    RevenueReportResponse.MonthlyRevenue monthly = new RevenueReportResponse.MonthlyRevenue();
                    monthly.setMonth(entry.getKey());
                    monthly.setRevenue(entry.getValue());
                    monthly.setTransactionCount(monthlyCountMap.getOrDefault(entry.getKey(), 0L));
                    return monthly;
                })
                .collect(Collectors.toList());
        
        // Đếm số lượng transactions
        long totalTransactions = allTransactions.size();
        long successfulCount = successfulTransactions.size();
        long failedCount = allTransactions.stream()
                .filter(txn -> txn.getStatus() == TransactionEnums.TxnStatus.FAILED)
                .count();
        long pendingCount = allTransactions.stream()
                .filter(txn -> txn.getStatus() == TransactionEnums.TxnStatus.PENDING)
                .count();
        
        response.setTotalRevenue(totalRevenue);
        response.setMonthlyRevenue(monthlyRevenue);
        response.setDailyRevenue(dailyRevenue);
        response.setTotalTransactions(totalTransactions);
        response.setSuccessfulTransactions(successfulCount);
        response.setFailedTransactions(failedCount);
        response.setPendingTransactions(pendingCount);
        response.setDailyRevenues(dailyRevenues);
        response.setMonthlyRevenues(monthlyRevenues);
        response.setReportGeneratedAt(LocalDateTime.now());
        
        return response;
    }

    @Override
    public byte[] exportRevenueReportToCsv() {
        RevenueReportResponse report = getRevenueReport();
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            // Thêm BOM cho UTF-8 để Excel hiển thị đúng tiếng Việt
            baos.write(0xEF);
            baos.write(0xBB);
            baos.write(0xBF);
            
            try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
                // Header tổng quan
                writer.write("BÁO CÁO TỔNG DOANH THU\n");
                writer.write("Ngày tạo báo cáo: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n\n");
                
                // Tổng quan
                writer.write("=== TỔNG QUAN ===\n");
                writer.write("Tổng doanh thu," + String.format("%.2f", report.getTotalRevenue()) + "\n");
                writer.write("Doanh thu tháng này," + String.format("%.2f", report.getMonthlyRevenue()) + "\n");
                writer.write("Doanh thu hôm nay," + String.format("%.2f", report.getDailyRevenue()) + "\n");
                writer.write("Tổng số giao dịch," + report.getTotalTransactions() + "\n");
                writer.write("Giao dịch thành công," + report.getSuccessfulTransactions() + "\n");
                writer.write("Giao dịch thất bại," + report.getFailedTransactions() + "\n");
                writer.write("Giao dịch đang chờ," + report.getPendingTransactions() + "\n\n");
                
                // Doanh thu theo ngày
                writer.write("=== DOANH THU THEO NGÀY (30 NGÀY GẦN NHẤT) ===\n");
                writer.write("Ngày,Doanh thu,Số giao dịch\n");
                for (RevenueReportResponse.DailyRevenue daily : report.getDailyRevenues()) {
                    writer.write(daily.getDate() + "," 
                        + String.format("%.2f", daily.getRevenue()) + "," 
                        + daily.getTransactionCount() + "\n");
                }
                writer.write("\n");
                
                // Doanh thu theo tháng
                writer.write("=== DOANH THU THEO THÁNG (12 THÁNG GẦN NHẤT) ===\n");
                writer.write("Tháng,Doanh thu,Số giao dịch\n");
                for (RevenueReportResponse.MonthlyRevenue monthly : report.getMonthlyRevenues()) {
                    writer.write(monthly.getMonth() + "," 
                        + String.format("%.2f", monthly.getRevenue()) + "," 
                        + monthly.getTransactionCount() + "\n");
                }
                
                writer.flush();
            }
            
            return baos.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file CSV: " + e.getMessage(), e);
        }
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
        response.setUsageCount(tag.getUsageCount().longValue());
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

    private AdminMoodResponse convertToAdminMoodResponse(Mood mood) {
        AdminMoodResponse response = new AdminMoodResponse();
        response.setId(mood.getId());
        response.setName(mood.getName());
        response.setEmoji(mood.getEmoji());
        response.setCreatedAt(mood.getCreatedAt());
        return response;
    }

    private ChallengeDefinitionResponse convertToChallengeDefinitionResponse(ChallengeDefinition challenge) {
        ChallengeDefinitionResponse response = new ChallengeDefinitionResponse();
        response.setId(challenge.getId());
        response.setTitle(challenge.getTitle());
        response.setDescription(challenge.getDescription());
        response.setChallengeType(challenge.getChallengeType());
        response.setRestaurantId(challenge.getRestaurantId());
        response.setTypeObjId(challenge.getTypeObjId());
        response.setImages(challenge.getImages());
        response.setTargetCount(challenge.getTargetCount());
        response.setStartDate(challenge.getStartDate());
        response.setEndDate(challenge.getEndDate());
        response.setRewardDescription(challenge.getRewardDescription());
        response.setCreatedBy(challenge.getCreatedBy());
        response.setCreatedAt(challenge.getCreatedAt());
        response.setIsActive(challenge.getIsActive());
        
        // Đếm số người tham gia
        Long participantCount = challengeParticipationRepository.countChallengeParticipationByChallengeId(challenge.getId());
        response.setParticipantCount(participantCount != null ? participantCount : 0L);
        
        // Lấy tên restaurant nếu có
        if (challenge.getRestaurantId() != null) {
            Optional<Restaurant> restaurantOpt = restaurantRepository.findById(challenge.getRestaurantId());
            if (restaurantOpt.isPresent()) {
                response.setRestaurantName(restaurantOpt.get().getName());
            }
        }
        
        // TODO: Lấy typeObjName nếu cần (cần TypeObjectsRepository)
        response.setTypeObjName(null);
        
        return response;
    }
}
