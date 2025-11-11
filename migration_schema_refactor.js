// Migration Script: Refactor Entity Schema
// File: migration_schema_refactor.js
// Purpose: Migrate existing data to new schema structure
// 
// IMPORTANT: 
// 1. Backup your database before running this script
// 2. Test with sample data first
// 3. Run this script in MongoDB shell or MongoDB Compass

print("Starting schema refactor migration...");

// ============================================
// STEP 1: BACKUP COLLECTIONS
// ============================================
print("Step 1: Creating backup collections...");

// Backup restaurants collection
db.restaurants.aggregate([
    { $out: "restaurants_backup_" + new Date().getTime() }
]);

// Backup challenge_definitions collection  
db.challenge_definitions.aggregate([
    { $out: "challenge_definitions_backup_" + new Date().getTime() }
]);

// Backup subscriptions collection
db.subscriptions.aggregate([
    { $out: "subscriptions_backup_" + new Date().getTime() }
]);

print("Backup collections created successfully");

// ============================================
// STEP 2: MIGRATE RESTAURANTS COLLECTION
// ============================================
print("Step 2: Migrating restaurants collection...");

// Get all restaurants
const restaurants = db.restaurants.find({}).toArray();

restaurants.forEach(restaurant => {
    print(`Migrating restaurant: ${restaurant.name}`);
    
    // NOTE: FoodType migration removed - restaurants now use embedded TypeObject JSON
    // If you have existing restaurant_food_types data, you need to manually migrate to types array
    
    // 2. Migrate restaurant_images to images JSON array
    const restaurantImages = db.restaurant_images.find({restaurant_id: restaurant._id}).toArray();
    const images = [];
    
    restaurantImages.forEach(img => {
        images.push({
            url: img.url,
            sort_order: img.sort_order || 0
        });
    });
    
    // 3. Convert avg_price (Double) to price (String)
    let priceString = "";
    if (restaurant.avg_price !== undefined && restaurant.avg_price !== null) {
        if (restaurant.avg_price < 1000) {
            priceString = `${restaurant.avg_price}k`;
        } else if (restaurant.avg_price < 1000000) {
            priceString = `${Math.round(restaurant.avg_price / 1000)}k`;
        } else {
            priceString = `${Math.round(restaurant.avg_price / 1000000)}M`;
        }
    }
    
    // 4. Update restaurant document
    db.restaurants.updateOne(
        { _id: restaurant._id },
        {
            $set: {
                images: images,
                price: priceString,
                district: restaurant.region || restaurant.district || ""
            },
            $unset: {
                avg_price: "",
                region: ""
            }
        }
    );
});

print(`Migrated ${restaurants.length} restaurants`);

// ============================================
// STEP 3: MIGRATE CHALLENGE_DEFINITIONS COLLECTION
// ============================================
print("Step 3: Migrating challenge_definitions collection...");

const challenges = db.challenge_definitions.find({}).toArray();

challenges.forEach(challenge => {
    print(`Migrating challenge: ${challenge.title}`);
    
    // NOTE: FoodType migration removed - challenges now use embedded TypeObject JSON
    // If you have existing food_type_id data, you need to manually migrate to type_obj
    
    // Update challenge document
    db.challenge_definitions.updateOne(
        { _id: challenge._id },
        {
            $set: {
                images: [] // Empty array for now
            },
            $unset: {
                food_type_id: ""
            }
        }
    );
});

print(`Migrated ${challenges.length} challenges`);

// ============================================
// STEP 4: MIGRATE SUBSCRIPTIONS COLLECTION
// ============================================
print("Step 4: Migrating subscriptions collection...");

const subscriptions = db.subscriptions.find({}).toArray();

subscriptions.forEach(subscription => {
    print(`Migrating subscription: ${subscription._id}`);
    
    // Update field names
    db.subscriptions.updateOne(
        { _id: subscription._id },
        {
            $rename: {
                "starts_at": "start_date",
                "expires_at": "end_date"
            }
        }
    );
});

print(`Migrated ${subscriptions.length} subscriptions`);

// ============================================
// STEP 5: CREATE TYPE_OBJECTS COLLECTION (if needed)
// ============================================
print("Step 5: Creating type_objects collection...");

// NOTE: If you have existing food_types data, migrate to type_objects
// Uncomment and modify the following if you need to migrate from old food_types collection:

/*
const foodTypes = db.food_types.find({}).toArray();

foodTypes.forEach(foodType => {
    const typeObject = {
        name: foodType.name,
        image_url: "",
        is_active: true,
        created_at: new Date(),
        updated_at: new Date()
    };
    
    db.type_objects.insertOne(typeObject);
});

print(`Created ${foodTypes.length} type objects`);
*/

print("Type objects collection ready (manual migration required if needed)");

// ============================================
// STEP 6: CLEANUP OLD COLLECTIONS (OPTIONAL)
// ============================================
print("Step 6: Cleanup old collections...");

// Uncomment these lines if you want to remove old collections
// db.restaurant_food_types.drop();
// db.restaurant_images.drop();
// db.food_types.drop(); // Only if you've migrated to type_objects

print("Migration completed successfully!");
print("Please verify the data and test your application before removing backup collections.");

// ============================================
// ROLLBACK SCRIPT (if needed)
// ============================================
print("\n=== ROLLBACK INSTRUCTIONS ===");
print("If you need to rollback:");
print("1. Drop current collections: db.restaurants.drop(), db.challenge_definitions.drop(), db.subscriptions.drop()");
print("2. Restore from backup collections");
print("3. Drop type_objects collection: db.type_objects.drop()");
print("4. Restore old collections if they were dropped");