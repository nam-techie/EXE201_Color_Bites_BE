package com.exe201.color_bites_be.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class DotEnvConfig {

    @PostConstruct
    public void loadEnvironmentVariables() {
        try {
            // Load .env file từ thư mục gốc của project
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")  // Thư mục chứa file .env
                    .filename(".env") // Tên file
                    .ignoreIfMalformed() // Bỏ qua nếu file có lỗi format
                    .ignoreIfMissing()   // Bỏ qua nếu file không tồn tại
                    .load();

            // Set các environment variables từ .env file
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                
                // Chỉ set nếu environment variable chưa tồn tại
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            });

        } catch (Exception e) {
            System.err.println("❌ Failed to load .env file: " + e.getMessage());
            System.err.println("⚠️  Application will continue with system environment variables only");
        }
    }
}
