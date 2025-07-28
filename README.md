# ColorBites Backend API

Hệ thống API backend cho ứng dụng ColorBites - một nền tảng đặt món ăn và quản lý thực phẩm được xây dựng bằng Spring Boot.

## 🌟 Tính năng chính

- **Xác thực & Phân quyền**: Hệ thống xác thực JWT với phân quyền theo vai trò người dùng
- **Quản lý người dùng**: Đăng ký, đăng nhập và quản lý thông tin cá nhân
- **Tài liệu API**: Giao diện Swagger UI tương tác để test và xem tài liệu API
- **Tích hợp cơ sở dữ liệu**: PostgreSQL với JPA/Hibernate ORM
- **Bảo mật**: Spring Security tích hợp JWT token validation
- **Xử lý ngoại lệ**: Xử lý lỗi toàn cục với thông báo lỗi tùy chỉnh
- **Validation**: Kiểm tra dữ liệu đầu vào với thông báo lỗi chi tiết
- **Blacklist Token**: Quản lý token bị vô hiệu hóa khi đăng xuất

## 🛠 Công nghệ sử dụng

- **Framework**: Spring Boot 3.5.4
- **Ngôn ngữ**: Java 21
- **Cơ sở dữ liệu**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Bảo mật**: Spring Security + JWT
- **Tài liệu API**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Thư viện bổ sung**: 
  - Lombok để giảm boilerplate code
  - ModelMapper cho object mapping
  - JJWT cho JWT token processing

## 📋 Yêu cầu hệ thống

- Java 21 trở lên
- Maven 3.6+
- PostgreSQL 12+
- IDE (khuyến nghị IntelliJ IDEA)

## ⚙️ Cài đặt & Thiết lập

### 1. Clone repository
```bash
git clone <repository-url>
cd ColorBites_be
```

### 2. Thiết lập cơ sở dữ liệu
Tạo database PostgreSQL:
```sql
CREATE DATABASE colorbites_db;
CREATE USER postgres WITH PASSWORD '12345';
GRANT ALL PRIVILEGES ON DATABASE colorbites_db TO postgres;
```

### 3. Cấu hình ứng dụng
Cập nhật file `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/colorbites_db
    username: postgres
    password: 12345
```

### 4. Build và chạy ứng dụng
```bash
# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run

# Hoặc chạy trực tiếp file JAR
java -jar target/ColorBites_be-0.0.1-SNAPSHOT.jar
```

Ứng dụng sẽ chạy tại `http://localhost:8080`

## 📚 Tài liệu API

Sau khi ứng dụng chạy, truy cập tài liệu API tương tác tại:
- **Swagger UI**: `http://localhost:8080/api`
- **API Docs JSON**: `http://localhost:8080/v3/api-docs`

## 🔐 Xác thực và Bảo mật

### JWT Authentication
API sử dụng JWT (JSON Web Token) để xác thực. Thêm token vào header Authorization:

```
Authorization: Bearer <jwt-token-của-bạn>
```

### Cấu trúc Token
- **Thời gian sống**: 60 phút
- **Secret Key**: Được mã hóa và lưu trong `TokenService`
- **Blacklist**: Token bị vô hiệu hóa sẽ được lưu trong blacklist

### Endpoints xác thực

#### Đăng ký người dùng
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "user123",
  "email": "user@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "fullName": "Nguyễn Văn A",
  "gender": "MALE",
  "dob": "1990-01-01"
}
```

#### Đăng nhập
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user123",
  "password": "password123"
}
```

#### Đăng xuất
```http
POST /api/auth/logout
Authorization: Bearer <token>
```

## 🏗 Cấu trúc dự án

```
src/
├── main/
│   ├── java/com/exe201/color_bites_be/
│   │   ├── config/              # Cấu hình ứng dụng
│   │   │   ├── JwtFilter.java   # Filter xử lý JWT
│   │   │   ├── SecurityConfig.java # Cấu hình bảo mật
│   │   │   └── SwaggerConfig.java  # Cấu hình Swagger
│   │   ├── controller/          # REST Controllers
│   │   │   └── AuthenticationController.java
│   │   ├── service/             # Business Logic
│   │   │   ├── AuthenticationService.java
│   │   │   └── TokenService.java
│   │   ├── entity/              # JPA Entities
│   │   ├── repository/          # Data Access Layer
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── exception/           # Exception Handling
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── enums/               # Enumerations
│   │   └── ColorBitesBeApplication.java
│   └── resources/
│       ├── application.yml      # Cấu hình chính
│       └── static/              # Static resources
└── test/                        # Test files
```

