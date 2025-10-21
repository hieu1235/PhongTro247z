package Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model cho bảng user_balance - Quản lý số dư xu của user
 */
public class UserBalance {
    private int balanceId;
    private int userId;
    private BigDecimal totalCoins;      // Tổng số xu đã nạp
    private BigDecimal spentCoins;      // Số xu đã tiêu
    private BigDecimal availableCoins;  // Số xu có thể sử dụng (totalCoins - spentCoins)
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructors
    public UserBalance() {}
    
    public UserBalance(int userId) {
        this.userId = userId;
        this.totalCoins = BigDecimal.ZERO;
        this.spentCoins = BigDecimal.ZERO;
        this.availableCoins = BigDecimal.ZERO;
    }
    
    public UserBalance(int userId, BigDecimal totalCoins, BigDecimal spentCoins, BigDecimal availableCoins) {
        this.userId = userId;
        this.totalCoins = totalCoins;
        this.spentCoins = spentCoins;
        this.availableCoins = availableCoins;
    }
    
    // Getters and Setters
    public int getBalanceId() { return balanceId; }
    public void setBalanceId(int balanceId) { this.balanceId = balanceId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public BigDecimal getTotalCoins() { return totalCoins; }
    public void setTotalCoins(BigDecimal totalCoins) { this.totalCoins = totalCoins; }
    
    public BigDecimal getSpentCoins() { return spentCoins; }
    public void setSpentCoins(BigDecimal spentCoins) { this.spentCoins = spentCoins; }
    
    public BigDecimal getAvailableCoins() { return availableCoins; }
    public void setAvailableCoins(BigDecimal availableCoins) { this.availableCoins = availableCoins; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    // Utility methods
    public boolean hasEnoughCoins(double requiredAmount) {
        return availableCoins.compareTo(BigDecimal.valueOf(requiredAmount)) >= 0;
    }
    
    public boolean hasEnoughCoins(BigDecimal requiredAmount) {
        return availableCoins.compareTo(requiredAmount) >= 0;
    }
    
    public double getAvailableCoinsAsDouble() {
        return availableCoins != null ? availableCoins.doubleValue() : 0.0;
    }
    
    public double getTotalCoinsAsDouble() {
        return totalCoins != null ? totalCoins.doubleValue() : 0.0;
    }
    
    public double getSpentCoinsAsDouble() {
        return spentCoins != null ? spentCoins.doubleValue() : 0.0;
    }
    
    /**
     * Recalculate available coins from total and spent
     */
    public void recalculateAvailableCoins() {
        if (totalCoins != null && spentCoins != null) {
            this.availableCoins = totalCoins.subtract(spentCoins);
        }
    }
    
    /**
     * Check if balance is consistent
     */
    public boolean isConsistent() {
        if (totalCoins == null || spentCoins == null || availableCoins == null) {
            return false;
        }
        
        BigDecimal calculated = totalCoins.subtract(spentCoins);
        return calculated.compareTo(availableCoins) == 0;
    }
    
    /**
     * Format coins for display
     */
    public String getAvailableCoinsFormatted() {
        return String.format("%.0f xu", getAvailableCoinsAsDouble());
    }
    
    public String getTotalCoinsFormatted() {
        return String.format("%.0f xu", getTotalCoinsAsDouble());
    }
    
    @Override
    public String toString() {
        return "UserBalance{" +
                "balanceId=" + balanceId +
                ", userId=" + userId +
                ", totalCoins=" + totalCoins +
                ", spentCoins=" + spentCoins +
                ", availableCoins=" + availableCoins +
                ", createdAt=" + createdAt +
                '}';
    }
}