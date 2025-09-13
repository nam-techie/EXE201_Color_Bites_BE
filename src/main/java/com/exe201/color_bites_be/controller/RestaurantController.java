package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreateRestaurantRequest;
import com.exe201.color_bites_be.dto.request.UpdateRestaurantRequest;
import com.exe201.color_bites_be.dto.response.RestaurantResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.model.UserPrincipal;
import com.exe201.color_bites_be.service.RestaurantService;
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

@RestController
@PreAuthorize("hasAuthority('USER')")
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    /**
     * Tạo nhà hàng mới
     */
    @PostMapping("/create")
    public ResponseDto<RestaurantResponse> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request,
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

            RestaurantResponse response = restaurantService.createRestaurant(accountId, request);
            return new ResponseDto<>(HttpStatus.CREATED.value(), "Nhà hàng đã được tạo thành công", response);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tạo nhà hàng: " + e.getMessage(), null);
        }
    }

    /**
     * Lấy thông tin nhà hàng theo ID
     */
    @GetMapping("/read/{restaurantId}")
    public ResponseDto<RestaurantResponse> readRestaurantById(
            @PathVariable String restaurantId,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            RestaurantResponse response = restaurantService.readRestaurantById(restaurantId, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Thông tin nhà hàng đã được tải thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy thông tin nhà hàng", null);
        }
    }

    /**
     * Lấy danh sách tất cả nhà hàng (có phân trang)
     */
    @GetMapping("/list")
    public ResponseDto<Page<RestaurantResponse>> readAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            Page<RestaurantResponse> restaurants = restaurantService.readAllRestaurants(page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách nhà hàng đã được tải thành công", restaurants);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách nhà hàng", null);
        }
    }

    /**
     * Tìm kiếm nhà hàng
     */
    @GetMapping("/search")
    public ResponseDto<Page<RestaurantResponse>> searchRestaurants(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            Page<RestaurantResponse> restaurants = restaurantService.searchRestaurants(keyword, page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Kết quả tìm kiếm nhà hàng đã được tải thành công", restaurants);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tìm kiếm nhà hàng", null);
        }
    }

    /**
     * Lấy nhà hàng theo khu vực
     */
    @GetMapping("/read/region/{region}")
    public ResponseDto<Page<RestaurantResponse>> readRestaurantsByRegion(
            @PathVariable String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            Page<RestaurantResponse> restaurants = restaurantService.readRestaurantsByRegion(region, page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Nhà hàng theo khu vực đã được tải thành công", restaurants);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy nhà hàng theo khu vực", null);
        }
    }

    /**
     * Lấy nhà hàng theo mood
     */
    @GetMapping("/read/mood/{mood}")
    public ResponseDto<Page<RestaurantResponse>> readRestaurantsByMood(
            @PathVariable String mood,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            String currentAccountId = null;
            if (authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                currentAccountId = userPrincipal.getAccount().getId();
            }

            Page<RestaurantResponse> restaurants = restaurantService.readRestaurantsByMood(mood, page, size, currentAccountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Nhà hàng theo mood đã được tải thành công", restaurants);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy nhà hàng theo mood", null);
        }
    }

    /**
     * Cập nhật nhà hàng
     */
    @PutMapping("/edit/{restaurantId}")
    public ResponseDto<RestaurantResponse> editRestaurant(
            @PathVariable String restaurantId,
            @Valid @RequestBody UpdateRestaurantRequest request,
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

            RestaurantResponse response = restaurantService.editRestaurant(restaurantId, accountId, request);
            return new ResponseDto<>(HttpStatus.OK.value(), "Nhà hàng đã được cập nhật thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (FuncErrorException e) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi cập nhật nhà hàng: " + e.getMessage(), null);
        }
    }

    /**
     * Xóa nhà hàng
     */
    @DeleteMapping("/delete/{restaurantId}")
    public ResponseDto<Void> deleteRestaurant(
            @PathVariable String restaurantId,
            Authentication authentication) {

        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accountId = userPrincipal.getAccount().getId();

            restaurantService.deleteRestaurant(restaurantId, accountId);
            return new ResponseDto<>(HttpStatus.OK.value(), "Nhà hàng đã được xóa thành công", null);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (FuncErrorException e) {
            return new ResponseDto<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi xóa nhà hàng: " + e.getMessage(), null);
        }
    }
}