## 🔧 Cấu hình chi tiết

### Cấu hình Database
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: create-drop
    database-platform: org.hibernate.dialect.H2Dialect
```

### Cấu hình Logging
```yaml
logging:
  level:
    root: warn
    org:
      hibernate:
        SQL: trace
        orm:
          jdbc:
            bind: trace
```

### Endpoints công khai (không cần xác thực)
- `/swagger-ui/**` - Swagger UI
- `/v3/api-docs/**` - API Documentation
- `/api/auth/login` - Đăng nhập
- `/api/auth/register` - Đăng ký
- `/api/loginByGoogle` - Đăng nhập Google
- `/oauth2/authorization/**` - OAuth2
- `/api/vnpay-return` - VNPay callback

## 🚦 API Endpoints

### Authentication APIs
| Method | Endpoint | Mô tả | Yêu cầu Auth |
|--------|----------|-------|--------------|
| POST | `/api/auth/register` | Đăng ký tài khoản mới | ❌ |
| POST | `/api/auth/login` | Đăng nhập | ❌ |
| POST | `/api/auth/logout` | Đăng xuất | ✅ |

### Response Format
```json
{
  "status": 200,
  "message": "Thành công",
  "data": {
    // Dữ liệu response
  }
}
```

### Error Response
```json
{
  "status": 400,
  "message": "Lỗi validation",
  "data": "Chi tiết lỗi"
}
```

## 🧪 Testing

### Chạy tests
```bash
# Chạy tất cả tests
mvn test

# Chạy tests với coverage
mvn test jacoco:report
```

### Test với Swagger UI
1. Truy cập `http://localhost:8080/api`
2. Đăng ký tài khoản mới qua `/api/auth/register`
3. Đăng nhập qua `/api/auth/login` để lấy token
4. Click "Authorize" và nhập token
5. Test các API khác

## 🔍 Troubleshooting

### Lỗi thường gặp

#### 1. Lỗi kết nối database
```
Caused by: org.postgresql.util.PSQLException: Connection refused
```
**Giải pháp**: Kiểm tra PostgreSQL đã chạy và cấu hình connection string

#### 2. Lỗi JWT token
```
Token không hợp lệ
```
**Giải pháp**: Kiểm tra token còn hạn và format đúng

#### 3. Lỗi validation
```
Lỗi xác thực: Email không hợp lệ
```
**Giải pháp**: Kiểm tra format dữ liệu đầu vào

## 🚀 Deployment

### Production Configuration
Tạo file `application-prod.yml`:
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

logging:
  level:
    root: info
```

### Build for production
```bash
mvn clean package -Pprod
```

## 🤝 Đóng góp

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/tinh-nang-moi`)
3. Commit changes (`git commit -m 'Thêm tính năng mới'`)
4. Push to branch (`git push origin feature/tinh-nang-moi`)
5. Tạo Pull Request

## 📝 License

Dự án này được cấp phép theo MIT License - xem file LICENSE để biết thêm chi tiết.

## 👥 Nhóm phát triển

- **Backend Team**: Phát triển API và logic nghiệp vụ
- **Frontend Repository**: [ColorBites Frontend](https://github.com/nam-techie/EXE201_Color_Bites_FE)

## 📞 Hỗ trợ

Để được hỗ trợ và giải đáp thắc mắc:
- Tạo issue trong repository
- Liên hệ team phát triển
- Email: support@colorbites.com

## 📈 Roadmap

- [ ] Tích hợp thanh toán VNPay
- [ ] API quản lý đơn hàng
- [ ] Hệ thống notification
- [ ] API quản lý menu
- [ ] Dashboard admin
- [ ] Mobile API optimization

---

**Lưu ý quan trọng**: 
- Cập nhật thông tin database và các thông tin nhạy cảm trước khi deploy
- Thay đổi SECRET_KEY trong production
- Sử dụng HTTPS trong môi trường production

