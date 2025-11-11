package com.exe201.color_bites_be.dto.response;

import com.exe201.color_bites_be.enums.FriendStatus;
import lombok.Data;

@Data
public class ListFriendResponse {
    String accountId;
    String userName;
    FriendStatus status;
}
