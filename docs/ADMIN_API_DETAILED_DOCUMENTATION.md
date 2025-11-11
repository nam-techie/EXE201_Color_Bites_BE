# 📊 Color Bites Admin API - Tài liệu chi tiết cho Frontend

## 🎯 Tổng quan

Tài liệu này cung cấp thông tin chi tiết về tất cả API admin của hệ thống Color Bites Backend, bao gồm DTO request/response, cấu trúc dữ liệu và hướng dẫn implement cho Frontend team.

## 🔐 Authentication & Authorization

### Yêu cầu Authentication
- **Tất cả endpoints admin** yêu cầu JWT token hợp lệ
- **Role yêu cầu**: `ADMIN` (chỉ admin mới được truy cập)
- **Header**: `Authorization: Bearer {jwt_token}`

### Cách lấy JWT Token
```javascript
// Login để lấy token
const response = await fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'admin_username',
    password: 'admin_password'
  })
});

const data = await response.json();
const token = data.data.token; // Lưu token này để dùng cho các API admin
```

---

## 📋 Cấu trúc Response chung

Tất cả API đều trả về cấu trúc response thống nhất:

```typescript
interface ResponseDto<T> {
  status: number;        // HTTP status code
  message: string;       // Thông báo
  data: T;              // Dữ liệu trả về
}
```

---

## 🏠 **Base URL**: `/api/admin`

---

## 👥 **1. USER MANAGEMENT**

### **1.1 Lấy danh sách tất cả users**
```http
GET /api/admin/user
Authorization: Bearer {jwt_token}
```

**Response DTO:**
```typescript
interface ListAccountResponse {
  id: string;
  username: string;
  isActive: boolean;
  role: string;
  avatarUrl: string;
  created: string;        // ISO 8601 format
  updated: string;        // ISO 8601 format
}
```

**Response Example:**
```json
{
  "status": 200,
  "message": "successfully",
  "data": [
    {
      "id": "user_id_1",
      "username": "john_doe",
      "isActive": true,
      "role": "USER",
      "avatarUrl": "https://example.com/avatar.jpg",
      "created": "2024-01-01T00:00:00",
      "updated": "2024-01-01T00:00:00"
    }
  ]
}
```

### **1.2 Chặn user**
```http
PUT /api/admin/block-user/{userId}
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Chặn người dùng thành công",
  "data": null
}
```

### **1.3 Kích hoạt lại user**
```http
PUT /api/admin/active-user/{userId}
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Người dùng đã được kích hoạt",
  "data": null
}
```

---

## 📝 **2. POST MANAGEMENT**

### **2.1 Lấy danh sách bài viết (phân trang)**
```http
GET /api/admin/posts?page=0&size=10
Authorization: Bearer {jwt_token}
```

**Query Parameters:**
- `page` (int, default: 0): Số trang
- `size` (int, default: 10): Số lượng items per page

**Response DTO:**
```typescript
interface AdminPostResponse {
  id: string;
  accountId: string;
  accountName: string;
  content: string;
  moodId: string;
  moodName: string;
  reactionCount: number;
  commentCount: number;
  isDeleted: boolean;
  createdAt: string;        // ISO 8601 format
  updatedAt: string;        // ISO 8601 format
  // Thông tin bổ sung cho admin
  authorEmail: string;
  authorIsActive: boolean;
  authorRole: string;
}
```

**Response Example:**
```json
{
  "status": 200,
  "message": "Lấy danh sách bài viết thành công",
  "data": {
    "content": [
      {
        "id": "post_id_1",
        "accountId": "user_id_1",
        "accountName": "john_doe",
        "content": "Nội dung bài viết...",
        "moodId": "mood_id_1",
        "moodName": "Happy",
        "reactionCount": 15,
        "commentCount": 8,
        "isDeleted": false,
        "createdAt": "2024-01-01T00:00:00",
        "updatedAt": "2024-01-01T00:00:00",
        "authorEmail": "john@example.com",
        "authorIsActive": true,
        "authorRole": "USER"
      }
    ],
    "totalElements": 100,
    "totalPages": 10,
    "size": 10,
    "number": 0
  }
}
```

