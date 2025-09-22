package com.exe201.color_bites_be.seed;

import com.exe201.color_bites_be.entity.Mood;
import com.exe201.color_bites_be.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Seed dữ liệu mood mặc định giống Facebook emotions
 */
@Component
public class MoodDataSeeder implements DataSeeder {

    @Autowired
    private MoodRepository moodRepository;

    @Override
    public void seed() {
        List<Mood> defaultMoods = Arrays.asList(
            // Mood cơ bản giống Facebook
            createMood("Hạnh phúc", "😊"),
            createMood("Yêu thích", "😍"),
            createMood("Buồn", "😢"),
            createMood("Tức giận", "😠"),
            createMood("Ngạc nhiên", "😮"),
            createMood("Sợ hãi", "😨"),
            
            // Mood mở rộng cho ẩm thực
            createMood("Thèm ăn", "🤤"),
            createMood("Thỏa mãn", "😌"),
            createMood("Phấn khích", "🤗"),
            createMood("Hoài niệm", "🥺"),
            createMood("Tò mò", "🤔"),
            createMood("Thất vọng", "😞")
        );

        moodRepository.saveAll(defaultMoods);
        System.out.println("✅ " + getSeederName() + ": Đã seed " + defaultMoods.size() + " mood mặc định thành công!");
    }

    @Override
    public boolean shouldSeed() {
        return moodRepository.count() == 0;
    }

    @Override
    public String getSeederName() {
        return "MoodDataSeeder";
    }

    private Mood createMood(String name, String emoji) {
        Mood mood = new Mood();
        mood.setName(name);
        mood.setEmoji(emoji);
        mood.setCreatedAt(LocalDateTime.now());
        return mood;
    }
}
