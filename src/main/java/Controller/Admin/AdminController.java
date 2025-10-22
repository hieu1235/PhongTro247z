package Controller.Admin;

import Dal.PostDAO;
import Dal.UserDAO;
import Dal.PaymentOrderDAO;
import DBcontext.DBContext;
import Model.Post;
import Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

/**
 * Admin Controller - Xử lý logic admin không dùng servlet API
 */
public class AdminController {
    
    private final UserDAO userDAO;
    private final PostDAO postDAO;
    private final PaymentOrderDAO paymentDAO;
    
    public AdminController() {
        this.userDAO = new UserDAO();
        this.postDAO = new PostDAO();
        this.paymentDAO = new PaymentOrderDAO();
    }
    
    /**
     * Lấy thống kê dashboard
     */
    public Map<String, Object> getDashboardStats() throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Tổng số người dùng
            List<User> allUsers = userDAO.getAllUsers();
            stats.put("totalUsers", allUsers.size());
            
            // Tổng số bài đăng - dùng findLatest với page size lớn để lấy tất cả
            List<Post> allPosts = postDAO.findLatest(1, 10000, null);
            stats.put("totalPosts", allPosts.size());
            
            // Thống kê users theo role
            long adminCount = allUsers.stream().filter(u -> "ADMIN".equals(u.getRoleName())).count();
            long userCount = allUsers.stream().filter(u -> "USER".equals(u.getRoleName())).count();
            long moderatorCount = allUsers.stream().filter(u -> "MODERATOR".equals(u.getRoleName())).count();
            
            stats.put("adminCount", adminCount);
            stats.put("userCount", userCount);
            stats.put("moderatorCount", moderatorCount);
            
            // Thống kê posts theo status
            long pendingPosts = allPosts.stream().filter(p -> "PENDING".equals(p.getStatusName())).count();
            long approvedPosts = allPosts.stream().filter(p -> "APPROVED".equals(p.getStatusName())).count();
            long rejectedPosts = allPosts.stream().filter(p -> "REJECTED".equals(p.getStatusName())).count();
            long draftPosts = allPosts.stream().filter(p -> "DRAFT".equals(p.getStatusName())).count();
            
            stats.put("pendingPosts", pendingPosts);
            stats.put("approvedPosts", approvedPosts);
            stats.put("rejectedPosts", rejectedPosts);
            stats.put("draftPosts", draftPosts);
            
            // Thống kê người dùng verified và pro
            long verifiedUsers = allUsers.stream().filter(u -> u.isVerified()).count();
            long proUsers = allUsers.stream().filter(u -> u.isPro()).count();
            long totalCoins = allUsers.stream().mapToLong(u -> u.getCoins()).sum();
            
            stats.put("verifiedUsers", verifiedUsers);
            stats.put("proUsers", proUsers);
            stats.put("totalCoins", totalCoins);
            
            // Active users (verified users)
            stats.put("activeUsers", verifiedUsers);
            
