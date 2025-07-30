package com.exe201.color_bites_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class ColorBitesBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ColorBitesBeApplication.class, args);
    }

}
