package com.exe201.color_bites_be.service.impl;

import com.exe201.color_bites_be.dto.request.CreateMoodMapRequest;
import com.exe201.color_bites_be.dto.request.UpdateMoodMapRequest;
import com.exe201.color_bites_be.dto.response.MoodMapResponse;
import com.exe201.color_bites_be.entity.Account;
import com.exe201.color_bites_be.entity.MoodMap;
import com.exe201.color_bites_be.entity.UserInformation;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.exception.FuncErrorException;
import com.exe201.color_bites_be.repository.AccountRepository;
import com.exe201.color_bites_be.repository.MoodMapRepository;
import com.exe201.color_bites_be.repository.UserInformationRepository;
import com.exe201.color_bites_be.service.IMoodMapService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * Implementation của IMoodMapService
 * Xử lý logic theo dõi cảm xúc và xuất dữ liệu
 */
@Service
public class MoodMapServiceImpl implements IMoodMapService {

    @Autowired
    private MoodMapRepository moodMapRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public MoodMapResponse createMoodMap(String accountId, CreateMoodMapRequest request) {
        try {
            // Kiểm tra account tồn tại
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));

            // Tạo mood map entity
            MoodMap moodMap = modelMapper.map(request, MoodMap.class);
            moodMap.setAccountId(accountId);
            moodMap.setCreatedAt(LocalDateTime.now());
            // TODO: Add updatedAt field to MoodMap entity
            // moodMap.setUpdatedAt(LocalDateTime.now());
            moodMap.setIsDeleted(false);

            // Lưu mood map
            MoodMap savedMoodMap = moodMapRepository.save(moodMap);

