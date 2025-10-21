package com.exe201.color_bites_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorResponsePost {
    private String accountId;
    private String authorName; // Tên tác giả từ UserInformation
    private String authorAvatar; // Avatar tác giả từ UserInformation
}
