# 🚀 Hướng Dẫn Deploy Spring Boot API lên Railway với Swagger HTTPS

## ✅ HOÀN THÀNH - Các file đã được cấu hình

Tất cả các file cần thiết đã được cấu hình xong:

1. ✅ **`src/main/resources/application-prod.yml`** - Production config với forward-headers-strategy
2. ✅ **`src/main/java/com/exe201/color_bites_be/config/SwaggerConfig.java`** - Thêm Railway HTTPS server URLs
3. ✅ **`src/main/java/com/exe201/color_bites_be/config/SecurityConfig.java`** - ForwardedHeaderFilter + OPTIONS + PATCH

---

## 🔧 BƯỚC 4: CẤU HÌNH RAILWAY ENVIRONMENT VARIABLES

### **Cách 1: Qua Railway Dashboard (Khuyến nghị)**

1. **Mở Railway Dashboard:**
   - Truy cập: https://railway.app
   - Đăng nhập vào tài khoản của bạn

2. **Chọn Project:**
   - Click vào project `Color Bites Backend` (hoặc tên project của bạn)

3. **Vào tab Variables:**
   - Trong dashboard project → Click tab **"Variables"**
   - Hoặc click vào service → **"Variables"**

4. **Thêm Environment Variable mới:**
   ```
   Variable Name:  SPRING_PROFILES_ACTIVE
   Value:          prod
   ```

5. **Save & Deploy:**
   - Click nút **"Add"** hoặc **"Save"**
   - Railway sẽ tự động **redeploy** ứng dụng

---

### **Cách 2: Qua Railway CLI (Tùy chọn)**

Nếu bạn có Railway CLI đã cài đặt:

```bash
# Set environment variable
railway variables set SPRING_PROFILES_ACTIVE=prod

# Deploy lại
railway up
```

---

## 🎯 KIỂM TRA SAU KHI DEPLOY

### **1. Kiểm tra Logs có active profile "prod"**

Trong Railway Dashboard → **Logs** → Tìm dòng:

```
The following 1 profile is active: "prod"
```

Nếu thấy dòng này → Cấu hình thành công! ✅

---

### **2. Kiểm tra Swagger UI**

Mở trình duyệt và truy cập:

```
https://api-mumii.namtechie.id.vn/swagger-ui/index.html
```

**Kiểm tra:**
- ✅ Swagger UI hiển thị bình thường
- ✅ Dropdown "Servers" có 2 options:
  - `https://api-mumii.namtechie.id.vn` (Production)
  - `http://localhost:8080` (Local)
- ✅ Chọn **Production server**
- ✅ Thử "Try it out" ở endpoint `/api/auth/login`

**Kỳ vọng:**
- Request URL phải là: `https://api-mumii.namtechie.id.vn/api/auth/login` (HTTPS ✅)
- Không còn lỗi **"Failed to fetch"**

---

### **3. Test CORS với CURL**

```bash
# Test OPTIONS preflight
curl -i -X OPTIONS https://api-mumii.namtechie.id.vn/api/auth/login \
  -H "Origin: https://api-mumii.namtechie.id.vn" \
  -H "Access-Control-Request-Method: POST"

# Kỳ vọng: HTTP/1.1 200 OK
# Header: Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
```

```bash
# Test POST login endpoint
curl -i -X POST https://api-mumii.namtechie.id.vn/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test123","password":"Test@123"}'

# Kỳ vọng: HTTP 400/401 (tùy data), KHÔNG còn CORS error
```

---

## 🔍 TROUBLESHOOTING

### **Vấn đề 1: Swagger vẫn hiển thị HTTP thay vì HTTPS**

**Nguyên nhân:** Railway chưa nhận biến môi trường `SPRING_PROFILES_ACTIVE=prod`

**Giải pháp:**
1. Kiểm tra lại Railway Variables đã set đúng chưa
2. Xem logs Railway có dòng `The following 1 profile is active: "prod"`
3. Nếu không có → Redeploy lại service

```bash
# Trigger redeploy bằng cách push empty commit
git commit --allow-empty -m "Trigger Railway redeploy"
git push origin main
```

---

### **Vấn đề 2: "Failed to fetch" khi bấm "Try it out"**

**Debug steps:**