            return stats;
            
        } catch (Exception e) {
            // Nếu có lỗi, trả về dữ liệu mặc định
            System.err.println("Error in getDashboardStats: " + e.getMessage());
            e.printStackTrace();
            
            stats.put("totalUsers", 0);
            stats.put("proUsers", 0);
            stats.put("verifiedUsers", 0);
            stats.put("activeUsers", 0);
            stats.put("totalCoins", 0);
            stats.put("totalPosts", 0);
            stats.put("pendingPosts", 0);
            stats.put("approvedPosts", 0);
            stats.put("rejectedPosts", 0);
            return stats;
        }
    }
    
    /**
     * Lấy danh sách bài đăng với phân trang
     */
    public Map<String, Object> getPostsData(int page, int limit, String status) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        List<Post> allPosts = postDAO.findLatest(1, 10000, null); // Lấy tất cả
        
        // Filter theo status nếu có
        if (status != null && !status.isEmpty() && !"all".equals(status)) {
            allPosts = allPosts.stream()
                .filter(p -> status.toUpperCase().equals(p.getStatusName()))
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Phân trang
        int total = allPosts.size();
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, total);
        
        List<Post> pagedPosts = allPosts.subList(start, end);
        
        result.put("posts", pagedPosts);
        result.put("total", total);
        result.put("totalPages", (int) Math.ceil((double) total / limit));
        result.put("currentPage", page);
        
        return result;
    }
    
    /**
     * Lấy danh sách users với phân trang
     */
    public Map<String, Object> getUsersData(int page, int limit, String role) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        List<User> allUsers = userDAO.getAllUsers();
        
        // Filter theo role nếu có
        if (role != null && !role.isEmpty() && !"all".equals(role)) {
            allUsers = allUsers.stream()
                .filter(u -> role.toUpperCase().equals(u.getRoleName()))
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Phân trang
        int total = allUsers.size();
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, total);
        
        List<User> pagedUsers = allUsers.subList(start, end);
        
        result.put("users", pagedUsers);
        result.put("total", total);
        result.put("totalPages", (int) Math.ceil((double) total / limit));
        result.put("currentPage", page);
        
        return result;
    }
    
    /**
     * Cập nhật trạng thái bài đăng
     */
    public boolean updatePostStatus(int postId, String status) throws SQLException {
        String sql = "UPDATE posts SET status_id = (SELECT status_id FROM post_status WHERE status_name = ?) WHERE post_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, postId);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Xóa bài đăng
     */
    public boolean deletePost(int postId) throws SQLException {
        return postDAO.deletePost(postId);
    }
    
    /**
     * Cập nhật thông tin user
     */
    public boolean updateUser(int userId, String fullName, String email, String phone) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE user_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Xóa user
     */
    public boolean deleteUser(int userId) throws SQLException {
        return userDAO.deleteUser(userId);
    }
    
    /**
     * Thêm xu cho user
     */
    public boolean addCoinsToUser(int userId, int amount) throws SQLException {
        String sql = "UPDATE users SET coins = coins + ? WHERE user_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Ban/Unban user
     */
    public boolean banUser(int userId, boolean ban) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, !ban); // is_active = false khi ban
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Lấy chi tiết thanh toán
     */
    public Map<String, Object> getPaymentDetail(int paymentId) throws SQLException {
        Map<String, Object> detail = new HashMap<>();
        String sql = "SELECT po.*, u.username, u.email FROM payment_orders po " +
                    "JOIN users u ON po.user_id = u.user_id WHERE po.order_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    detail.put("paymentId", rs.getInt("order_id"));
                    detail.put("userId", rs.getInt("user_id"));
                    detail.put("username", rs.getString("username"));
                    detail.put("email", rs.getString("email"));
                    detail.put("amount", rs.getBigDecimal("amount"));
                    detail.put("paymentMethod", rs.getString("payment_method"));
                    detail.put("status", rs.getString("status"));
                    detail.put("transactionId", rs.getString("order_code"));
                    detail.put("createdAt", rs.getTimestamp("created_at"));
                }
            }
        }
        return detail;
    }
    
    /**
     * Lấy danh sách payments với phân trang
     */
    public Map<String, Object> getPaymentsData(int page, int limit) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        String countSql = "SELECT COUNT(*) FROM payment_orders";
        String sql = "SELECT po.order_id, po.order_code, po.amount, po.payment_method, po.status, po.created_at, " +
                    "u.full_name as userName FROM payment_orders po " +
                    "JOIN users u ON po.user_id = u.user_id " +
                    "ORDER BY po.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        try (Connection conn = DBContext.getConnection()) {
            // Đếm total
            try (PreparedStatement ps = conn.prepareStatement(countSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.put("total", rs.getInt(1));
                }
            }
            
            // Lấy data với phân trang
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, (page - 1) * limit);
                ps.setInt(2, limit);
                
                List<Map<String, Object>> payments = new java.util.ArrayList<>();
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> payment = new HashMap<>();
                        payment.put("orderId", rs.getInt("order_id"));
                        payment.put("orderCode", rs.getString("order_code"));
                        payment.put("userName", rs.getString("userName"));
                        payment.put("amount", rs.getBigDecimal("amount"));
                        payment.put("paymentMethod", rs.getString("payment_method"));
                        payment.put("status", rs.getString("status"));
                        payment.put("createdAt", rs.getTimestamp("created_at"));
                        payments.add(payment);
                    }
                }
                result.put("payments", payments);
            }
        }
        
        int total = (Integer) result.get("total");
        result.put("totalPages", (int) Math.ceil((double) total / limit));
        result.put("currentPage", page);
        
        return result;
    }
    
    /**
     * Lấy thống kê payments
     */
    public Map<String, Object> getPaymentStats() throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        String sql = "SELECT COUNT(*) as totalPayments, " +
                    "SUM(CASE WHEN status = 'success' THEN 1 ELSE 0 END) as successPayments, " +
                    "SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) as pendingPayments, " +
                    "SUM(CASE WHEN status = 'success' THEN amount ELSE 0 END) as totalRevenue " +
                    "FROM payment_orders";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.put("totalPayments", rs.getInt("totalPayments"));
                stats.put("successPayments", rs.getInt("successPayments"));
                stats.put("pendingPayments", rs.getInt("pendingPayments"));
                stats.put("totalRevenue", rs.getBigDecimal("totalRevenue"));
            }
        }
        
        return stats;
    }
    
    /**
     * Toggle Pro status của user
     */
    public boolean toggleUserProStatus(int userId) throws SQLException {
        String sql = "UPDATE users SET is_pro = CASE WHEN is_pro = 1 THEN 0 ELSE 1 END WHERE user_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }
}