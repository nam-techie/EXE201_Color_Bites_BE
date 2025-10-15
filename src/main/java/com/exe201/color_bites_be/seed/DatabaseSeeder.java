package com.exe201.color_bites_be.seed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Database seeder chính - quản lý tất cả các seeder
 * Chạy sau khi Spring Boot khởi động
 */
@Component
@Order(1) // Đảm bảo chạy sau khi tất cả bean đã được khởi tạo
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private List<DataSeeder> seeders;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🌱 Bắt đầu seed database...");

        boolean hasSeeded = false;

        for (DataSeeder seeder : seeders) {
            try {
                if (seeder.shouldSeed()) {
                    System.out.println("📝 Đang chạy " + seeder.getSeederName() + "...");
                    seeder.seed();
                    hasSeeded = true;
                } else {
                    System.out.println("⏭️  " + seeder.getSeederName() + ": Đã có dữ liệu, bỏ qua");
                }
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi chạy " + seeder.getSeederName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (hasSeeded) {
            System.out.println("🎉 Hoàn thành seed database!");
        } else {
            System.out.println("✨ Database đã có dữ liệu đầy đủ!");
        }
    }
}
