package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreateQuizRequest;
import com.exe201.color_bites_be.dto.response.QuizResponse;
import com.exe201.color_bites_be.dto.response.RestaurantResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.model.UserPrincipal;
import com.exe201.color_bites_be.service.QuizService;
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

import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    /**
     * Tạo quiz mới
     */
    @PostMapping("/create")
    public ResponseDto<QuizResponse> createQuiz(
            @Valid @RequestBody CreateQuizRequest request,
            BindingResult bindingResult,
            Authentication authentication) {

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append(". ");
            }
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", null);
        }

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            QuizResponse response = quizService.createQuiz(accountId, request);
            return new ResponseDto<>(HttpStatus.CREATED.value(), "Quiz đã được tạo thành công", response);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tạo quiz: " + e.getMessage(), null);
        }
    }

    /**
     * Lấy quiz theo ID
     */
    @GetMapping("/read/{quizId}")
    public ResponseDto<QuizResponse> readQuizById(@PathVariable String quizId) {

        try {
            QuizResponse response = quizService.readQuizById(quizId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Thông tin quiz đã được tải thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy thông tin quiz", null);
        }
    }

    /**
     * Lấy danh sách quiz của user hiện tại
     */
    @GetMapping("/list")
    public ResponseDto<Page<QuizResponse>> readUserQuizzes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            Page<QuizResponse> quizzes = quizService.readQuizzesByUser(accountId, page, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách quiz đã được tải thành công", quizzes);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách quiz", null);
        }
    }

    /**
     * Lấy quiz mới nhất của user hiện tại
     */
    @GetMapping("/latest")
    public ResponseDto<QuizResponse> readLatestQuiz(Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            QuizResponse response = quizService.readLatestQuizByUser(accountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Quiz mới nhất đã được tải thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy quiz mới nhất", null);
        }
    }

    /**
     * Lấy quiz theo mood result
     */
    @GetMapping("/read/mood/{moodResult}")
    public ResponseDto<Page<QuizResponse>> readQuizzesByMood(
            @PathVariable String moodResult,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<QuizResponse> quizzes = quizService.readQuizzesByMood(moodResult, page, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Quiz theo mood đã được tải thành công", quizzes);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy quiz theo mood", null);
        }
    }

    /**
     * Đếm số quiz của user hiện tại
     */
    @GetMapping("/count")
    public ResponseDto<Map<String, Object>> countUserQuizzes(Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            long quizCount = quizService.countQuizzesByUser(accountId);

            Map<String, Object> result = Map.of("quizCount", quizCount);

            return new ResponseDto<>(HttpStatus.OK.value(), "Số lượng quiz đã được đếm", result);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi đếm số lượng quiz", null);
        }
    }

    /**
     * Xóa quiz
     */
    @DeleteMapping("/delete/{quizId}")
    public ResponseDto<Void> deleteQuiz(
            @PathVariable String quizId,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            quizService.deleteQuiz(quizId, accountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Quiz đã được xóa thành công", null);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (FuncErrorException e) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi xóa quiz: " + e.getMessage(), null);
        }
    }

    /**
     * Lấy gợi ý nhà hàng theo mood
     */
    @GetMapping("/recommendations/{moodResult}")
    public ResponseDto<List<RestaurantResponse>> getRestaurantRecommendations(
            @PathVariable String moodResult,
            @RequestParam(defaultValue = "5") int limit) {

        try {
            List<RestaurantResponse> recommendations = quizService.getRestaurantRecommendations(moodResult, limit);
            return new ResponseDto<>(HttpStatus.OK.value(), "Gợi ý nhà hàng đã được tải thành công", recommendations);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy gợi ý nhà hàng", null);
        }
    }
}
