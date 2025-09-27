package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO response cho admin view transactions
 * Chứa thông tin chi tiết về giao dịch cho admin quản lý
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminTransactionResponse {
    
    private String id;
    private String accountId;
    private String accountName;
    private String accountEmail;
    private Double amount;
    private String currency;
    private String type;
    private String status;
    private String plan;
    private String gateway;
    private String orderCode;
    private String providerTxnId;
    private Map<String, Object> metadata;
    private Map<String, Object> rawPayload;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Thông tin bổ sung cho admin
    private Boolean accountIsActive;
    private String accountRole;
}
