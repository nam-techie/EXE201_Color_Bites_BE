package com.exe201.color_bites_be.service;


/**
 * Interface định nghĩa các phương thức quản lý đơn hàng
 * TODO: Implement khi phát triển tính năng đặt hàng
 */
public interface IOrderService {
    
    /**
     * Tạo đơn hàng mới
     * TODO: Define CreateOrderRequest DTO
     */
    // OrderResponse createOrder(String accountId, CreateOrderRequest request);
    
    /**
     * Lấy đơn hàng theo ID
     * TODO: Define OrderResponse DTO
     */
    // OrderResponse readOrderById(String orderId, String currentAccountId);
    
    /**
     * Lấy danh sách đơn hàng của người dùng
     * TODO: Define OrderResponse DTO
     */
    // Page<OrderResponse> readUserOrders(String accountId, int page, int size);
    
    /**
     * Cập nhật trạng thái đơn hàng
     * TODO: Define OrderStatus enum
     */
    // void updateOrderStatus(String orderId, OrderStatus status);
    
    /**
     * Hủy đơn hàng
     */
    // void cancelOrder(String orderId, String accountId);
    
    /**
     * Tính tổng tiền đơn hàng
     */
    // OrderTotal calculateOrderTotal(String orderId);
}
