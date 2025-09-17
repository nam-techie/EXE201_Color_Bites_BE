package com.exe201.color_bites_be.service.impl;

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
import com.exe201.color_bites_be.service.IRestaurantService;
import com.exe201.color_bites_be.repository.FavoriteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * Implementation của IRestaurantService
 * Xử lý logic quản lý nhà hàng, tìm kiếm và phân trang
 */
@Service
public class RestaurantServiceImpl implements IRestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private ModelMapper modelMapper;

    // TODO: Remove circular dependency - inject FavoriteRepository directly
    // @Autowired
    // private IFavoriteService favoriteService;
    
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Override
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        try {
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // Kiểm tra account tồn tại
            // Tạo restaurant entity
            Restaurant restaurant = modelMapper.map(request, Restaurant.class);
            // TODO: Add accountId field to Restaurant entity
            // restaurant.setAccountId(accountId);
            restaurant.setCreatedAt(LocalDateTime.now());
            // TODO: Add updatedAt field to Restaurant entity
            // restaurant.setUpdatedAt(LocalDateTime.now());
            restaurant.setIsDeleted(false);
            // TODO: Add favoriteCount field to Restaurant entity
            // restaurant.setFavoriteCount(0);

            // Lưu restaurant
            Restaurant savedRestaurant = restaurantRepository.save(restaurant);

            return buildRestaurantResponse(savedRestaurant, account.getId());

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi tạo nhà hàng: " + e.getMessage());
        }
    }

    @Override
    public RestaurantResponse readRestaurantById(String restaurantId) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(restaurantId)
                .orElseThrow(() -> new NotFoundException("Nhà hàng không tồn tại"));

        return buildRestaurantResponse(restaurant, account.getId());
    }

    @Override
    public Page<RestaurantResponse> readAllRestaurants(int page, int size, String currentAccountId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // TODO: Add findAllByNotDeleted method to RestaurantRepository
        Page<Restaurant> restaurants = restaurantRepository.findAll(pageable);

        return restaurants.map(restaurant -> buildRestaurantResponse(restaurant, currentAccountId));
    }

    @Override
    public Page<RestaurantResponse> searchRestaurants(String keyword, int page, int size, String currentAccountId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Restaurant> restaurants = restaurantRepository.findByKeywordAndNotDeleted(keyword, pageable);

        return restaurants.map(restaurant -> buildRestaurantResponse(restaurant, currentAccountId));
    }

    @Override
    public Page<RestaurantResponse> readRestaurantsByRegion(String region, int page, int size, String currentAccountId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Restaurant> restaurants = restaurantRepository.findByRegionAndNotDeleted(region, pageable);

        return restaurants.map(restaurant -> buildRestaurantResponse(restaurant, currentAccountId));
    }

    @Override
    public Page<RestaurantResponse> readRestaurantsByMood(String mood, int page, int size, String currentAccountId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // TODO: Add findByMoodAndNotDeleted method to RestaurantRepository
        Page<Restaurant> restaurants = restaurantRepository.findAll(pageable);

        return restaurants.map(restaurant -> buildRestaurantResponse(restaurant, currentAccountId));
    }

    @Override
    public RestaurantResponse editRestaurant(String restaurantId, String accountId, UpdateRestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(restaurantId)
                .orElseThrow(() -> new NotFoundException("Nhà hàng không tồn tại"));

        // TODO: Add getAccountId method to Restaurant entity
        /*
        if (!restaurant.getAccountId().equals(accountId)) {
            throw new FuncErrorException("Bạn không có quyền chỉnh sửa nhà hàng này");
        }
        */

        try {
            // Cập nhật các field từ request
            if (request.getName() != null) {
                restaurant.setName(request.getName());
            }
            if (request.getDescription() != null) {
                restaurant.setDescription(request.getDescription());
            }
            if (request.getAddress() != null) {
                restaurant.setAddress(request.getAddress());
            }
            // TODO: Add these fields to UpdateRestaurantRequest and Restaurant entities
            /*
            if (request.getPhone() != null) {
                restaurant.setPhone(request.getPhone());
            }
            if (request.getMenuUrl() != null) {
                restaurant.setMenuUrl(request.getMenuUrl());
            }
            if (request.getOpeningHours() != null) {
                restaurant.setOpeningHours(request.getOpeningHours());
            }
            if (request.getPriceRange() != null) {
                restaurant.setPriceRange(request.getPriceRange());
            }
            if (request.getCuisineType() != null) {
                restaurant.setCuisineType(request.getCuisineType());
            }
            if (request.getMood() != null) {
                restaurant.setMood(request.getMood());
            }
            if (request.getLatitude() != null) {
                restaurant.setLatitude(request.getLatitude());
            }
            if (request.getLongitude() != null) {
                restaurant.setLongitude(request.getLongitude());
            }
            */
            if (request.getImageUrls() != null) {
                restaurant.setImageUrls(request.getImageUrls());
            }

            // TODO: Add updatedAt field to Restaurant entity
            // restaurant.setUpdatedAt(LocalDateTime.now());

            Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
            return buildRestaurantResponse(updatedRestaurant, accountId);

        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi cập nhật nhà hàng: " + e.getMessage());
        }
    }

    @Override
    public void deleteRestaurant(String restaurantId, String accountId) {
        Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(restaurantId)
                .orElseThrow(() -> new NotFoundException("Nhà hàng không tồn tại"));

        // TODO: Add getAccountId method to Restaurant entity
        /*
        if (!restaurant.getAccountId().equals(accountId)) {
            throw new FuncErrorException("Bạn không có quyền xóa nhà hàng này");
        }
        */

        try {
            // Soft delete
            restaurant.setIsDeleted(true);
            // TODO: Add updatedAt field to Restaurant entity
            // restaurant.setUpdatedAt(LocalDateTime.now());
            restaurantRepository.save(restaurant);

        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi xóa nhà hàng: " + e.getMessage());
        }
    }

    /**
     * Xây dựng RestaurantResponse từ Restaurant entity
     */
    private RestaurantResponse buildRestaurantResponse(Restaurant restaurant, String currentAccountId) {
        RestaurantResponse response = modelMapper.map(restaurant, RestaurantResponse.class);

        // TODO: Add getAccountId method to Restaurant entity and setOwnerName/setOwnerAvatar to RestaurantResponse
        /*
        UserInformation userInfo = userInformationRepository.findByAccountId(restaurant.getAccountId());
        if (userInfo != null) {
            response.setOwnerName(userInfo.getFullName());
            response.setOwnerAvatar(userInfo.getAvatarUrl());
        }

        // Kiểm tra quyền sở hữu
        response.setIsOwner(restaurant.getAccountId().equals(currentAccountId));
        */

        // Kiểm tra đã favorite chưa - sử dụng repository trực tiếp để tránh circular dependency
        if (currentAccountId != null) {
            boolean isFavorited = favoriteRepository.existsByAccountIdAndRestaurantId(currentAccountId, restaurant.getId());
            response.setIsFavorited(isFavorited);
        } else {
            response.setIsFavorited(false);
        }

        // Set favorite count - sử dụng repository trực tiếp
        long favoriteCount = favoriteRepository.countByRestaurantId(restaurant.getId());
        // TODO: Fix setFavoriteCount parameter type
        response.setFavoriteCount(favoriteCount);

        return response;
    }
}
