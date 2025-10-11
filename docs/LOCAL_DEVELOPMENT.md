# 💻 Local Development Guide

## 🎯 Tổng quan

Hướng dẫn setup môi trường local để phát triển Color Bites Backend.

---

## 📋 Yêu cầu

- **Java 21** (JDK)
- **Maven 3.8+**
- **MongoDB** (chọn 1 trong 2):
  - MongoDB Atlas (cloud) - **Khuyến nghị**
  - MongoDB local (cài trên máy)
- **IDE**: IntelliJ IDEA hoặc VS Code

---

## 🔧 Setup MongoDB

### **Option 1: MongoDB Atlas (Khuyến nghị)**

1. Đăng ký tài khoản tại https://cloud.mongodb.com
2. Tạo cluster miễn phí (M0)
3. Tạo database user với username/password
4. Whitelist IP: `0.0.0.0/0` (cho phép tất cả IP)
5. Lấy connection string:
   - Click **Connect** → **Connect your application**
   - Copy connection string:
     ```
     mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority
     ```
   - Thay `<username>` và `<password>` bằng thông tin thật
   - Thêm database name: `/color_bites_db` trước dấu `?`
   
   **Kết quả cuối:**
   ```
   mongodb+srv://youruser:yourpass@cluster0.xxxxx.mongodb.net/color_bites_db?retryWrites=true&w=majority
   ```

### **Option 2: MongoDB Local**

1. Download MongoDB Community Server: https://www.mongodb.com/try/download/community
2. Cài đặt và chạy MongoDB service
3. Connection string mặc định: `mongodb://localhost:27017/color_bites_db`

---

## ⚙️ Cấu hình Environment Variables

### **IntelliJ IDEA**

1. Mở **Run** → **Edit Configurations...**
2. Chọn `ColorBitesBeApplication`
3. Tìm mục **Environment variables**
4. Click icon **folder** hoặc **+**
5. Thêm các biến sau:

```
MONGODB_URI=mongodb+srv://youruser:yourpass@cluster0.xxxxx.mongodb.net/color_bites_db?retryWrites=true&w=majority
SECRET_KEY=your_base64_encoded_jwt_secret_key_here
PAYOS_CLIENT_ID=your_payos_client_id
PAYOS_API_KEY=your_payos_api_key
PAYOS_CHECKSUM_KEY=your_payos_checksum_key
```

**Tùy chọn (Optional):**
```
MONGODB_DATABASE=color_bites_db
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
```

6. Click **Apply** → **OK**

### **VS Code**

Tạo file `.vscode/launch.json`:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot - ColorBitesBeApplication",
      "request": "launch",
      "mainClass": "com.exe201.color_bites_be.ColorBitesBeApplication",
      "projectName": "ColorBites_be",
      "env": {
        "MONGODB_URI": "mongodb+srv://youruser:yourpass@cluster0.xxxxx.mongodb.net/color_bites_db?retryWrites=true&w=majority",
        "SECRET_KEY": "your_base64_encoded_jwt_secret_key_here",
        "PAYOS_CLIENT_ID": "your_payos_client_id",
        "PAYOS_API_KEY": "your_payos_api_key",
        "PAYOS_CHECKSUM_KEY": "your_payos_checksum_key"
      }
    }
  ]
}
```

**⚠️ LƯU Ý:** Thêm `.vscode/` vào `.gitignore` để không commit credentials!

---

## 🚀 Chạy Application

### **Trong IDE**

1. Mở `ColorBitesBeApplication.java`
2. Click **Run** (▶️) hoặc `Shift + F10` (IntelliJ)
3. Kiểm tra logs:

```
✅ The following 1 profile is active: "default"
✅ Started ColorBitesBeApplication in X seconds
```

### **Command Line**

```bash
# Set environment variables trước
export MONGODB_URI="mongodb+srv://..."
export SECRET_KEY="..."
# ... các biến khác

# Chạy app
mvn spring-boot:run
```

**Windows PowerShell:**
```powershell
$env:MONGODB_URI="mongodb+srv://..."
$env:SECRET_KEY="..."
# ... các biến khác

mvn spring-boot:run
```

---

## 🧪 Testing

### **Chạy tests với MongoDB**

Nếu bạn có MongoDB local hoặc muốn test với Atlas:

```bash
# Set MONGODB_URI trước
export MONGODB_URI="mongodb://localhost:27017/color_bites_db"

# Chạy tests
mvn test
```

### **Skip tests**

Tests đã được skip mặc định trong `pom.xml` để build nhanh hơn:

```bash
# Build mà không chạy tests
mvn clean package

# Hoặc explicit skip
mvn clean package -DskipTests
```

---

## 📚 API Documentation

Sau khi chạy app, truy cập Swagger UI:

```
http://localhost:8080/api
```

hoặc

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🔍 Troubleshooting

### **Lỗi: Connection refused (localhost:27017)**

**Nguyên nhân:** Không có MongoDB chạy local và chưa set biến `MONGODB_URI`.

**Giải pháp:**
1. Set biến `MONGODB_URI` trong IDE Environment Variables (xem hướng dẫn trên)
2. Hoặc cài MongoDB local và chạy service

---

### **Lỗi: The connection string is invalid**

**Nguyên nhân:** Connection string sai format hoặc thiếu prefix.

**Giải pháp:** Đảm bảo connection string bắt đầu bằng:
- `mongodb://` (local)
- `mongodb+srv://` (Atlas)

---

### **Lỗi: Authentication failed**

**Nguyên nhân:** Username/password sai hoặc user chưa được tạo trong MongoDB Atlas.

**Giải pháp:**
1. Kiểm tra username/password trong Atlas Dashboard
2. Đảm bảo password được URL-encode (ký tự đặc biệt như `@` → `%40`)
3. Kiểm tra user có quyền `readWrite` trên database

---

### **Lỗi: JWT secret key error**

**Nguyên nhân:** Biến `SECRET_KEY` chưa được set hoặc không đúng format Base64.

**Giải pháp:** Generate secret key mới:

```bash
# Linux/Mac
openssl rand -base64 64

# Windows PowerShell
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

Copy kết quả và set vào biến `SECRET_KEY`.

---

## 🔒 Security Best Practices

1. **KHÔNG BAO GIỜ** commit credentials vào Git
2. **LUÔN LUÔN** dùng Environment Variables cho sensitive data
3. **THÊM** `.vscode/`, `.idea/`, `*.env` vào `.gitignore`
4. **SỬ DỤNG** password manager để lưu credentials
5. **ROTATE** credentials định kỳ (3-6 tháng)

---

## 📚 Related Documentation

- [README.md](../README.md) - Project overview
- [Railway Deployment](RAILWAY_DEPLOYMENT.md) - Production deployment
- [API Development Notes](API_DEVELOPMENT_NOTES.md) - API details
- [PayOS Integration](PAYOS_INTEGRATION_GUIDE.md) - Payment setup

---

**Last Updated:** 2025-10-11  
**Version:** 1.0.0

