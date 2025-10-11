# 🔐 HƯỚNG DẪN SETUP ENVIRONMENT VARIABLES TRÊN RAILWAY

## ⚠️ QUAN TRỌNG - ĐỌC TRƯỚC KHI DEPLOY

Sau khi refactor, **TẤT CẢ CREDENTIALS** đã được xóa khỏi file config. Bạn **PHẢI** set các biến môi trường này trên Railway trước khi deploy, nếu không ứng dụng sẽ không chạy được!

---

## 📋 DANH SÁCH BIẾN MÔI TRƯỜNG CẦN SET

### ✅ **BẮT BUỘC (Required)**

| Biến | Mô tả | Ví dụ |
|------|-------|-------|
| `SPRING_PROFILES_ACTIVE` | Kích hoạt profile production | `prod` |
| `MONGODB_URI` | MongoDB connection string | `mongodb+srv://username:password@cluster.mongodb.net/dbname?retryWrites=true&w=majority` |
| `PAYOS_CLIENT_ID` | PayOS Client ID | `8ec4087f-72a7-4f2b-a346-c5d7003f996f` |
| `PAYOS_API_KEY` | PayOS API Key | `1b5b8aec-6bc8-411e-b52a-a869b25441c5` |
| `PAYOS_CHECKSUM_KEY` | PayOS Checksum Key | `204f6bfcbaa9269db6d170407b00d35a7276e28c17ad411783898d2519b630c0` |

### 🔧 **TÙY CHỌN (Optional - Có Default)**

| Biến | Default Value | Mô tả |
|------|---------------|-------|
| `MONGODB_DATABASE` | `color_bites_db` | Tên database MongoDB |
| `PAYOS_RETURN_URL` | `https://api-mumii.namtechie.id.vn/api/payment/payos/return` | URL return sau thanh toán thành công |
| `PAYOS_CANCEL_URL` | `https://api-mumii.namtechie.id.vn/api/payment/payos/cancel` | URL khi hủy thanh toán |

---

## 🚀 CÁCH 1: SETUP QUA RAILWAY DASHBOARD (KHUYẾN NGHỊ)

### **Bước 1: Truy cập Railway Dashboard**

1. Mở trình duyệt: https://railway.app
2. Đăng nhập vào tài khoản Railway của bạn
3. Chọn project **Color Bites Backend**

### **Bước 2: Mở Variables Tab**

1. Click vào **service** của bạn (thường là tên repo Git)
2. Click tab **"Variables"** ở thanh menu bên trái
3. Bạn sẽ thấy giao diện để add Environment Variables

### **Bước 3: Thêm từng biến môi trường**

#### 🔹 **Biến 1: SPRING_PROFILES_ACTIVE**

```
Variable Name:  SPRING_PROFILES_ACTIVE
Value:          prod
```

Click **"Add"** hoặc nhấn Enter.

---

#### 🔹 **Biến 2: MONGODB_URI**

```
Variable Name:  MONGODB_URI
Value:          mongodb+srv://namdpse180259:nam180259@cluster0.xssff0p.mongodb.net/color_bites_db?retryWrites=true&w=majority&appName=Cluster0
```

⚠️ **LƯU Ý:** Đây là giá trị thật từ file cũ của bạn. Thay đổi nếu cần.

---

#### 🔹 **Biến 3: PAYOS_CLIENT_ID**

```
Variable Name:  PAYOS_CLIENT_ID
Value:          8ec4087f-72a7-4f2b-a346-c5d7003f996f
```

---

#### 🔹 **Biến 4: PAYOS_API_KEY**

```
Variable Name:  PAYOS_API_KEY
Value:          1b5b8aec-6bc8-411e-b52a-a869b25441c5
```

---

#### 🔹 **Biến 5: PAYOS_CHECKSUM_KEY**

```
Variable Name:  PAYOS_CHECKSUM_KEY
Value:          204f6bfcbaa9269db6d170407b00d35a7276e28c17ad411783898d2519b630c0
```

---

### **Bước 4: Kiểm tra lại**

Sau khi thêm xong, bạn sẽ thấy **5 biến** như sau:

```
SPRING_PROFILES_ACTIVE     = prod
MONGODB_URI                = mongodb+srv://namdpse180259:...
PAYOS_CLIENT_ID            = 8ec4087f-72a7-4f2b-a346-c5d7003f996f
PAYOS_API_KEY              = 1b5b8aec-6bc8-411e-b52a-a869b25441c5
PAYOS_CHECKSUM_KEY         = 204f6bfcbaa9269db6d170407b00d35a7276e28c17ad411783898d2519b630c0
```