### **2.2 Lấy chi tiết bài viết**
```http
GET /api/admin/posts/{postId}
Authorization: Bearer {jwt_token}
```

**Response:** Trả về `AdminPostResponse` (single object)

### **2.3 Xóa bài viết (soft delete)**
```http
DELETE /api/admin/posts/{postId}
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Xóa bài viết thành công",
  "data": null
}
```

### **2.4 Khôi phục bài viết đã xóa**
```http
PUT /api/admin/posts/{postId}/restore
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Khôi phục bài viết thành công",
  "data": null
}
```

---

## 💬 **3. COMMENT MANAGEMENT**

### **3.1 Lấy danh sách tất cả comments**
```http
GET /api/admin/comments?page=0&size=10
Authorization: Bearer {jwt_token}
```

**Response DTO:**
```typescript
interface AdminCommentResponse {
  id: string;
  postId: string;
  postTitle: string;
  content: string;
  accountId: string;
  accountName: string;
  parentCommentId: string | null;
  replyCount: number;
  isDeleted: boolean;
  createdAt: string;        // ISO 8601 format
  updatedAt: string;        // ISO 8601 format
  // Thông tin bổ sung cho admin
  authorEmail: string;
  authorIsActive: boolean;
  authorRole: string;
  postAuthorName: string;
  postAuthorEmail: string;
}
```

**Response Example:**
```json
{
  "status": 200,
  "message": "Lấy danh sách comment thành công",
  "data": {
    "content": [
      {
        "id": "comment_id_1",
        "postId": "post_id_1",
        "postTitle": "Nội dung bài viết...",
        "content": "Comment hay quá!",
        "accountId": "user_id_1",
        "accountName": "john_doe",
        "parentCommentId": null,
        "replyCount": 3,
        "isDeleted": false,
        "createdAt": "2024-01-01T00:00:00",
        "updatedAt": "2024-01-01T00:00:00",
        "authorEmail": "john@example.com",
        "authorIsActive": true,
        "authorRole": "USER",
        "postAuthorName": "post_author",
        "postAuthorEmail": "post_author@example.com"
      }
    ],
    "totalElements": 50,
    "totalPages": 5
  }
}
```

### **3.2 Lấy chi tiết comment**
```http
GET /api/admin/comments/{commentId}
Authorization: Bearer {jwt_token}
```

**Response:** Trả về `AdminCommentResponse` (single object)

### **3.3 Xóa comment (soft delete)**
```http
DELETE /api/admin/comments/{commentId}
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Xóa comment thành công",
  "data": null
}
```

### **3.4 Khôi phục comment đã xóa**
```http
PUT /api/admin/comments/{commentId}/restore
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Khôi phục comment thành công",
  "data": null
}
```

### **3.5 Lấy comments theo bài viết**
```http
GET /api/admin/comments/post/{postId}?page=0&size=10
Authorization: Bearer {jwt_token}
```

**Response:** Trả về `Page<AdminCommentResponse>`

### **3.6 Thống kê comments**
```http
GET /api/admin/comments/statistics
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Lấy thống kê comment thành công",
  "data": {
    "totalComments": 1000,
    "activeComments": 950,
    "deletedComments": 50
  }
}
```

---

## 🍽️ **4. RESTAURANT MANAGEMENT**

### **4.1 Lấy danh sách nhà hàng (phân trang)**
```http
GET /api/admin/restaurants?page=0&size=10
Authorization: Bearer {jwt_token}
```

**Response DTO:**
```typescript
interface AdminRestaurantResponse {
  id: string;
  name: string;
  address: string;
  longitude: number;
  latitude: number;
  description: string;
  type: string;
  region: string;
  avgPrice: number;
  rating: number;
  featured: boolean;
  createdBy: string;
  createdByName: string;
  createdAt: string;        // ISO 8601 format
  isDeleted: boolean;
  // Thông tin bổ sung cho admin
  creatorEmail: string;
  creatorIsActive: boolean;
  creatorRole: string;
  favoriteCount: number;
}
```

