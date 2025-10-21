package Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model cho bảng coin_transactions - Lịch sử giao dịch xu
 */
public class CoinTransaction {
    private int transactionId;
    private int userId;
    private String transactionType;     // 'payment', 'subscription', 'refund'
    private BigDecimal amount;          // Số xu (dương = nạp, âm = tiêu)
    private String description;
    private String referenceId;         // ID liên quan (payment order, subscription, etc.)
    private String referenceType;       // 'qr_payment', 'pro_purchase', etc.
    private BigDecimal balanceBefore;   // Số dư trước giao dịch
    private BigDecimal balanceAfter;    // Số dư sau giao dịch
    private Timestamp createdAt;
    
    // Constructors
    public CoinTransaction() {}
    
    public CoinTransaction(int userId, String transactionType, BigDecimal amount, String description) {
        this.userId = userId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
    }
    
    // Getters and Setters
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    
    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
    
    public BigDecimal getBalanceBefore() { return balanceBefore; }
    public void setBalanceBefore(BigDecimal balanceBefore) { this.balanceBefore = balanceBefore; }
    
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    // Utility methods
    public boolean isPositive() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isNegative() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }
    
    public double getAmountAsDouble() {
        return amount != null ? amount.doubleValue() : 0.0;
    }
    
    public double getBalanceBeforeAsDouble() {
        return balanceBefore != null ? balanceBefore.doubleValue() : 0.0;
    }
    
    public double getBalanceAfterAsDouble() {
        return balanceAfter != null ? balanceAfter.doubleValue() : 0.0;
    }
    
    /**
     * Format amount for display with + or - sign
     */
    public String getAmountFormatted() {
        if (amount == null) return "0 xu";
        
        double value = amount.doubleValue();
        if (value >= 0) {
            return String.format("+%.0f xu", value);
        } else {
            return String.format("%.0f xu", value); // Số âm đã có dấu -
        }
    }
    
    /**
     * Get transaction type display name
     */
    public String getTransactionTypeDisplay() {
        if (transactionType == null) return "Unknown";
        
        switch (transactionType.toLowerCase()) {
            case "payment":
                return "Nạp xu";
            case "subscription":
                return "Mua Pro";
            case "refund":
                return "Hoàn xu";
            default:
                return transactionType;
        }
    }
    
    /**
     * Get reference type display name
     */
    public String getReferenceTypeDisplay() {
        if (referenceType == null) return "";
        
        switch (referenceType.toLowerCase()) {
            case "qr_payment":
                return "QR Banking";
            case "momo_payment":
                return "MoMo";
            case "vnpay_payment":
                return "VNPay";
            case "pro_purchase":
                return "Gói Pro";
            default:
                return referenceType;
        }
    }
    
    /**
     * Check if this is a payment transaction (adding coins)
     */
    public boolean isPaymentTransaction() {
        return "payment".equalsIgnoreCase(transactionType);
    }
    
    /**
     * Check if this is a subscription transaction (spending coins)
     */
    public boolean isSubscriptionTransaction() {
        return "subscription".equalsIgnoreCase(transactionType);
    }
    
    /**
     * Check if this is a refund transaction
     */
    public boolean isRefundTransaction() {
        return "refund".equalsIgnoreCase(transactionType);
    }
    
    /**
     * Get CSS class for transaction type (for UI styling)
     */
    public String getTransactionCssClass() {
        if (isPositive()) {
            return "transaction-positive";
        } else if (isNegative()) {
            return "transaction-negative";
        } else {
            return "transaction-neutral";
        }
    }
    
    @Override
    public String toString() {
        return "CoinTransaction{" +
                "transactionId=" + transactionId +
                ", userId=" + userId +
                ", transactionType='" + transactionType + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", referenceId='" + referenceId + '\'' +
                ", referenceType='" + referenceType + '\'' +
                ", balanceBefore=" + balanceBefore +
                ", balanceAfter=" + balanceAfter +
                ", createdAt=" + createdAt +
                '}';
    }
}