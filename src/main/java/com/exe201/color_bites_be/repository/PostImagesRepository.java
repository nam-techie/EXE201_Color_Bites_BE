package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.PostImages;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PostImagesRepository  extends MongoRepository<PostImages, String> {
    // Lấy toàn bộ ảnh của 1 post (có sort nếu cần)
    List<PostImages> findByPostIdOrderByCreatedAtAsc(String postId);

    // Chỉ lấy URL để trả về nhanh gọn
    @Query(value = "{'post_id': ?0}", fields = "{'url': 1, '_id': 0}")
    List<String> findUrlsByPostId(String postId);

    // Xoá tất cả ảnh theo post (khi xoá post)
    void deleteByPostId(String postId);
}
