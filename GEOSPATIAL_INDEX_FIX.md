# Fix: Geospatial Query Error - NoQueryExecutionPlans

## 🐛 Lỗi ban đầu

```json
{
  "status": 500,
  "message": "Đã xảy ra lỗi khi tìm nhà hàng gần đây: Command failed with error 291 (NoQueryExecutionPlans): 'error processing query: ns=color_bites_db.restaurantsTree: $and\n    $not\n        is_deleted $eq true\n    GEONEAR  field=location maxdist=5000 isNearSphere=0\nSort: {}\nProj: {}\n planner returned error :: caused by :: unable to find index for $geoNear query'"
}
```

**Nguyên nhân**: MongoDB không thể thực thi geospatial query (`$near`) vì không có index 2dsphere cho field `location` trong collection `restaurants`.

## ✅ Giải pháp

### 1. Thêm Index Initialization vào MongoConfig

**File**: `src/main/java/com/exe201/color_bites_be/config/MongoConfig.java`

Thêm bean `CommandLineRunner` để tự động tạo geospatial index khi application khởi động.

**⚠️ Lưu ý**: Inject `MongoTemplate` vào method parameter của `CommandLineRunner` thay vì inject vào class field để tránh **circular dependency**.

```java
@Bean
public CommandLineRunner initializeGeoIndexes(MongoTemplate mongoTemplate) {
    return args -> {
        try {
            // Tạo 2dsphere index cho location field
            var indexOps = mongoTemplate.indexOps(Restaurant.class);
            indexOps.ensureIndex(new GeospatialIndex("location"));
            
            System.out.println("✓ Geospatial index (2dsphere) đã được tạo cho Restaurant.location");
        } catch (Exception e) {
            System.err.println("✗ Lỗi khi tạo geospatial index: " + e.getMessage());
            // Log nhưng không throw exception để app vẫn start được
        }
    };
}
```

**Giải thích về Circular Dependency**:
- ❌ **SAI**: `@Autowired private MongoTemplate mongoTemplate` trong class → gây circular dependency vì `MongoConfig` được Spring dùng để tạo `MongoTemplate`
- ✅ **ĐÚNG**: Inject vào method parameter `CommandLineRunner initializeGeoIndexes(MongoTemplate mongoTemplate)` → Spring sẽ inject sau khi `MongoTemplate` đã được khởi tạo xong

### 2. Cải thiện Repository Query

**File**: `src/main/java/com/exe201/color_bites_be/repository/RestaurantRepository.java`

Điều chỉnh thứ tự điều kiện trong query để tối ưu:

```java
// TRƯỚC (có thể gây lỗi)
@Query(value = "{ 'location': { $near: { $geometry: ?0, $maxDistance: ?1 } }, 'is_deleted': { $ne: true } }")

// SAU (tối ưu hơn)
@Query(value = "{ 'is_deleted': { $ne: true }, 'location': { $near: { $geometry: ?0, $maxDistance: ?1 } } }")
```

### 3. Thêm Exception Handling

**File**: `src/main/java/com/exe201/color_bites_be/service/impl/RestaurantServiceImpl.java`

Bọc logic trong try-catch để catch các lỗi MongoDB và trả về message rõ ràng:

```java
@Override
public List<RestaurantResponse> findNearby(double lat, double lon, double radiusKm, int limit) {
    try {
        // Validation logic...
        
        List<Restaurant> restaurants = restaurantRepository.findNearbyByLocation(location, radiusMeters);
        
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
```

## 🚀 Cách khắc phục

### Option 1: Restart Application (Recommended)

Restart Spring Boot application để `CommandLineRunner` tự động tạo index:

```bash
mvn spring-boot:run
```

Hoặc nếu đang chạy:

```bash
# Stop app
Ctrl + C

# Start lại
mvn spring-boot:run
```

Khi app khởi động, bạn sẽ thấy log:

```
✓ Geospatial index (2dsphere) đã được tạo cho Restaurant.location
```

### Option 2: Tạo Index thủ công trong MongoDB

Nếu không muốn restart, connect vào MongoDB và chạy:

```javascript
use color_bites_db;

db.restaurants.createIndex({ location: "2dsphere" });
```

## 🧪 Kiểm tra

### 1. Verify Index đã tồn tại

```javascript
use color_bites_db;

db.restaurants.getIndexes();

// Kết quả mong đợi sẽ có:
// {
//   "v": 2,
//   "key": { "location": "2dsphere" },
//   "name": "location_2dsphere"
// }
```

### 2. Test API endpoint

```bash
curl -X GET "http://localhost:8080/api/restaurants/nearby?lat=10.756951153746993&lon=106.65972858667376&radiusKm=5&limit=50" \
  -H "accept: */*" \
  -H "Authorization: Bearer <your_token>"
```

**Response mong đợi**:

```json
{
  "status": 200,
  "message": "Nearby restaurants loaded",
  "data": [
    {
      "id": "...",
      "name": "...",
      "address": "...",
      // ...
    }
  ]
}
```

## 📝 Lưu ý

1. **Index tự động**: Index sẽ được tạo mỗi khi app restart. Nếu index đã tồn tại, MongoDB sẽ skip việc tạo lại.

2. **Annotation không đủ**: Annotation `@GeoSpatialIndexed` trên entity chỉ là metadata, không tự động tạo index trong MongoDB. Cần phải explicitly tạo index thông qua `IndexOperations`.

3. **Query syntax**: Geospatial query `$near` yêu cầu:
   - 2dsphere index trên field được query
   - GeoJSON format: `{ type: "Point", coordinates: [longitude, latitude] }`
   - MaxDistance tính bằng meters

4. **Migration script**: Nếu muốn chạy migration riêng, file `migration_add_location_field.js` đã có sẵn để add location field và tạo index.

## 🔗 Related Files

- `src/main/java/com/exe201/color_bites_be/config/MongoConfig.java`
- `src/main/java/com/exe201/color_bites_be/entity/Restaurant.java`
- `src/main/java/com/exe201/color_bites_be/repository/RestaurantRepository.java`
- `src/main/java/com/exe201/color_bites_be/service/impl/RestaurantServiceImpl.java`
- `src/main/java/com/exe201/color_bites_be/controller/RestaurantController.java`
- `migration_add_location_field.js`
- `API_GEOSPATIAL_ENDPOINTS.md`