**Response Example:**
```json
{
  "status": 200,
  "message": "Lấy danh sách nhà hàng thành công",
  "data": {
    "content": [
      {
        "id": "restaurant_id_1",
        "name": "Nhà hàng ABC",
        "address": "123 Đường ABC, Quận 1, TP.HCM",
        "longitude": 106.6297,
        "latitude": 10.8231,
        "description": "Nhà hàng ngon",
        "type": "Vietnamese",
        "region": "Ho Chi Minh City",
        "avgPrice": 150000.0,
        "rating": 4.5,
        "featured": true,
        "createdBy": "user_id_1",
        "createdByName": "john_doe",
        "createdAt": "2024-01-01T00:00:00",
        "isDeleted": false,
        "creatorEmail": "john@example.com",
        "creatorIsActive": true,
        "creatorRole": "USER",
        "favoriteCount": 25
      }
    ],
    "totalElements": 200,
    "totalPages": 20
  }
}
```

### **4.2 Lấy chi tiết nhà hàng**
```http
GET /api/admin/restaurants/{restaurantId}
Authorization: Bearer {jwt_token}
```

**Response:** Trả về `AdminRestaurantResponse` (single object)

### **4.3 Xóa nhà hàng (soft delete)**
```http
DELETE /api/admin/restaurants/{restaurantId}
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Xóa nhà hàng thành công",
  "data": null
}
```

### **4.4 Khôi phục nhà hàng đã xóa**
```http
PUT /api/admin/restaurants/{restaurantId}/restore
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Khôi phục nhà hàng thành công",
  "data": null
}
```

---

## 💳 **5. TRANSACTION/PAYMENT MANAGEMENT**

### **5.1 Lấy danh sách giao dịch (phân trang)**
```http
GET /api/admin/transactions?page=0&size=10
Authorization: Bearer {jwt_token}
```

**Response DTO:**
```typescript
interface AdminTransactionResponse {
  id: string;
  accountId: string;
  accountName: string;
  accountEmail: string;
  amount: number;
  currency: string;
  type: string;
  status: string;
  plan: string;
  gateway: string;
  orderCode: string;
  providerTxnId: string;
  metadata: Record<string, any>;
  rawPayload: Record<string, any>;
  createdAt: string;        // ISO 8601 format
  updatedAt: string;        // ISO 8601 format
  // Thông tin bổ sung cho admin
  accountIsActive: boolean;
  accountRole: string;
}
```

**Response Example:**
```json
{
  "status": 200,
  "message": "Lấy danh sách giao dịch thành công",
  "data": {
    "content": [
      {
        "id": "transaction_id_1",
        "accountId": "user_id_1",
        "accountName": "john_doe",
        "accountEmail": "john@example.com",
        "amount": 100000.0,
        "currency": "VND",
        "type": "PAYMENT",
        "status": "SUCCESS",
        "plan": "PREMIUM",
        "gateway": "PayOS",
        "orderCode": "1234567890",
        "providerTxnId": "payos_txn_123",
        "metadata": {},
        "rawPayload": {},
        "createdAt": "2024-01-01T00:00:00",
        "updatedAt": "2024-01-01T00:00:00",
        "accountIsActive": true,
        "accountRole": "USER"
      }
    ],
    "totalElements": 500,
    "totalPages": 50
  }
}
```

### **5.2 Lấy chi tiết giao dịch**
```http
GET /api/admin/transactions/{transactionId}
Authorization: Bearer {jwt_token}
```

**Response:** Trả về `AdminTransactionResponse` (single object)

### **5.3 Lấy giao dịch theo trạng thái**
```http
GET /api/admin/transactions/status/{status}?page=0&size=10
Authorization: Bearer {jwt_token}
```

**Status Values**: `SUCCESS`, `PENDING`, `FAILED`, `CANCELED`

**Response:** Trả về `Page<AdminTransactionResponse>`

---

## 🏷️ **6. TAG MANAGEMENT**

### **6.1 Lấy danh sách tất cả tags**
```http
GET /api/admin/tags?page=0&size=10
Authorization: Bearer {jwt_token}
```

