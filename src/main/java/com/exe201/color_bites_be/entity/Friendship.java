package com.exe201.color_bites_be.entity;

import com.exe201.color_bites_be.enums.FriendStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Document(collection = "friendships")
@CompoundIndexes({
	@CompoundIndex(name = "uniq_user_pair", def = "{ 'user_a': 1, 'user_b': 1 }", unique = true)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Friendship {
	@Id
	private String id;

	@Field("user_a")
	@Indexed
	private String userA;

	@Field("user_b")
	@Indexed
	private String userB;

	@Field("status")
	@Indexed
	private FriendStatus status = FriendStatus.PENDING;

	@Field("requested_by")
	private String requestedBy;

	@Field("created_at")
	@CreatedDate
	private LocalDateTime createdAt;

	@Field("updated_at")
	@LastModifiedDate
	private LocalDateTime updatedAt;
}


