package Dal;

import Model.UserBalance;

import java.math.BigDecimal;
import java.sql.*;

/**
 * DAO đơn giản cho Balance (tương thích với SubscriptionService)
 * Wrapper around UserBalanceDAO với interface đơn giản hơn
 */
public class BalanceDAO {

    private final UserBalanceDAO userBalanceDAO = new UserBalanceDAO();

    /**
     * Lấy balance của user
     */
    public UserBalance getUserBalance(int userId) {
        return userBalanceDAO.getByUserId(userId);
    }

    /**
     * Cập nhật balance của user
     */
    public boolean updateBalance(int userId, BigDecimal amount, String description, String type) {
        try {
            UserBalance currentBalance = userBalanceDAO.getByUserId(userId);
            
            if (currentBalance == null) {
                // Tạo balance mới nếu chưa có
                UserBalance newBalance = new UserBalance();
                newBalance.setUserId(userId);
                newBalance.setTotalCoins(amount.max(BigDecimal.ZERO)); // Không cho phép âm
                newBalance.setSpentCoins(BigDecimal.ZERO);
                newBalance.setAvailableCoins(amount.max(BigDecimal.ZERO));
                newBalance.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                newBalance.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                
                int balanceId = userBalanceDAO.create(newBalance);
                return balanceId > 0;
            } else {
                // Cập nhật balance hiện có
                BigDecimal newAvailableCoins = currentBalance.getAvailableCoins().add(amount);
                
                // Kiểm tra không cho phép số dư âm
                if (newAvailableCoins.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("BalanceDAO: Cannot update balance - insufficient funds. Current: " + 
                                     currentBalance.getAvailableCoins() + ", Trying to deduct: " + amount.abs());
                    return false;
                }
                
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    // Thêm xu
                    currentBalance.setTotalCoins(currentBalance.getTotalCoins().add(amount));
                } else {
                    // Trừ xu (tiêu)
                    currentBalance.setSpentCoins(currentBalance.getSpentCoins().add(amount.abs()));
                }
                
                currentBalance.setAvailableCoins(newAvailableCoins);
                currentBalance.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                
                return userBalanceDAO.update(currentBalance);
            }
            
        } catch (Exception e) {
            System.out.println("BalanceDAO.updateBalance error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra user có đủ xu không
     */
    public boolean hasEnoughBalance(int userId, BigDecimal requiredAmount) {
        UserBalance balance = getUserBalance(userId);
        if (balance == null) {
            return false;
        }
        return balance.getAvailableCoins().compareTo(requiredAmount) >= 0;
    }

    /**
     * Thêm xu vào tài khoản
     */
    public boolean addCoins(int userId, BigDecimal amount, String description) {
        return updateBalance(userId, amount, description, "add");
    }

    /**
     * Trừ xu từ tài khoản
     */
    public boolean subtractCoins(int userId, BigDecimal amount, String description) {
        return updateBalance(userId, amount.negate(), description, "subtract");
    }
}