package com.exe201.color_bites_be.repository;

import com.exe201.color_bites_be.entity.Transaction;
import com.exe201.color_bites_be.enums.TransactionEnums.TxnStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    
    /**
     * Tìm transaction theo orderCode
     */
    Optional<Transaction> findByOrderCode(String orderCode);
    
    /**
     * Tìm transaction theo providerTxnId
     */
    Optional<Transaction> findByProviderTxnId(String providerTxnId);
    
    /**
     * Tìm tất cả transactions của user
     */
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(String accountId);
    
    /**
     * Tìm transactions theo status
     */
    List<Transaction> findByAccountIdAndStatus(String accountId, TxnStatus status);
    
    /**
     * Kiểm tra orderCode đã tồn tại chưa
     */
    boolean existsByOrderCode(String orderCode);
    
    /**
     * Kiểm tra providerTxnId đã tồn tại chưa
     */
    boolean existsByProviderTxnId(String providerTxnId);
}
