package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.AddFavoriteRequest;
import com.exe201.color_bites_be.dto.response.FavoriteResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.Favorite;
import com.exe201.color_bites_be.entity.Restaurant;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.FavoriteRepository;
import com.exe201.color_bites_be.repository.RestaurantRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import com.exe201.color_bites_be.service.IFavoriteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation của IFavoriteService
 * Xử lý logic quản lý yêu thích nhà hàng
 */
@Service
public class FavoriteServiceImpl implements IFavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public FavoriteResponse toggleFavorite(String accountId, AddFavoriteRequest request) {
        try {
            // Kiểm tra account tồn tại
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));

            // Kiểm tra restaurant tồn tại
            Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(request.getRestaurantId())
                    .orElseThrow(() -> new NotFoundException("Nhà hàng không tồn tại"));

            // Kiểm tra đã favorite chưa
            return favoriteRepository.findByAccountIdAndRestaurantId(accountId, request.getRestaurantId())
                    .map(existingFavorite -> {
                        // Đã favorite -> Remove favorite
                        favoriteRepository.delete(existingFavorite);
                        
                        // TODO: Add favoriteCount field to Restaurant entity
                        // restaurant.setFavoriteCount(Math.max(0, restaurant.getFavoriteCount() - 1));
                        restaurantRepository.save(restaurant);
                        
                        // TODO: Add setRestaurantId, setRestaurantName, setAction, setMessage methods to FavoriteResponse
                        FavoriteResponse response = new FavoriteResponse();
                        // response.setRestaurantId(request.getRestaurantId());
                        // response.setRestaurantName(restaurant.getName());
                        // response.setAction("removed");
                        // response.setMessage("Đã bỏ yêu thích nhà hàng");
                        return response;
                    })
                    .orElseGet(() -> {
                        // Chưa favorite -> Add favorite
                        Favorite favorite = new Favorite();
                        favorite.setAccountId(accountId);
                        favorite.setRestaurantId(request.getRestaurantId());
                        favorite.setCreatedAt(LocalDateTime.now());
                        
                        Favorite savedFavorite = favoriteRepository.save(favorite);
                        
                        // TODO: Add favoriteCount field to Restaurant entity
                        // restaurant.setFavoriteCount(restaurant.getFavoriteCount() + 1);
                        restaurantRepository.save(restaurant);
                        
                        // TODO: Add setAction, setMessage methods to FavoriteResponse
                        FavoriteResponse response = buildFavoriteResponse(savedFavorite, restaurant);
                        // response.setAction("added");
                        // response.setMessage("Đã thêm vào danh sách yêu thích");
                        return response;
                    });

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi xử lý yêu thích: " + e.getMessage());
        }
    }

    @Override
    public Page<FavoriteResponse> readUserFavorites(String accountId, int page, int size) {
        try {
            // Kiểm tra account tồn tại
            accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Favorite> favorites = favoriteRepository.findByAccountId(accountId, pageable);

            return favorites.map(favorite -> {
                Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(favorite.getRestaurantId())
                        .orElse(null);
                        
                if (restaurant != null) {
                    return buildFavoriteResponse(favorite, restaurant);
                } else {
                    // TODO: Add setter methods to FavoriteResponse
                    FavoriteResponse response = new FavoriteResponse();
                    // response.setId(favorite.getId());
                    // response.setRestaurantId(favorite.getRestaurantId());
                    // response.setRestaurantName("Nhà hàng không còn tồn tại");
                    // response.setCreatedAt(favorite.getCreatedAt());
                    return response;
                }
            });

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi lấy danh sách yêu thích: " + e.getMessage());
        }
    }

    @Override
    public boolean isRestaurantFavorited(String accountId, String restaurantId) {
        return favoriteRepository.existsByAccountIdAndRestaurantId(accountId, restaurantId);
    }

    @Override
    public long countFavoritesByRestaurant(String restaurantId) {
        return favoriteRepository.countByRestaurantId(restaurantId);
    }

    /**
     * Xây dựng FavoriteResponse từ Favorite và Restaurant entity
     * TODO: Add all setter methods to FavoriteResponse and getter methods to Restaurant
     */
    private FavoriteResponse buildFavoriteResponse(Favorite favorite, Restaurant restaurant) {
        FavoriteResponse response = modelMapper.map(favorite, FavoriteResponse.class);
        
        // TODO: Add setter methods to FavoriteResponse and getter methods to Restaurant
        /*
        response.setRestaurantId(restaurant.getId());
        response.setRestaurantName(restaurant.getName());
        response.setRestaurantDescription(restaurant.getDescription());
        response.setRestaurantAddress(restaurant.getAddress());
        response.setRestaurantImageUrls(restaurant.getImageUrls());
        response.setRestaurantPriceRange(restaurant.getPriceRange());
        response.setRestaurantCuisineType(restaurant.getCuisineType());
        response.setRestaurantRegion(restaurant.getRegion());
        response.setRestaurantMood(restaurant.getMood());
        
        // Set thông tin owner của restaurant
        UserInformation ownerInfo = userInformationRepository.findByAccountId(restaurant.getAccountId());
        if (ownerInfo != null) {
            response.setRestaurantOwnerName(ownerInfo.getFullName());
            response.setRestaurantOwnerAvatar(ownerInfo.getAvatarUrl());
        }
        */
        
        return response;
    }
}
