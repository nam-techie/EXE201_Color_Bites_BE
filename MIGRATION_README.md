# Database Migration Guide

## Thêm Location Field với GeoJSON Point Format

### Mục đích
Migration này thêm field `location` với định dạng GeoJSON Point vào collection `restaurants` để hỗ trợ các geospatial queries hiệu quả hơn.

### Yêu cầu
- MongoDB 4.0 trở lên
- mongosh (MongoDB Shell) đã được cài đặt
- Connection string đến database `color_bites_db`

### Các bước thực hiện

#### 1. Backup Database (Khuyến nghị)
```bash
mongodump --uri="<your_connection_string>" --out=backup_before_migration
```

#### 2. Chạy Migration Script

**Option A: Chạy trực tiếp với mongosh**
```bash
mongosh "<your_connection_string>" migration_add_location_field.js
```

**Option B: Chạy từ file trong mongosh**
```bash
mongosh "<your_connection_string>"
# Trong mongosh shell:
load('migration_add_location_field.js')
```

**Connection String Example:**
```
mongodb+srv://username:password@cluster0.xssff0p.mongodb.net/color_bites_db
```

#### 3. Verify Migration

Script sẽ tự động hiển thị kết quả migration. Kiểm tra:
- ✅ Số documents đã được update
- ✅ Index `2dsphere` đã được tạo trên field `location`
- ✅ Sample documents với field `location` mới

### Migration Thực Hiện Gì?

1. **Update Documents**: Thêm field `location` cho tất cả restaurants có `latitude` và `longitude`:
   ```json
   {
     "location": {
       "type": "Point",
       "coordinates": [longitude, latitude]
     }
   }
   ```

2. **Create Index**: Tạo 2dsphere index trên field `location`:
   ```javascript
   db.restaurants.createIndex({ location: "2dsphere" })
   ```

### Lưu ý quan trọng

⚠️ **Thứ tự coordinates**: GeoJSON sử dụng `[longitude, latitude]` (KHÔNG phải `[latitude, longitude]`)

⚠️ **Idempotent**: Script có thể chạy nhiều lần an toàn. Nó chỉ update documents chưa có field `location`.

⚠️ **Index**: Nếu index đã tồn tại, script sẽ bỏ qua việc tạo index mới.

### Rollback

Nếu cần rollback migration:

```javascript
use color_bites_db;

// Remove location field
db.restaurants.updateMany(
  { location: { $exists: true } },
  { $unset: { location: "" } }
);

// Drop index
db.restaurants.dropIndex("location_2dsphere");
```

### Kiểm tra sau Migration

Test geospatial query để đảm bảo hoạt động đúng:

```javascript
// Test $near query
db.restaurants.find({
  location: {
    $near: {
      $geometry: {
        type: "Point",
        coordinates: [106.6297, 10.8231] // Sài Gòn
      },
      $maxDistance: 5000 // 5km
    }
  },
  is_deleted: { $ne: true }
}).limit(10);
```

### Support

Nếu gặp vấn đề:
1. Kiểm tra connection string
2. Kiểm tra quyền user (cần quyền `readWrite` và `dbAdmin`)
3. Kiểm tra log của MongoDB
4. Restore từ backup nếu cần

### Thời gian ước tính

- **< 1,000 documents**: < 1 giây
- **1,000 - 10,000 documents**: 1-5 giây
- **> 10,000 documents**: 5-30 giây

Migration chạy as single transaction nên an toàn với concurrent operations.

