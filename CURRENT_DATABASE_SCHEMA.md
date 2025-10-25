# Database Schema - Color Bites Backend (Current Implementation)

## Enums
```sql
-- All enums match the provided schema
ChallengeType: PARTNER_LOCATION, THEME_COUNT
ParticipationStatus: IN_PROGRESS, COMPLETED, FAILED
EntryStatus: PENDING, APPROVED, REJECTED
Role: USER, ADMIN, PARTNER
SubcriptionPlan: FREE, PREMIUM
LoginMethod: GOOGLE, USERNAME
Gender: MALE, FEMALE, OTHER
Visibility: PRIVATE, PUBLIC, FRIENDS
ChallengeStatus: ACTIVE, COMPLETED, EXPIRED
ReactionType: LOVE
CurrencyCode: VND, USD
TxnStatus: PENDING, SUCCESS, FAILED, CANCELED, REFUNDED
TxnType: PAYMENT, REFUND, SUBSCRIPTION
FriendStatus: PENDING, ACCEPTED
```

## Current Database Schema (MongoDB Collections)

### Core User Management
```javascript
// accounts collection
{
  _id: ObjectId,
  user_name: String (unique, indexed),
  email: String (unique, indexed),
  password: String,
  role: Role,
  is_active: Boolean (default: true),
  login_method: LoginMethod,
  google_id: String (unique, indexed),
  created_at: Date,
  updated_at: Date
}

// user_information collection
{
  _id: ObjectId,
  account: ObjectId (ref: accounts._id), // DBRef
  gender: Gender,
  avatar_url: String,
  subscription_plan: SubcriptionPlan,
  bio: String,
  created_at: Date,
  updated_at: Date
}
```

### Restaurant & Location
```javascript
// restaurants collection
{
  _id: ObjectId,
  name: String,
  address: String,
  longitude: BigDecimal,
  latitude: BigDecimal,
  location: GeoJsonPoint (2dsphere index),
  region: String,
  avg_price: Double,
  rating: Double,
  featured: Boolean (indexed),
  created_by: String (indexed), // account_id
  created_at: Date,
  updated_at: Date,
  is_deleted: Boolean (indexed)
}

// food_types collection
{
  _id: ObjectId,
  name: String (unique, indexed),
  created_at: Date
}

// restaurant_food_types collection (many-to-many)
{
  _id: ObjectId,
  restaurant_id: ObjectId (ref: restaurants._id),
  food_type_id: ObjectId (ref: food_types._id)
}

// restaurant_images collection
{
  _id: ObjectId,
  restaurant_id: ObjectId (ref: restaurants._id),
  url: String,
  sort_order: Number,
  created_at: Date
}

// tags collection
{
  _id: ObjectId,
  name: String (unique, indexed),
  usage_count: Number (default: 0),
  created_at: Date
}

// restaurant_tags collection (many-to-many)
{
  _id: ObjectId,
  restaurant_id: ObjectId (ref: restaurants._id),
  tag_id: ObjectId (ref: tags._id)
}
```

### Friends System
```javascript
// friendships collection
{
  _id: ObjectId,
  user_a: String (ref: accounts._id), // smaller id
  user_b: String (ref: accounts._id), // larger id
  status: FriendStatus (indexed),
  requested_by: String (ref: accounts._id),
  created_at: Date,
  updated_at: Date
}
```

### Content & Social Features
```javascript
// posts collection
{
  _id: ObjectId,
  account_id: String (ref: accounts._id, indexed),
  content: String,
  mood_id: ObjectId (ref: moods._id, indexed),
  video_url: String,
  reaction_count: Number (default: 0),
  comment_count: Number (default: 0),
  is_deleted: Boolean (indexed),
  created_at: Date (indexed),
  updated_at: Date
}

// post_images collection
{
  _id: ObjectId,
  post_id: ObjectId (ref: posts._id),
  url: String,
  created_at: Date
}

// moods collection
{
  _id: ObjectId,
  name: String (unique, indexed),
  emoji: String,
  created_at: Date
}

// comments collection
{
  _id: ObjectId,
  post_id: ObjectId (ref: posts._id, indexed),
  account_id: String (ref: accounts._id, indexed),
  parent_comment_id: ObjectId (ref: comments._id),
  depth: Number (default: 0),
  content: String,
  is_deleted: Boolean (indexed),
  created_at: Date,
  updated_at: Date
}

// reactions collection
{
  _id: ObjectId,
  post_id: ObjectId (ref: posts._id, indexed),
  account_id: String (ref: accounts._id, indexed),
  reaction: ReactionType,
  created_at: Date
}

// post_tags collection (many-to-many)
{
  _id: ObjectId,
  post_id: ObjectId (ref: posts._id),
  tag_id: ObjectId (ref: tags._id)
}
```

