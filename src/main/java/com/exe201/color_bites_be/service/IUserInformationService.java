package com.exe201.color_bites_be.service;

import com.exe201.color_bites_be.dto.request.UserInformationRequest;
import com.exe201.color_bites_be.dto.response.UserInformationResponse;
import com.exe201.color_bites_be.enums.SubcriptionPlan;

/**
 * Interface định nghĩa các phương thức quản lý thông tin người dùng
 * Bao gồm lấy thông tin, cập nhật thông tin và quản lý subscription plan
 */
public interface IUserInformationService {
    
    /**
     * Lấy thông tin người dùng hiện tại
     * @return UserInformationResponse Thông tin người dùng
     */
    UserInformationResponse getUserInformation();
    
    /**
     * Cập nhật thông tin người dùng
     * @param request Thông tin cần cập nhật
     * @return UserInformationResponse Thông tin đã được cập nhật
     */
    UserInformationResponse updateUserInformation(UserInformationRequest request);
    
    /**
     * Nâng cấp subscription plan (chỉ dành cho hệ thống thanh toán)
     * @param accountId ID của tài khoản
     * @param newPlan Subscription plan mới
     */
    void upgradeSubscriptionPlan(String accountId, SubcriptionPlan newPlan);
    
    /**
     * Hạ cấp về FREE (khi hết hạn premium)
     * @param accountId ID của tài khoản
     */
    void downgradeToFree(String accountId);
}
