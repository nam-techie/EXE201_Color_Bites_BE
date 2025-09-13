package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.AddFavoriteRequest;
import com.exe201.color_bites_be.dto.response.FavoriteResponse;
import com.exe201.color_bites_be.dto.response.RestaurantResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.Favorite;
import com.exe201.color_bites_be.entity.Restaurant;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.exception.DuplicateEntity;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.FavoriteRepository;
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

@Service
public class FavoriteService {

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

    /**
     * Thêm nhà hàng vào danh sách yêu thích
     */
    public FavoriteResponse addFavorite(String accountId, AddFavoriteRequest request) {
        try {
            // Kiểm tra account tồn tại
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));

            // Kiểm tra restaurant tồn tại
            Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(request.getRestaurantId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà hàng"));

            // Kiểm tra đã favorite chưa
            if (favoriteRepository.existsByAccountIdAndRestaurantId(accountId, request.getRestaurantId())) {
                throw new DuplicateEntity("Nhà hàng đã có trong danh sách yêu thích");
            }

            // Tạo favorite
            Favorite favorite = new Favorite();
            favorite.setAccountId(accountId);
            favorite.setRestaurantId(request.getRestaurantId());
            favorite.setCreatedAt(LocalDateTime.now());

            Favorite savedFavorite = favoriteRepository.save(favorite);
            return convertToResponse(savedFavorite);

        } catch (Exception e) {
            if (e instanceof NotFoundException || e instanceof DuplicateEntity) {
                throw e;
            }
            throw new FuncErrorException("Lỗi khi thêm vào danh sách yêu thích: " + e.getMessage());
        }
    }

    /**
     * Xóa nhà hàng khỏi danh sách yêu thích
     */
    public void removeFavorite(String accountId, String restaurantId) {
        // Kiểm tra favorite tồn tại
        Favorite favorite = favoriteRepository.findByAccountIdAndRestaurantId(accountId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà hàng trong danh sách yêu thích"));

        favoriteRepository.delete(favorite);
    }

    /**
     * Lấy danh sách yêu thích của user
     */
    public Page<FavoriteResponse> readUserFavorites(String accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Favorite> favorites = favoriteRepository.findByAccountId(accountId, pageable);

        return favorites.map(this::convertToResponse);
    }

    /**
     * Kiểm tra user đã favorite restaurant chưa
     */
    public boolean isFavorited(String accountId, String restaurantId) {
        return favoriteRepository.existsByAccountIdAndRestaurantId(accountId, restaurantId);
    }

    /**
     * Đếm số lượng favorite của restaurant
     */
    public long countFavoritesByRestaurant(String restaurantId) {
        return favoriteRepository.countByRestaurantId(restaurantId);
    }

    /**
     * Đếm số lượng favorite của user
     */
    public long countFavoritesByUser(String accountId) {
        return favoriteRepository.countByAccountId(accountId);
    }

    /**
     * Toggle favorite (thêm nếu chưa có, xóa nếu đã có)
     */
    public boolean toggleFavorite(String accountId, String restaurantId) {
        if (favoriteRepository.existsByAccountIdAndRestaurantId(accountId, restaurantId)) {
            // Đã favorite -> xóa
            removeFavorite(accountId, restaurantId);
            return false; // Đã xóa
        } else {
            // Chưa favorite -> thêm
            AddFavoriteRequest request = new AddFavoriteRequest();
            request.setRestaurantId(restaurantId);
            addFavorite(accountId, request);
            return true; // Đã thêm
        }
    }

    /**
     * Chuyển đổi Favorite entity sang FavoriteResponse
     */
    private FavoriteResponse convertToResponse(Favorite favorite) {
        FavoriteResponse response = new FavoriteResponse();
        response.setId(favorite.getId());
        response.setCreatedAt(favorite.getCreatedAt());

        // Set account info
        if (favorite.getAccountId() != null) {
            response.setAccountId(favorite.getAccountId());

            UserInformation userInfo = userInformationRepository.findByAccountId(favorite.getAccountId());
            if (userInfo != null && userInfo.getFullName() != null) {
                response.setAccountName(userInfo.getFullName());
            } else {
                Account account = accountRepository.findById(favorite.getAccountId()).orElse(null);
                if (account != null) {
                    response.setAccountName(account.getUserName());
                }
            }
        }

        // Set restaurant info
        if (favorite.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(favorite.getRestaurantId()).orElse(null);
            if (restaurant != null) {
                RestaurantResponse restaurantResponse = modelMapper.map(restaurant, RestaurantResponse.class);

                // Set creator info
                if (restaurant.getCreatedBy() != null) {
                    restaurantResponse.setCreatedById(restaurant.getCreatedBy().getId());

                    UserInformation creatorInfo = userInformationRepository.findByAccountId(restaurant.getCreatedBy().getId());
                    if (creatorInfo != null && creatorInfo.getFullName() != null) {
                        restaurantResponse.setCreatedByName(creatorInfo.getFullName());
                    } else {
                        restaurantResponse.setCreatedByName(restaurant.getCreatedBy().getUserName());
                    }
                }

                // Set favorite info
                restaurantResponse.setIsFavorited(true);
                restaurantResponse.setFavoriteCount(countFavoritesByRestaurant(favorite.getRestaurantId()));

                response.setRestaurant(restaurantResponse);
            }
        }

        return response;
    }
}
