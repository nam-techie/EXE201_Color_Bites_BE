package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.CreateRestaurantRequest;
import com.exe201.color_bites_be.dto.request.UpdateRestaurantRequest;
import com.exe201.color_bites_be.dto.response.FoodTypeResponse;
import com.exe201.color_bites_be.dto.response.RestaurantResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.Restaurant;
import com.exe201.color_bites_be.exception.BadRequestException;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.RestaurantRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import com.exe201.color_bites_be.service.IFoodTypeService;
import com.exe201.color_bites_be.service.IRestaurantService;
import com.exe201.color_bites_be.repository.FavoriteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private IFoodTypeService foodTypeService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Override
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        try {
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            
            // Create restaurant entity
            Restaurant restaurant = new Restaurant();
            restaurant.setName(request.getName());
            restaurant.setAddress(request.getAddress());
            restaurant.setRegion(request.getRegion());
            restaurant.setAvgPrice(request.getAvgPrice());
            restaurant.setRating(request.getRating());
            restaurant.setFeatured(request.getFeatured() != null ? request.getFeatured() : false);
            
            // Handle coordinates
            if (request.getCoordinates() != null && request.getCoordinates().length == 2) {
                double longitude = request.getCoordinates()[0];
                double latitude = request.getCoordinates()[1];
                restaurant.setLongitude(BigDecimal.valueOf(longitude));
                restaurant.setLatitude(BigDecimal.valueOf(latitude));
                restaurant.setLocation(new GeoJsonPoint(longitude, latitude));
            }
            
            restaurant.setCreatedBy(account.getId());
            restaurant.setCreatedAt(LocalDateTime.now());
            restaurant.setUpdatedAt(LocalDateTime.now());
            restaurant.setIsDeleted(false);

            // Save restaurant first
            Restaurant savedRestaurant = restaurantRepository.save(restaurant);

            // Assign food types
            if (request.getFoodTypeIds() != null && !request.getFoodTypeIds().isEmpty()) {
                foodTypeService.assignFoodTypesToRestaurant(savedRestaurant.getId(), request.getFoodTypeIds());
            }

            return buildRestaurantResponse(savedRestaurant);

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi tạo nhà hàng: " + e.getMessage());
        }
    }

    @Override
    public RestaurantResponse readRestaurantById(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(restaurantId)
                .orElseThrow(() -> new NotFoundException("Nhà hàng không tồn tại"));

        return buildRestaurantResponse(restaurant);
    }

    @Override
    public Page<RestaurantResponse> readAllRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // TODO: Add findAllByNotDeleted method to RestaurantRepository
        Page<Restaurant> restaurants = restaurantRepository.findAll(pageable);

        return restaurants.map(restaurant -> buildRestaurantResponse(restaurant));
    }

    @Override
    public Page<RestaurantResponse> searchRestaurants(String keyword, int page, int size) {
        // PageRequest: Spring dùng index 0-based → nếu FE truyền 1 thì trừ đi 1
        int pageIndex = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(Sort.Order.desc("createdAt")));

        // Chuyển keyword -> regex an toàn và dạng 'contains'
        String regex = toContainsRegex(keyword);

        Page<Restaurant> restaurants =
                restaurantRepository.findByKeywordAndNotDeleted(regex, pageable);

        return restaurants.map(this::buildRestaurantResponse);
    }

    @Override
    public Page<RestaurantResponse> searchRestaurants(String keyword, String district, int page, int size) {
        int pageIndex = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(Sort.Order.desc("createdAt")));

        String regex = toContainsRegex(keyword);

        Page<Restaurant> restaurants;
        if (district != null && !district.trim().isEmpty()) {
            restaurants = restaurantRepository.findByKeywordAndDistrictAndNotDeleted(regex, district, pageable);
        } else {
            restaurants = restaurantRepository.findByKeywordAndNotDeleted(regex, pageable);
        }

        return restaurants.map(this::buildRestaurantResponse);
    }

    private String toContainsRegex(String keyword) {
        if (keyword == null || keyword.isBlank()) return ".*"; // match-all
        return ".*" + java.util.regex.Pattern.quote(keyword.trim()) + ".*";
    }

    @Override
    public Page<RestaurantResponse> readRestaurantsByDistrict(String district, int page, int size) {
        int pageIndex = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by("name").ascending());
        Page<Restaurant> restaurants = restaurantRepository.searchByDistrict(district, pageable);

        return restaurants.map(restaurant -> buildRestaurantResponse(restaurant));
    }

    @Override
    public List<RestaurantResponse> findNearby(double lat, double lon, double radiusKm, int limit) {
        try {
            // Validate parameters
            if (lat < -90 || lat > 90) {
                throw new BadRequestException("Vĩ độ phải trong khoảng -90 đến 90");
            }
            if (lon < -180 || lon > 180) {
                throw new BadRequestException("Kinh độ phải trong khoảng -180 đến 180");
            }

            // Clamp radiusKm and limit
            radiusKm = Math.max(0.2, Math.min(20, radiusKm));
            limit = Math.max(1, Math.min(100, limit));

            // Convert km to meters
            double radiusMeters = radiusKm * 1000;

            // Create GeoJsonPoint (longitude, latitude order for GeoJSON)
            GeoJsonPoint location = new GeoJsonPoint(lon, lat);

            // Query database
            List<Restaurant> restaurants = restaurantRepository.findNearbyByLocation(location, radiusMeters);

            // Limit results
            return restaurants.stream()
                    .limit(limit)
                    .map(this::buildRestaurantResponse)
                    .collect(Collectors.toList());
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi tìm nhà hàng gần đây: " + e.getMessage());
        }
    }

    @Override
    public List<RestaurantResponse> findInBounds(double minLat, double maxLat, double minLon, double maxLon, int limit) {
        // Validate parameters
        if (minLat < -90 || minLat > 90 || maxLat < -90 || maxLat > 90) {
            throw new BadRequestException("Vĩ độ phải trong khoảng -90 đến 90");
        }
        if (minLon < -180 || minLon > 180 || maxLon < -180 || maxLon > 180) {
            throw new BadRequestException("Kinh độ phải trong khoảng -180 đến 180");
        }

        // Normalize bounds (swap if min > max)
        if (minLat > maxLat) {
            double temp = minLat;
            minLat = maxLat;
            maxLat = temp;
        }
        if (minLon > maxLon) {
            double temp = minLon;
            minLon = maxLon;
            maxLon = temp;
        }

        // Clamp limit
        limit = Math.max(1, Math.min(100, limit));

        // Query database
        List<Restaurant> restaurants = restaurantRepository.findInBounds(minLon, minLat, maxLon, maxLat);

        // Limit results
        return restaurants.stream()
                .limit(limit)
                .map(this::buildRestaurantResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RestaurantResponse> readRestaurantsByMood(String mood, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // TODO: Add findByMoodAndNotDeleted method to RestaurantRepository
        Page<Restaurant> restaurants = restaurantRepository.findAll(pageable);

        return restaurants.map(restaurant -> buildRestaurantResponse(restaurant));
    }

    @Override
    public RestaurantResponse editRestaurant(String restaurantId, UpdateRestaurantRequest request) {
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
            // ImageUrls will be handled by RestaurantImages entity
            // if (request.getImageUrls() != null) {
            //     restaurant.setImageUrls(request.getImageUrls());
            // }

            // TODO: Add updatedAt field to Restaurant entity
            // restaurant.setUpdatedAt(LocalDateTime.now());

            Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
            return buildRestaurantResponse(updatedRestaurant);

        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi cập nhật nhà hàng: " + e.getMessage());
        }
    }

    @Override
    public void deleteRestaurant(String restaurantId) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Restaurant restaurant = restaurantRepository.findByIdAndNotDeleted(restaurantId)
                .orElseThrow(() -> new NotFoundException("Nhà hàng không tồn tại"));

        // Kiểm tra quyền sở hữu
        if (restaurant.getCreatedBy() != null && !restaurant.getCreatedBy().equals(account.getId())) {
            throw new FuncErrorException("Bạn không có quyền xóa nhà hàng này");
        }

        try {
            // Soft delete
            restaurant.setIsDeleted(true);
            restaurantRepository.save(restaurant);

        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi xóa nhà hàng: " + e.getMessage());
        }
    }

    /**
     * Xây dựng RestaurantResponse từ Restaurant entity
     */
    private RestaurantResponse buildRestaurantResponse(Restaurant restaurant) {
        RestaurantResponse response = new RestaurantResponse();
        
        // Map basic fields
        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setAddress(restaurant.getAddress());
        response.setRegion(restaurant.getRegion());
        response.setAvgPrice(restaurant.getAvgPrice() != null ? BigDecimal.valueOf(restaurant.getAvgPrice()) : null);
        response.setRating(restaurant.getRating() != null ? BigDecimal.valueOf(restaurant.getRating()) : null);
        response.setFeatured(restaurant.getFeatured());
        response.setLatitude(restaurant.getLatitude());
        response.setLongitude(restaurant.getLongitude());
        
        // Map metadata fields
        response.setCreatedById(restaurant.getCreatedBy());
        response.setCreatedBy(restaurant.getCreatedBy());
        response.setCreatedAt(restaurant.getCreatedAt());
        response.setUpdatedAt(restaurant.getUpdatedAt());
        response.setIsDeleted(restaurant.getIsDeleted());
        
        // Get food types for this restaurant
        try {
            List<FoodTypeResponse> foodTypes = foodTypeService.getFoodTypesByRestaurant(restaurant.getId());
            response.setFoodTypes(foodTypes);
        } catch (Exception e) {
            // If there's an error getting food types, set empty list
            response.setFoodTypes(List.of());
        }
        
        // TODO: Add favorite status and count if needed
        // TODO: Add distance calculation if needed

        return response;
    }
}
