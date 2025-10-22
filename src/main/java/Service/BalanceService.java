package Service;

import Model.UserBalance;
import Model.CoinTransaction;
import Dal.UserBalanceDAO;
import Dal.CoinTransactionDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import DBcontext.DBContext;

/**
 * Service để quản lý balance và coin transactions
 * Quan trọng: Phải đảm bảo tính toàn vẹn dữ liệu khi xử lý balance
 */
public class BalanceService {
    
    private final UserBalanceDAO balanceDAO = new UserBalanceDAO();
    private final CoinTransactionDAO transactionDAO = new CoinTransactionDAO();
    
    /**
     * Lấy balance của user
     */
    public UserBalance getUserBalance(int userId) {
        UserBalance balance = balanceDAO.getByUserId(userId);
        
        // Nếu chưa có balance, tạo mới với 0 xu
        if (balance == null) {
            balance = createInitialBalance(userId);
        }
        
        return balance;
    }
    
    /**
     * Tạo balance ban đầu cho user mới
     */
    public UserBalance createInitialBalance(int userId) {
        UserBalance balance = new UserBalance();
        balance.setUserId(userId);
        balance.setTotalCoins(BigDecimal.ZERO);
        balance.setSpentCoins(BigDecimal.ZERO);
        balance.setAvailableCoins(BigDecimal.ZERO);
        
        int balanceId = balanceDAO.create(balance);
        if (balanceId > 0) {
            balance.setBalanceId(balanceId);
            return balance;
        }
        
        return null;
    }
    
    /**
     * Nạp xu vào tài khoản (từ payment)
     * Sử dụng transaction để đảm bảo atomic
     */
    public boolean addCoins(int userId, double amount, String referenceId, String description) {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            
            // Lấy balance hiện tại
            UserBalance currentBalance = getUserBalance(userId);
            if (currentBalance == null) {
                currentBalance = createInitialBalance(userId);
                if (currentBalance == null) {
                    throw new SQLException("Cannot create balance for user");
                }
            }
            
            BigDecimal addAmount = BigDecimal.valueOf(amount);
            BigDecimal balanceBefore = currentBalance.getAvailableCoins();
            BigDecimal balanceAfter = balanceBefore.add(addAmount);
            
            // Cập nhật balance
            currentBalance.setTotalCoins(currentBalance.getTotalCoins().add(addAmount));
            currentBalance.setAvailableCoins(balanceAfter);
            
            boolean balanceUpdated = balanceDAO.update(currentBalance);
            if (!balanceUpdated) {
                throw new SQLException("Failed to update balance");
            }
            
            // Tạo coin transaction record
            CoinTransaction transaction = new CoinTransaction();
            transaction.setUserId(userId);
            transaction.setTransactionType("payment");
            transaction.setAmount(addAmount);
            transaction.setDescription(description != null ? description : "Nạp xu từ payment");
            transaction.setReferenceId(referenceId);
            transaction.setReferenceType("payment");
            transaction.setBalanceBefore(balanceBefore);
            transaction.setBalanceAfter(balanceAfter);
            
            int transactionId = transactionDAO.create(transaction);
            if (transactionId <= 0) {
                throw new SQLException("Failed to create transaction record");
            }
            
            conn.commit();
            System.out.println("BalanceService: Added " + amount + " coins to user " + userId + 
                             " (reference: " + referenceId + ")");
            return true;
            
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Rollback error: " + ex.getMessage());
            }
            
            System.out.println("BalanceService.addCoins error: " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Connection close error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Trừ xu (khi mua Pro)
     * Sử dụng transaction để đảm bảo atomic
     */
    public boolean spendCoins(int userId, double amount, String description, String referenceType) {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);
            
            UserBalance currentBalance = getUserBalance(userId);
            if (currentBalance == null) {
                throw new SQLException("User balance not found");
            }
            
            BigDecimal spendAmount = BigDecimal.valueOf(amount);
            BigDecimal balanceBefore = currentBalance.getAvailableCoins();
            
            // Kiểm tra đủ xu không
            if (balanceBefore.compareTo(spendAmount) < 0) {
                throw new IllegalArgumentException("Insufficient coins. Available: " + 
                                                 balanceBefore + ", Required: " + spendAmount);
            }
            
            BigDecimal balanceAfter = balanceBefore.subtract(spendAmount);
            
            // Cập nhật balance
            currentBalance.setSpentCoins(currentBalance.getSpentCoins().add(spendAmount));
            currentBalance.setAvailableCoins(balanceAfter);
            
            boolean balanceUpdated = balanceDAO.update(currentBalance);
            if (!balanceUpdated) {
                throw new SQLException("Failed to update balance");
            }
            
            // Tạo transaction record (số âm cho việc tiêu)
            CoinTransaction transaction = new CoinTransaction();
            transaction.setUserId(userId);
            transaction.setTransactionType("subscription");
            transaction.setAmount(spendAmount.negate()); // Âm số
            transaction.setDescription(description != null ? description : "Mua gói Pro");
            transaction.setReferenceType(referenceType != null ? referenceType : "pro_purchase");
            transaction.setBalanceBefore(balanceBefore);
            transaction.setBalanceAfter(balanceAfter);
            
            int transactionId = transactionDAO.create(transaction);
            if (transactionId <= 0) {
                throw new SQLException("Failed to create transaction record");
            }
            
            conn.commit();
            System.out.println("BalanceService: Spent " + amount + " coins for user " + userId);
            return true;
            
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Rollback error: " + ex.getMessage());
            }
            
            System.out.println("BalanceService.spendCoins error: " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Connection close error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Kiểm tra user có đủ xu không
     */
    public boolean hasEnoughCoins(int userId, double requiredAmount) {
        UserBalance balance = getUserBalance(userId);
        if (balance == null) return false;
        
        return balance.getAvailableCoins().compareTo(BigDecimal.valueOf(requiredAmount)) >= 0;
    }
    
    /**
     * Lấy số xu available của user
     */
    public double getAvailableCoins(int userId) {
        UserBalance balance = getUserBalance(userId);
        return balance != null ? balance.getAvailableCoins().doubleValue() : 0.0;
    }
    
    /**
     * Lấy lịch sử giao dịch xu của user
     */
    public java.util.List<CoinTransaction> getTransactionHistory(int userId, int limit) {
        return transactionDAO.getByUserId(userId, limit);
    }
    
    /**
     * Refresh balance từ database (tính lại từ transactions)
     * Sử dụng khi cần đảm bảo data integrity
     */
    public boolean refreshBalance(int userId) {
        try {
            // Tính lại balance từ tất cả transactions
            BigDecimal totalAdded = transactionDAO.getTotalAddedCoins(userId);
            BigDecimal totalSpent = transactionDAO.getTotalSpentCoins(userId);
            BigDecimal availableCoins = totalAdded.add(totalSpent); // totalSpent đã là số âm
            
            UserBalance balance = getUserBalance(userId);
            if (balance != null) {
                balance.setTotalCoins(totalAdded);
                balance.setSpentCoins(totalSpent.abs()); // Lưu số dương
                balance.setAvailableCoins(availableCoins);
                
                return balanceDAO.update(balance);
            }
            
        } catch (Exception e) {
            System.out.println("BalanceService.refreshBalance error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}