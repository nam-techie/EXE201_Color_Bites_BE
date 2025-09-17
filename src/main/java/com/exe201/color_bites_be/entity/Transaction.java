package com.exe201.color_bites_be.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.DBRef;
import com.exe201.color_bites_be.enums.CurrencyCode;
import com.exe201.color_bites_be.enums.TxnStatus;
import com.exe201.color_bites_be.enums.TxnType;
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

    @Field("account_id")
    private String accountId;

    @Field("amount")
    private Double amount;

    @Field("currency")
    private CurrencyCode currency;

    @Field("type")
    private TxnType type;

    @Field("status")
    private TxnStatus status;

    @Field("metadata")
    private Map<String, Object> metadata;

    @Field("created_at")
    private LocalDateTime createdAt;
}
