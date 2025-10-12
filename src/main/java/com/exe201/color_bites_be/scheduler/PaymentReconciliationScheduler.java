package com.exe201.color_bites_be.scheduler;

import com.exe201.color_bites_be.entity.Transaction;
import com.exe201.color_bites_be.enums.TransactionEnums.TxnStatus;
import com.exe201.color_bites_be.repository.TransactionRepository;
import com.exe201.color_bites_be.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled Job để đồng bộ trạng thái thanh toán với PayOS
 * Chạy định kỳ để xử lý các transaction PENDING bị mất webhook
 * 
 * Reconciliation Strategy:
 * - Quét transactions PENDING trong 24h gần nhất
 * - Gọi PayOS API để lấy trạng thái mới nhất
 * - Cập nhật DB nếu có thay đổi
 * - Log các giao dịch bất thường
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentReconciliationScheduler {
    
    private final TransactionRepository transactionRepository;
    private final IPaymentService paymentService;
    
    /**
     * Chạy reconciliation mỗi 1 phút (NHANH HƠN CHO TESTING/DEVELOPMENT)
     * Cron: 0 STAR/1 * * * * = Every 1 minute at second 0
     * (STAR = *)
     * Production nên để 5 phút: "0 STAR/5 * * * *"
     */
    @Scheduled(cron = "0 */1 * * * *")
    public void reconcilePendingTransactions() {
        reconcile();
    }
    
    /**
     * Method công khai để trigger reconciliation manually
     * Có thể gọi từ controller hoặc test
     */
    public void reconcile() {
        log.info("=== BẮT ĐẦU PAYMENT RECONCILIATION JOB ===");
        
        try {
            // 1. Tìm tất cả transactions PENDING trong 24h gần nhất
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
            List<Transaction> pendingTransactions = transactionRepository
                .findPendingTransactionsSince(cutoffTime);
            
            if (pendingTransactions.isEmpty()) {
                log.info("Không có transaction PENDING nào cần reconcile");
                return;
            }
            
            log.info("Tìm thấy {} transaction(s) PENDING cần kiểm tra", pendingTransactions.size());
            
            // 2. Xử lý từng transaction
            int updatedCount = 0;
            int errorCount = 0;
            int unchangedCount = 0;
            
            for (Transaction transaction : pendingTransactions) {
                try {
                    ReconcileResult result = reconcileTransaction(transaction);
                    
                    switch (result) {
                        case UPDATED -> updatedCount++;
                        case UNCHANGED -> unchangedCount++;
                        case ERROR -> errorCount++;
                    }
                    
                } catch (Exception e) {
                    log.error("Lỗi reconcile transaction {}: {}", 
                        transaction.getOrderCode(), e.getMessage());
                    errorCount++;
                }
            }
            
            // 3. Log summary
            log.info("=== KẾT QUẢ RECONCILIATION JOB ===");
            log.info("Tổng số transactions kiểm tra: {}", pendingTransactions.size());
            log.info("Đã cập nhật: {}", updatedCount);
            log.info("Không thay đổi: {}", unchangedCount);
            log.info("Lỗi: {}", errorCount);
            
        } catch (Exception e) {
            log.error("Lỗi chạy reconciliation job: ", e);
        }
    }
    
    /**
     * Reconcile một transaction cụ thể
     * Gọi PayOS API để lấy trạng thái mới nhất và cập nhật DB
     */
    private ReconcileResult reconcileTransaction(Transaction transaction) {
        String orderCode = transaction.getOrderCode();
        TxnStatus oldStatus = transaction.getStatus();
        
        log.info("Reconciling transaction: orderCode={}, currentStatus={}, age={} minutes",
            orderCode, oldStatus, getTransactionAgeMinutes(transaction));
        
        try {
            // Gọi PayOS để lấy trạng thái mới nhất
            // Sử dụng orderCode hoặc providerTxnId
            String queryId = transaction.getProviderTxnId() != null 
                ? transaction.getProviderTxnId() 
                : orderCode;
            
            // Gọi service để update từ gateway
            paymentService.updateStatusFromGateway(queryId);
            
            // Lấy lại transaction để check xem có update không
            Transaction updatedTx = transactionRepository.findByOrderCode(orderCode)
                .orElse(transaction);
            
            if (updatedTx.getStatus() != oldStatus) {
                log.info("✅ Transaction {} đã được cập nhật: {} → {}", 
                    orderCode, oldStatus, updatedTx.getStatus());
                return ReconcileResult.UPDATED;
            } else {
                log.debug("Transaction {} vẫn giữ status: {}", orderCode, oldStatus);
                
                // Log cảnh báo nếu transaction quá lâu mà vẫn PENDING
                long ageMinutes = getTransactionAgeMinutes(transaction);
                if (ageMinutes > 60) { // Hơn 1 giờ
                    log.warn("⚠️ Transaction {} đã PENDING {} phút - Cần kiểm tra thủ công!", 
                        orderCode, ageMinutes);
                }
                
                return ReconcileResult.UNCHANGED;
            }
            
        } catch (Exception e) {
            log.error("❌ Lỗi reconcile transaction {}: {}", orderCode, e.getMessage());
            return ReconcileResult.ERROR;
        }
    }
    
    /**
     * Tính tuổi của transaction (phút)
     */
    private long getTransactionAgeMinutes(Transaction transaction) {
        return java.time.Duration.between(
            transaction.getCreatedAt(), 
            LocalDateTime.now()
        ).toMinutes();
    }
    
    /**
     * Enum kết quả reconcile
     */
    private enum ReconcileResult {
        UPDATED,    // Transaction được cập nhật trạng thái
        UNCHANGED,  // Transaction vẫn giữ nguyên trạng thái
        ERROR       // Có lỗi xảy ra khi reconcile
    }
}