**Response DTO:**
```typescript
interface AdminTagResponse {
  id: string;
  name: string;
  description: string;
  usageCount: number;
  isDeleted: boolean;
  createdAt: string;        // ISO 8601 format
  updatedAt: string;        // ISO 8601 format
  // Thông tin bổ sung cho admin
  createdBy: string;
  createdByName: string;
  createdByEmail: string;
  postCount: number;
  restaurantCount: number;
}
```

**Response Example:**
```json
{
  "status": 200,
  "message": "Lấy danh sách tag thành công",
  "data": {
    "content": [
      {
        "id": "tag_id_1",
        "name": "Vietnamese Food",
        "description": "Món ăn Việt Nam",
        "usageCount": 150,
        "isDeleted": false,
        "createdAt": "2024-01-01T00:00:00",
        "updatedAt": "2024-01-01T00:00:00",
        "createdBy": "System",
        "createdByName": "System",
        "createdByEmail": "system@colorbites.com",
        "postCount": 100,
        "restaurantCount": 50
      }
    ],
    "totalElements": 50,
    "totalPages": 5
  }
}
```

### **6.2 Lấy chi tiết tag**
```http
GET /api/admin/tags/{tagId}
Authorization: Bearer {jwt_token}
```

**Response:** Trả về `AdminTagResponse` (single object)

### **6.3 Tạo tag mới**
```http
POST /api/admin/tags?name=New Tag&description=Tag description
Authorization: Bearer {jwt_token}
```

**Query Parameters:**
- `name` (string, required): Tên tag
- `description` (string, optional): Mô tả tag

**Response:** Trả về `AdminTagResponse` (single object)

### **6.4 Cập nhật tag**
```http
PUT /api/admin/tags/{tagId}?name=Updated Tag&description=Updated description
Authorization: Bearer {jwt_token}
```

**Query Parameters:**
- `name` (string, required): Tên tag mới
- `description` (string, optional): Mô tả tag mới

**Response:** Trả về `AdminTagResponse` (single object)

### **6.5 Xóa tag (soft delete)**
```http
DELETE /api/admin/tags/{tagId}
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Xóa tag thành công",
  "data": null
}
```

### **6.6 Thống kê tags**
```http
GET /api/admin/tags/statistics
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Lấy thống kê tag thành công",
  "data": {
    "totalTags": 100,
    "activeTags": 95,
    "deletedTags": 5
  }
}
```

---

## 📊 **7. STATISTICS & ANALYTICS**

### **7.1 Thống kê tổng quan hệ thống**
```http
GET /api/admin/statistics
Authorization: Bearer {jwt_token}
```

**Response DTO:**
```typescript
interface StatisticsResponse {
  // Basic counts
  totalUsers: number;
  activeUsers: number;
  totalPosts: number;
  totalRestaurants: number;
  totalComments: number;
  totalTags: number;
  totalChallenges: number;
  totalTransactions: number;
  
  // Revenue statistics
  totalRevenue: number;
  monthlyRevenue: number;
  dailyRevenue: number;
  successfulTransactions: number;
  failedTransactions: number;
  pendingTransactions: number;
  
  // Engagement statistics
  totalReactions: number;
  totalFavorites: number;
  averageRating: number;
  totalMoodMaps: number;
  totalQuizzes: number;
  
  // Time-based data
  userGrowthData: Array<Record<string, any>>;
  postActivityData: Array<Record<string, any>>;
  revenueData: Array<Record<string, any>>;
  engagementData: Array<Record<string, any>>;
  
  // Top performers
  topPosts: Array<Record<string, any>>;
  topRestaurants: Array<Record<string, any>>;
  topUsers: Array<Record<string, any>>;
  popularTags: Array<Record<string, any>>;
  
  // System health
  lastUpdated: string;        // ISO 8601 format
  systemStatus: string;
  activeSessions: number;
}
```

