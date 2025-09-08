package dao;

import DBcontext.DBContext;
import models.ActivityLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO extends DBContext {
    
    /**
     * Tạo activity log mới
     */
    public boolean createActivityLog(Long userId, String actionType, String targetId, 
                                   String targetType, String summary, String details) {
        String sql = "INSERT INTO activity_logs (user_id, action_type, target_id, target_type, summary, details, created_at) VALUES (?, ?, ?, ?, ?, ?, GETUTCDATE())";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, actionType);
            ps.setString(3, targetId);
            ps.setString(4, targetType);
            ps.setString(5, summary);
            ps.setString(6, details);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Lỗi createActivityLog: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy activity logs theo user ID
     */
    public List<ActivityLog> getActivityLogsByUserId(Long userId, int limit) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT TOP (?) * FROM activity_logs WHERE user_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setLong(2, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToActivityLog(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getActivityLogsByUserId: " + e.getMessage());
            e.printStackTrace();
        }
        
        return logs;
    }
    
    /**
     * Lấy activity logs theo action type
     */
    public List<ActivityLog> getActivityLogsByActionType(String actionType, int limit) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT TOP (?) * FROM activity_logs WHERE action_type = ? ORDER BY created_at DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setString(2, actionType);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToActivityLog(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getActivityLogsByActionType: " + e.getMessage());
            e.printStackTrace();
        }
        
        return logs;
    }
    
    /**
     * Kiểm tra xem listing đã được post lên Facebook thành công chưa
     */
    public boolean isListingPostedToFacebook(Long listingId, Long userId) {
        String sql = """
            SELECT COUNT(*) FROM activity_logs 
            WHERE user_id = ? 
            AND action_type = 'FACEBOOK_POST' 
            AND target_id = ? 
            AND JSON_VALUE(details, '$.success') = 'true'
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, listingId.toString());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi isListingPostedToFacebook: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Lấy Facebook post logs của một listing
     */
    public List<ActivityLog> getFacebookPostLogs(Long listingId) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs WHERE action_type = 'FACEBOOK_POST' AND target_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, listingId.toString());
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToActivityLog(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getFacebookPostLogs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return logs;
    }
    
    /**
     * Xóa logs cũ (tự động cleanup)
     */
    public boolean cleanupOldLogs(int daysToKeep) {
        String sql = "DELETE FROM activity_logs WHERE created_at < DATEADD(DAY, ?, GETUTCDATE())";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, -daysToKeep);
            
            int deletedRows = ps.executeUpdate();
            System.out.println("Đã xóa " + deletedRows + " activity logs cũ");
            return true;
            
        } catch (SQLException e) {
            System.out.println("Lỗi cleanupOldLogs: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy thống kê Facebook posts
     */
    public int getFacebookPostCount(Long userId, String status, int days) {
        String sql = """
            SELECT COUNT(*) FROM activity_logs 
            WHERE user_id = ? 
            AND action_type = 'FACEBOOK_POST' 
            AND JSON_VALUE(details, '$.success') = ? 
            AND created_at >= DATEADD(DAY, ?, GETUTCDATE())
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, status.equals("success") ? "true" : "false");
            ps.setInt(3, -days);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getFacebookPostCount: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Map ResultSet thành ActivityLog object
     */
    private ActivityLog mapResultSetToActivityLog(ResultSet rs) throws SQLException {
        ActivityLog log = new ActivityLog();
        log.setLogId(rs.getLong("log_id"));
        log.setUserId(rs.getLong("user_id"));
        log.setActionType(rs.getString("action_type"));
        log.setTargetId(rs.getString("target_id"));
        log.setTargetType(rs.getString("target_type"));
        log.setSummary(rs.getString("summary"));
        log.setDetails(rs.getString("details"));
        log.setCreatedAt(rs.getTimestamp("created_at"));
        
        return log;
    }
}