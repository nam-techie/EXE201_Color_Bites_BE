package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Mood;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MoodRepository extends MongoRepository<Mood, String> {
    Mood findMoodById(String id);
}
