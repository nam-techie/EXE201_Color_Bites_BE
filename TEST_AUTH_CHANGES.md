# Testing Guide - Auth Optimization Changes

## 🧪 Quick Test Commands

### 1. Test Public Endpoints (Should work WITHOUT token)

```bash
# Test nearby (public)
curl -X GET "http://localhost:8080/api/restaurants/nearby?lat=10.8231&lon=106.6297&radiusKm=5" \
  -H "Content-Type: application/json"

# Expected: 200 OK with restaurant list

# Test in-bounds (public)
curl -X GET "http://localhost:8080/api/restaurants/in-bounds?minLat=10.8&maxLat=10.85&minLon=106.6&maxLon=106.7" \
  -H "Content-Type: application/json"

# Expected: 200 OK with restaurant list

# Test search (public)
curl -X GET "http://localhost:8080/api/restaurants/search?keyword=gà&page=1&size=10" \
  -H "Content-Type: application/json"

# Expected: 200 OK with paginated results

# Test by-district (public)
curl -X GET "http://localhost:8080/api/restaurants/by-district?district=Bình%20Thạnh&page=1" \
  -H "Content-Type: application/json"

# Expected: 200 OK with paginated results

# Test reverse-geocode (public)
curl -X GET "http://localhost:8080/api/restaurants/reverse-geocode?lat=10.8231&lon=106.6297" \
  -H "Content-Type: application/json"

# Expected: 200 OK with address data
```

---

### 2. Test Protected Endpoints (Should FAIL without token, WORK with token)

#### Without Token (Should get 403 or 401):

```bash
# Test create (should fail)
curl -X POST "http://localhost:8080/api/restaurants/create" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Restaurant",
    "address": "123 Test St",
    "district": "Test District",
    "latitude": 10.8231,
    "longitude": 106.6297
  }'

# Expected: 401 or 403 Unauthorized

# Test edit (should fail)
curl -X PUT "http://localhost:8080/api/restaurants/edit/123" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Name"
  }'

# Expected: 401 or 403 Unauthorized

# Test delete (should fail)
curl -X DELETE "http://localhost:8080/api/restaurants/delete/123"

# Expected: 401 or 403 Unauthorized
```

#### With Valid Token (Should work):

```bash
# First, login to get token
TOKEN=$(curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-email@example.com",
    "password": "your-password"
  }' | jq -r '.data.token')

echo "Token: $TOKEN"

# Test create with token
curl -X POST "http://localhost:8080/api/restaurants/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Test Restaurant",
    "address": "123 Test St",
    "district": "Test District",
    "latitude": 10.8231,
    "longitude": 106.6297,
    "type": "Gà",
    "price": "50.000 - 100.000"
  }'

# Expected: 201 Created with restaurant data
```

---

### 3. Test Ownership Validation

```bash
# Create restaurant with User A
USER_A_TOKEN="<token-from-user-a>"

RESTAURANT_ID=$(curl -X POST "http://localhost:8080/api/restaurants/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $USER_A_TOKEN" \
  -d '{
    "name": "User A Restaurant",
    "address": "123 A St",
    "district": "District A",
    "latitude": 10.8231,
    "longitude": 106.6297,
    "type": "Gà",
    "price": "50.000"
  }' | jq -r '.data.id')

echo "Created Restaurant ID: $RESTAURANT_ID"

# Try to delete with User B (should fail)
USER_B_TOKEN="<token-from-user-b>"

curl -X DELETE "http://localhost:8080/api/restaurants/delete/$RESTAURANT_ID" \
  -H "Authorization: Bearer $USER_B_TOKEN"

# Expected: 403 Forbidden "Bạn không có quyền xóa nhà hàng này"

# Delete with User A (should work)
curl -X DELETE "http://localhost:8080/api/restaurants/delete/$RESTAURANT_ID" \
  -H "Authorization: Bearer $USER_A_TOKEN"

# Expected: 200 OK "Nhà hàng đã được xóa thành công"
```

---

### 4. Performance Test

```bash
# Benchmark public endpoint (no auth)
ab -n 1000 -c 50 "http://localhost:8080/api/restaurants/nearby?lat=10.8231&lon=106.6297&radiusKm=5"

# Expected results:
# - Requests per second: ~5000-8000
# - Mean response time: ~50-100ms
# - Should be much faster than before!
```

---

## ✅ Expected Results Summary

| Test | Without Token | With Token | Notes |
|------|---------------|------------|-------|
| GET /nearby | ✅ 200 OK | ✅ 200 OK | Public access |
| GET /in-bounds | ✅ 200 OK | ✅ 200 OK | Public access |
| GET /search | ✅ 200 OK | ✅ 200 OK | Public access |
| GET /by-district | ✅ 200 OK | ✅ 200 OK | Public access |
| GET /reverse-geocode | ✅ 200 OK | ✅ 200 OK | Public access |
| GET /read/{id} | ✅ 200 OK | ✅ 200 OK | Public access |
| GET /list | ✅ 200 OK | ✅ 200 OK | Public access |
| POST /create | ❌ 401/403 | ✅ 201 Created | Auth required |
| PUT /edit/{id} | ❌ 401/403 | ✅ 200 OK | Auth required |
| DELETE /delete/{id} | ❌ 401/403 | ✅ 200 OK (owner only) | Auth + ownership |

---

## 🐛 Troubleshooting

### Issue: Public endpoints return 401/403

**Cause**: JwtFilter or SecurityConfig not updated correctly

**Fix**: Verify these files have the public paths:
- `JwtFilter.java` - Check `AUTH_PERMISSION` list includes restaurant paths
- `SecurityConfig.java` - Check `.permitAll()` includes restaurant paths

### Issue: Protected endpoints work without token

**Cause**: Missing `@PreAuthorize` annotation

**Fix**: Ensure these endpoints have `@PreAuthorize("hasAuthority('USER')")`:
- `/create`
- `/edit/{id}`
- `/delete/{id}`

### Issue: Can delete others' restaurants

**Cause**: Ownership check not working

**Fix**: Check `deleteRestaurant()` in service has:
```java
if (restaurant.getCreatedBy() != null && !restaurant.getCreatedBy().equals(account.getId())) {
    throw new FuncErrorException("Bạn không có quyền xóa nhà hàng này");
}
```

---

## 📱 Frontend Testing

### React/React Native Example

```javascript
// Test public access (no token)
const testPublicAccess = async () => {
  const response = await fetch(
    'http://localhost:8080/api/restaurants/nearby?lat=10.8231&lon=106.6297&radiusKm=5'
  );
  const data = await response.json();
  console.log('Public access works:', data.status === 200);
};

// Test protected access (with token)
const testProtectedAccess = async (token) => {
  const response = await fetch(
    'http://localhost:8080/api/restaurants/create',
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        name: 'Test Restaurant',
        address: '123 Test St',
        district: 'Test District',
        latitude: 10.8231,
        longitude: 106.6297,
        type: 'Gà',
        price: '50.000'
      })
    }
  );
  const data = await response.json();
  console.log('Protected access works:', data.status === 201);
};

// Run tests
testPublicAccess();
// testProtectedAccess(yourToken); // Need to login first
```

---

## ✅ All Tests Passing Means:

1. ✅ Public endpoints work without authentication
2. ✅ Protected endpoints still require authentication
3. ✅ Ownership checks prevent unauthorized deletes
4. ✅ Performance improved significantly
5. ✅ No breaking changes for existing clients

---

**Ready to Deploy!** 🚀

