package Dal;

import DBcontext.DBContext;
import Model.User;

import java.sql.*;

/**
 * DAO cho Pro subscription system
 * Quản lý việc nâng cấp/hạ cấp Pro status của user
 */
public class SubscriptionDAO {

    /**
     * Mua gói Pro cho user (30 ngày)
     */
    public boolean buyProSubscription(int userId) {
        String sql = "UPDATE users SET is_pro = 1, pro_expires_at = DATEADD(day, 30, GETDATE()), updated_at = GETDATE() WHERE user_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("SubscriptionDAO: Updated Pro status for user " + userId);
                return true;
            }
            
        } catch (SQLException e) {
            System.out.println("SubscriptionDAO.buyProSubscription error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Lấy thông tin user theo ID (bao gồm Pro status)
     */
    public User getUserById(int userId) {
        String sql = "SELECT u.user_id, u.username, u.password, u.full_name, u.email, " +
                    "u.phone, u.role_id, r.role_name, u.created_at, u.updated_at, " +
                    "u.is_pro, u.pro_expires_at " +
                    "FROM users u " +
                    "INNER JOIN roles r ON u.role_id = r.role_id " +
                    "WHERE u.user_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setRoleId(rs.getInt("role_id"));
                    user.setRoleName(rs.getString("role_name"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setUpdatedAt(rs.getTimestamp("updated_at"));
                    user.setPro(rs.getBoolean("is_pro"));
                    user.setProExpiresAt(rs.getTimestamp("pro_expires_at"));
                    
                    return user;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("SubscriptionDAO.getUserById error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Cập nhật các user Pro đã hết hạn thành Free
     */
    public int updateExpiredProUsers() {
        String sql = "UPDATE users SET is_pro = 0, pro_expires_at = NULL, updated_at = GETDATE() " +
                    "WHERE is_pro = 1 AND pro_expires_at IS NOT NULL AND pro_expires_at < GETDATE()";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("SubscriptionDAO: Updated " + rowsAffected + " expired Pro users");
            return rowsAffected;
            
        } catch (SQLException e) {
            System.out.println("SubscriptionDAO.updateExpiredProUsers error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    /**
     * Lấy thống kê Pro users
     */
    public ProStats getProStats() {
        String sql = "SELECT " +
                    "COUNT(CASE WHEN is_pro = 1 AND pro_expires_at > GETDATE() THEN 1 END) as active_pro, " +
                    "COUNT(CASE WHEN is_pro = 0 OR pro_expires_at <= GETDATE() THEN 1 END) as free_users, " +
                    "COUNT(*) as total_users " +
                    "FROM users";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                ProStats stats = new ProStats();
                stats.setActivePro(rs.getInt("active_pro"));
                stats.setFreeUsers(rs.getInt("free_users"));
                stats.setTotalUsers(rs.getInt("total_users"));
                return stats;
            }
            
        } catch (SQLException e) {
            System.out.println("SubscriptionDAO.getProStats error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new ProStats(); // Return empty stats if error
    }

    /**
     * Class để lưu thống kê Pro
     */
    public static class ProStats {
        private int activePro;
        private int freeUsers;
        private int totalUsers;

        public int getActivePro() { return activePro; }
        public void setActivePro(int activePro) { this.activePro = activePro; }

        public int getFreeUsers() { return freeUsers; }
        public void setFreeUsers(int freeUsers) { this.freeUsers = freeUsers; }

        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }
    }
}