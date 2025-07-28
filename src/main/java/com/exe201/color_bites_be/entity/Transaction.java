package com.exe201.color_bites_be.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    private String id;

    @DBRef
    private Account account;

    @Field("amount")
    private Double amount;

    @Field("currency")
    private String currency;

    @Field("type")
    private String type;

    @Field("status")
    private String status;

    @Field("metadata")
    private Map<String, Object> metadata;

    @Field("created_at")
    private LocalDateTime createdAt;
}
