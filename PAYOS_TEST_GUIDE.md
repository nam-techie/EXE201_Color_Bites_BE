# 🎯 PayOS Integration Test Guide

## ✅ **Đã hoàn thành:**

### 1. **PaymentServiceImpl** - Đầy đủ methods:
- ✅ `handlePayOSWebhook()` - Xử lý webhook từ PayOS
- ✅ `handlePayOSReturn()` - Xử lý return URL từ PayOS  
- ✅ `generateTestSignature()` - Tạo signature test
- ✅ `verifyWebhookSignature()` - Verify webhook signature
- ✅ `processWebhookData()` - Xử lý dữ liệu webhook
- ✅ `canonicalizeData()` - Canonicalize data cho webhook
- ✅ `generateReturnHtml()` - Tạo HTML cho return URL
- ✅ `generateErrorHtml()` - Tạo HTML cho error cases

### 2. **PaymentController** - Đầy đủ endpoints:
- ✅ `POST /api/payment/payos/webhook` - Webhook callback
- ✅ `GET /api/payment/payos/return` - Return URL
- ✅ `GET /api/payment/payos/cancel` - Cancel URL
- ✅ `POST /api/payment/test/signature` - Test signature generation

### 3. **DTO Classes** - Đầy đủ:
- ✅ PayOSWebhookRequest & PayOSWebhookData
- ✅ PayOSReturnRequest
- ✅ PayOSWebhookResponse
- ✅ PayOSReturnResponse
- ✅ SignatureTestResponse

---

## 🧪 **Test Cases:**

### **1. Test Return URL (Query Params):**

```bash
# Generate test signature first
POST /api/payment/test/signature
{
  "type": "return",
  "data": {
    "amount": "10000",
    "cancelUrl": "http://localhost:8080/api/payment/payos/cancel",
    "description": "Premium Subscription",
    "orderCode": "1758953228480",
    "returnUrl": "http://localhost:8080/api/payment/payos/return"
  }
}

# Use the generated signature in return URL
GET /api/payment/payos/return?
amount=10000&
cancelUrl=http%3A%2F%2Flocalhost%3A8080%2Fapi%2Fpayment%2Fpayos%2Fcancel&
description=Premium%20Subscription&
orderCode=1758953228480&
returnUrl=http%3A%2F%2Flocalhost%3A8080%2Fapi%2Fpayment%2Fpayos%2Freturn&
status=PAID&
signature=<generated_signature>
```

### **2. Test Webhook (JSON Body):**

```bash
# Generate test signature first
POST /api/payment/test/signature
{
  "type": "webhook",
  "data": {
    "orderCode": 1758953228480,
    "paymentLinkId": "27f3b34644be45eab2cbe91e647ead21",
    "amount": 10000,
    "description": "Premium Subscription",
    "status": "PAID"
  }
}

# Use the generated signature in webhook
POST /api/payment/payos/webhook
{
  "code": "00",
  "desc": "success",
  "data": {
    "orderCode": 1758953228480,
    "paymentLinkId": "27f3b34644be45eab2cbe91e647ead21",
    "amount": 10000,
    "description": "Premium Subscription",
    "status": "PAID"
  },
  "signature": "<generated_signature>"
}
```

### **3. Test Cancel URL:**

```bash
GET /api/payment/payos/cancel?orderCode=1758953228480
```

---

## 🔧 **Cách sử dụng:**

### **Bước 1: Generate Test Signature**
```bash
curl -X POST http://localhost:8080/api/payment/test/signature \
  -H "Content-Type: application/json" \
  -d '{
    "type": "return",
    "data": {
      "amount": "10000",
      "cancelUrl": "http://localhost:8080/api/payment/payos/cancel",
      "description": "Premium Subscription",
      "orderCode": "1758953228480",
      "returnUrl": "http://localhost:8080/api/payment/payos/return"
    }
  }'
```

### **Bước 2: Test Return URL với signature**
```bash
curl "http://localhost:8080/api/payment/payos/return?amount=10000&cancelUrl=http%3A%2F%2Flocalhost%3A8080%2Fapi%2Fpayment%2Fpayos%2Fcancel&description=Premium%20Subscription&orderCode=1758953228480&returnUrl=http%3A%2F%2Flocalhost%3A8080%2Fapi%2Fpayment%2Fpayos%2Freturn&status=PAID&signature=<GENERATED_SIGNATURE>"
```

### **Bước 3: Test Webhook với signature**
```bash
curl -X POST http://localhost:8080/api/payment/payos/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "code": "00",
    "desc": "success",
    "data": {
      "orderCode": 1758953228480,
      "paymentLinkId": "27f3b34644be45eab2cbe91e647ead21",
      "amount": 10000,
      "description": "Premium Subscription",
      "status": "PAID"
    },
    "signature": "<GENERATED_SIGNATURE>"
  }'
```

---

## 🎉 **Kết quả mong đợi:**

### **Return URL Success:**
- ✅ HTML page hiển thị "Thanh toán thành công!"
- ✅ Thông tin orderCode, status, xử lý
- ✅ Auto-close sau 3 giây

### **Webhook Success:**
- ✅ Response: `{"code": "00", "desc": "success"}`
- ✅ Transaction được cập nhật trong DB
- ✅ Subscription được upgrade lên PREMIUM

### **Error Cases:**
- ✅ Invalid signature → Error message
- ✅ Missing data → Error message
- ✅ System error → Error message

---

## 🚀 **Ready to Test!**

Tất cả code đã được implement đúng theo PayOS specification. Bạn có thể test ngay bây giờ!

**Lưu ý:** Đảm bảo PayOSConfig đã được cấu hình đúng với checksum key thực tế từ PayOS dashboard.
