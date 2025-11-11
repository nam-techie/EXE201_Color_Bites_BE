package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.response.*;
import com.exe201.color_bites_be.exception.NotFoundException;
import com.exe201.color_bites_be.dto.response.ListAccountResponse;
import com.exe201.color_bites_be.dto.response.AdminPostResponse;
import com.exe201.color_bites_be.dto.response.AdminRestaurantResponse;
import com.exe201.color_bites_be.dto.response.AdminTransactionResponse;
import com.exe201.color_bites_be.dto.response.AdminCommentResponse;
import com.exe201.color_bites_be.dto.response.AdminTagResponse;
import com.exe201.color_bites_be.dto.response.UserStatisticsResponse;
import com.exe201.color_bites_be.dto.response.PostStatisticsResponse;
import com.exe201.color_bites_be.dto.response.RestaurantStatisticsResponse;
import com.exe201.color_bites_be.dto.response.RevenueStatisticsResponse;
import com.exe201.color_bites_be.dto.response.EngagementStatisticsResponse;
import com.exe201.color_bites_be.dto.response.ChallengeStatisticsResponse;
import com.exe201.color_bites_be.dto.response.AdminMoodResponse;
import com.exe201.color_bites_be.dto.response.ChallengeDefinitionResponse;
import com.exe201.color_bites_be.dto.response.RevenueReportResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.service.IAdminService;
import com.exe201.color_bites_be.service.IUserInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    @Autowired
    IAdminService adminService;

    @Autowired
    private IUserInformationService userInformationService;

    @GetMapping("/user")
    public ResponseDto<List<ListAccountResponse>> getAllUserByAdmin(){
        List<ListAccountResponse> listAccountResponses = adminService.getAllUserByAdmin();
        return new ResponseDto<>(HttpStatus.OK.value(),"successfully", listAccountResponses);
    }

    @PutMapping("/block-user/{id}")
    public ResponseDto<Void> blockUser(@PathVariable String id) {
        adminService.blockUser(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Chặn người dùng thành công", null);
    }

    @PutMapping("/active-user/{id}")
    public ResponseDto<Void> activeUser(@PathVariable String id) {
        adminService.activeUser(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Người dùng đã được kích hoạt", null);
    }

    @GetMapping("/viewDetailUser/{id}")
    public ResponseDto<UserInformationResponse> getUserInformation(@PathVariable String id) {
        try {
            UserInformationResponse response = adminService.getUserInformation(id);
            return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thông tin người dùng thành công", response);
        } catch (NotFoundException e) {
            return new ResponseDto<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Đã xảy ra lỗi khi lấy thông tin người dùng", null);
        }
    }

    // ========== POST MANAGEMENT ==========

    @GetMapping("/posts")
    public ResponseDto<Page<AdminPostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AdminPostResponse> posts = adminService.getAllPostsByAdmin(page, size);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy danh sách bài viết thành công", posts);
    }

    @GetMapping("/posts/{id}")
    public ResponseDto<AdminPostResponse> getPostById(@PathVariable String id) {
        AdminPostResponse post = adminService.getPostByIdByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thông tin bài viết thành công", post);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseDto<Void> deletePost(@PathVariable String id) {
        adminService.deletePostByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Xóa bài viết thành công", null);
    }

    @PutMapping("/posts/{id}/restore")
    public ResponseDto<Void> restorePost(@PathVariable String id) {
        adminService.restorePostByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Khôi phục bài viết thành công", null);
    }

    // ========== RESTAURANT MANAGEMENT ==========

    @GetMapping("/restaurants")
    public ResponseDto<Page<AdminRestaurantResponse>> getAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AdminRestaurantResponse> restaurants = adminService.getAllRestaurantsByAdmin(page, size);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy danh sách nhà hàng thành công", restaurants);
    }

    @GetMapping("/restaurants/{id}")
    public ResponseDto<AdminRestaurantResponse> getRestaurantById(@PathVariable String id) {
        AdminRestaurantResponse restaurant = adminService.getRestaurantByIdByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thông tin nhà hàng thành công", restaurant);
    }

    @DeleteMapping("/restaurants/{id}")
    public ResponseDto<Void> deleteRestaurant(@PathVariable String id) {
        adminService.deleteRestaurantByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Xóa nhà hàng thành công", null);
    }

    @PutMapping("/restaurants/{id}/restore")
    public ResponseDto<Void> restoreRestaurant(@PathVariable String id) {
        adminService.restoreRestaurantByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Khôi phục nhà hàng thành công", null);
    }

    // ========== TRANSACTION MANAGEMENT ==========

    @GetMapping("/transactions")
    public ResponseDto<Page<AdminTransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AdminTransactionResponse> transactions = adminService.getAllTransactionsByAdmin(page, size);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy danh sách giao dịch thành công", transactions);
    }

    @GetMapping("/transactions/{id}")
    public ResponseDto<AdminTransactionResponse> getTransactionById(@PathVariable String id) {
        AdminTransactionResponse transaction = adminService.getTransactionByIdByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thông tin giao dịch thành công", transaction);
    }

    @GetMapping("/transactions/status/{status}")
    public ResponseDto<Page<AdminTransactionResponse>> getTransactionsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AdminTransactionResponse> transactions = adminService.getTransactionsByStatusByAdmin(status, page, size);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy giao dịch theo trạng thái thành công", transactions);
    }

    @GetMapping("/transactions/all")
    public ResponseDto<List<AdminTransactionResponse>> getAllTransactionsList() {
        List<AdminTransactionResponse> transactions = adminService.getAllTransactionsListByAdmin();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy toàn bộ giao dịch thành công", transactions);
    }

    // ========== COMMENT MANAGEMENT ==========

    @GetMapping("/comments")
    public ResponseDto<Page<AdminCommentResponse>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AdminCommentResponse> comments = adminService.getAllCommentsByAdmin(page, size);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy danh sách comment thành công", comments);
    }

    @GetMapping("/comments/{id}")
    public ResponseDto<AdminCommentResponse> getCommentById(@PathVariable String id) {
        AdminCommentResponse comment = adminService.getCommentByIdByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thông tin comment thành công", comment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseDto<Void> deleteComment(@PathVariable String id) {
        adminService.deleteCommentByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Xóa comment thành công", null);
    }

    @PutMapping("/comments/{id}/restore")
    public ResponseDto<Void> restoreComment(@PathVariable String id) {
        adminService.restoreCommentByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Khôi phục comment thành công", null);
    }

    @GetMapping("/comments/post/{postId}")
    public ResponseDto<Page<AdminCommentResponse>> getCommentsByPost(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AdminCommentResponse> comments = adminService.getCommentsByPostByAdmin(postId, page, size);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy comment theo bài viết thành công", comments);
    }

    @GetMapping("/comments/statistics")
    public ResponseDto<Map<String, Object>> getCommentStatistics() {
        Map<String, Object> statistics = adminService.getCommentStatistics();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thống kê comment thành công", statistics);
    }

    // ========== TAG MANAGEMENT ==========

    @GetMapping("/tags")
    public ResponseDto<Page<AdminTagResponse>> getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AdminTagResponse> tags = adminService.getAllTagsByAdmin(page, size);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy danh sách tag thành công", tags);
    }

    @GetMapping("/tags/{id}")
    public ResponseDto<AdminTagResponse> getTagById(@PathVariable String id) {
        AdminTagResponse tag = adminService.getTagByIdByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thông tin tag thành công", tag);
    }

    @PostMapping("/tags")
    public ResponseDto<AdminTagResponse> createTag(
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        AdminTagResponse tag = adminService.createTagByAdmin(name, description);
        return new ResponseDto<>(HttpStatus.CREATED.value(), "Tạo tag thành công", tag);
    }

    @PutMapping("/tags/{id}")
    public ResponseDto<AdminTagResponse> updateTag(
            @PathVariable String id,
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        AdminTagResponse tag = adminService.updateTagByAdmin(id, name, description);
        return new ResponseDto<>(HttpStatus.OK.value(), "Cập nhật tag thành công", tag);
    }

    @DeleteMapping("/tags/{id}")
    public ResponseDto<Void> deleteTag(@PathVariable String id) {
        adminService.deleteTagByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Xóa tag thành công", null);
    }

    @GetMapping("/tags/statistics")
    public ResponseDto<Map<String, Object>> getTagStatistics() {
        Map<String, Object> statistics = adminService.getTagStatistics();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thống kê tag thành công", statistics);
    }

    // ========== ADVANCED STATISTICS ==========

    @GetMapping("/statistics")
    public ResponseDto<Map<String, Object>> getSystemStatistics() {
        Map<String, Object> statistics = adminService.getSystemStatistics();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thống kê hệ thống thành công", statistics);
    }

    @GetMapping("/statistics/users")
    public ResponseDto<StatisticsResponse> getUserStatistics() {
        StatisticsResponse statistics = adminService.getUserStatistics();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thống kê users thành công", statistics);
    }

    @GetMapping("/statistics/posts")
    public ResponseDto<StatisticsResponse> getPostStatistics() {
        StatisticsResponse statistics = adminService.getPostStatistics();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thống kê posts thành công", statistics);
    }

    @GetMapping("/statistics/restaurants")
    public ResponseDto<StatisticsResponse> getRestaurantStatistics() {
        StatisticsResponse statistics = adminService.getRestaurantStatistics();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thống kê restaurants thành công", statistics);
    }

    @GetMapping("/statistics/revenue")
    public ResponseDto<StatisticsResponse> getRevenueStatistics() {
        StatisticsResponse statistics = adminService.getRevenueStatistics();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thống kê doanh thu thành công", statistics);
    }

    @GetMapping("/statistics/engagement")
    public ResponseDto<StatisticsResponse> getEngagementStatistics() {
        StatisticsResponse statistics = adminService.getEngagementStatistics();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thống kê tương tác thành công", statistics);
    }

    @GetMapping("/statistics/challenges")
    public ResponseDto<StatisticsResponse> getChallengeStatistics() {
        StatisticsResponse statistics = adminService.getChallengeStatistics();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thống kê challenges thành công", statistics);
    }

    // ========== REVENUE REPORT ==========

    @GetMapping("/revenue/report")
    public ResponseDto<RevenueReportResponse> getRevenueReport() {
        RevenueReportResponse report = adminService.getRevenueReport();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy báo cáo doanh thu thành công", report);
    }

    @GetMapping("/revenue/export-csv")
    public ResponseEntity<byte[]> exportRevenueReportToCsv() {
        byte[] csvData = adminService.exportRevenueReportToCsv();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "bao_cao_doanh_thu_" +
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
        headers.setContentLength(csvData.length);

        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }

    // ========== MOOD MANAGEMENT ==========

    @GetMapping("/moods")
    public ResponseDto<List<AdminMoodResponse>> getAllMoods() {
        List<AdminMoodResponse> moods = adminService.getAllMoodsByAdmin();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy danh sách moods thành công", moods);
    }

    @GetMapping("/moods/{id}")
    public ResponseDto<AdminMoodResponse> getMoodById(@PathVariable String id) {
        AdminMoodResponse mood = adminService.getMoodByIdByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thông tin mood thành công", mood);
    }

    // ========== CHALLENGE MANAGEMENT ==========

    @GetMapping("/challenges")
    public ResponseDto<List<ChallengeDefinitionResponse>> getAllChallenges() {
        List<ChallengeDefinitionResponse> challenges = adminService.getAllChallengesByAdmin();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy danh sách challenges thành công", challenges);
    }

    @GetMapping("/challenges/{id}")
    public ResponseDto<ChallengeDefinitionResponse> getChallengeById(@PathVariable String id) {
        ChallengeDefinitionResponse challenge = adminService.getChallengeByIdByAdmin(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thông tin challenge thành công", challenge);
    }

}
