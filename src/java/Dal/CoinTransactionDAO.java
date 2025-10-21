package Dal;

import Model.CoinTransaction;
import DBcontext.DBContext;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO để quản lý coin_transactions
 */
public class CoinTransactionDAO {
    
    /**
     * Tạo coin transaction mới
     */
    public int create(CoinTransaction transaction) {
        String sql = "INSERT INTO coin_transactions (user_id, transaction_type, amount, description, " +
                    "reference_id, reference_type, balance_before, balance_after) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, transaction.getUserId());
            stmt.setString(2, transaction.getTransactionType());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setString(4, transaction.getDescription());
            stmt.setString(5, transaction.getReferenceId());
            stmt.setString(6, transaction.getReferenceType());
            stmt.setBigDecimal(7, transaction.getBalanceBefore());
            stmt.setBigDecimal(8, transaction.getBalanceAfter());
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.out.println("CoinTransactionDAO.create error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Lấy transaction theo ID
     */
    public CoinTransaction getById(int transactionId) {
        String sql = "SELECT * FROM coin_transactions WHERE transaction_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, transactionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCoinTransaction(rs);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("CoinTransactionDAO.getById error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lấy transactions của user (có giới hạn số lượng)
     */
    public List<CoinTransaction> getByUserId(int userId, int limit) {
        String sql = "SELECT TOP (?) * FROM coin_transactions WHERE user_id = ? ORDER BY created_at DESC";
        List<CoinTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToCoinTransaction(rs));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("CoinTransactionDAO.getByUserId error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Lấy tất cả transactions của user
     */
    public List<CoinTransaction> getAllByUserId(int userId) {
        String sql = "SELECT * FROM coin_transactions WHERE user_id = ? ORDER BY created_at DESC";
        List<CoinTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToCoinTransaction(rs));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("CoinTransactionDAO.getAllByUserId error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Lấy transactions theo loại
     */
    public List<CoinTransaction> getByUserIdAndType(int userId, String transactionType, int limit) {
        String sql = "SELECT TOP (?) * FROM coin_transactions WHERE user_id = ? AND transaction_type = ? " +
                    "ORDER BY created_at DESC";
        List<CoinTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, userId);
            stmt.setString(3, transactionType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToCoinTransaction(rs));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("CoinTransactionDAO.getByUserIdAndType error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Lấy transactions theo reference
     */
    public List<CoinTransaction> getByReference(String referenceId, String referenceType) {
        String sql = "SELECT * FROM coin_transactions WHERE reference_id = ? AND reference_type = ? " +
                    "ORDER BY created_at DESC";
        List<CoinTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, referenceId);
            stmt.setString(2, referenceType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToCoinTransaction(rs));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("CoinTransactionDAO.getByReference error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Tính tổng số xu đã nạp của user
     */
    public BigDecimal getTotalAddedCoins(int userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM coin_transactions " +
                    "WHERE user_id = ? AND amount > 0";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("CoinTransactionDAO.getTotalAddedCoins error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Tính tổng số xu đã tiêu của user (trả về số âm)
     */
    public BigDecimal getTotalSpentCoins(int userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM coin_transactions " +
                    "WHERE user_id = ? AND amount < 0";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("CoinTransactionDAO.getTotalSpentCoins error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Tính số dư hiện tại từ transactions
     */
    public BigDecimal getCurrentBalance(int userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as balance FROM coin_transactions WHERE user_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("CoinTransactionDAO.getCurrentBalance error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Đếm số transactions của user
     */
    public int countByUserId(int userId) {
        String sql = "SELECT COUNT(*) as count FROM coin_transactions WHERE user_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("CoinTransactionDAO.countByUserId error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Xóa transaction (chỉ nên dùng trong trường hợp đặc biệt)
     */
    public boolean delete(int transactionId) {
        String sql = "DELETE FROM coin_transactions WHERE transaction_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, transactionId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("CoinTransactionDAO.delete error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Map ResultSet to CoinTransaction object
     */
    private CoinTransaction mapResultSetToCoinTransaction(ResultSet rs) throws SQLException {
        CoinTransaction transaction = new CoinTransaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setUserId(rs.getInt("user_id"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setDescription(rs.getString("description"));
        transaction.setReferenceId(rs.getString("reference_id"));
        transaction.setReferenceType(rs.getString("reference_type"));
        transaction.setBalanceBefore(rs.getBigDecimal("balance_before"));
        transaction.setBalanceAfter(rs.getBigDecimal("balance_after"));
        transaction.setCreatedAt(rs.getTimestamp("created_at"));
        return transaction;
    }
}