package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateQuizRequest;
import com.exe201.color_bites_be.dto.response.QuizResponse;
import com.exe201.color_bites_be.dto.response.RestaurantResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.Quiz;
import com.exe201.color_bites_be.entity.Restaurant;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.QuizRepository;
import com.exe201.color_bites_be.repository.RestaurantRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Tạo quiz mới
     */
    public QuizResponse createQuiz(String accountId, CreateQuizRequest request) {
        try {
            // Kiểm tra account tồn tại
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));

            // Tạo quiz entity
            Quiz quiz = modelMapper.map(request, Quiz.class);
            quiz.setAccountId(accountId);
            quiz.setCreatedAt(LocalDateTime.now());

            // Lưu quiz
            Quiz savedQuiz = quizRepository.save(quiz);

            return convertToResponse(savedQuiz);

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw e;
            }
            throw new FuncErrorException("Lỗi khi tạo quiz: " + e.getMessage());
        }
    }

    /**
     * Lấy quiz theo ID
     */
    public QuizResponse readQuizById(String quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quiz"));

        return convertToResponse(quiz);
    }

    /**
     * Lấy danh sách quiz của user
     */
    public Page<QuizResponse> readQuizzesByUser(String accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Quiz> quizzes = quizRepository.findByAccountId(accountId, pageable);

        return quizzes.map(this::convertToResponse);
    }

    /**
     * Lấy quiz mới nhất của user
     */
    public QuizResponse readLatestQuizByUser(String accountId) {
        Quiz quiz = quizRepository.findLatestByAccountId(accountId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quiz nào của người dùng"));

        return convertToResponse(quiz);
    }

    /**
     * Lấy quiz theo mood result
     */
    public Page<QuizResponse> readQuizzesByMood(String moodResult, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Quiz> quizzes = quizRepository.findByMoodResult(moodResult, pageable);

        return quizzes.map(this::convertToResponse);
    }

    /**
     * Đếm số quiz của user
     */
    public long countQuizzesByUser(String accountId) {
        return quizRepository.countByAccountId(accountId);
    }

    /**
     * Xóa quiz
     */
    public void deleteQuiz(String quizId, String accountId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quiz"));

        // Kiểm tra quyền sở hữu
        if (!quiz.getAccountId().equals(accountId)) {
            throw new FuncErrorException("Bạn không có quyền xóa quiz này");
        }

        quizRepository.delete(quiz);
    }

    /**
     * Gợi ý nhà hàng dựa trên mood result
     */
    public List<RestaurantResponse> getRestaurantRecommendations(String moodResult, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Restaurant> restaurants = restaurantRepository.findByMoodTagsContainingAndNotDeleted(moodResult, pageable);

        return restaurants.getContent().stream()
                .map(restaurant -> {
                    RestaurantResponse response = modelMapper.map(restaurant, RestaurantResponse.class);
                    // Set basic info without favorite details for recommendation
                    response.setIsFavorited(false);
                    response.setFavoriteCount(0L);
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi Quiz entity sang QuizResponse
     */
    private QuizResponse convertToResponse(Quiz quiz) {
        QuizResponse response = modelMapper.map(quiz, QuizResponse.class);

        // Set account info
        if (quiz.getAccountId() != null) {
            UserInformation userInfo = userInformationRepository.findByAccountId(quiz.getAccountId());
            if (userInfo != null && userInfo.getFullName() != null) {
                response.setAccountName(userInfo.getFullName());
            } else {
                // Fallback to username
                Account account = accountRepository.findById(quiz.getAccountId()).orElse(null);
                if (account != null) {
                    response.setAccountName(account.getUserName());
                }
            }
        }

        // Set recommended restaurant details
        if (quiz.getRecommendedRestaurants() != null && !quiz.getRecommendedRestaurants().isEmpty()) {
            List<RestaurantResponse> restaurantDetails = quiz.getRecommendedRestaurants().stream()
                    .map(restaurantId -> {
                        try {
                            Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(restaurantId).orElse(null);
                            if (restaurant != null) {
                                RestaurantResponse restaurantResponse = modelMapper.map(restaurant, RestaurantResponse.class);
                                restaurantResponse.setIsFavorited(false);
                                restaurantResponse.setFavoriteCount(0L);
                                return restaurantResponse;
                            }
                            return null;
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(restaurant -> restaurant != null)
                    .collect(Collectors.toList());
            
            response.setRecommendedRestaurantDetails(restaurantDetails);
        }

        return response;
    }
}
