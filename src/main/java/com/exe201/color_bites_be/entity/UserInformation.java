package com.exe201.color_bites_be.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "user_information")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInformation {
    @Id
    private String id;

    @DBRef
    private Account account;

    @Field("full_name")
    private String fullName;

    @Field("gender")
    private String gender;

    @Field("dob")
    private LocalDate dob;
    //abc tessttesst

    @Field("phone")
    private String phone;

    @Field("address")
    private String address;

    @Field("avatar_url")
    private String avatarUrl;


    @Field("subscription_plan")
    private String subscriptionPlan;

    @Field("bio")
    private String bio;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}
