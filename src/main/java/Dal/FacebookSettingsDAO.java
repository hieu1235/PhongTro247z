package Dal;

import DBcontext.DBContext;
import Model.FacebookSettings;
import Utility.TokenEncryptionUtil;

import java.sql.*;
import java.util.*;

/**
 * DAO cho bảng facebook_settings
 */
public class FacebookSettingsDAO {
    
    /**
     * Tìm Facebook settings mặc định của user
     */
    public FacebookSettings findByUser(int userId) {
        try {
            return getDefaultPage(userId);
        } catch (SQLException e) {
            return null;
        }
    }
    
    /**
     * Lấy Page mặc định của user
     */
    public FacebookSettings getDefaultPage(int userId) throws SQLException {
        String sql = "SELECT * FROM facebook_settings WHERE user_id = ? AND is_default = 1 AND is_active = 1";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapRowToFacebookSettings(rs);
            }
            
            return getFirstActivePage(userId);
        }
    }
    
    /**
     * Lấy tất cả Pages active của user
     */
    public List<FacebookSettings> getAllActivePages(int userId) throws SQLException {
        System.out.println("DEBUG: FacebookSettingsDAO.getAllActivePages called for userId=" + userId);
        List<FacebookSettings> pages = new ArrayList<>();
        String sql = "SELECT * FROM facebook_settings WHERE user_id = ? AND is_active = 1 " +
                    "ORDER BY is_default DESC, page_name ASC";
        
        System.out.println("DEBUG: Executing SQL: " + sql + " with userId=" + userId);
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                FacebookSettings page = mapRowToFacebookSettings(rs);
                pages.add(page);
                count++;
                System.out.println("DEBUG: Found page " + count + ": " + page.getPageName() + 
                                  " (ID: " + page.getPageId() + ", Active: " + page.isActive() + 
                                  ", AutoPost: " + page.isAutoPost() + ", Default: " + page.isDefault() + ")");
            }
            
            System.out.println("DEBUG: Total pages found: " + pages.size());
        }
        
        return pages;
    }
    
    /**
     * Lấy Page theo ID cụ thể
     */
    public FacebookSettings getPageById(int userId, String pageId) throws SQLException {
        String sql = "SELECT * FROM facebook_settings WHERE user_id = ? AND page_id = ? AND is_active = 1";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, pageId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapRowToFacebookSettings(rs);
            }
        }
        
        return null;
    }
    
    /**
     * Lấy Page theo ID (bất kể active hay không) - dành cho saveOrUpdate
     */
    public FacebookSettings getPageByIdAny(int userId, String pageId) throws SQLException {
        String sql = "SELECT * FROM facebook_settings WHERE user_id = ? AND page_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, pageId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapRowToFacebookSettings(rs);
            }
        }
        
        return null;
    }
    
    /**
     * Set Page mặc định
     */
    public void setDefaultPage(int userId, String pageId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);
            
            // Reset all pages to non-default
            String resetSql = "UPDATE facebook_settings SET is_default = 0 WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(resetSql)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
            
            // Set selected page as default
            String setSql = "UPDATE facebook_settings SET is_default = 1, updated_at = NOW() " +
                           "WHERE user_id = ? AND page_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(setSql)) {
                ps.setInt(1, userId);
                ps.setString(2, pageId);
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected == 0) {
                    throw new SQLException("Page not found or not owned by user");
                }
            }
            
            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    // Silent
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    // Silent
                }
            }
        }
    }
    
    /**
     * Lưu hoặc cập nhật Facebook settings
     */
    public void saveOrUpdate(FacebookSettings settings) throws SQLException {
        System.out.println("DEBUG: saveOrUpdate called for pageId=" + settings.getPageId() + 
                          ", userId=" + settings.getUserId());
        
        FacebookSettings existing = getPageByIdAny(settings.getUserId(), settings.getPageId());
        
        if (existing == null) {
            System.out.println("DEBUG: No existing page found, calling insert()");
            insert(settings);
        } else {
            System.out.println("DEBUG: Existing page found (settingId=" + existing.getSettingId() + 
                              "), calling update()");
            settings.setSettingId(existing.getSettingId());
            update(settings);
        }
        System.out.println("DEBUG: saveOrUpdate completed successfully");
    }
    
    /**
     * Thêm Facebook Page mới
     */
    public void insert(FacebookSettings settings) throws SQLException {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);
            
            // Nếu page mới được set làm default, reset tất cả pages khác
            if (settings.isDefault()) {
                String resetSql = "UPDATE facebook_settings SET is_default = 0 WHERE user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(resetSql)) {
                    ps.setInt(1, settings.getUserId());
                    ps.executeUpdate();
                }
            }
            
            // Insert page mới
            String sql = "INSERT INTO facebook_settings (page_id, page_name, access_token, user_id, " +
                        "is_active, auto_post, is_default) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                ps.setString(1, settings.getPageId());
                ps.setString(2, settings.getPageName());
                
                // ✅ Mã hóa access token trước khi lưu vào database
                String encryptedToken = TokenEncryptionUtil.encrypt(settings.getAccessToken());
                ps.setString(3, encryptedToken);
                
                ps.setInt(4, settings.getUserId());
                ps.setBoolean(5, settings.isActive());
                ps.setBoolean(6, settings.isAutoPost());
                ps.setBoolean(7, settings.isDefault());
                
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        settings.setSettingId(rs.getInt(1));
                    }
                }
            }
            
            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Cập nhật Facebook settings
     */
    public void update(FacebookSettings settings) throws SQLException {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);
            
            // Nếu page được set làm default, reset tất cả pages khác
            if (settings.isDefault()) {
                String resetSql = "UPDATE facebook_settings SET is_default = 0 WHERE user_id = ? AND setting_id != ?";
                try (PreparedStatement ps = conn.prepareStatement(resetSql)) {
                    ps.setInt(1, settings.getUserId());
                    ps.setInt(2, settings.getSettingId());
                    ps.executeUpdate();
                }
            }
            
            // Update page
            String sql = "UPDATE facebook_settings SET page_id = ?, page_name = ?, access_token = ?, " +
                         "is_active = ?, auto_post = ?, is_default = ?, updated_at = NOW() " +
                         "WHERE setting_id = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, settings.getPageId());
                ps.setString(2, settings.getPageName());
                
                // ✅ Mã hóa access token trước khi update vào database
                String encryptedToken = TokenEncryptionUtil.encrypt(settings.getAccessToken());
                ps.setString(3, encryptedToken);
                
                ps.setBoolean(4, settings.isActive());
                ps.setBoolean(5, settings.isAutoPost());
                ps.setBoolean(6, settings.isDefault());
                ps.setInt(7, settings.getSettingId());
                
                ps.executeUpdate();
            }
            
            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Xóa Facebook Page
     */
    public void deletePage(int userId, String pageId) throws SQLException {
        String sql = "DELETE FROM facebook_settings WHERE user_id = ? AND page_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, pageId);
            ps.executeUpdate();
        }
    }
    
    /**
     * Toggle auto post cho một page
     */
    public void toggleAutoPost(int userId, String pageId, boolean autoPost) throws SQLException {
        String sql = "UPDATE facebook_settings SET auto_post = ?, updated_at = NOW() " +
                     "WHERE user_id = ? AND page_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setBoolean(1, autoPost);
            ps.setInt(2, userId);
            ps.setString(3, pageId);
            ps.executeUpdate();
        }
    }
    
    /**
     * Đếm số lượng active pages của user
     */
    public int countActivePages(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM facebook_settings WHERE user_id = ? AND is_active = 1";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    /**
     * Helper: Lấy page active đầu tiên
     */
    private FacebookSettings getFirstActivePage(int userId) throws SQLException {
        String sql = "SELECT * FROM facebook_settings WHERE user_id = ? AND is_active = 1 " +
                    "ORDER BY created_at ASC LIMIT 1";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapRowToFacebookSettings(rs);
            }
        }
        
        return null;
    }
    
    /**
     * Map ResultSet to FacebookSettings
     */
    private FacebookSettings mapRowToFacebookSettings(ResultSet rs) throws SQLException {
        FacebookSettings settings = new FacebookSettings();
        settings.setSettingId(rs.getInt("setting_id"));
        settings.setPageId(rs.getString("page_id"));
        settings.setPageName(rs.getString("page_name"));
        
        // ✅ Giải mã access token khi đọc từ database
        String encryptedToken = rs.getString("access_token");
        String decryptedToken = TokenEncryptionUtil.decrypt(encryptedToken);
        settings.setAccessToken(decryptedToken);
        
        settings.setUserId(rs.getInt("user_id"));
        settings.setActive(rs.getBoolean("is_active"));
        settings.setAutoPost(rs.getBoolean("auto_post"));
        
        try {
            settings.setDefault(rs.getBoolean("is_default"));
        } catch (SQLException e) {
            settings.setDefault(false);
        }
        
        settings.setCreatedAt(rs.getTimestamp("created_at"));
        settings.setUpdatedAt(rs.getTimestamp("updated_at"));
        return settings;
    }
}