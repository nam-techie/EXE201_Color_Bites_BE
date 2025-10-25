# 📊 Color Bites Admin API Documentation

## 🎯 Tổng quan

Tài liệu này cung cấp đầy đủ thông tin về các API admin của hệ thống Color Bites Backend, bao gồm tất cả endpoints, request/response examples, và hướng dẫn implement cho Frontend.

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

## 📋 Tổng hợp TẤT CẢ API Admin

### 🏠 **Base URL**: `/api/admin`

---

## 👥 **1. USER MANAGEMENT**

### **1.1 Lấy danh sách tất cả users**
```http
GET /api/admin/user
Authorization: Bearer {jwt_token}
```

**Response:**
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

### **1.3 Kích hoạt lại user**
```http
PUT /api/admin/active-user/{userId}
Authorization: Bearer {jwt_token}
```

---

## 📝 **2. POST MANAGEMENT**

### **2.1 Lấy danh sách bài viết (phân trang)**
```http
GET /api/admin/posts?page=0&size=10
Authorization: Bearer {jwt_token}
```

**Response:**
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

### **2.3 Xóa bài viết (soft delete)**
```http
DELETE /api/admin/posts/{postId}
Authorization: Bearer {jwt_token}
```

### **2.4 Khôi phục bài viết đã xóa**
```http
PUT /api/admin/posts/{postId}/restore
Authorization: Bearer {jwt_token}
```

---

## 💬 **3. COMMENT MANAGEMENT** ⭐ **MỚI**

### **3.1 Lấy danh sách tất cả comments**
```http
GET /api/admin/comments?page=0&size=10
Authorization: Bearer {jwt_token}
```

**Response:**
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

### **3.3 Xóa comment**
```http
DELETE /api/admin/comments/{commentId}
Authorization: Bearer {jwt_token}
```

### **3.4 Khôi phục comment đã xóa**
```http
PUT /api/admin/comments/{commentId}/restore
Authorization: Bearer {jwt_token}
```

### **3.5 Lấy comments theo bài viết**
```http
GET /api/admin/comments/post/{postId}?page=0&size=10
Authorization: Bearer {jwt_token}
```

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

**Response:**
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

### **4.3 Xóa nhà hàng (soft delete)**
```http
DELETE /api/admin/restaurants/{restaurantId}
Authorization: Bearer {jwt_token}
```

### **4.4 Khôi phục nhà hàng đã xóa**
```http
PUT /api/admin/restaurants/{restaurantId}/restore
Authorization: Bearer {jwt_token}
```

---

## 💳 **5. TRANSACTION/PAYMENT MANAGEMENT**

### **5.1 Lấy danh sách giao dịch (phân trang)**
```http
GET /api/admin/transactions?page=0&size=10
Authorization: Bearer {jwt_token}
```

**Response:**
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

### **5.3 Lấy giao dịch theo trạng thái**
```http
GET /api/admin/transactions/status/{status}?page=0&size=10
Authorization: Bearer {jwt_token}
```

**Status Values**: `SUCCESS`, `PENDING`, `FAILED`, `CANCELED`

---

## 🏷️ **6. TAG MANAGEMENT** ⭐ **MỚI**

### **6.1 Lấy danh sách tất cả tags**
```http
GET /api/admin/tags?page=0&size=10
Authorization: Bearer {jwt_token}
```

**Response:**
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

### **6.3 Tạo tag mới**
```http
POST /api/admin/tags?name=New Tag&description=Tag description
Authorization: Bearer {jwt_token}
```

### **6.4 Cập nhật tag**
```http
PUT /api/admin/tags/{tagId}?name=Updated Tag&description=Updated description
Authorization: Bearer {jwt_token}
```

