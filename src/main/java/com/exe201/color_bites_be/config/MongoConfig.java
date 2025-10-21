package com.exe201.color_bites_be.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.Restaurant;

import java.time.LocalDateTime;

/**
 * Configuration cho MongoDB Auditing
 * Tự động set createdAt và updatedAt cho các entity
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {

    /**
     * Callback để tự động set thời gian trước khi lưu vào database
     */
    @Bean
    public BeforeConvertCallback<Account> beforeConvertCallback() {
        return (entity, collection) -> {
            LocalDateTime now = LocalDateTime.now();
            if (entity.getCreatedAt() == null) {
                entity.setCreatedAt(now);
            }
            entity.setUpdatedAt(now);
            return entity;
        };
    }
}
