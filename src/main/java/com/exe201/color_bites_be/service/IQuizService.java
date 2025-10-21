package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateQuizRequest;
import com.exe201.color_bites_be.dto.response.QuizResponse;
import com.exe201.color_bites_be.dto.response.RestaurantResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Interface định nghĩa các phương thức quản lý quiz
 * Bao gồm tạo quiz, đánh giá mood và gợi ý nhà hàng
 */
public interface IQuizService {
    
    /**
     * Tạo quiz mới
     */
    QuizResponse createQuiz(String accountId, CreateQuizRequest request);
    
    /**
     * Lấy quiz theo ID
     */
    QuizResponse readQuizById(String quizId, String currentAccountId);
    
    /**
     * Lấy tất cả quiz của người dùng
     */
    Page<QuizResponse> readUserQuizzes(String accountId, int page, int size, String currentAccountId);
    
    /**
     * Gợi ý nhà hàng dựa trên kết quả quiz
     */
    List<RestaurantResponse> getRestaurantRecommendations(String quizId, String currentAccountId);
    
    /**
     * Lấy lịch sử quiz của người dùng
     */
    Page<QuizResponse> readQuizHistory(String accountId, int page, int size, String currentAccountId);
}