### **6.5 Xóa tag**
```http
DELETE /api/admin/tags/{tagId}
Authorization: Bearer {jwt_token}
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

## 📊 **7. STATISTICS & ANALYTICS** ⭐ **MỚI**

### **7.1 Thống kê tổng quan hệ thống**
```http
GET /api/admin/statistics
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Lấy thống kê hệ thống thành công",
  "data": {
    "totalUsers": 1000,
    "activeUsers": 950,
    "blockedUsers": 50,
    "totalPosts": 5000,
    "deletedPosts": 100,
    "activePosts": 4900,
    "totalRestaurants": 500,
    "deletedRestaurants": 10,
    "activeRestaurants": 490,
    "totalTransactions": 2000
  }
}
```

### **7.2 Thống kê users**
```http
GET /api/admin/statistics/users
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Lấy thống kê users thành công",
  "data": {
    "totalUsers": 1000,
    "activeUsers": 950
  }
}
```

### **7.3 Thống kê posts**
```http
GET /api/admin/statistics/posts
Authorization: Bearer {jwt_token}
```

### **7.4 Thống kê restaurants**
```http
GET /api/admin/statistics/restaurants
Authorization: Bearer {jwt_token}
```

### **7.5 Thống kê doanh thu**
```http
GET /api/admin/statistics/revenue
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Lấy thống kê doanh thu thành công",
  "data": {
    "totalTransactions": 2000,
    "successfulTransactions": 1800,
    "failedTransactions": 100,
    "pendingTransactions": 100
  }
}
```

### **7.6 Thống kê tương tác**
```http
GET /api/admin/statistics/engagement
Authorization: Bearer {jwt_token}
```

### **7.7 Thống kê challenges**
```http
GET /api/admin/statistics/challenges
Authorization: Bearer {jwt_token}
```

---

## 🎭 **8. MOOD MANAGEMENT**

### **8.1 Lấy danh sách moods**
```http
GET /api/moods/list?page=0&size=10
Authorization: Bearer {jwt_token}
```

### **8.2 Tạo mood mới (ADMIN only)**
```http
POST /api/moods/create
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "name": "Excited",
  "description": "Cảm xúc phấn khích"
}
```

### **8.3 Cập nhật mood (ADMIN only)**
```http
PUT /api/moods/edit/{moodId}
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "name": "Very Excited",
  "description": "Cảm xúc rất phấn khích"
}
```

### **8.4 Xóa mood (ADMIN only)**
```http
DELETE /api/moods/delete/{moodId}
Authorization: Bearer {jwt_token}
```

---

## 🏆 **9. CHALLENGE MANAGEMENT**

### **9.1 Lấy danh sách thử thách**
```http
GET /api/challenges?page=0&size=10
Authorization: Bearer {jwt_token}
```

### **9.2 Tạo thử thách mới**
```http
POST /api/challenges
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "title": "Thử thách ăn uống",
  "description": "Ăn 10 món mới trong tháng",
  "type": "FOOD_CHALLENGE",
  "restaurantId": "restaurant_id_1"
}
```

### **9.3 Cập nhật thử thách**
```http
PUT /api/challenges/{challengeId}
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "title": "Thử thách ăn uống cập nhật",
  "description": "Ăn 15 món mới trong tháng"
}
```

### **9.4 Xóa thử thách**
```http
DELETE /api/challenges/{challengeId}
Authorization: Bearer {jwt_token}
```

### **9.5 Kích hoạt thử thách**
```http
PUT /api/challenges/{challengeId}/activate
Authorization: Bearer {jwt_token}
```

### **9.6 Vô hiệu hóa thử thách**
```http
PUT /api/challenges/{challengeId}/deactivate
Authorization: Bearer {jwt_token}
```

### **9.7 Duyệt bài nộp thử thách**
```http
PUT /api/challenges/entries/{entryId}/approve
Authorization: Bearer {jwt_token}
```

### **9.8 Từ chối bài nộp thử thách**
```http
PUT /api/challenges/entries/{entryId}/reject
Authorization: Bearer {jwt_token}
```

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

💬 Comments Management ⭐ MỚI
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

🎭 Moods Management
├── 😊 All Moods
├── ➕ Create Mood
├── ✏️ Edit Moods
└── 📊 Mood Usage

🏆 Challenges Management
├── 🎯 All Challenges
├── ➕ Create Challenge
├── ✏️ Edit Challenges
├── 📝 Challenge Entries
└── 📊 Challenge Analytics

🏷️ Tags Management ⭐ MỚI
├── 🏷️ All Tags
├── ➕ Create Tag
├── ✏️ Edit Tags
├── 📊 Tag Statistics
└── 🔍 Search Tags

📈 Statistics & Reports ⭐ MỚI
├── 👥 User Analytics
├── 📝 Post Analytics
├── 🍽️ Restaurant Analytics
├── 💰 Revenue Reports
├── 💬 Engagement Analytics
└── 📊 System Health

⚙️ Settings
├── 🔧 System Settings
├── 🔐 Security Settings
└── 📧 Notification Settings
```

