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
import java.util.List;
import java.util.Map;

@Document(collection = "quizzes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {
    @Id
    private String id;

    @DBRef
    private Account account;

    @Field("answers")
    private Map<String, Object> answers;

    @Field("mood_result")
    private String moodResult;

    @Field("recommended_foods")
    private List<String> recommendedFoods;

    @DBRef
    @Field("recommended_restaurants")
    private List<Restaurant> recommendedRestaurants;

    @Field("created_at")
    private LocalDateTime createdAt;
}
