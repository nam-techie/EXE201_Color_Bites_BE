package com.exe201.color_bites_be.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.exe201.color_bites_be.enums.CurrencyCode;
import com.exe201.color_bites_be.enums.SubcriptionPlan;
import com.exe201.color_bites_be.enums.TransactionEnums.TxnStatus;
import com.exe201.color_bites_be.enums.TransactionEnums.TxnType;
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

    // Thêm fields theo database schema
    @Field("order_code")
    private String orderCode; // unique

    @Field("plan")
    private SubcriptionPlan plan; // enum SubscriptionPlan

    @Field("gateway")
    private String gateway;

    @Field("provider_txn_id")
    private String providerTxnId; // unique, sparse

    @Field("raw_payload")
    private Map<String, Object> rawPayload;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}
