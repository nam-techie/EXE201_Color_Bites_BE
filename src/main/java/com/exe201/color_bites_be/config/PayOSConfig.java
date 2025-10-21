package com.exe201.color_bites_be.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình PayOS từ application.yml
 * Chứa các thông tin credentials và endpoints cần thiết
 */
@Configuration
@ConfigurationProperties(prefix = "payos")
@Getter
@Setter
public class PayOSConfig {
    
    /**
     * Client ID từ PayOS dashboard
     */
    private String clientId;
    
    /**
     * API Key từ PayOS dashboard  
     */
    private String apiKey;
    
    /**
     * Checksum Key để verify webhook data
     */
    private String checksumKey;
    
    /**
     * Base URL của PayOS API
     */
    private String apiUrl;
    
    /**
     * URL để redirect sau khi thanh toán thành công
     */
    private String returnUrl;
    
    /**
     * URL để redirect khi người dùng hủy thanh toán
     */
    private String cancelUrl;
}
