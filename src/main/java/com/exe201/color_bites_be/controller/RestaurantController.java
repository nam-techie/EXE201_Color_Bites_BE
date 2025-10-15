package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.request.CreateRestaurantRequest;
import com.exe201.color_bites_be.dto.request.UpdateRestaurantRequest;
import com.exe201.color_bites_be.dto.response.RestaurantResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.model.UserPrincipal;
import com.exe201.color_bites_be.service.IGeocodingService;
import com.exe201.color_bites_be.service.IRestaurantService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private IRestaurantService restaurantService;

    @Autowired
    private IGeocodingService geocodingService;

    /**
     * Tìm nhà hàng gần vị trí (PUBLIC - không cần auth)
     */
    @GetMapping("/nearby")
    public ResponseDto<List<RestaurantResponse>> getNearbyRestaurants(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5") double radiusKm,
            @RequestParam(defaultValue = "50") int limit,
            HttpServletResponse response) {

        try {
            List<RestaurantResponse> restaurants = restaurantService.findNearby(lat, lon, radiusKm, limit);

            // Set custom headers
            response.setHeader("X-Mode", "nearby");
            response.setHeader("X-Center", String.format("{\"lat\":%.6f,\"lon\":%.6f}", lat, lon));
            response.setHeader("X-RadiusKm", String.valueOf(radiusKm));
            response.setHeader("X-Limit", String.valueOf(limit));
            response.setHeader("X-Count", String.valueOf(restaurants.size()));
            response.setHeader("X-Has-More", String.valueOf(restaurants.size() >= limit));

            return new ResponseDto<>(HttpStatus.OK.value(), "Nearby restaurants loaded", restaurants);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tìm nhà hàng gần đây: " + e.getMessage(), null);
        }
    }

    /**
     * Tìm nhà hàng trong viewport/bounding box (PUBLIC - không cần auth)
     */
    @GetMapping("/in-bounds")
    public ResponseDto<List<RestaurantResponse>> getRestaurantsInBounds(
            @RequestParam double minLat,
            @RequestParam double maxLat,
            @RequestParam double minLon,
            @RequestParam double maxLon,
            @RequestParam(defaultValue = "100") int limit,
            HttpServletResponse response) {

        try {
            List<RestaurantResponse> restaurants = restaurantService.findInBounds(minLat, maxLat, minLon, maxLon, limit);

            // Set custom headers
            response.setHeader("X-Mode", "in-bounds");
            response.setHeader("X-BBox", String.format("{\"minLat\":%.6f,\"maxLat\":%.6f,\"minLon\":%.6f,\"maxLon\":%.6f}",
                    minLat, maxLat, minLon, maxLon));
            response.setHeader("X-Limit", String.valueOf(limit));
            response.setHeader("X-Count", String.valueOf(restaurants.size()));
            response.setHeader("X-Has-More", String.valueOf(restaurants.size() >= limit));

            return new ResponseDto<>(HttpStatus.OK.value(), "Restaurants in bounds loaded", restaurants);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tìm nhà hàng trong khu vực: " + e.getMessage(), null);
        }
    }

    /**
     * Reverse geocode tọa độ thành địa chỉ (PUBLIC - không cần auth)
     */
    @GetMapping("/reverse-geocode")
    public ResponseDto<Map<String, Object>> reverseGeocode(
            @RequestParam double lat,
            @RequestParam double lon) {

        try {
            Map<String, Object> result = geocodingService.reverseGeocode(lat, lon);
            return new ResponseDto<>(HttpStatus.OK.value(), "Reverse geocode successful", result);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi reverse geocode: " + e.getMessage(), null);
        }
    }

    /**
     * Tạo nhà hàng mới (CẦN AUTH)
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseDto<RestaurantResponse> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request) {
        try {
            RestaurantResponse response = restaurantService.createRestaurant( request);
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
            @PathVariable String restaurantId) {

        try {
            RestaurantResponse response = restaurantService.readRestaurantById(restaurantId);
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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            

            Page<RestaurantResponse> restaurants = restaurantService.readAllRestaurants(page, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Danh sách nhà hàng đã được tải thành công", restaurants);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy danh sách nhà hàng", null);
        }
    }

    /**
     * Tìm kiếm nhà hàng (PUBLIC - không cần auth)
     */
    @GetMapping("/search")
    public ResponseDto<Page<RestaurantResponse>> searchRestaurants(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) String district,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletResponse response) {

        try {
            Page<RestaurantResponse> restaurants;
            if (district != null && !district.trim().isEmpty()) {
                restaurants = restaurantService.searchRestaurants(keyword, district, page, size);
            } else {
                restaurants = restaurantService.searchRestaurants(keyword, page, size);
            }

            // Set custom header
            response.setHeader("X-Mode", "search");

            return new ResponseDto<>(HttpStatus.OK.value(), "Kết quả tìm kiếm nhà hàng đã được tải thành công", restaurants);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi tìm kiếm nhà hàng", null);
        }
    }

    /**
     * Lấy nhà hàng theo khu vực (PUBLIC - không cần auth)
     * Keeping old path for backward compatibility
     */
    @GetMapping("/read/by-district/{district}")
    public ResponseDto<Page<RestaurantResponse>> readRestaurantsByDistrictPath(
            @PathVariable String district,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletResponse response) {

        try {
            Page<RestaurantResponse> restaurants = restaurantService.readRestaurantsByDistrict(district, page, size);

            // Set custom headers
            response.setHeader("X-Mode", "by-district");
            response.setHeader("X-District", district);

            return new ResponseDto<>(HttpStatus.OK.value(), "Nhà hàng theo khu vực đã được tải thành công", restaurants);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy nhà hàng theo khu vực", null);
        }
    }

    /**
     * Lấy nhà hàng theo khu vực - Query param version (PUBLIC - không cần auth)
     */
    @GetMapping("/by-district")
    public ResponseDto<Page<RestaurantResponse>> readRestaurantsByDistrict(
            @RequestParam String district,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletResponse response) {

        try {
            Page<RestaurantResponse> restaurants = restaurantService.readRestaurantsByDistrict(district, page, size);

            // Set custom headers
            response.setHeader("X-Mode", "by-district");
            response.setHeader("X-District", district);

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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            

            Page<RestaurantResponse> restaurants = restaurantService.readRestaurantsByMood(mood, page, size);
            return new ResponseDto<>(HttpStatus.OK.value(), "Nhà hàng theo mood đã được tải thành công", restaurants);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy nhà hàng theo mood", null);
        }
    }

    /**
     * Cập nhật nhà hàng (CẦN AUTH)
     */
    @PutMapping("/edit/{restaurantId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseDto<RestaurantResponse> editRestaurant(
            @PathVariable String restaurantId,
            @Valid @RequestBody UpdateRestaurantRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append(". ");
            }
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", null);
        }

        try {

            RestaurantResponse response = restaurantService.editRestaurant(restaurantId, request);
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
     * Xóa nhà hàng (CẦN AUTH)
     */
    @DeleteMapping("/delete/{restaurantId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseDto<Void> deleteRestaurant(
            @PathVariable String restaurantId) {

        try {
            restaurantService.deleteRestaurant(restaurantId);
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
