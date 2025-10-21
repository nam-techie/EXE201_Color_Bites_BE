package com.exe201.color_bites_be.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class để trích xuất hashtags từ content
 * Hỗ trợ phát hiện pattern #hashtag trong text
 */
public class HashtagExtractor {
    
    // Pattern để match hashtags: #[a-zA-Z0-9_]+ (không có dấu, chỉ chữ cái, số, underscore)
    private static final String HASHTAG_PATTERN = "#([a-zA-Z0-9_]+)";
    private static final Pattern pattern = Pattern.compile(HASHTAG_PATTERN);
    
    /**
     * Trích xuất tất cả hashtags từ content
     * @param content Nội dung cần trích xuất hashtags
     * @return List<String> Danh sách hashtags (không có dấu #, đã lowercase, không duplicate)
     */
    public static List<String> extractHashtags(String content) {
        // Sử dụng LinkedHashSet để giữ thứ tự và loại bỏ duplicate
        Set<String> hashtagSet = new LinkedHashSet<>();
        
        if (content == null || content.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Tìm tất cả matches
        Matcher matcher = pattern.matcher(content);
        
        while (matcher.find()) {
            // Lấy phần sau dấu # (group 1)
            String hashtag = matcher.group(1);
            
            // Chuẩn hóa: lowercase và trim
            String normalizedHashtag = hashtag.toLowerCase().trim();
            
            // Kiểm tra độ dài hợp lệ (1-50 ký tự)
            if (normalizedHashtag.length() >= 1 && normalizedHashtag.length() <= 50) {
                hashtagSet.add(normalizedHashtag);
            }
        }
        
        // Convert Set sang List
        return new ArrayList<>(hashtagSet);
    }
    
    /**
     * Kiểm tra content có chứa hashtags không
     * @param content Nội dung cần kiểm tra
     * @return boolean True nếu có hashtags
     */
    public static boolean containsHashtags(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        return pattern.matcher(content).find();
    }
    
    /**
     * Đếm số lượng hashtags trong content
     * @param content Nội dung cần đếm
     * @return int Số lượng hashtags
     */
    public static int countHashtags(String content) {
        return extractHashtags(content).size();
    }
    
    /**
     * Combine manual tags với extracted hashtags, loại bỏ duplicate
     * @param manualTags Tags được nhập thủ công
     * @param extractedHashtags Tags được trích xuất từ content
     * @return List<String> Danh sách tags đã combined và normalized
     */
    public static List<String> combineAndNormalizeTags(List<String> manualTags, List<String> extractedHashtags) {
        Set<String> combinedTags = new LinkedHashSet<>();
        
        // Thêm manual tags trước (ưu tiên)
        if (manualTags != null) {
            for (String tag : manualTags) {
                if (tag != null && !tag.trim().isEmpty()) {
                    String normalizedTag = tag.toLowerCase().trim();
                    if (normalizedTag.length() >= 1 && normalizedTag.length() <= 50) {
                        combinedTags.add(normalizedTag);
                    }
                }
            }
        }
        
        // Thêm extracted hashtags
        if (extractedHashtags != null) {
            combinedTags.addAll(extractedHashtags);
        }
        
        return new ArrayList<>(combinedTags);
    }
}
