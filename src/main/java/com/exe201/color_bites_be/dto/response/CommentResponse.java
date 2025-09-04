package com.exe201.color_bites_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private String id;
    private String postId;
    private String accountId;
    private String authorName; // Tên tác giả từ UserInformation
    private String authorAvatar; // Avatar tác giả từ UserInformation
    private String parentCommentId;
    private Integer depth;
    private String content;
    private Boolean isOwner; // Người xem có phải chủ comment không
    private Boolean isEdited; // Comment đã được chỉnh sửa chưa
    private Integer replyCount; // Số lượng reply
    private List<CommentResponse> replies; // Danh sách reply (nếu có)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
