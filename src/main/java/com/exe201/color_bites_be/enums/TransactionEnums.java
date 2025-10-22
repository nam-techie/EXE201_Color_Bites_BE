package com.exe201.color_bites_be.enums;

/**
 * Tất cả enums liên quan đến Transaction
 */
public class TransactionEnums {
    
    public enum TxnStatus {
        PENDING, SUCCESS, FAILED, CANCELED
    }
    
    public enum TxnType {
        PAYMENT, REFUND
    }
}
