# 🍽️ Color Bites Backend API

Backend API cho ứng dụng Color Bites - Nền tảng khám phá nhà hàng và chia sẻ trải nghiệm ẩm thực.

## 🚀 Tech Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: MongoDB Atlas
- **Authentication**: JWT
- **File Storage**: Cloudinary
- **Payment**: PayOS
- **API Documentation**: Swagger/OpenAPI

## 📋 Features

### Core Features
- ✅ User Authentication & Authorization (JWT)
- ✅ Restaurant Management (CRUD + Geospatial Search)
- ✅ Post & Comment System (Social Feed)
- ✅ Mood Tracking & Quiz System
- ✅ Favorite Restaurants
- ✅ Payment Integration (PayOS)
- ✅ File Upload (Cloudinary)

### Advanced Features
- ✅ Geospatial Search (Nearby, In-Bounds)
- ✅ Rate Limiting (100 req/min per IP)
- ✅ Reverse Geocoding (Nominatim)
- ✅ Soft Delete Pattern
- ✅ Pagination & Sorting
- ✅ CORS Configuration

## 🛠️ Setup & Installation

### Prerequisites
- Java 17+
- Maven 3.8+
- MongoDB Atlas account
- Cloudinary account (optional)
- PayOS account (optional)

### Environment Variables

Tạo file `.env` trong root project với các biến sau:

```bash
# Database
MONGODB_URI=your_mongodb_connection_string
MONGODB_DATABASE=color_bites_db

# JWT Secret
SECRET_KEY=your_jwt_secret_key_base64

# Cloudinary (Optional)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# PayOS (Optional)
PAYOS_CLIENT_ID=your_client_id
PAYOS_API_KEY=your_api_key
PAYOS_CHECKSUM_KEY=your_checksum_key
```

> **Lưu ý:** File `.env` đã được gitignore. Tham khảo `ENV_TEMPLATE.txt` để biết chi tiết.

### Run Locally

```bash
# Clone repository
git clone <repository-url>
cd EXE201_Color_Bites_BE

# Install dependencies
mvn clean install

# Run application
mvn spring-boot:run
```

Application sẽ chạy tại: `http://localhost:8080`

## 📖 API Documentation

Sau khi start ứng dụng, truy cập Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

Hoặc xem OpenAPI spec:

```
http://localhost:8080/v3/api-docs
```

## 🌐 Deployment

### Railway Deployment

1. **Setup Environment Variables** trên Railway Dashboard:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `MONGODB_URI=<your_mongodb_uri>`
   - `SECRET_KEY=<your_jwt_secret>`
   - `PAYOS_CLIENT_ID`, `PAYOS_API_KEY`, `PAYOS_CHECKSUM_KEY`
   - `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`

2. **Push to Git**:
   ```bash
   git add .
   git commit -m "Deploy to Railway"
   git push origin main
   ```

3. **Railway** sẽ tự động build và deploy.

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/exe201/color_bites_be/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST Controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # MongoDB Entities
│   │   ├── exception/       # Custom Exceptions
│   │   ├── repository/      # MongoDB Repositories
│   │   ├── service/         # Business Logic
│   │   └── util/            # Utility Classes
│   └── resources/
│       ├── application.yml       # Default config
│       └── application-prod.yml  # Production config
└── test/                    # Unit & Integration Tests
```

## 🔒 Security

- ✅ JWT Authentication cho protected endpoints
- ✅ Password encryption với BCrypt
- ✅ CORS configuration
- ✅ Rate limiting cho public endpoints
- ✅ Input validation
- ✅ Environment variables cho sensitive data

## 📚 Documentation

Xem thêm tài liệu chi tiết trong folder `docs/`:

- [API Development Notes](docs/API_DEVELOPMENT_NOTES.md)
- [PayOS Integration Guide](docs/PAYOS_INTEGRATION_GUIDE.md)
- [Implementation Summary](docs/IMPLEMENTATION_SUMMARY.md)

## 🤝 Contributing

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## 📞 Support

Nếu gặp vấn đề, vui lòng tạo issue trên GitHub hoặc liên hệ team.

## 📄 License

This project is private and proprietary.

---

**Made with ❤️ by Color Bites Team**