---

## 🚀 **PROMPT CHO FRONTEND DEVELOPER**

### **Yêu cầu implement trang Admin Dashboard:**

**1. Authentication Setup:**
```javascript
// Tạo admin context để quản lý authentication
const AdminContext = createContext();

// Admin login component
const AdminLogin = () => {
  const [credentials, setCredentials] = useState({ username: '', password: '' });
  
  const handleLogin = async () => {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials)
    });
    
    const data = await response.json();
    if (data.status === 200) {
      localStorage.setItem('admin_token', data.data.token);
      // Redirect to admin dashboard
    }
  };
};
```

**2. API Service Layer:**
```javascript
// Tạo admin API service
class AdminAPIService {
  constructor() {
    this.baseURL = '/api/admin';
    this.token = localStorage.getItem('admin_token');
  }
  
  async request(endpoint, options = {}) {
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
  async getUsers() {
    return this.request('/user');
  }
  
  async blockUser(userId) {
    return this.request(`/block-user/${userId}`, { method: 'PUT' });
  }
  
  // Posts
  async getPosts(page = 0, size = 10) {
    return this.request(`/posts?page=${page}&size=${size}`);
  }
  
  async deletePost(postId) {
    return this.request(`/posts/${postId}`, { method: 'DELETE' });
  }
  
  // Comments (MỚI)
  async getComments(page = 0, size = 10) {
    return this.request(`/comments?page=${page}&size=${size}`);
  }
  
  async deleteComment(commentId) {
    return this.request(`/comments/${commentId}`, { method: 'DELETE' });
  }
  
  // Tags (MỚI)
  async getTags(page = 0, size = 10) {
    return this.request(`/tags?page=${page}&size=${size}`);
  }
  
  async createTag(name, description) {
    return this.request(`/tags?name=${encodeURIComponent(name)}&description=${encodeURIComponent(description)}`, {
      method: 'POST'
    });
  }
  
  // Statistics
  async getSystemStatistics() {
    return this.request('/statistics');
  }
  
  async getUserStatistics() {
    return this.request('/statistics/users');
  }
}
```

**3. Dashboard Components:**
```javascript
// Dashboard overview component
const DashboardOverview = () => {
  const [stats, setStats] = useState(null);
  
  useEffect(() => {
    const fetchStats = async () => {
      const response = await adminAPI.getSystemStatistics();
      setStats(response.data);
    };
    fetchStats();
  }, []);
  
  return (
    <div className="dashboard-grid">
      <StatCard title="Total Users" value={stats?.totalUsers} />
      <StatCard title="Active Users" value={stats?.activeUsers} />
      <StatCard title="Total Posts" value={stats?.totalPosts} />
      <StatCard title="Total Revenue" value={stats?.totalRevenue} />
    </div>
  );
};
```

**4. Data Tables:**
```javascript
// Reusable data table component
const AdminDataTable = ({ 
  title, 
  columns, 
  data, 
  onEdit, 
  onDelete, 
  onRestore,
  pagination 
}) => {
  return (
    <div className="data-table">
      <h2>{title}</h2>
      <table>
        <thead>
          <tr>
            {columns.map(col => <th key={col.key}>{col.title}</th>)}
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {data.map(item => (
            <tr key={item.id}>
              {columns.map(col => (
                <td key={col.key}>{item[col.key]}</td>
              ))}
              <td>
                <button onClick={() => onEdit(item.id)}>Edit</button>
                <button onClick={() => onDelete(item.id)}>Delete</button>
                {item.isDeleted && (
                  <button onClick={() => onRestore(item.id)}>Restore</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <Pagination {...pagination} />
    </div>
  );
};
```