**Response Example:**
```json
{
  "status": 200,
  "message": "Lấy thống kê hệ thống thành công",
  "data": {
    "totalUsers": 1000,
    "activeUsers": 950,
    "totalPosts": 5000,
    "totalRestaurants": 500,
    "totalComments": 2000,
    "totalTags": 100,
    "totalChallenges": 50,
    "totalTransactions": 2000,
    "totalRevenue": 10000000.0,
    "monthlyRevenue": 500000.0,
    "dailyRevenue": 15000.0,
    "successfulTransactions": 1800,
    "failedTransactions": 100,
    "pendingTransactions": 100,
    "totalReactions": 50000,
    "totalFavorites": 10000,
    "averageRating": 4.2,
    "totalMoodMaps": 200,
    "totalQuizzes": 100,
    "userGrowthData": [],
    "postActivityData": [],
    "revenueData": [],
    "engagementData": [],
    "topPosts": [],
    "topRestaurants": [],
    "topUsers": [],
    "popularTags": [],
    "lastUpdated": "2024-01-01T00:00:00",
    "systemStatus": "HEALTHY",
    "activeSessions": 150
  }
}
```

### **7.2 Thống kê users**
```http
GET /api/admin/statistics/users
Authorization: Bearer {jwt_token}
```

**Response:** Trả về `StatisticsResponse` (focused on user data)

### **7.3 Thống kê posts**
```http
GET /api/admin/statistics/posts
Authorization: Bearer {jwt_token}
```

**Response:** Trả về `StatisticsResponse` (focused on post data)

### **7.4 Thống kê restaurants**
```http
GET /api/admin/statistics/restaurants
Authorization: Bearer {jwt_token}
```

**Response:** Trả về `StatisticsResponse` (focused on restaurant data)

### **7.5 Thống kê doanh thu**
```http
GET /api/admin/statistics/revenue
Authorization: Bearer {jwt_token}
```

**Response:** Trả về `StatisticsResponse` (focused on revenue data)

### **7.6 Thống kê tương tác**
```http
GET /api/admin/statistics/engagement
Authorization: Bearer {jwt_token}
```

**Response:** Trả về `StatisticsResponse` (focused on engagement data)

### **7.7 Thống kê challenges**
```http
GET /api/admin/statistics/challenges
Authorization: Bearer {jwt_token}
```

**Response:** Trả về `StatisticsResponse` (focused on challenge data)

---

## 🎨 **CẤU TRÚC NAVBAR ADMIN ĐỀ XUẤT**

```
📊 Dashboard
├── 📈 Overview Statistics
├── 📊 Charts & Graphs
└── 🎯 Key Metrics

👥 Users Management
├── 👤 All Users
├── 🚫 Blocked Users
├── ✅ Active Users
└── 📊 User Statistics

📝 Posts Management
├── 📄 All Posts
├── 🗑️ Deleted Posts
├── 📊 Post Analytics
└── 🔍 Search Posts

💬 Comments Management
├── 💬 All Comments
├── 🗑️ Deleted Comments
├── 📊 Comment Statistics
└── 🔍 Search Comments

🍽️ Restaurants Management
├── 🏪 All Restaurants
├── 🗑️ Deleted Restaurants
├── ⭐ Featured Restaurants
└── 📊 Restaurant Analytics

💳 Transactions/Payments
├── 💰 All Transactions
├── ✅ Successful Payments
├── ❌ Failed Payments
├── ⏳ Pending Payments
└── 📊 Revenue Analytics

🏷️ Tags Management
├── 🏷️ All Tags
├── ➕ Create Tag
├── ✏️ Edit Tags
├── 📊 Tag Statistics
└── 🔍 Search Tags

📈 Statistics & Reports
├── 👥 User Analytics
├── 📝 Post Analytics
├── 🍽️ Restaurant Analytics
├── 💰 Revenue Reports
├── 💬 Engagement Analytics
└── 📊 System Health
```

---

## 🚨 **LƯU Ý QUAN TRỌNG VỀ SOFT DELETE**

### **APIs có thể soft delete (có trường isDeleted):**
- ✅ **Posts**: `/api/admin/posts/{id}` (DELETE) + `/api/admin/posts/{id}/restore` (PUT)
- ✅ **Comments**: `/api/admin/comments/{id}` (DELETE) + `/api/admin/comments/{id}/restore` (PUT)
- ✅ **Restaurants**: `/api/admin/restaurants/{id}` (DELETE) + `/api/admin/restaurants/{id}/restore` (PUT)
- ✅ **Tags**: `/api/admin/tags/{id}` (DELETE) + restore endpoint (nếu có)

