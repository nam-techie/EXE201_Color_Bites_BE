package com.exe201.color_bites_be.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import com.exe201.color_bites_be.enums.SubcriptionPlan;
import com.exe201.color_bites_be.enums.SubscriptionStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Document(collection = "subscriptions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CompoundIndex(name = "end_date_accountId_status", def = "{'endDate': 1, 'accountId': 1, 'status': 1}")
public class Subscription {
    @Id
    private String id;

    @Field("account_id")
    private String accountId;

    @Field("plan")
    private SubcriptionPlan plan; // enum SubscriptionPlan

    @Field("status")
    private SubscriptionStatus status;

    @Field("start_date")
    private LocalDateTime startsAt;

    @Field("end_date")
    private LocalDateTime expiresAt;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

}