            return buildMoodMapResponse(savedMoodMap, accountId);

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi tạo mood map: " + e.getMessage());
        }
    }

    @Override
    public MoodMapResponse readMoodMapById(String moodMapId, String currentAccountId) {
        MoodMap moodMap = moodMapRepository.findByIdAndNotDeleted(moodMapId)
                .orElseThrow(() -> new NotFoundException("Mood map không tồn tại"));

        return buildMoodMapResponse(moodMap, currentAccountId);
    }

    @Override
    public Page<MoodMapResponse> readUserMoodMaps(String accountId, int page, int size, String currentAccountId) {
        try {
            // Kiểm tra account tồn tại
            accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<MoodMap> moodMaps = moodMapRepository.findByAccountIdAndNotDeleted(accountId, pageable);

            return moodMaps.map(moodMap -> buildMoodMapResponse(moodMap, currentAccountId));

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi lấy danh sách mood map: " + e.getMessage());
        }
    }

    @Override
    public Page<MoodMapResponse> readPublicMoodMaps(int page, int size, String currentAccountId) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<MoodMap> moodMaps = moodMapRepository.findPublicMoodMaps(pageable);

            return moodMaps.map(moodMap -> buildMoodMapResponse(moodMap, currentAccountId));

        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi lấy danh sách mood map công khai: " + e.getMessage());
        }
    }

    @Override
    public Page<MoodMapResponse> searchMoodMaps(String keyword, int page, int size, String currentAccountId) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            // TODO: Add findByKeywordAndNotDeleted method to MoodMapRepository
            Page<MoodMap> moodMaps = moodMapRepository.findAll(pageable);

            return moodMaps.map(moodMap -> buildMoodMapResponse(moodMap, currentAccountId));

        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi tìm kiếm mood map: " + e.getMessage());
        }
    }

    @Override
    public MoodMapResponse editMoodMap(String moodMapId, String accountId, UpdateMoodMapRequest request) {
        MoodMap moodMap = moodMapRepository.findByIdAndNotDeleted(moodMapId)
                .orElseThrow(() -> new NotFoundException("Mood map không tồn tại"));

        // Kiểm tra quyền sở hữu
        if (!moodMap.getAccountId().equals(accountId)) {
            throw new FuncErrorException("Bạn không có quyền chỉnh sửa mood map này");
        }

        try {
            // TODO: Add getter methods to UpdateMoodMapRequest
            if (request.getTitle() != null) {
                moodMap.setTitle(request.getTitle());
            }
            // TODO: Add getDescription, getMoodData, getIsPublic methods to UpdateMoodMapRequest
            /*
            if (request.getDescription() != null) {
                moodMap.setDescription(request.getDescription());
            }
            if (request.getMoodData() != null) {
                moodMap.setMoodData(request.getMoodData());
            }
            if (request.getIsPublic() != null) {
                moodMap.setIsPublic(request.getIsPublic());
            }
            */

            // TODO: Add updatedAt field to MoodMap entity
            // moodMap.setUpdatedAt(LocalDateTime.now());

            MoodMap updatedMoodMap = moodMapRepository.save(moodMap);
            return buildMoodMapResponse(updatedMoodMap, accountId);

        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi cập nhật mood map: " + e.getMessage());
        }
    }

    @Override
    public void deleteMoodMap(String moodMapId, String accountId) {
        MoodMap moodMap = moodMapRepository.findByIdAndNotDeleted(moodMapId)
                .orElseThrow(() -> new NotFoundException("Mood map không tồn tại"));

        // Kiểm tra quyền sở hữu
        if (!moodMap.getAccountId().equals(accountId)) {
            throw new FuncErrorException("Bạn không có quyền xóa mood map này");
        }

        try {
            // Soft delete
            moodMap.setIsDeleted(true);
            // TODO: Add updatedAt field to MoodMap entity
            // moodMap.setUpdatedAt(LocalDateTime.now());
            moodMapRepository.save(moodMap);

        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi xóa mood map: " + e.getMessage());
        }
    }

    @Override
    public String exportMoodMapData(String moodMapId, String accountId) {
        MoodMap moodMap = moodMapRepository.findByIdAndNotDeleted(moodMapId)
                .orElseThrow(() -> new NotFoundException("Mood map không tồn tại"));

        // Kiểm tra quyền sở hữu
        if (!moodMap.getAccountId().equals(accountId)) {
            throw new FuncErrorException("Bạn không có quyền xuất dữ liệu mood map này");
        }

        try {
            // Tạo dữ liệu export dạng JSON
            StringBuilder exportData = new StringBuilder();
            exportData.append("{\n");
            exportData.append("  \"id\": \"").append(moodMap.getId()).append("\",\n");
            exportData.append("  \"title\": \"").append(moodMap.getTitle()).append("\",\n");
            // TODO: Add getDescription, getMoodData, getUpdatedAt methods to MoodMap entity
            exportData.append("  \"description\": \"TODO\",\n");
            exportData.append("  \"moodData\": \"TODO\",\n");
            exportData.append("  \"createdAt\": \"").append(moodMap.getCreatedAt()).append("\",\n");
            exportData.append("  \"updatedAt\": \"TODO\"\n");
            exportData.append("}");

            return exportData.toString();

        } catch (Exception e) {
            throw new FuncErrorException("Lỗi khi xuất dữ liệu mood map: " + e.getMessage());
        }
    }

    /**
     * Xây dựng MoodMapResponse từ MoodMap entity
     */
    private MoodMapResponse buildMoodMapResponse(MoodMap moodMap, String currentAccountId) {
        MoodMapResponse response = modelMapper.map(moodMap, MoodMapResponse.class);

        // TODO: Add setter methods to MoodMapResponse
        /*
        UserInformation userInfo = userInformationRepository.findByAccountId(moodMap.getAccountId());
        if (userInfo != null) {
            response.setUserName(userInfo.getFullName());
            response.setUserAvatar(userInfo.getAvatarUrl());
        }

        // Kiểm tra quyền sở hữu
        response.setIsOwner(moodMap.getAccountId().equals(currentAccountId));
        */

        return response;
    }
}
