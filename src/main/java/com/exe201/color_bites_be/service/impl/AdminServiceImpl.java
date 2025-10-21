package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.response.ListAccountResponse;
import com.exe201.color_bites_be.dto.response.AdminPostResponse;
import com.exe201.color_bites_be.dto.response.AdminRestaurantResponse;
import com.exe201.color_bites_be.dto.response.AdminTransactionResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.entity.Post;
import com.exe201.color_bites_be.entity.Restaurant;
import com.exe201.color_bites_be.entity.Transaction;
import com.exe201.color_bites_be.entity.Mood;
import com.exe201.color_bites_be.enums.TransactionEnums;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import com.exe201.color_bites_be.repository.PostRepository;
import com.exe201.color_bites_be.repository.RestaurantRepository;
import com.exe201.color_bites_be.repository.TransactionRepository;
import com.exe201.color_bites_be.repository.MoodRepository;
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
        response.setLongitude(restaurant.getLongitude());
        response.setLatitude(restaurant.getLatitude());
        response.setType(restaurant.getType());
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
}
