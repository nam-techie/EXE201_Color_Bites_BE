# Fix: Restaurant Entity Field Mapping

## 🐛 Vấn Đề

API `/api/restaurants/*` trả về `null` cho tất cả các field vì:
- MongoDB có field names **khác** với Entity mapping
- Dữ liệu trong DB dùng: `Restaurant Name`, `Address`, `quận`, `Type`, `Price`, `lat`, `lon`
- Entity chỉ map: `name`, `address`, `district`, `type`, `price`, `latitude`, `longitude`

## ✅ Giải Pháp

### 1. Cập Nhật Entity - Hỗ Trợ Nhiều Field Names

**File**: `src/main/java/com/exe201/color_bites_be/entity/Restaurant.java`

Thêm mapping cho **tất cả variants** của field names:

```java
@Document(collection = "restaurants")
public class Restaurant {
    // Hỗ trợ cả PascalCase và camelCase
    @Field("Restaurant Name")
    private String restaurantName;
    
    @Field("name")
    private String name;
    
    @Field("Address")
    private String addressCapital;
    
    @Field("address")
    private String address;
    
    @Field("quận")
    private String quan;  // Tiếng Việt
    
    @Field("district")
    private String district;
    
    @Field("Type")
    private String typeCapital;
    
    @Field("type")
    private String type;
    
    @Field("Price")
    private String priceCapital;
    
    @Field("price")
    private String price;
    
    @Field("lat")
    private Double lat;
    
    @Field("latitude")
    private Double latitude;
    
    @Field("lon")
    private Double lon;
    
    @Field("longitude")
    private Double longitude;
    
    // Helper methods để lấy giá trị ưu tiên
    public String getDisplayName() {
        return restaurantName != null ? restaurantName : name;
    }
    
    public String getDisplayAddress() {
        return addressCapital != null ? addressCapital : address;
    }
    
    public String getDisplayDistrict() {
        return quan != null ? quan : district;
    }
    
    public String getDisplayType() {
        return typeCapital != null ? typeCapital : type;
    }
    
    public String getDisplayPrice() {
        return priceCapital != null ? priceCapital : price;
    }
    
    public Double getDisplayLatitude() {
        return lat != null ? lat : latitude;
    }
    
    public Double getDisplayLongitude() {
        return lon != null ? lon : longitude;
    }
}
```

### 2. Cập Nhật Response DTO

**File**: `src/main/java/com/exe201/color_bites_be/dto/response/RestaurantResponse.java`

Thêm các field mở rộng:

```java
public class RestaurantResponse {
    private String id;
    private String name;
    private String address;
    private String district;
    private String type;
    private String price;
    private Double latitude;
    private Double longitude;
    
    private String createdById;
    private String createdBy;
    private LocalDateTime createdAt;
    private Boolean isDeleted;
    
    // Thêm các field mở rộng
    private Boolean isFavorited;      // user đã yêu thích chưa
    private Long favoriteCount;       // số người yêu thích
    private Double distance;          // khoảng cách (meters)
}
```

### 3. Cập Nhật Service Mapper

**File**: `src/main/java/com/exe201/color_bites_be/service/impl/RestaurantServiceImpl.java`

Sử dụng helper methods thay vì ModelMapper:

```java
private RestaurantResponse buildRestaurantResponse(Restaurant restaurant) {
    RestaurantResponse response = new RestaurantResponse();
    
    // Sử dụng helper methods để ưu tiên field đúng
    response.setId(restaurant.getId());
    response.setName(restaurant.getDisplayName());
    response.setAddress(restaurant.getDisplayAddress());
    response.setDistrict(restaurant.getDisplayDistrict());
    response.setType(restaurant.getDisplayType());
    response.setPrice(restaurant.getDisplayPrice());
    response.setLatitude(restaurant.getDisplayLatitude());
    response.setLongitude(restaurant.getDisplayLongitude());
    
    // Các field khác
    response.setCreatedById(restaurant.getCreatedBy());
    response.setCreatedBy(restaurant.getCreatedBy());
    response.setCreatedAt(restaurant.getCreatedAt());
    response.setIsDeleted(restaurant.getIsDeleted());
    
    return response;
}
```