**5. Navigation Structure:**
```javascript
const AdminSidebar = () => {
  const menuItems = [
    { icon: '📊', label: 'Dashboard', path: '/admin/dashboard' },
    { icon: '👥', label: 'Users', path: '/admin/users' },
    { icon: '📝', label: 'Posts', path: '/admin/posts' },
    { icon: '💬', label: 'Comments', path: '/admin/comments' },
    { icon: '🍽️', label: 'Restaurants', path: '/admin/restaurants' },
    { icon: '💳', label: 'Transactions', path: '/admin/transactions' },
    { icon: '🎭', label: 'Moods', path: '/admin/moods' },
    { icon: '🏆', label: 'Challenges', path: '/admin/challenges' },
    { icon: '🏷️', label: 'Tags', path: '/admin/tags' },
    { icon: '📈', label: 'Statistics', path: '/admin/statistics' }
  ];
  
  return (
    <nav className="admin-sidebar">
      {menuItems.map(item => (
        <NavLink key={item.path} to={item.path}>
          <span className="icon">{item.icon}</span>
          <span className="label">{item.label}</span>
        </NavLink>
      ))}
    </nav>
  );
};
```

**6. Error Handling:**
```javascript
const useAdminAPI = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  const apiCall = async (apiFunction) => {
    try {
      setLoading(true);
      setError(null);
      const result = await apiFunction();
      return result;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };
  
  return { apiCall, loading, error };
};
```

---

## 🔧 **TECHNICAL REQUIREMENTS**

### **Frontend Stack:**
- **Framework**: React/Vue/Angular
- **State Management**: Redux/Vuex/NgRx
- **UI Library**: Material-UI/Ant Design/Element UI
- **Charts**: Chart.js/Recharts/D3.js
- **HTTP Client**: Axios/Fetch API

### **Key Features to Implement:**
1. **Responsive Design** - Mobile-friendly admin panel
2. **Real-time Updates** - WebSocket cho live statistics
3. **Data Export** - Export reports to CSV/PDF
4. **Search & Filter** - Advanced filtering cho tất cả tables
5. **Bulk Actions** - Select multiple items để thao tác hàng loạt
6. **Audit Logs** - Track admin actions
7. **Role-based Access** - Different permissions cho different admin levels

### **Performance Considerations:**
- **Pagination** - Tất cả danh sách phải có pagination
- **Lazy Loading** - Load data khi cần
- **Caching** - Cache API responses
- **Debouncing** - Cho search inputs
- **Virtual Scrolling** - Cho large datasets

---

## 📱 **MOBILE RESPONSIVE DESIGN**

```css
/* Mobile-first responsive design */
@media (max-width: 768px) {
  .admin-sidebar {
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }
  
  .admin-sidebar.open {
    transform: translateX(0);
  }
  
  .data-table {
    overflow-x: auto;
  }
  
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
```

---

## 🎯 **IMPLEMENTATION CHECKLIST**

### **Phase 1: Core Setup**
- [ ] Authentication system
- [ ] Admin routing
- [ ] Basic layout với sidebar
- [ ] API service layer

### **Phase 2: Basic CRUD**
- [ ] Users management
- [ ] Posts management
- [ ] Restaurants management
- [ ] Transactions management

### **Phase 3: Advanced Features**
- [ ] Comments management ⭐ MỚI
- [ ] Tags management ⭐ MỚI
- [ ] Advanced statistics ⭐ MỚI
- [ ] Moods management
- [ ] Challenges management

### **Phase 4: Polish**
- [ ] Search & filtering
- [ ] Bulk actions
- [ ] Export functionality
- [ ] Mobile optimization
- [ ] Error handling
- [ ] Loading states

---

## 🚨 **ERROR HANDLING**

### **Common Error Responses:**
```json
// 401 Unauthorized
{
  "status": 401,
  "message": "Chưa xác thực",
  "data": null
}

// 403 Forbidden
{
  "status": 403,
  "message": "Không có quyền truy cập",
  "data": null
}

// 404 Not Found
{
  "status": 404,
  "message": "Không tìm thấy dữ liệu",
  "data": null
}

// 500 Internal Server Error
{
  "status": 500,
  "message": "Lỗi hệ thống",
  "data": null
}
```

### **Error Handling Strategy:**
```javascript
const handleAPIError = (error) => {
  if (error.status === 401) {
    // Redirect to login
    window.location.href = '/admin/login';
  } else if (error.status === 403) {
    // Show access denied message
    showNotification('Không có quyền truy cập', 'error');
  } else {
    // Show generic error
    showNotification('Đã xảy ra lỗi', 'error');
  }
};
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
**🏷️ Version**: 1.0.0  
**📋 Status**: ✅ Complete - Ready for Frontend Implementation
