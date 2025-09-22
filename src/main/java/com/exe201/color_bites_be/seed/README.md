# Database Seeders

Thư mục này chứa tất cả các data seeder cho ứng dụng Color Bites.

## Cấu trúc

```
📂 seed/
  ├── DataSeeder.java          # Interface chung cho tất cả seeder
  ├── DatabaseSeeder.java      # Seeder chính - quản lý tất cả seeder
  ├── MoodDataSeeder.java      # Seed dữ liệu mood mặc định
  └── README.md               # Tài liệu này
```

## Cách hoạt động

1. **DataSeeder Interface**: Định nghĩa contract chung cho tất cả seeder
2. **DatabaseSeeder**: Tự động phát hiện và chạy tất cả seeder khi Spring Boot khởi động
3. **Các Seeder cụ thể**: Implement DataSeeder và được tự động phát hiện

## Tạo Seeder mới

1. Tạo class implement `DataSeeder`
2. Thêm annotation `@Component`
3. Implement 3 method:
   - `seed()`: Logic seed data
   - `shouldSeed()`: Kiểm tra có cần seed không
   - `getSeederName()`: Tên seeder để log

### Ví dụ:

```java
@Component
public class UserDataSeeder implements DataSeeder {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void seed() {
        // Logic seed users
    }
    
    @Override
    public boolean shouldSeed() {
        return userRepository.count() == 0;
    }
    
    @Override
    public String getSeederName() {
        return "UserDataSeeder";
    }
}
```

## Log Output

Khi chạy ứng dụng, bạn sẽ thấy log như sau:

```
🌱 Bắt đầu seed database...
📝 Đang chạy MoodDataSeeder...
✅ MoodDataSeeder: Đã seed 12 mood mặc định thành công!
🎉 Hoàn thành seed database!
```

## Lưu ý

- Seeder chỉ chạy khi database trống (theo logic `shouldSeed()`)
- Seeder chạy theo thứ tự Spring tự động phát hiện
- Nếu có lỗi, seeder sẽ log lỗi nhưng không crash ứng dụng