## 📊 Field Mapping Table

| MongoDB Field | Entity Field (Primary) | Entity Field (Fallback) | Helper Method |
|--------------|----------------------|------------------------|---------------|
| `Restaurant Name` | `restaurantName` | `name` | `getDisplayName()` |
| `Address` | `addressCapital` | `address` | `getDisplayAddress()` |
| `quận` | `quan` | `district` | `getDisplayDistrict()` |
| `Type` | `typeCapital` | `type` | `getDisplayType()` |
| `Price` | `priceCapital` | `price` | `getDisplayPrice()` |
| `lat` | `lat` | `latitude` | `getDisplayLatitude()` |
| `lon` | `lon` | `longitude` | `getDisplayLongitude()` |
| `location` | `location` | - | Direct access |

## 🧪 Testing

### Test API Response

```bash
# 1. Test /api/restaurants/nearby
curl "http://localhost:8080/api/restaurants/nearby?lat=10.7837&lon=106.6609&radiusKm=5"

# Expected Response (trước đây toàn null):
{
  "status": 200,
  "message": "Nearby restaurants loaded",
  "data": [
    {
      "id": "68e8f6603403e5bd74f39019",
      "name": "Bún Bò Đặt Thánh - Shop Online",
      "address": "221/16 Đặt Thánh, P. 6, Tân Bình, TP. HCM",
      "district": "Tân Bình",
      "type": "Bún",
      "price": "15.000 - 50.000",
      "latitude": 10.78375581,
      "longitude": 106.6609548,
      ...
    }
  ]
}
```

### Verify MongoDB Data

```javascript
use color_bites_db;

// Kiểm tra field names trong DB
db.restaurants.findOne({}, {
    "Restaurant Name": 1,
    "Address": 1,
    "quận": 1,
    "Type": 1,
    "Price": 1,
    "lat": 1,
    "lon": 1,
    "location": 1
});
```

## 🎯 Kết Quả

✅ **Trước**: API trả về toàn `null`
```json
{
  "id": "68e8f6603403e5bd74f39019",
  "name": null,
  "address": null,
  "district": null,
  "type": null,
  "price": null,
  "latitude": null,
  "longitude": null
}
```

✅ **Sau**: API trả về đầy đủ data
```json
{
  "id": "68e8f6603403e5bd74f39019",
  "name": "Bún Bò Đặt Thánh - Shop Online",
  "address": "221/16 Đặt Thánh, P. 6, Tân Bình, TP. HCM",
  "district": "Tân Bình",
  "type": "Bún",
  "price": "15.000 - 50.000",
  "latitude": 10.78375581,
  "longitude": 106.6609548
}
```

## 📝 Lưu Ý

1. **Backward Compatibility**: Entity vẫn hỗ trợ cả field names cũ (`name`, `address`, etc.) và mới (`Restaurant Name`, `Address`, etc.)

2. **Helper Methods**: Luôn dùng helper methods (`getDisplayName()`, etc.) để đảm bảo lấy đúng data

3. **Future-Proof**: Nếu cần standardize field names trong DB, chỉ cần:
   - Migrate data sang lowercase/camelCase
   - Remove các field mapping không cần thiết
   - Giữ nguyên helper methods

4. **Geospatial Index**: Index `location_2dsphere` đã có sẵn và hoạt động bình thường

## 🔗 Related Issues Fixed

- ✅ Geospatial query error (NoQueryExecutionPlans) - Fixed với index creation
- ✅ Circular dependency - Fixed với `@Lazy` injection
- ✅ Field mapping mismatch - Fixed với multiple `@Field` annotations
- ✅ Null response data - Fixed với helper methods

## 🚀 Next Steps

1. Test tất cả endpoints trong RestaurantController
2. Verify geospatial queries hoạt động đúng
3. Có thể thêm distance calculation vào response
4. Có thể thêm favorite status cho user đã login