### **Bước 5: Deploy lại**

Railway sẽ **tự động redeploy** sau khi bạn thêm/sửa biến môi trường. Nếu không tự động:

1. Click tab **"Deployments"**
2. Click nút **"Redeploy"** ở deployment mới nhất

---

## 🖥️ CÁCH 2: SETUP QUA RAILWAY CLI

Nếu bạn có Railway CLI đã cài đặt:

```bash
# Đăng nhập Railway
railway login

# Link với project
railway link

# Set từng biến
railway variables set SPRING_PROFILES_ACTIVE=prod
railway variables set MONGODB_URI="mongodb+srv://namdpse180259:nam180259@cluster0.xssff0p.mongodb.net/color_bites_db?retryWrites=true&w=majority&appName=Cluster0"
railway variables set PAYOS_CLIENT_ID="8ec4087f-72a7-4f2b-a346-c5d7003f996f"
railway variables set PAYOS_API_KEY="1b5b8aec-6bc8-411e-b52a-a869b25441c5"
railway variables set PAYOS_CHECKSUM_KEY="204f6bfcbaa9269db6d170407b00d35a7276e28c17ad411783898d2519b630c0"

# Deploy lại
railway up
```

---

## 📸 HÌNH ẢNH MINH HỌA

### Railway Dashboard - Variables Tab

```
┌─────────────────────────────────────────────────────────┐
│ Project: Color Bites Backend                            │
├─────────────────────────────────────────────────────────┤
│ [Overview] [Deployments] [Metrics] [Settings]           │
│ ► [Variables] ◄ YOU ARE HERE                            │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Environment Variables                                   │
│                                                          │
│  Variable Name              Value                        │
│  ─────────────────────────────────────────────────────  │
│  SPRING_PROFILES_ACTIVE     prod                   [×]   │
│  MONGODB_URI                mongodb+srv://...      [×]   │
│  PAYOS_CLIENT_ID            8ec4087f-72a7...       [×]   │
│  PAYOS_API_KEY              1b5b8aec-6bc8...       [×]   │
│  PAYOS_CHECKSUM_KEY         204f6bfcbaa9...        [×]   │
│                                                          │
│  [+ Add Variable]                                        │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

---

## ✅ KIỂM TRA SAU KHI SETUP

### **1. Xem Logs có nhận đúng variables không**

1. Trong Railway Dashboard → Click tab **"Logs"**
2. Tìm dòng:

```
The following 1 profile is active: "prod"
```

3. Nếu thấy → ✅ **SPRING_PROFILES_ACTIVE** đã hoạt động!

4. Nếu ứng dụng start thành công → ✅ **MONGODB_URI** đúng!

### **2. Test Swagger UI**

Mở trình duyệt:

```
https://api-mumii.namtechie.id.vn/swagger-ui/index.html
```

- ✅ Swagger UI hiển thị bình thường
- ✅ Dropdown "Servers" có option HTTPS
- ✅ Thử "Try it out" endpoint login → Không còn "Failed to fetch"

### **3. Test Payment với PayOS**

Nếu PayOS credentials đúng, API payment sẽ hoạt động:

```bash
curl -X POST https://api-mumii.namtechie.id.vn/api/payment/payos/create \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"amount": 50000, "description": "Test payment"}'
```

Kỳ vọng: HTTP 200/201, không phải 500 Internal Server Error.

---

## 🔍 TROUBLESHOOTING

### ❌ **Lỗi: Application failed to start**

**Logs hiển thị:**
```
Binding to target org.springframework.boot.context.properties.bind.BindException: 
Failed to bind properties under 'spring.data.mongodb.uri'
```

**Nguyên nhân:** Biến `MONGODB_URI` chưa được set hoặc giá trị sai.

**Giải pháp:**
1. Kiểm tra lại Railway Variables có `MONGODB_URI` chưa
2. Kiểm tra giá trị có đúng format `mongodb+srv://...` không
3. Test connection string bằng MongoDB Compass/Studio 3T

---

### ❌ **Lỗi: PayOS payment failed**

**Logs hiển thị:**
```
PayOS API authentication failed: Invalid API key
```

**Nguyên nhân:** Một trong 3 biến PayOS sai.

**Giải pháp:**
1. Login vào https://payos.vn/portal/
2. Vào **API Keys** → Copy lại 3 giá trị mới nhất
3. Update lại 3 biến trên Railway:
   - `PAYOS_CLIENT_ID`
   - `PAYOS_API_KEY`
   - `PAYOS_CHECKSUM_KEY`

