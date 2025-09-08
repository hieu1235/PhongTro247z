package dao;

import DBcontext.DBContext;
import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DBContext {
    
    /**
     * Lấy user theo ID
     */
    public User getUserById(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getUserById: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lấy user theo username
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi getUserByUsername: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Cập nhật Facebook Page configuration
     */
    public boolean updateFacebookPageConfig(Long userId, String pageId, String pageAccessToken) {
        String sql = "UPDATE users SET facebook_page_id = ?, facebook_page_access_token = ?, updated_at = GETUTCDATE() WHERE user_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, pageId);
            ps.setString(2, pageAccessToken);
            ps.setLong(3, userId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Lỗi updateFacebookPageConfig: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy danh sách users có Facebook Page config
     */
    public List<User> getUsersWithFacebookConfig() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE facebook_page_id IS NOT NULL AND facebook_page_access_token IS NOT NULL AND status = 'active'";
        
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            System.out.println("Lỗi getUsersWithFacebookConfig: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Kiểm tra user có Facebook config không
     */
    public boolean hasFacebookConfig(Long userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ? AND facebook_page_id IS NOT NULL AND facebook_page_access_token IS NOT NULL";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hasFacebookConfig: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Xóa Facebook Page configuration
     */
    public boolean removeFacebookPageConfig(Long userId) {
        String sql = "UPDATE users SET facebook_page_id = NULL, facebook_page_access_token = NULL, updated_at = GETUTCDATE() WHERE user_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Lỗi removeFacebookPageConfig: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Map ResultSet thành User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        user.setFacebookPageAccessToken(rs.getString("facebook_page_access_token"));
        user.setFacebookPageId(rs.getString("facebook_page_id"));
        
        return user;
    }
}