package com.exe201.color_bites_be.service.impl;

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
import com.exe201.color_bites_be.service.IQuizService;
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

/**
 * Implementation của IQuizService
 * Xử lý logic quiz đánh giá mood và gợi ý nhà hàng
 */
@Service
public class QuizServiceImpl implements IQuizService {

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

    @Override
    public QuizResponse createQuiz(String accountId, CreateQuizRequest request) {
        try {
            // Kiểm tra account tồn tại
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));

            // Tạo quiz entity
            Quiz quiz = modelMapper.map(request, Quiz.class);
            quiz.setAccountId(accountId);
            quiz.setCreatedAt(LocalDateTime.now());
            // TODO: Add updatedAt field to Quiz entity
            // quiz.setUpdatedAt(LocalDateTime.now());
            
            // Tính toán mood dựa trên câu trả lời
            String calculatedMood = calculateMoodFromAnswers(request);
            // TODO: Add resultMood field to Quiz entity
            // quiz.setResultMood(calculatedMood);

            // Lưu quiz
            Quiz savedQuiz = quizRepository.save(quiz);

            return buildQuizResponse(savedQuiz, accountId);

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi tạo quiz: " + e.getMessage());
        }
    }

    @Override
    public QuizResponse readQuizById(String quizId, String currentAccountId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz không tồn tại"));

        return buildQuizResponse(quiz, currentAccountId);
    }

    @Override
    public Page<QuizResponse> readUserQuizzes(String accountId, int page, int size, String currentAccountId) {
        try {
            // Kiểm tra account tồn tại
            accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Quiz> quizzes = quizRepository.findByAccountId(accountId, pageable);

            return quizzes.map(quiz -> buildQuizResponse(quiz, currentAccountId));

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi lấy danh sách quiz: " + e.getMessage());
        }
    }

    @Override
    public List<RestaurantResponse> getRestaurantRecommendations(String quizId, String currentAccountId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz không tồn tại"));

        try {
            // TODO: Add getResultMood method to Quiz entity and repository method
            // Temporary workaround - get all restaurants
            List<Restaurant> recommendedRestaurants = restaurantRepository.findAll();
            // List<Restaurant> recommendedRestaurants = restaurantRepository
            //         .findByMoodAndNotDeletedOrderByFavoriteCountDesc(quiz.getResultMood());

            // Giới hạn số lượng gợi ý (tối đa 10)
            List<Restaurant> limitedRecommendations = recommendedRestaurants.stream()
                    .limit(10)
                    .collect(Collectors.toList());

            // Convert sang RestaurantResponse
            return limitedRecommendations.stream()
                    .map(restaurant -> buildRestaurantResponse(restaurant, currentAccountId))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi lấy gợi ý nhà hàng: " + e.getMessage());
        }
    }

    @Override
    public Page<QuizResponse> readQuizHistory(String accountId, int page, int size, String currentAccountId) {
        return readUserQuizzes(accountId, page, size, currentAccountId);
    }

    /**
     * Tính toán mood dựa trên câu trả lời của quiz
     */
    private String calculateMoodFromAnswers(CreateQuizRequest request) {
        // Logic đơn giản để tính mood
        // Có thể mở rộng thành thuật toán phức tạp hơn
        
        String mood = "neutral"; // default
        
        // Ví dụ logic tính mood dựa trên các câu trả lời
        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            // TODO: Fix this - getAnswers() returns Map, not List
            String answers = ""; // Temporary fix
            if (request.getAnswers() != null) {
                answers = request.getAnswers().toString().toLowerCase();
            }
            
            if (answers.contains("happy") || answers.contains("excited") || answers.contains("energetic")) {
                mood = "happy";
            } else if (answers.contains("sad") || answers.contains("tired") || answers.contains("stressed")) {
                mood = "calm";
            } else if (answers.contains("romantic") || answers.contains("intimate") || answers.contains("cozy")) {
                mood = "romantic";
            } else if (answers.contains("social") || answers.contains("party") || answers.contains("friends")) {
                mood = "social";
            }
        }
        
        return mood;
    }

    /**
     * Xây dựng QuizResponse từ Quiz entity
     */
    private QuizResponse buildQuizResponse(Quiz quiz, String currentAccountId) {
        QuizResponse response = modelMapper.map(quiz, QuizResponse.class);

        // TODO: Add setter methods to QuizResponse and getAccountId to Quiz
        /*
        UserInformation userInfo = userInformationRepository.findByAccountId(quiz.getAccountId());
        if (userInfo != null) {
            response.setUserName(userInfo.getFullName());
            response.setUserAvatar(userInfo.getAvatarUrl());
        }

        // Kiểm tra quyền sở hữu
        response.setIsOwner(quiz.getAccountId().equals(currentAccountId));
        */

        return response;
    }

    /**
     * Xây dựng RestaurantResponse từ Restaurant entity (simplified version)
     */
    private RestaurantResponse buildRestaurantResponse(Restaurant restaurant, String currentAccountId) {
        RestaurantResponse response = modelMapper.map(restaurant, RestaurantResponse.class);

        // TODO: Add getAccountId to Restaurant and setter methods to RestaurantResponse
        /*
        UserInformation ownerInfo = userInformationRepository.findByAccountId(restaurant.getAccountId());
        if (ownerInfo != null) {
            response.setOwnerName(ownerInfo.getFullName());
            response.setOwnerAvatar(ownerInfo.getAvatarUrl());
        }

        // Kiểm tra quyền sở hữu
        response.setIsOwner(restaurant.getAccountId().equals(currentAccountId));
        */

        return response;
    }
}