---

### ❌ **Lỗi: Swagger vẫn hiển thị HTTP thay vì HTTPS**

**Nguyên nhân:** Biến `SPRING_PROFILES_ACTIVE` chưa set hoặc không phải `prod`.

**Giải pháp:**
1. Kiểm tra Railway Variables có `SPRING_PROFILES_ACTIVE=prod`
2. Redeploy lại service
3. Xem logs có dòng `The following 1 profile is active: "prod"`

---

## 📊 CHECKLIST HOÀN CHỈNH

Trước khi deploy, đảm bảo:

- [ ] Đã set biến `SPRING_PROFILES_ACTIVE=prod`
- [ ] Đã set biến `MONGODB_URI` với connection string đúng
- [ ] Đã set 3 biến PayOS: `CLIENT_ID`, `API_KEY`, `CHECKSUM_KEY`
- [ ] Đã commit & push code mới lên Git (không còn credentials trong .yml)
- [ ] Railway đã trigger deployment mới
- [ ] Xem logs không có lỗi "Failed to bind properties"
- [ ] Test Swagger UI hoạt động với HTTPS
- [ ] Test ít nhất 1 API endpoint qua Swagger

---

## 💾 BACKUP GIÁ TRỊ GỐC (Chỉ cho bạn - KHÔNG PUSH LÊN GIT)

Dưới đây là giá trị thật từ file config cũ của bạn. **Lưu vào nơi an toàn** (1Password, Bitwarden, hoặc file text local):

```ini
# MongoDB
MONGODB_URI=mongodb+srv://namdpse180259:nam180259@cluster0.xssff0p.mongodb.net/color_bites_db?retryWrites=true&w=majority&appName=Cluster0
MONGODB_DATABASE=color_bites_db

# PayOS
PAYOS_CLIENT_ID=8ec4087f-72a7-4f2b-a346-c5d7003f996f
PAYOS_API_KEY=1b5b8aec-6bc8-411e-b52a-a869b25441c5
PAYOS_CHECKSUM_KEY=204f6bfcbaa9269db6d170407b00d35a7276e28c17ad411783898d2519b630c0

# URLs Production
PAYOS_RETURN_URL=https://api-mumii.namtechie.id.vn/api/payment/payos/return
PAYOS_CANCEL_URL=https://api-mumii.namtechie.id.vn/api/payment/payos/cancel

# Spring Profile
SPRING_PROFILES_ACTIVE=prod
```

⚠️ **XÓA SECTION NÀY SAU KHI ĐÃ SET XONG TRÊN RAILWAY!**

---

## 🎓 TẠI SAO PHẢI LÀM THẾ NÀY?

### **Trước (Không an toàn):**
```yaml
# application.yml
mongodb:
  uri: mongodb+srv://username:password@...  # ← LỘ TRÊN GIT!
```

### **Sau (An toàn):**
```yaml
# application.yml
mongodb:
  uri: ${MONGODB_URI}  # ← Lấy từ Railway Variables
```

**Lợi ích:**
1. ✅ **Bảo mật:** Credentials không bao giờ push lên Git
2. ✅ **Linh hoạt:** Đổi password MongoDB không cần commit code mới
3. ✅ **Best Practice:** Theo chuẩn 12-Factor App
4. ✅ **Team-friendly:** Mỗi dev có DB riêng trên local

---

## 🆘 CẦN HỖ TRỢ?

Nếu gặp vấn đề trong quá trình setup:

1. **Kiểm tra Railway Logs:**
   ```
   Dashboard → Service → Logs (tab)
   ```

2. **Test biến môi trường đã được load chưa:**
   - Thêm endpoint test trong Spring Boot (tạm thời):
   ```java
   @GetMapping("/debug/env")
   public Map<String, String> getEnv() {
       return Map.of(
           "MONGODB_URI", System.getenv("MONGODB_URI") != null ? "✅ Set" : "❌ Not set",
           "PAYOS_CLIENT_ID", System.getenv("PAYOS_CLIENT_ID") != null ? "✅ Set" : "❌ Not set"
       );
   }
   ```
   - Call endpoint: `https://api-mumii.namtechie.id.vn/debug/env`

3. **Share logs/screenshots** để được hỗ trợ nhanh hơn

---

**✨ Chúc bạn setup thành công và deploy an toàn!** 🔐

**Last Updated:** 2025-10-11  
**Version:** 1.0.0

