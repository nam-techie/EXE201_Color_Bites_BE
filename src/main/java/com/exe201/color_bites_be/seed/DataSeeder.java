package com.exe201.color_bites_be.seed;

/**
 * Base interface cho tất cả data seeder
 * Định nghĩa contract chung cho việc seed data
 */
public interface DataSeeder {

    /**
     * Thực hiện seed data
     */
    void seed();

    /**
     * Kiểm tra xem có cần seed data không
     * @return true nếu cần seed, false nếu đã có data
     */
    boolean shouldSeed();

    /**
     * Tên của seeder để log
     * @return tên seeder
     */
    String getSeederName();
}
