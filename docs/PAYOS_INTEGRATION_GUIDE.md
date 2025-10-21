# PayOS Payment Integration Guide

## 📋 Tổng quan

PayOS đã được tích hợp thành công vào Color Bites Backend để hỗ trợ thanh toán cho mobile app.

## 🔧 Cấu hình

### Environment Variables

```yaml
# application.yml
payos:
  client-id: ${PAYOS_CLIENT_ID:your_client_id}
  api-key: ${PAYOS_API_KEY:your_api_key}
  checksum-key: ${PAYOS_CHECKSUM_KEY:your_checksum_key}
  api-url: https://api-merchant.payos.vn
  return-url: ${PAYOS_RETURN_URL:http://localhost:8080/api/payment/payos/return}
  cancel-url: ${PAYOS_CANCEL_URL:http://localhost:8080/api/payment/payos/cancel}
```

### Dependencies Added

```xml
<!-- PayOS Integration -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>

<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.15</version>
</dependency>
```

## 🚀 API Endpoints

### 1. Tạo thanh toán mới

```http
POST /api/payment/create
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "description": "Thanh toán đơn hàng #123",
  "amount": 100000,
  "currency": "VND",
  "orderInfo": "Thông tin bổ sung"
}
```

**Response:**

```json
{
  "status": 200,
  "message": "Tạo thanh toán thành công",
  "data": {
    "checkoutUrl": "https://pay.payos.vn/web/...",
    "paymentLinkId": "1234567890",
    "orderCode": 1234567890,
    "status": "SUCCESS",
    "createdAt": "2024-09-18T10:00:00",
    "message": "Tạo thanh toán thành công"
  }
}
```

### 2. Kiểm tra trạng thái thanh toán

```http
GET /api/payment/status/{transactionId}
Authorization: Bearer {jwt_token}
```

**Response:**

```json
{
  "status": 200,
  "message": "Lấy trạng thái thanh toán thành công",
  "data": {
    "transactionId": "1234567890",
    "status": "SUCCESS",
    "gatewayName": "PayOS",
    "message": "Thanh toán thành công"
  }
}
```

### 3. Lấy lịch sử giao dịch của user

```http
GET /api/payment/history
Authorization: Bearer {jwt_token}
```

**Response:**

```json
{
  "status": 200,
  "message": "Lấy lịch sử giao dịch thành công",
  "data": [
    {
      "transactionId": "1234567890",
      "orderCode": 1234567890,
      "status": "SUCCESS",
      "amount": 100000,
      "description": "Thanh toán đơn hàng #123",
      "gatewayName": "PayOS",
      "message": "Thanh toán thành công",
      "createdAt": "2024-09-18T10:00:00",
      "updatedAt": "2024-09-18T10:05:00"
    }
  ]
}
```

### 4. Webhook callback (Internal)

```http
POST /api/payment/payos/webhook
Content-Type: application/json

{
  "orderCode": "1234567890",
  "status": "PAID",
  "amount": 100000,
  "signature": "generated_signature"
}
```

### 5. Return URL (Internal)

```http
GET /api/payment/payos/return?orderCode=123&status=SUCCESS
```

## 📱 Mobile App Integration

### Flow thanh toán cho mobile:

1. **Mobile gọi API tạo thanh toán:**

   ```javascript
   const response = await fetch("/api/payment/create", {
     method: "POST",
     headers: {
       Authorization: "Bearer " + token,
       "Content-Type": "application/json",
     },
     body: JSON.stringify({
       description: "Thanh toán đơn hàng #123",
       amount: 100000,
       currency: "VND",
     }),
   });

   const result = await response.json();
   const checkoutUrl = result.data.checkoutUrl;
   ```

2. **Mobile mở URL thanh toán:**

   ```javascript
   // React Native WebView hoặc In-App Browser
   import { Linking } from "react-native";

   Linking.openURL(checkoutUrl);
   ```

3. **Mobile kiểm tra trạng thái (polling):**

   ```javascript
   const checkStatus = async (transactionId) => {
     const response = await fetch(`/api/payment/confirm?id=${transactionId}`, {
       headers: { Authorization: "Bearer " + token },
     });

     const result = await response.json();
     return result.data.status; // SUCCESS, PENDING, FAILED, CANCELED
   };

   // Polling mỗi 3 giây
   const interval = setInterval(async () => {
     const status = await checkStatus(transactionId);
     if (status !== "PENDING") {
       clearInterval(interval);
       handlePaymentResult(status);
     }
   }, 3000);
   ```