### **APIs KHÔNG có soft delete (không có trường isDeleted):**
- ❌ **Users**: Chỉ có block/unblock, không có delete
- ❌ **Transactions**: Không có delete (chỉ xem và thống kê)

---

## 🔧 **FRONTEND IMPLEMENTATION GUIDE**

### **1. API Service Layer:**
```typescript
class AdminAPIService {
  private baseURL = '/api/admin';
  private token = localStorage.getItem('admin_token');
  
  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<ResponseDto<T>> {
    const response = await fetch(`${this.baseURL}${endpoint}`, {
      ...options,
      headers: {
        'Authorization': `Bearer ${this.token}`,
        'Content-Type': 'application/json',
        ...options.headers
      }
    });
    
    return response.json();
  }
  
  // Users
  async getUsers(): Promise<ResponseDto<ListAccountResponse[]>> {
    return this.request<ListAccountResponse[]>('/user');
  }
  
  async blockUser(userId: string): Promise<ResponseDto<void>> {
    return this.request<void>(`/block-user/${userId}`, { method: 'PUT' });
  }
  
  async activeUser(userId: string): Promise<ResponseDto<void>> {
    return this.request<void>(`/active-user/${userId}`, { method: 'PUT' });
  }
  
  // Posts
  async getPosts(page = 0, size = 10): Promise<ResponseDto<Page<AdminPostResponse>>> {
    return this.request<Page<AdminPostResponse>>(`/posts?page=${page}&size=${size}`);
  }
  
  async getPostById(postId: string): Promise<ResponseDto<AdminPostResponse>> {
    return this.request<AdminPostResponse>(`/posts/${postId}`);
  }
  
  async deletePost(postId: string): Promise<ResponseDto<void>> {
    return this.request<void>(`/posts/${postId}`, { method: 'DELETE' });
  }
  
  async restorePost(postId: string): Promise<ResponseDto<void>> {
    return this.request<void>(`/posts/${postId}/restore`, { method: 'PUT' });
  }
  
  // Comments
  async getComments(page = 0, size = 10): Promise<ResponseDto<Page<AdminCommentResponse>>> {
    return this.request<Page<AdminCommentResponse>>(`/comments?page=${page}&size=${size}`);
  }
  
  async getCommentById(commentId: string): Promise<ResponseDto<AdminCommentResponse>> {
    return this.request<AdminCommentResponse>(`/comments/${commentId}`);
  }
  
  async deleteComment(commentId: string): Promise<ResponseDto<void>> {
    return this.request<void>(`/comments/${commentId}`, { method: 'DELETE' });
  }
  
  async restoreComment(commentId: string): Promise<ResponseDto<void>> {
    return this.request<void>(`/comments/${commentId}/restore`, { method: 'PUT' });
  }
  
  async getCommentsByPost(postId: string, page = 0, size = 10): Promise<ResponseDto<Page<AdminCommentResponse>>> {
    return this.request<Page<AdminCommentResponse>>(`/comments/post/${postId}?page=${page}&size=${size}`);
  }
  
  async getCommentStatistics(): Promise<ResponseDto<Record<string, any>>> {
    return this.request<Record<string, any>>('/comments/statistics');
  }
  
  // Restaurants
  async getRestaurants(page = 0, size = 10): Promise<ResponseDto<Page<AdminRestaurantResponse>>> {
    return this.request<Page<AdminRestaurantResponse>>(`/restaurants?page=${page}&size=${size}`);
  }
  
  async getRestaurantById(restaurantId: string): Promise<ResponseDto<AdminRestaurantResponse>> {
    return this.request<AdminRestaurantResponse>(`/restaurants/${restaurantId}`);
  }
  
  async deleteRestaurant(restaurantId: string): Promise<ResponseDto<void>> {
    return this.request<void>(`/restaurants/${restaurantId}`, { method: 'DELETE' });
  }
  
  async restoreRestaurant(restaurantId: string): Promise<ResponseDto<void>> {
    return this.request<void>(`/restaurants/${restaurantId}/restore`, { method: 'PUT' });
  }
  
  // Transactions
  async getTransactions(page = 0, size = 10): Promise<ResponseDto<Page<AdminTransactionResponse>>> {
    return this.request<Page<AdminTransactionResponse>>(`/transactions?page=${page}&size=${size}`);
  }
  
  async getTransactionById(transactionId: string): Promise<ResponseDto<AdminTransactionResponse>> {
    return this.request<AdminTransactionResponse>(`/transactions/${transactionId}`);
  }
  
  async getTransactionsByStatus(status: string, page = 0, size = 10): Promise<ResponseDto<Page<AdminTransactionResponse>>> {
    return this.request<Page<AdminTransactionResponse>>(`/transactions/status/${status}?page=${page}&size=${size}`);
  }
  
  // Tags
  async getTags(page = 0, size = 10): Promise<ResponseDto<Page<AdminTagResponse>>> {
    return this.request<Page<AdminTagResponse>>(`/tags?page=${page}&size=${size}`);
  }
  
  async getTagById(tagId: string): Promise<ResponseDto<AdminTagResponse>> {
    return this.request<AdminTagResponse>(`/tags/${tagId}`);
  }
  
  async createTag(name: string, description?: string): Promise<ResponseDto<AdminTagResponse>> {
    const params = new URLSearchParams({ name });
    if (description) params.append('description', description);
    return this.request<AdminTagResponse>(`/tags?${params.toString()}`, { method: 'POST' });
  }
  
  async updateTag(tagId: string, name: string, description?: string): Promise<ResponseDto<AdminTagResponse>> {
    const params = new URLSearchParams({ name });
    if (description) params.append('description', description);
    return this.request<AdminTagResponse>(`/tags/${tagId}?${params.toString()}`, { method: 'PUT' });
  }
  
  async deleteTag(tagId: string): Promise<ResponseDto<void>> {
    return this.request<void>(`/tags/${tagId}`, { method: 'DELETE' });
  }
  
  async getTagStatistics(): Promise<ResponseDto<Record<string, any>>> {
    return this.request<Record<string, any>>('/tags/statistics');
  }
  
  // Statistics
  async getSystemStatistics(): Promise<ResponseDto<StatisticsResponse>> {
    return this.request<StatisticsResponse>('/statistics');
  }
  
  async getUserStatistics(): Promise<ResponseDto<StatisticsResponse>> {
    return this.request<StatisticsResponse>('/statistics/users');
  }
  
  async getPostStatistics(): Promise<ResponseDto<StatisticsResponse>> {
    return this.request<StatisticsResponse>('/statistics/posts');
  }
  
  async getRestaurantStatistics(): Promise<ResponseDto<StatisticsResponse>> {
    return this.request<StatisticsResponse>('/statistics/restaurants');
  }
  
  async getRevenueStatistics(): Promise<ResponseDto<StatisticsResponse>> {
    return this.request<StatisticsResponse>('/statistics/revenue');
  }
  
  async getEngagementStatistics(): Promise<ResponseDto<StatisticsResponse>> {
    return this.request<StatisticsResponse>('/statistics/engagement');
  }
  
  async getChallengeStatistics(): Promise<ResponseDto<StatisticsResponse>> {
    return this.request<StatisticsResponse>('/statistics/challenges');
  }
}
```

### **2. TypeScript Interfaces:**
```typescript
// Base response structure
interface ResponseDto<T> {
  status: number;
  message: string;
  data: T;
}

// Pagination structure
interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// All DTO interfaces are defined above in each section
```

---

## 📞 **SUPPORT & CONTACT**

- **Backend API**: `http://localhost:8080/api/admin`
- **Swagger Documentation**: `http://localhost:8080/swagger-ui/`
- **Database**: MongoDB
- **Authentication**: JWT Bearer Token

---

**📝 Last Updated**: 2025-01-25  
**👤 Created by**: AI Assistant  
**🏷️ Version**: 2.0.0  
**📋 Status**: ✅ Complete - Ready for Frontend Implementation
