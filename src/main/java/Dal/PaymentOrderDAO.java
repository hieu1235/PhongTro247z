package Dal;

import Model.PaymentOrder;
import DBcontext.DBContext;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO để quản lý payment orders
 */
public class PaymentOrderDAO {
    
    /**
     * Tạo payment order mới - Hỗ trợ cả cột coins (legacy) và coins_amount (mới)
     */
    public int createPaymentOrder(PaymentOrder order) {
        // Try with coins_amount first (new schema)
        String sql = "INSERT INTO payment_orders (user_id, order_code, amount, coins_amount, payment_method, " +
                    "description, status, expires_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, order.getUserId());
            stmt.setString(2, order.getOrderCode());
            stmt.setBigDecimal(3, order.getAmount());
            stmt.setBigDecimal(4, order.getCoinsAmount());
            stmt.setString(5, order.getPaymentMethod());
            stmt.setString(6, order.getDescription());
            stmt.setString(7, order.getStatus());
            stmt.setTimestamp(8, order.getExpiresAt());
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            // If coins_amount doesn't exist, try with coins (legacy schema)
            if (e.getMessage().contains("coins_amount")) {
                System.out.println("PaymentOrderDAO: Trying legacy 'coins' column...");
                return createPaymentOrderLegacy(order);
            }
            
            System.out.println("PaymentOrderDAO.createPaymentOrder error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Fallback method cho legacy schema (dùng cột 'coins' thay vì 'coins_amount')
     */
    private int createPaymentOrderLegacy(PaymentOrder order) {
        String sql = "INSERT INTO payment_orders (user_id, order_code, amount, coins, payment_method, " +
                    "description, status, expires_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, order.getUserId());
            stmt.setString(2, order.getOrderCode());
            stmt.setBigDecimal(3, order.getAmount());
            stmt.setBigDecimal(4, order.getCoinsAmount());
            stmt.setString(5, order.getPaymentMethod());
            stmt.setString(6, order.getDescription());
            stmt.setString(7, order.getStatus());
            stmt.setTimestamp(8, order.getExpiresAt());
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        System.out.println("PaymentOrderDAO: Used legacy 'coins' column successfully");
                        return rs.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.createPaymentOrderLegacy error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Lấy payment order theo order code
     */
    public PaymentOrder getByOrderCode(String orderCode) {
        String sql = "SELECT * FROM payment_orders WHERE order_code = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPaymentOrder(rs);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.getByOrderCode error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lấy payment order theo ID
     */
    public PaymentOrder getById(int orderId) {
        String sql = "SELECT * FROM payment_orders WHERE order_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPaymentOrder(rs);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.getById error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Cập nhật payment order
     */
    public boolean updatePaymentOrder(PaymentOrder order) {
        String sql = "UPDATE payment_orders SET status = ?, gateway_order_id = ?, gateway_transaction_id = ?, " +
                    "callback_data = ?, updated_at = NOW() WHERE order_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, order.getStatus());
            stmt.setString(2, order.getGatewayOrderId());
            stmt.setString(3, order.getGatewayTransactionId());
            stmt.setString(4, order.getCallbackData());
            stmt.setInt(5, order.getOrderId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.updatePaymentOrder error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Lấy danh sách payment orders của user
     */
    public List<PaymentOrder> getOrdersByUserId(int userId, int limit) {
        String sql = "SELECT * FROM payment_orders WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        List<PaymentOrder> orders = new ArrayList<>();
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToPaymentOrder(rs));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.getOrdersByUserId error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orders;
    }
    
    /**
     * Cập nhật status của payment order
     */
    public boolean updateStatus(String orderCode, String status, String gatewayTransactionId) {
        String sql = "UPDATE payment_orders SET status = ?, gateway_transaction_id = ?, updated_at = NOW() " +
                    "WHERE order_code = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setString(2, gatewayTransactionId);
            stmt.setString(3, orderCode);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.updateStatus error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Lấy orders đang pending và đã hết hạn
     */
    public List<PaymentOrder> getExpiredPendingOrders() {
        String sql = "SELECT * FROM payment_orders WHERE status IN ('PENDING', 'PROCESSING') " +
                    "AND expires_at < NOW()"; // Fixed: expires_at thay vì expired_at
        List<PaymentOrder> orders = new ArrayList<>();
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToPaymentOrder(rs));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.getExpiredPendingOrders error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orders;
    }
    
    /**
     * Map ResultSet to PaymentOrder object
     */
    private PaymentOrder mapResultSetToPaymentOrder(ResultSet rs) throws SQLException {
        PaymentOrder order = new PaymentOrder();
        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setOrderCode(rs.getString("order_code"));
        order.setAmount(rs.getBigDecimal("amount"));
        
        // Kiểm tra có cột coins_amount không (có thể không có trong DB hiện tại)
        try {
            order.setCoinsAmount(rs.getBigDecimal("coins_amount"));
        } catch (SQLException e) {
            // Nếu không có cột coins_amount, tính từ amount (1000 VND = 1 coin)
            BigDecimal amount = rs.getBigDecimal("amount");
            if (amount != null) {
                order.setCoinsAmount(amount.divide(BigDecimal.valueOf(1000), 2, java.math.RoundingMode.HALF_UP));
            }
        }
        
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setStatus(rs.getString("status"));
        order.setGatewayOrderId(rs.getString("gateway_order_id"));
        order.setGatewayTransactionId(rs.getString("gateway_transaction_id"));
        order.setDescription(rs.getString("description"));
        order.setCallbackData(rs.getString("callback_data"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        order.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Lấy expires_at từ database
        try {
            order.setExpiresAt(rs.getTimestamp("expires_at"));
        } catch (SQLException e) {
            // Fallback nếu tên cột khác
            try {
                order.setExpiresAt(rs.getTimestamp("expired_at"));
            } catch (SQLException ex) {
                // Ignore if column doesn't exist
            }
        }
        
        return order;
    }
    
    /**
     * Lấy payment order theo order code (method bị thiếu)
     */
    public PaymentOrder getPaymentOrderByCode(String orderCode) {
        return getByOrderCode(orderCode); // Alias cho method đã có
    }
    
    /**
     * Cập nhật payment status (method bị thiếu)
     */
    public boolean updatePaymentStatus(String orderCode, String status, String gatewayTransactionId, String callbackData) {
        String sql = "UPDATE payment_orders SET status = ?, gateway_transaction_id = ?, callback_data = ?, " +
                    "updated_at = NOW() WHERE order_code = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setString(2, gatewayTransactionId);
            stmt.setString(3, callbackData);
            stmt.setString(4, orderCode);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.updatePaymentStatus error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Cập nhật payment status với paid_at timestamp
     */
    public boolean updatePaymentStatusWithPaidAt(String orderCode, String status, String gatewayTransactionId, String callbackData) {
        String sql = "UPDATE payment_orders SET status = ?, gateway_transaction_id = ?, callback_data = ?, " +
                    "paid_at = CASE WHEN ? = 'SUCCESS' THEN NOW() ELSE paid_at END, " +
                    "updated_at = NOW() WHERE order_code = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setString(2, gatewayTransactionId);
            stmt.setString(3, callbackData);
            stmt.setString(4, status);
            stmt.setString(5, orderCode);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.updatePaymentStatusWithPaidAt error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Lấy orders đang pending của user
     */
    public List<PaymentOrder> getPendingOrdersByUserId(int userId) {
        String sql = "SELECT * FROM payment_orders WHERE user_id = ? AND status IN ('PENDING', 'PROCESSING') " +
                    "ORDER BY created_at DESC";
        List<PaymentOrder> orders = new ArrayList<>();
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToPaymentOrder(rs));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.getPendingOrdersByUserId error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orders;
    }
    
    /**
     * Lấy successful orders của user
     */
    public List<PaymentOrder> getSuccessfulOrdersByUserId(int userId, int limit) {
        String sql = "SELECT * FROM payment_orders WHERE user_id = ? AND status = 'SUCCESS' " +
                    "ORDER BY created_at DESC LIMIT ?";
        List<PaymentOrder> orders = new ArrayList<>();
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToPaymentOrder(rs));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.getSuccessfulOrdersByUserId error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orders;
    }
    
    /**
     * Lấy danh sách orders pending trong khoảng thời gian (giờ)
     */
    public List<PaymentOrder> getPendingOrdersInTimeRange(int hours) {
        List<PaymentOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM payment_orders WHERE status = 'PENDING' " +
                    "AND created_at >= NOW() + INTERVAL '" + (-hours) + " hours' " +
                    "ORDER BY created_at DESC";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToPaymentOrder(rs));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.getPendingOrdersInTimeRange error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orders;
    }
    
    /**
     * Lấy lịch sử thanh toán của user
     */
    public List<PaymentOrder> getPaymentHistory(int userId, int limit) {
        List<PaymentOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM payment_orders WHERE user_id = ? ORDER BY created_at DESC";
        
        if (limit > 0) {
            sql += " LIMIT " + limit;
        }
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToPaymentOrder(rs));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.getPaymentHistory error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orders;
    }
    
    /**
     * Cộng xu cho user (sử dụng UserBalanceDAO)
     */
    public boolean addCoinsToUser(int userId, double coins, String orderCode) {
        String checkBalanceSql = "SELECT balance_id FROM user_balance WHERE user_id = ?";
        String insertBalanceSql = "INSERT INTO user_balance (user_id, total_coins, spent_coins, available_coins, created_at, updated_at) " +
                                "VALUES (?, ?, 0, ?, NOW(), NOW())";
        String updateBalanceSql = "UPDATE user_balance SET " +
                                "total_coins = total_coins + ?, " +
                                "available_coins = available_coins + ?, " +
                                "updated_at = NOW() " +
                                "WHERE user_id = ?";
        
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Check if balance record exists
                boolean balanceExists = false;
                try (PreparedStatement checkStmt = conn.prepareStatement(checkBalanceSql)) {
                    checkStmt.setInt(1, userId);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        balanceExists = rs.next();
                    }
                }
                
                // Insert if not exists
                if (!balanceExists) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertBalanceSql)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setDouble(2, coins);
                        insertStmt.setDouble(3, coins);
                        insertStmt.executeUpdate();
                        System.out.println("PaymentOrderDAO: Created new balance record for user " + userId);
                    }
                } else {
                    // Update existing balance
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSql)) {
                        updateStmt.setDouble(1, coins);
                        updateStmt.setDouble(2, coins);
                        updateStmt.setInt(3, userId);
                        updateStmt.executeUpdate();
                    }
                }
                
                conn.commit();
                System.out.println("PaymentOrderDAO: Added " + coins + " coins to user " + userId + " (order: " + orderCode + ")");
                return true;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.out.println("PaymentOrderDAO.addCoinsToUser error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}