4. **Mobile lấy lịch sử giao dịch:**

   ```javascript
   const getPaymentHistory = async () => {
     const response = await fetch("/api/payment/history", {
       headers: { Authorization: "Bearer " + token },
     });

     const result = await response.json();
     return result.data; // Array of transactions
   };

   // Sử dụng trong component
   const [transactions, setTransactions] = useState([]);

   useEffect(() => {
     getPaymentHistory().then(setTransactions);
   }, []);
   ```

## 🔒 Security Features

### 1. Signature Verification

- Tất cả callback từ PayOS được verify signature bằng HMAC-SHA256
- Sử dụng checksum key để đảm bảo tính toàn vẹn dữ liệu

### 2. JWT Authentication

- Tất cả API endpoints yêu cầu JWT token hợp lệ
- Chỉ user có role USER mới được tạo thanh toán

### 3. HTTPS Only

- Tất cả communication với PayOS qua HTTPS
- Webhook URLs phải là HTTPS trong production

## 🗄️ Database Schema

### Transaction Entity

```java
@Document(collection = "transactions")
public class Transaction {
    private String id;
    private String accountId;
    private Double amount;
    private CurrencyCode currency;  // VND, USD
    private TxnType type;          // PAYMENT, REFUND
    private TxnStatus status;      // PENDING, SUCCESS, FAILED, CANCELED
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}
```

### Metadata Structure

```json
{
  "orderCode": 1234567890,
  "gateway": "PayOS",
  "description": "Thanh toán đơn hàng #123",
  "paymentLinkId": "link_id_from_payos"
}
```

## 🧪 Testing

### Test Cases

#### 1. Tạo thanh toán thành công

```bash
curl -X POST http://localhost:8080/api/payment/create \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Test payment",
    "amount": 10000,
    "currency": "VND"
  }'
```

#### 2. Kiểm tra trạng thái

```bash
curl -X GET http://localhost:8080/api/payment/status/1234567890 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### 3. Test webhook (Local testing)

```bash
curl -X POST http://localhost:8080/api/payment/payos/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "orderCode": "1234567890",
    "status": "PAID",
    "amount": 10000,
    "signature": "calculated_signature"
  }'
```

## 🚨 Error Handling

### Common Error Responses

#### 1. Invalid Amount

```json
{
  "status": 400,
  "message": "Số tiền tối thiểu là 1,000 VND"
}
```

#### 2. PayOS API Error

```json
{
  "status": 500,
  "message": "Không thể tạo thanh toán. Vui lòng thử lại sau."
}
```

#### 3. Unauthorized

```json
{
  "status": 401,
  "message": "Chưa xác thực"
}
```

## 🔧 Maintenance

### Logs Monitoring

```java
// PayOSGateway logs
log.info("Tạo thanh toán PayOS cho order: {}", orderInfo.getOrderId());
log.info("PayOS API response: {}", responseBody);
log.error("PayOS API error: {}", responseBody);

// PaymentController logs
log.info("Tạo thanh toán cho user: {}", authentication.getName());
log.info("Kiểm tra trạng thái thanh toán: {} cho user: {}", transactionId, authentication.getName());
```

### Health Check

- Monitor PayOS API availability
- Check webhook delivery success rate
- Track payment success/failure rates

## 📈 Future Enhancements

### Planned Features

1. **Refund Support** - Hỗ trợ hoàn tiền
2. **Multiple Payment Methods** - Thêm VNPAY, MoMo
3. **Payment History** - Lịch sử thanh toán chi tiết
4. **Auto Retry** - Tự động retry khi API fail
5. **Payment Analytics** - Thống kê thanh toán

### Configuration for Production

```yaml
# Production settings
payos:
  return-url: https://your-domain.com/api/payment/payos/return
  cancel-url: https://your-domain.com/api/payment/payos/cancel
```

---

**📝 Last Updated**: September 18, 2024  
**👤 Updated by**: AI Assistant  
**🏷️ Version**: 1.0.0
