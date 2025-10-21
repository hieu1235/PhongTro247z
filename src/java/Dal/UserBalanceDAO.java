package Dal;

import Model.UserBalance;
import DBcontext.DBContext;

import java.math.BigDecimal;
import java.sql.*;

/**
 * DAO để quản lý user_balance
 */
public class UserBalanceDAO {
    
    /**
     * Tạo balance mới cho user
     */
    public int create(UserBalance balance) {
        String sql = "INSERT INTO user_balance (user_id, total_coins, spent_coins, available_coins) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, balance.getUserId());
            stmt.setBigDecimal(2, balance.getTotalCoins());
            stmt.setBigDecimal(3, balance.getSpentCoins());
            stmt.setBigDecimal(4, balance.getAvailableCoins());
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.out.println("UserBalanceDAO.create error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Lấy balance theo user ID
     */
    public UserBalance getByUserId(int userId) {
        String sql = "SELECT * FROM user_balance WHERE user_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserBalance(rs);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("UserBalanceDAO.getByUserId error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lấy balance theo balance ID
     */
    public UserBalance getById(int balanceId) {
        String sql = "SELECT * FROM user_balance WHERE balance_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, balanceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserBalance(rs);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("UserBalanceDAO.getById error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Cập nhật balance
     */
    public boolean update(UserBalance balance) {
        String sql = "UPDATE user_balance SET total_coins = ?, spent_coins = ?, available_coins = ?, " +
                    "updated_at = GETDATE() WHERE balance_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, balance.getTotalCoins());
            stmt.setBigDecimal(2, balance.getSpentCoins());
            stmt.setBigDecimal(3, balance.getAvailableCoins());
            stmt.setInt(4, balance.getBalanceId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("UserBalanceDAO.update error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Cập nhật balance theo user ID
     */
    public boolean updateByUserId(UserBalance balance) {
        String sql = "UPDATE user_balance SET total_coins = ?, spent_coins = ?, available_coins = ?, " +
                    "updated_at = GETDATE() WHERE user_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, balance.getTotalCoins());
            stmt.setBigDecimal(2, balance.getSpentCoins());
            stmt.setBigDecimal(3, balance.getAvailableCoins());
            stmt.setInt(4, balance.getUserId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("UserBalanceDAO.updateByUserId error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Thêm coins vào balance (atomic operation)
     */
    public boolean addCoins(int userId, BigDecimal amount) {
        String sql = "UPDATE user_balance SET " +
                    "total_coins = total_coins + ?, " +
                    "available_coins = available_coins + ?, " +
                    "updated_at = GETDATE() " +
                    "WHERE user_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, amount);
            stmt.setBigDecimal(2, amount);
            stmt.setInt(3, userId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("UserBalanceDAO.addCoins error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Trừ coins từ balance (atomic operation)
     */
    public boolean subtractCoins(int userId, BigDecimal amount) {
        String sql = "UPDATE user_balance SET " +
                    "spent_coins = spent_coins + ?, " +
                    "available_coins = available_coins - ?, " +
                    "updated_at = GETDATE() " +
                    "WHERE user_id = ? AND available_coins >= ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, amount);
            stmt.setBigDecimal(2, amount);
            stmt.setInt(3, userId);
            stmt.setBigDecimal(4, amount); // Đảm bảo đủ coins
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("UserBalanceDAO.subtractCoins error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Kiểm tra user có đủ coins không
     */
    public boolean hasEnoughCoins(int userId, BigDecimal requiredAmount) {
        String sql = "SELECT available_coins FROM user_balance WHERE user_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal availableCoins = rs.getBigDecimal("available_coins");
                    return availableCoins.compareTo(requiredAmount) >= 0;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("UserBalanceDAO.hasEnoughCoins error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Xóa balance (cascade delete sẽ xóa transactions)
     */
    public boolean delete(int balanceId) {
        String sql = "DELETE FROM user_balance WHERE balance_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, balanceId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("UserBalanceDAO.delete error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Map ResultSet to UserBalance object
     */
    private UserBalance mapResultSetToUserBalance(ResultSet rs) throws SQLException {
        UserBalance balance = new UserBalance();
        balance.setBalanceId(rs.getInt("balance_id"));
        balance.setUserId(rs.getInt("user_id"));
        balance.setTotalCoins(rs.getBigDecimal("total_coins"));
        balance.setSpentCoins(rs.getBigDecimal("spent_coins"));
        balance.setAvailableCoins(rs.getBigDecimal("available_coins"));
        balance.setCreatedAt(rs.getTimestamp("created_at"));
        balance.setUpdatedAt(rs.getTimestamp("updated_at"));
        return balance;
    }
}