package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.CreateMoodRequest;
import com.exe201.color_bites_be.dto.request.UpdateMoodRequest;
import com.exe201.color_bites_be.dto.response.MoodResponse;
import com.exe201.color_bites_be.entity.Mood;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.repository.MoodRepository;
import com.exe201.color_bites_be.repository.PostRepository;
import com.exe201.color_bites_be.service.IMoodService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation của IMoodService
 * Xử lý logic CRUD và quản lý mood
 */
@Service
public class MoodServiceImpl implements IMoodService {

    @Autowired
    private MoodRepository moodRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public MoodResponse createMood(CreateMoodRequest request) {
        try {
            // Kiểm tra mood đã tồn tại
            if (moodRepository.existsByNameIgnoreCase(request.getName())) {
                throw new FuncErrorException("Mood với tên '" + request.getName() + "' đã tồn tại");
            }

            // Tạo mood entity
            Mood mood = new Mood();
            mood.setName(request.getName());
            mood.setEmoji(request.getEmoji());
            mood.setCreatedAt(LocalDateTime.now());

            // Lưu mood
            Mood savedMood = moodRepository.save(mood);

            return buildMoodResponse(savedMood);

        } catch (FuncErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi tạo mood: " + e.getMessage());
        }
    }

    @Override
    public MoodResponse readMoodById(String moodId) {
        Mood mood = moodRepository.findById(moodId)
                .orElseThrow(() -> new NotFoundException("Mood không tồn tại"));

        return buildMoodResponse(mood);
    }

    @Override
    public Page<MoodResponse> readAllMoods(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Sắp xếp theo tên
        Page<Mood> moods = moodRepository.findAllOrderByName(pageable);

        return moods.map(this::buildMoodResponse);
    }

    @Override
    public Page<MoodResponse> searchMoods(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Mood> moods = moodRepository.findByNameContainingIgnoreCase(keyword, pageable);

        return moods.map(this::buildMoodResponse);
    }

    @Override
    @Transactional
    public MoodResponse editMood(String moodId, UpdateMoodRequest request) {
        try {
            Mood mood = moodRepository.findById(moodId)
                    .orElseThrow(() -> new NotFoundException("Mood không tồn tại"));

            // Kiểm tra tên mới có trung lặp không (nếu có thay đổi tên)
            if (request.getName() != null && !request.getName().equals(mood.getName())) {
                if (moodRepository.existsByNameIgnoreCase(request.getName())) {
                    throw new FuncErrorException("Mood với tên '" + request.getName() + "' đã tồn tại");
                }
                mood.setName(request.getName());
            }

            // Cập nhật các field nếu có
            if (request.getEmoji() != null) {
                mood.setEmoji(request.getEmoji());
            }

            // Lưu mood đã cập nhật
            Mood updatedMood = moodRepository.save(mood);

            return buildMoodResponse(updatedMood);

        } catch (NotFoundException | FuncErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi cập nhật mood: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteMood(String moodId) {
        try {
            Mood mood = moodRepository.findById(moodId)
                    .orElseThrow(() -> new NotFoundException("Mood không tồn tại"));

            // Kiểm tra xem mood có đang được sử dụng trong bài viết không
            long postCount = postRepository.countByAccountIdAndNotDeleted(moodId); // Cần sửa query này
            if (postCount > 0) {
                throw new FuncErrorException("Không thể xóa mood vì đang có " + postCount + " bài viết sử dụng");
            }

            // Xóa mood
            moodRepository.delete(mood);

        } catch (NotFoundException | FuncErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi xóa mood: " + e.getMessage());
        }
    }

    @Override
    public long countAllMoods() {
        return moodRepository.count();
    }

    @Override
    public Page<MoodResponse> readPopularMoods(int page, int size) {
        // Tạm thời sắp xếp theo ngày tạo mới nhất
        // TODO: Implement logic thống kê mood được sử dụng nhiều nhất
        Pageable pageable = PageRequest.of(page, size);
        Page<Mood> moods = moodRepository.findAllOrderByCreatedAtDesc(pageable);

        return moods.map(this::buildMoodResponse);
    }

    @Override
    public boolean existsByName(String name) {
        return moodRepository.existsByNameIgnoreCase(name);
    }

    /**
     * Helper method để build MoodResponse từ Mood entity
     */
    private MoodResponse buildMoodResponse(Mood mood) {
        MoodResponse response = modelMapper.map(mood, MoodResponse.class);
        
        // Đếm số bài viết sử dụng mood này
        long postCount = countPostsByMood(mood.getId());
        response.setPostCount(postCount);
        
        return response;
    }

    /**
     * Helper method để đếm số bài viết theo mood
     */
    private long countPostsByMood(String moodId) {
        try {
            // Sử dụng query từ PostRepository để đếm bài viết theo moodId
            return postRepository.findByMoodIdAndNotDeleted(moodId, PageRequest.of(0, 1)).getTotalElements();
        } catch (Exception e) {
            return 0L; // Trả về 0 nếu có lỗi
        }
    }
}