### User Preferences
```javascript
// favorites collection
{
  _id: ObjectId,
  account_id: String (ref: accounts._id, indexed),
  restaurant_id: ObjectId (ref: restaurants._id, indexed),
  created_at: Date
}
```

### Mood & Personalization
```javascript
// mood_maps collection
{
  _id: ObjectId,
  account_id: String (ref: accounts._id, indexed),
  title: String,
  entries: Object, // JSON
  visibility: Visibility (indexed),
  exported: Boolean (default: false),
  export_url: String,
  created_at: Date,
  is_deleted: Boolean (indexed)
}

// quizzes collection
{
  _id: ObjectId,
  account_id: String (ref: accounts._id, indexed),
  answers: Object, // JSON
  mood_result: String,
  recommended_foods: Object, // JSON array
  recommended_restaurants: Object, // JSON array
  created_at: Date
}
```

### Gamification (Challenges)
```javascript
// challenge_definitions collection
{
  _id: ObjectId,
  title: String,
  description: String,
  challenge_type: ChallengeType (indexed),
  restaurant_id: String, // for PARTNER_LOCATION
  food_type_id: String, // for THEME_COUNT (ref to food_types)
  target_count: Number,
  start_date: Date (indexed),
  end_date: Date (indexed),
  reward_description: String,
  created_by: String (ref: accounts._id),
  created_at: Date,
  is_active: Boolean (indexed)
}

// challenge_participations collection
{
  _id: ObjectId,
  account_id: String (ref: accounts._id, indexed),
  challenge_id: ObjectId (ref: challenge_definitions._id, indexed),
  status: ParticipationStatus (indexed),
  progress_count: Number (default: 0),
  completed_at: Date,
  created_at: Date
}

// challenge_entries collection
{
  _id: ObjectId,
  participation_id: ObjectId (ref: challenge_participations._id, indexed),
  restaurant_id: ObjectId (ref: restaurants._id, indexed),
  photo_url: String,
  latitude: BigDecimal,
  longitude: BigDecimal,
  status: EntryStatus (indexed),
  notes: String,
  created_at: Date
}
```

### Business Features
```javascript
// promotions collection
{
  _id: ObjectId,
  restaurant_id: ObjectId (ref: restaurants._id, indexed),
  title: String,
  description: String,
  start_date: Date (indexed),
  end_date: Date (indexed),
  price: Number,
  type: String,
  link: String,
  created_at: Date
}

// transactions collection
{
  _id: ObjectId,
  account_id: String (ref: accounts._id, indexed),
  amount: Double, // Note: Should be BigDecimal for precision
  currency: CurrencyCode,
  type: TxnType,
  status: TxnStatus (indexed),
  metadata: Object, // JSON
  order_code: String (unique),
  plan: SubcriptionPlan,
  gateway: String,
  provider_txn_id: String (unique, sparse),
  raw_payload: Object, // JSON
  created_at: Date,
  updated_at: Date
}

// subscriptions collection
{
  _id: ObjectId,
  account_id: String (ref: accounts._id, indexed),
  plan: SubcriptionPlan,
  status: SubscriptionStatus,
  start_date: Date,
  end_date: Date,
  created_at: Date,
  updated_at: Date
}
```

## Key Differences from Original Schema

1. **Restaurant Types**: Using many-to-many relationship with `food_types` instead of JSON array
2. **Challenge Type Objects**: Using `food_type_id` reference instead of JSON object
3. **Transaction Amount**: Using `Double` instead of `BigDecimal`
4. **Missing**: `type_objects` collection (global type catalog)
5. **Additional**: `subscriptions` collection for subscription management

## Recommendations

1. **Add `type_objects` collection** for global type catalog
2. **Change Transaction.amount** from `Double` to `BigDecimal` for precision
3. **Consider adding `types_json`** to restaurants for snapshot approach
4. **Add proper validation** for challenge type constraints
