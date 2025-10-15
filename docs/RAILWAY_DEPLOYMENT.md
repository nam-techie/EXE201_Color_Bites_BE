# 🚀 Railway Deployment Guide

## 📋 Required Environment Variables

Trước khi deploy, bạn **PHẢI** set các biến môi trường sau trên Railway Dashboard.

### **Bắt buộc (Required):**

| Variable Name | Description | Example |
|---------------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Kích hoạt production profile | `prod` |
| `MONGODB_URI` | MongoDB connection string | `mongodb+srv://username:password@cluster.mongodb.net/dbname?retryWrites=true&w=majority` |
| `SECRET_KEY` | JWT secret key (Base64) | `your_base64_encoded_secret` |
| `PAYOS_CLIENT_ID` | PayOS Client ID | `your_payos_client_id` |
| `PAYOS_API_KEY` | PayOS API Key | `your_payos_api_key` |
| `PAYOS_CHECKSUM_KEY` | PayOS Checksum Key | `your_payos_checksum_key` |

### **Tùy chọn (Optional):**

| Variable Name | Description | Default |
|---------------|-------------|---------|
| `MONGODB_DATABASE` | MongoDB database name | `color_bites_db` |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name | - |
| `CLOUDINARY_API_KEY` | Cloudinary API key | - |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret | - |

---

## 🔧 Setup Steps

### **Step 1: Mở Railway Dashboard**

1. Truy cập: https://railway.app
2. Đăng nhập vào tài khoản
3. Chọn project **Color Bites Backend**

### **Step 2: Thêm Environment Variables**

1. Click vào **service** của bạn
2. Click tab **"Variables"**
3. Thêm từng biến một:

```
Variable Name:  SPRING_PROFILES_ACTIVE
Value:          prod
```

```
Variable Name:  MONGODB_URI
Value:          <your_mongodb_connection_string>
```

```
Variable Name:  SECRET_KEY
Value:          <your_jwt_secret_base64>
```

... (tiếp tục với các biến khác)

### **Step 3: Deploy**

Railway sẽ tự động redeploy sau khi bạn thêm/sửa biến môi trường.

Hoặc push code mới:

```bash
git add .
git commit -m "Update deployment config"
git push origin main
```

---

## ✅ Verification

### **1. Kiểm tra Logs**

Trong Railway Dashboard → **Logs** → Tìm dòng:

```
The following 1 profile is active: "prod"
Started ColorBitesBeApplication in X seconds
```

### **2. Test Swagger UI**

Mở trình duyệt:

```
https://your-app.up.railway.app/swagger-ui/index.html
```

- ✅ Swagger UI hiển thị bình thường
- ✅ Dropdown "Servers" có HTTPS option
- ✅ Test endpoint `/api/auth/login`

### **3. Test API**

```bash
curl -X POST https://your-app.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'
```

---

## 🔍 Troubleshooting

### **Lỗi: Application failed to start**

**Logs:**
```
Failed to bind properties under 'spring.data.mongodb.uri'
```

**Giải pháp:** Kiểm tra biến `MONGODB_URI` đã được set chưa.

---

### **Lỗi: Build failed - MongoDB connection test**

**Logs:**
```
The connection string is invalid. Connection strings must start with either 'mongodb://' or 'mongodb+srv://'
```

**Nguyên nhân:** Maven test chạy mà không có MongoDB connection.

**Giải pháp:** Tests đã được skip mặc định trong `pom.xml` (`<skipTests>true</skipTests>`). Railway sẽ build thành công.

---

### **Lỗi: Swagger hiển thị HTTP thay vì HTTPS**

**Nguyên nhân:** Biến `SPRING_PROFILES_ACTIVE` chưa set hoặc không phải `prod`.

**Giải pháp:** 
1. Kiểm tra Railway Variables có `SPRING_PROFILES_ACTIVE=prod`
2. Redeploy lại service

---

### **Lỗi: PayOS payment failed**

**Nguyên nhân:** Một trong 3 biến PayOS sai hoặc chưa set.

**Giải pháp:** Kiểm tra lại 3 biến:
- `PAYOS_CLIENT_ID`
- `PAYOS_API_KEY`
- `PAYOS_CHECKSUM_KEY`

---

## 🔒 Security Notes

⚠️ **QUAN TRỌNG:**

1. **KHÔNG BAO GIỜ** commit credentials vào Git
2. **KHÔNG BAO GIỜ** share credentials qua chat/email không mã hóa
3. **LƯU TRỮ** credentials trong password manager (1Password, Bitwarden)
4. **ROTATE** credentials định kỳ (3-6 tháng)
5. **XÓA** credentials khỏi file documentation sau khi setup xong

---

## 📚 Related Documentation

- [README.md](../README.md) - Project overview
- [API Development Notes](API_DEVELOPMENT_NOTES.md) - API details
- [PayOS Integration](PAYOS_INTEGRATION_GUIDE.md) - Payment setup

---

**Last Updated:** 2025-10-11  
**Version:** 1.0.0

