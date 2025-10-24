package com.exe201.color_bites_be.entity;

import com.exe201.color_bites_be.enums.ParticipationStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "challenge_participations")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "account_id_idx", def = "{'account_id': 1}"),
    @CompoundIndex(name = "challenge_id_idx", def = "{'challenge_id': 1}"),
    @CompoundIndex(name = "status_idx", def = "{'status': 1}")
})
public class ChallengeParticipation {

    @Id
    @Field("_id")
    private String id;

    @Field("account_id")
    @Indexed
    private String accountId;

    @Field("challenge_id")
    @Indexed
    private String challengeId;

    @Field("status")
    @Indexed
    private ParticipationStatus status;

    @Field("progress_count")
    private Integer progressCount = 0;

    @Field("completed_at")
    private LocalDateTime completedAt;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;
}

