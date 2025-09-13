package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.CreateRestaurantRequest;
import com.exe201.color_bites_be.dto.request.UpdateRestaurantRequest;
import com.exe201.color_bites_be.dto.response.RestaurantResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.Restaurant;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.repository.AccountRepository;
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

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FavoriteService favoriteService;

    /**
     * Tạo nhà hàng mới
     */
    public RestaurantResponse createRestaurant(String accountId, CreateRestaurantRequest request) {
        try {
            // Kiểm tra account tồn tại
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));

            // Tạo restaurant entity
            Restaurant restaurant = modelMapper.map(request, Restaurant.class);
            restaurant.setCreatedBy(account);
            restaurant.setCreatedAt(LocalDateTime.now());

            // Lưu restaurant
            Restaurant savedRestaurant = restaurantRepository.save(restaurant);

            // Chuyển đổi sang response
            return convertToResponse(savedRestaurant, null);

        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi tạo nhà hàng: " + e.getMessage());
        }
    }

    /**
     * Lấy thông tin nhà hàng theo ID
     */
    public RestaurantResponse readRestaurantById(String restaurantId, String currentAccountId) {
        Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(restaurantId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà hàng"));

        return convertToResponse(restaurant, currentAccountId);
    }

    /**
     * Lấy danh sách tất cả nhà hàng
     */
    public Page<RestaurantResponse> readAllRestaurants(int page, int size, String currentAccountId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Restaurant> restaurants = restaurantRepository.findAllActiveRestaurants(pageable);

        return restaurants.map(restaurant -> convertToResponse(restaurant, currentAccountId));
    }

    /**
     * Tìm kiếm nhà hàng theo từ khóa
     */
    public Page<RestaurantResponse> searchRestaurants(String keyword, int page, int size, String currentAccountId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Restaurant> restaurants = restaurantRepository.findByKeywordAndNotDeleted(keyword, pageable);

        return restaurants.map(restaurant -> convertToResponse(restaurant, currentAccountId));
    }

    /**
     * Lấy nhà hàng theo khu vực
     */
    public Page<RestaurantResponse> readRestaurantsByRegion(String region, int page, int size, String currentAccountId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Restaurant> restaurants = restaurantRepository.findByRegionAndNotDeleted(region, pageable);

        return restaurants.map(restaurant -> convertToResponse(restaurant, currentAccountId));
    }

    /**
     * Lấy nhà hàng theo mood tag
     */
    public Page<RestaurantResponse> readRestaurantsByMood(String mood, int page, int size, String currentAccountId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Restaurant> restaurants = restaurantRepository.findByMoodTagsContainingAndNotDeleted(mood, pageable);

        return restaurants.map(restaurant -> convertToResponse(restaurant, currentAccountId));
    }

    /**
     * Cập nhật nhà hàng
     */
    public RestaurantResponse editRestaurant(String restaurantId, String accountId, UpdateRestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(restaurantId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà hàng"));

        // Kiểm tra quyền sở hữu hoặc admin
        if (!restaurant.getCreatedBy().getId().equals(accountId)) {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
            if (!"admin".equals(account.getRole())) {
                throw new FuncErrorException("Bạn không có quyền chỉnh sửa nhà hàng này");
            }
        }

        // Cập nhật thông tin
        updateRestaurantFromRequest(restaurant, request);

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return convertToResponse(updatedRestaurant, accountId);
    }

    /**
     * Xóa nhà hàng (soft delete)
     */
    public void deleteRestaurant(String restaurantId, String accountId) {
        Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(restaurantId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà hàng"));

        // Kiểm tra quyền sở hữu hoặc admin
        if (!restaurant.getCreatedBy().getId().equals(accountId)) {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
            if (!"admin".equals(account.getRole())) {
                throw new FuncErrorException("Bạn không có quyền xóa nhà hàng này");
            }
        }

        // Soft delete
        restaurant.setIsDeleted(true);
        restaurantRepository.save(restaurant);
    }

    /**
     * Chuyển đổi Restaurant entity sang RestaurantResponse
     */
    private RestaurantResponse convertToResponse(Restaurant restaurant, String currentAccountId) {
        RestaurantResponse response = modelMapper.map(restaurant, RestaurantResponse.class);
        
        // Set thông tin người tạo
        if (restaurant.getCreatedBy() != null) {
            response.setCreatedById(restaurant.getCreatedBy().getId());
            
            // Lấy tên người tạo từ UserInformation
            UserInformation userInfo = userInformationRepository.findByAccountId(restaurant.getCreatedBy().getId());
            if (userInfo != null && userInfo.getFullName() != null) {
                response.setCreatedByName(userInfo.getFullName());
            } else {
                response.setCreatedByName(restaurant.getCreatedBy().getUserName());
            }
        }

        // Set favorite info
        if (currentAccountId != null) {
            response.setIsFavorited(favoriteService.isFavorited(currentAccountId, restaurant.getId()));
        } else {
            response.setIsFavorited(false);
        }
        response.setFavoriteCount(favoriteService.countFavoritesByRestaurant(restaurant.getId()));

        return response;
    }

    /**
     * Cập nhật Restaurant từ UpdateRestaurantRequest
     */
    private void updateRestaurantFromRequest(Restaurant restaurant, UpdateRestaurantRequest request) {
        if (request.getName() != null) restaurant.setName(request.getName());
        if (request.getAddress() != null) restaurant.setAddress(request.getAddress());
        if (request.getCoordinates() != null) restaurant.setCoordinates(request.getCoordinates());
        if (request.getDescription() != null) restaurant.setDescription(request.getDescription());
        if (request.getType() != null) restaurant.setType(request.getType());
        if (request.getMoodTags() != null) restaurant.setMoodTags(request.getMoodTags());
        if (request.getRegion() != null) restaurant.setRegion(request.getRegion());
        if (request.getImageUrls() != null) restaurant.setImageUrls(request.getImageUrls());
        if (request.getAvgPrice() != null) restaurant.setAvgPrice(request.getAvgPrice());
        if (request.getRating() != null) restaurant.setRating(request.getRating());
        if (request.getFeatured() != null) restaurant.setFeatured(request.getFeatured());
    }
}
