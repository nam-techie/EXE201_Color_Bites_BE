package com.exe201.color_bites_be.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoodMapResponse {
    
    private String id;
    private String accountId;
    private String accountName;
    private String title;
    private Map<String, Object> entries;
    private String visibility;
    private Boolean exported;
    private String exportUrl;
    private LocalDateTime createdAt;
    
    // Thêm thông tin bổ sung
    private Boolean isOwner; // Người dùng hiện tại có phải chủ sở hữu không
}