1. **Mở DevTools (F12) → Network tab**
2. Bấm "Try it out" và xem request URL
3. Nếu URL vẫn là `http://...` → Quay lại Vấn đề 1
4. Nếu URL đã là `https://...` nhưng vẫn lỗi → Kiểm tra CORS

**Kiểm tra CORS:**
```bash
curl -v -X OPTIONS https://api-mumii.namtechie.id.vn/api/auth/login \
  -H "Origin: https://api-mumii.namtechie.id.vn" \
  -H "Access-Control-Request-Method: POST"
```

**Xem response headers phải có:**
- `Access-Control-Allow-Origin: https://api-mumii.namtechie.id.vn`
- `Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS`

---

### **Vấn đề 3: CORS error từ Frontend khác**

Nếu frontend của bạn (Vercel, Netlify) gọi API bị CORS error:

**Giải pháp:** Thêm frontend domain vào `SecurityConfig.java`:

```java
config.setAllowedOriginPatterns(List.of(
    // ... existing origins
    "https://your-frontend-app.vercel.app", // Thêm dòng này
    "https://*.vercel.app" // Hoặc wildcard
));
```

Sau đó commit & push lại.

---

## 📊 NHỮNG GÌ ĐÃ ĐƯỢC CẤU HÌNH

### **1. application-prod.yml**

```yaml
server:
  forward-headers-strategy: framework  # ← Key setting
```

**Mục đích:** Cho Spring hiểu `X-Forwarded-Proto: https` từ Railway proxy → Swagger sinh đúng HTTPS URLs.

---

### **2. SwaggerConfig.java**

```java
.addServersItem(new Server()
        .url("https://api-mumii.namtechie.id.vn")
        .description("Production Server - Railway"))
```

**Mục đích:** Ép Swagger UI sử dụng HTTPS trong dropdown "Servers".

---

### **3. SecurityConfig.java**

**a) ForwardedHeaderFilter:**
```java
@Bean
public ForwardedHeaderFilter forwardedHeaderFilter() {
    return new ForwardedHeaderFilter();
}
```

**Mục đích:** Process `X-Forwarded-*` headers để tất cả components trong Spring nhận đúng scheme/host.

**b) OPTIONS Preflight:**
```java
.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
```

**Mục đích:** Cho phép browser gửi OPTIONS preflight mà không cần JWT → CORS hoạt động.

**c) PATCH Method:**
```java
config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
```

**Mục đích:** Hỗ trợ đầy đủ REST operations.

---

## 🎓 TẠI SAO GIẢI PHÁP NÀY HOẠT ĐỘNG?

### **Flow request trên Railway:**

1. **Client** (Browser/Postman) gửi request:
   ```
   https://api-mumii.namtechie.id.vn/api/auth/login
   ```

2. **Railway Proxy** nhận request → Forward tới Spring container:
   ```
   X-Forwarded-Proto: https
   X-Forwarded-Host: api-mumii.namtechie.id.vn
   X-Forwarded-For: client-ip
   ```

3. **ForwardedHeaderFilter** process headers → Cập nhật request:
   ```
   request.getScheme() → "https" (thay vì "http")
   request.getServerName() → "api-mumii.namtechie.id.vn"
   ```

4. **Swagger OpenAPI** sinh spec với đúng URLs:
   ```json
   {
     "servers": [
       {"url": "https://api-mumii.namtechie.id.vn"}
     ]
   }
   ```

5. **Browser** trong Swagger UI gọi đúng HTTPS → Không còn "Failed to fetch" ✅

---

## 🚀 NEXT STEPS

Sau khi deploy thành công:

1. ✅ Test tất cả endpoints qua Swagger UI
2. ✅ Test CORS từ frontend (nếu có)
3. ✅ Monitor logs Railway để phát hiện lỗi
4. ✅ Set up custom domain (nếu cần)

---

## 📞 HỖ TRỢ

Nếu còn gặp vấn đề, cung cấp các thông tin sau:

1. **Screenshot** lỗi trên Swagger UI
2. **Railway Logs** (10-20 dòng cuối)
3. **Response headers** của OPTIONS request:
   ```bash
   curl -v -X OPTIONS https://api-mumii.namtechie.id.vn/api/auth/login \
     -H "Origin: https://api-mumii.namtechie.id.vn"
   ```

---

**✨ Chúc bạn deploy thành công!** 🎉

**Last Updated:** $(date)  
**Version:** 1.0.0

