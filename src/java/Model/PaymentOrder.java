package Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model cho bảng payment_orders - Lưu thông tin đơn hàng thanh toán
 */
public class PaymentOrder {
    private int orderId;
    private int userId;
    private String orderCode;              // Mã đơn hàng unique
    private BigDecimal amount;             // Số tiền VNĐ
    private BigDecimal coinsAmount;        // Số xu được nạp
    private String paymentMethod;          // momo (MoMo Payment Gateway)
    private String status;                 // PENDING, PROCESSING, SUCCESS, FAILED, CANCELLED
    private String gatewayOrderId;         // ID từ payment gateway
    private String gatewayTransactionId;   // Transaction ID từ gateway
    private String description;
    private String callbackData;           // JSON data từ callback
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp expiresAt;           // Thời gian hết hạn thanh toán (đổi tên từ expiredAt)
    
    // Constructors
    public PaymentOrder() {}
    
    public PaymentOrder(int userId, String orderCode, BigDecimal amount, BigDecimal coinsAmount, 
                       String paymentMethod, String description) {
        this.userId = userId;
        this.orderCode = orderCode;
        this.amount = amount;
        this.coinsAmount = coinsAmount;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public BigDecimal getCoinsAmount() { return coinsAmount; }
    public void setCoinsAmount(BigDecimal coinsAmount) { this.coinsAmount = coinsAmount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getGatewayOrderId() { return gatewayOrderId; }
    public void setGatewayOrderId(String gatewayOrderId) { this.gatewayOrderId = gatewayOrderId; }
    
    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public void setGatewayTransactionId(String gatewayTransactionId) { this.gatewayTransactionId = gatewayTransactionId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCallbackData() { return callbackData; }
    public void setCallbackData(String callbackData) { this.callbackData = callbackData; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public Timestamp getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Timestamp expiresAt) { this.expiresAt = expiresAt; }
    
    // Utility methods
    public boolean isExpired() {
        return expiresAt != null && expiresAt.before(new Timestamp(System.currentTimeMillis()));
    }
    
    public boolean isPending() {
        return "PENDING".equals(status) || "PROCESSING".equals(status);
    }
    
    public boolean isSuccess() {
        return "SUCCESS".equals(status);
    }
    
    public boolean canProcess() {
        return isPending() && !isExpired();
    }
    
    public long getMinutesUntilExpired() {
        if (expiresAt == null) return 0;
        
        long diffMs = expiresAt.getTime() - System.currentTimeMillis();
        return Math.max(0, diffMs / (60 * 1000));
    }
    
    @Override
    public String toString() {
        return "PaymentOrder{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", orderCode='" + orderCode + '\'' +
                ", amount=" + amount +
                ", coinsAmount=" + coinsAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}