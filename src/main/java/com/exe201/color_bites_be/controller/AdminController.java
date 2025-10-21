package com.exe201.color_bites_be.controller;

import com.exe201.color_bites_be.dto.response.ListAccountResponse;
import com.exe201.color_bites_be.dto.response.AdminPostResponse;
import com.exe201.color_bites_be.dto.response.AdminRestaurantResponse;
import com.exe201.color_bites_be.dto.response.AdminTransactionResponse;
import com.exe201.color_bites_be.dto.response.ResponseDto;
import com.exe201.color_bites_be.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

    // ========== STATISTICS ==========

    @GetMapping("/statistics")
    public ResponseDto<Map<String, Object>> getSystemStatistics() {
        Map<String, Object> statistics = adminService.getSystemStatistics();
        return new ResponseDto<>(HttpStatus.OK.value(), "Lấy thống kê hệ thống thành công", statistics);
    }

}
