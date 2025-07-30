package com.exe201.color_bites_be.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary(){
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dnpguhdhx");
        config.put("api_key", "822112182435981");
        config.put("api_secret", "I0RKszI-tJiM1KM5vMhfE7zVuTU");
        return new Cloudinary(config);

    }
}
