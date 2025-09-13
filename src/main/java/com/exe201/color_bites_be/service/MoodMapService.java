package com.exe201.color_bites_be.service;

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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class MoodMapService {

    @Autowired
    private MoodMapRepository moodMapRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Tạo mood map mới
     */
    public MoodMapResponse createMoodMap(String accountId, CreateMoodMapRequest request) {
        try {
            // Kiểm tra account tồn tại
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));

            // Tạo mood map entity
            MoodMap moodMap = modelMapper.map(request, MoodMap.class);
            moodMap.setAccountId(accountId);
            moodMap.setCreatedAt(LocalDateTime.now());
            moodMap.setExported(false);

            // Lưu mood map
            MoodMap savedMoodMap = moodMapRepository.save(moodMap);

            return convertToResponse(savedMoodMap, accountId);

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw e;
            }
            throw new FuncErrorException("Lỗi khi tạo mood map: " + e.getMessage());
        }
    }

    /**
     * Lấy mood map theo ID
     */
    public MoodMapResponse readMoodMapById(String moodMapId, String currentAccountId) {
        MoodMap moodMap = moodMapRepository.findByIdAndNotDeleted(moodMapId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mood map"));

        // Kiểm tra quyền truy cập
        if ("private".equals(moodMap.getVisibility()) &&
            !moodMap.getAccountId().equals(currentAccountId)) {
            throw new FuncErrorException("Bạn không có quyền xem mood map này");
        }

        return convertToResponse(moodMap, currentAccountId);
    }

    /**
     * Lấy danh sách mood map của user
     */
    public Page<MoodMapResponse> readMoodMapsByUser(String accountId, int page, int size, String currentAccountId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<MoodMap> moodMaps = moodMapRepository.findByAccountIdAndNotDeleted(accountId, pageable);

        return moodMaps.map(moodMap -> convertToResponse(moodMap, currentAccountId));
    }

    /**
     * Lấy mood map public
     */
    public Page<MoodMapResponse> readPublicMoodMaps(int page, int size, String currentAccountId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<MoodMap> moodMaps = moodMapRepository.findPublicMoodMaps(pageable);

        return moodMaps.map(moodMap -> convertToResponse(moodMap, currentAccountId));
    }

    /**
     * Tìm kiếm mood map theo title
     */
    public Page<MoodMapResponse> searchMoodMaps(String accountId, String keyword, int page, int size, String currentAccountId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<MoodMap> moodMaps = moodMapRepository.findByAccountIdAndTitleContaining(accountId, keyword, pageable);

        return moodMaps.map(moodMap -> convertToResponse(moodMap, currentAccountId));
    }

    /**
     * Cập nhật mood map
     */
    public MoodMapResponse editMoodMap(String moodMapId, String accountId, UpdateMoodMapRequest request) {
        MoodMap moodMap = moodMapRepository.findByIdAndNotDeleted(moodMapId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mood map"));

        // Kiểm tra quyền sở hữu
        if (!moodMap.getAccountId().equals(accountId)) {
            throw new FuncErrorException("Bạn không có quyền chỉnh sửa mood map này");
        }

        // Cập nhật thông tin
        updateMoodMapFromRequest(moodMap, request);

        MoodMap updatedMoodMap = moodMapRepository.save(moodMap);
        return convertToResponse(updatedMoodMap, accountId);
    }

    /**
     * Xóa mood map (soft delete)
     */
    public void deleteMoodMap(String moodMapId, String accountId) {
        MoodMap moodMap = moodMapRepository.findByIdAndNotDeleted(moodMapId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mood map"));

        // Kiểm tra quyền sở hữu
        if (!moodMap.getAccountId().equals(accountId)) {
            throw new FuncErrorException("Bạn không có quyền xóa mood map này");
        }

        // Soft delete
        moodMap.setIsDeleted(true);
        moodMapRepository.save(moodMap);
    }

    /**
     * Export mood map
     */
    public MoodMapResponse exportMoodMap(String moodMapId, String accountId) {
        MoodMap moodMap = moodMapRepository.findByIdAndNotDeleted(moodMapId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mood map"));

        // Kiểm tra quyền sở hữu
        if (!moodMap.getAccountId().equals(accountId)) {
            throw new FuncErrorException("Bạn không có quyền export mood map này");
        }

        // Tạo export URL (có thể tích hợp với service khác để tạo file)
        String exportUrl = generateExportUrl(moodMapId);
        
        moodMap.setExported(true);
        moodMap.setExportUrl(exportUrl);

        MoodMap updatedMoodMap = moodMapRepository.save(moodMap);
        return convertToResponse(updatedMoodMap, accountId);
    }

    /**
     * Đếm số mood map của user
     */
    public long countMoodMapsByUser(String accountId) {
        return moodMapRepository.countByAccountIdAndNotDeleted(accountId);
    }

    /**
     * Chuyển đổi MoodMap entity sang MoodMapResponse
     */
    private MoodMapResponse convertToResponse(MoodMap moodMap, String currentAccountId) {
        MoodMapResponse response = modelMapper.map(moodMap, MoodMapResponse.class);
        
        // Set account info
        if (moodMap.getAccountId() != null) {
            response.setAccountId(moodMap.getAccountId());

            UserInformation userInfo = userInformationRepository.findByAccountId(moodMap.getAccountId());
            if (userInfo != null && userInfo.getFullName() != null) {
                response.setAccountName(userInfo.getFullName());
            } else {
                Account account = accountRepository.findById(moodMap.getAccountId()).orElse(null);
                if (account != null) {
                    response.setAccountName(account.getUserName());
                }
            }
        }

        // Set ownership info
        response.setIsOwner(moodMap.getAccountId().equals(currentAccountId));

        return response;
    }

    /**
     * Cập nhật MoodMap từ UpdateMoodMapRequest
     */
    private void updateMoodMapFromRequest(MoodMap moodMap, UpdateMoodMapRequest request) {
        if (request.getTitle() != null) moodMap.setTitle(request.getTitle());
        if (request.getEntries() != null) moodMap.setEntries(request.getEntries());
        if (request.getVisibility() != null) moodMap.setVisibility(request.getVisibility());
    }

    /**
     * Tạo export URL (placeholder implementation)
     */
    private String generateExportUrl(String moodMapId) {
        // TODO: Implement actual export functionality
        return "/api/moodmaps/export/" + moodMapId + "?token=" + System.currentTimeMillis();
    }
}
