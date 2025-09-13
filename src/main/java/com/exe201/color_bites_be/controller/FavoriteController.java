package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.AddFavoriteRequest;
import com.exe201.color_bites_be.dto.response.FavoriteResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.exception.DuplicateEntity;
import com.exe201.color_bites_be.model.UserPrincipal;
import com.exe201.color_bites_be.service.FavoriteService;
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

import java.util.Map;

@RestController
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * Thêm nhà hàng vào danh sách yêu thích
     */
    @PostMapping("/add")
    public ResponseDto<FavoriteResponse> addFavorite(
            @Valid @RequestBody AddFavoriteRequest request,
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

            FavoriteResponse response = favoriteService.addFavorite(accountId, request);
            return new ResponseDto<>(HttpStatus.CREATED.value(), "Nhà hàng đã được thêm vào danh sách yêu thích", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (DuplicateEntity e) {
            return new ResponseDto<>(HttpStatus.CONFLICT.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi thêm vào danh sách yêu thích: " + e.getMessage(), null);
        }
    }

    /**
     * Xóa nhà hàng khỏi danh sách yêu thích
     */
    @DeleteMapping("/remove/{restaurantId}")
    public ResponseDto<Void> removeFavorite(
            @PathVariable String restaurantId,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            favoriteService.removeFavorite(accountId, restaurantId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Nhà hàng đã được xóa khỏi danh sách yêu thích", null);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi xóa khỏi danh sách yêu thích: " + e.getMessage(), null);
        }
    }

    /**
     * Lấy danh sách yêu thích của user hiện tại
     */
    @GetMapping("/list")
    public ResponseDto<Page<FavoriteResponse>> readUserFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            Page<FavoriteResponse> favorites = favoriteService.readUserFavorites(accountId, page, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách yêu thích đã được tải thành công", favorites);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách yêu thích", null);
        }
    }

    /**
     * Toggle favorite (thêm nếu chưa có, xóa nếu đã có)
     */
    @PutMapping("/toggle/{restaurantId}")
    public ResponseDto<Map<String, Object>> toggleFavorite(
            @PathVariable String restaurantId,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            boolean isFavorited = favoriteService.toggleFavorite(accountId, restaurantId);
            long favoriteCount = favoriteService.countFavoritesByRestaurant(restaurantId);

            Map<String, Object> result = Map.of(
                "isFavorited", isFavorited,
                "favoriteCount", favoriteCount
            );

            String message = isFavorited ? 
                "Nhà hàng đã được thêm vào danh sách yêu thích" : 
                "Nhà hàng đã được xóa khỏi danh sách yêu thích";

            return new ResponseDto<>(HttpStatus.OK.value(), message, result);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi cập nhật danh sách yêu thích: " + e.getMessage(), null);
        }
    }

    /**
     * Kiểm tra nhà hàng đã được yêu thích chưa
     */
    @GetMapping("/check/{restaurantId}")
    public ResponseDto<Map<String, Object>> checkFavorite(
            @PathVariable String restaurantId,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            boolean isFavorited = favoriteService.isFavorited(accountId, restaurantId);
            long favoriteCount = favoriteService.countFavoritesByRestaurant(restaurantId);

            Map<String, Object> result = Map.of(
                "isFavorited", isFavorited,
                "favoriteCount", favoriteCount
            );

            return new ResponseDto<>(HttpStatus.OK.value(), "Trạng thái yêu thích đã được kiểm tra", result);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi kiểm tra trạng thái yêu thích", null);
        }
    }

    /**
     * Đếm số lượng yêu thích của user hiện tại
     */
    @GetMapping("/count")
    public ResponseDto<Map<String, Object>> countUserFavorites(Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            long favoriteCount = favoriteService.countFavoritesByUser(accountId);

            Map<String, Object> result = Map.of("favoriteCount", favoriteCount);

            return new ResponseDto<>(HttpStatus.OK.value(), "Số lượng yêu thích đã được đếm", result);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi đếm số lượng yêu thích", null);
        }
    }
}
