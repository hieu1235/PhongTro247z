package Dal;

import DBcontext.DBContext;
import Model.PostFacebookPage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho bảng post_facebook_pages
 */
public class PostFacebookPageDAO {
    
    /**
     * Lưu selected pages cho một post
     * ✅ Sửa lỗi: Kiểm tra duplicate trước khi insert
     */
    public void insertPostPages(int postId, List<String> pageIds) throws SQLException {
        if (pageIds == null || pageIds.isEmpty()) {
            return;
        }
        
        // ✅ Sử dụng INSERT ... ON CONFLICT để tránh duplicate key error (PostgreSQL syntax)
        String sql = "INSERT INTO post_facebook_pages (post_id, page_id, status) VALUES (?, ?, ?) " +
                     "ON CONFLICT (post_id, page_id) DO UPDATE SET status = EXCLUDED.status";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (String pageId : pageIds) {
                ps.setInt(1, postId);
                ps.setString(2, pageId);
                ps.setString(3, "PENDING");
                ps.executeUpdate(); // Execute each insert individually
            }
            
            System.out.println("PostFacebookPageDAO: Processed " + pageIds.size() + " page selections for post " + postId);
        }
    }
    
    /**
     * Cập nhật kết quả đăng Facebook
     */
    public void updatePostResult(int postId, String pageId, String fbPostId, String status, String errorMessage) throws SQLException {
        String sql = "UPDATE post_facebook_pages SET facebook_post_id = ?, status = ?, error_message = ?, posted_at = NOW() " +
                     "WHERE post_id = ? AND page_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, fbPostId);
            ps.setString(2, status);
            ps.setString(3, errorMessage);
            ps.setInt(4, postId);
            ps.setString(5, pageId);
            
            ps.executeUpdate();
        }
    }
    
    /**
     * Lấy danh sách post_facebook_pages theo post_id
     */
    public List<PostFacebookPage> getByPostId(int postId) throws SQLException {
        List<PostFacebookPage> results = new ArrayList<>();
        String sql = "SELECT * FROM post_facebook_pages WHERE post_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, postId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PostFacebookPage item = new PostFacebookPage();
                    item.setId(rs.getInt("id"));
                    item.setPostId(rs.getInt("post_id"));
                    item.setPageId(rs.getString("page_id"));
                    item.setFacebookPostId(rs.getString("facebook_post_id"));
                    item.setPostedAt(rs.getTimestamp("posted_at"));
                    item.setStatus(rs.getString("status"));
                    item.setErrorMessage(rs.getString("error_message"));
                    results.add(item);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Lấy danh sách pending posts để auto-post
     */
    public List<PostFacebookPage> getPendingPosts() throws SQLException {
        List<PostFacebookPage> results = new ArrayList<>();
        String sql = "SELECT pfp.*, p.title, p.content, p.address, p.price, p.area " +
                     "FROM post_facebook_pages pfp " +
                     "INNER JOIN posts p ON pfp.post_id = p.post_id " +
                     "WHERE pfp.status = 'PENDING' AND p.status_id = 2"; // Only approved posts
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PostFacebookPage item = new PostFacebookPage();
                    item.setId(rs.getInt("id"));
                    item.setPostId(rs.getInt("post_id"));
                    item.setPageId(rs.getString("page_id"));
                    item.setFacebookPostId(rs.getString("facebook_post_id"));
                    item.setPostedAt(rs.getTimestamp("posted_at"));
                    item.setStatus(rs.getString("status"));
                    item.setErrorMessage(rs.getString("error_message"));
                    
                    // Post info
                    item.setPostTitle(rs.getString("title"));
                    item.setPostContent(rs.getString("content"));
                    item.setPostAddress(rs.getString("address"));
                    item.setPostPrice(rs.getBigDecimal("price"));
                    item.setPostArea(rs.getBigDecimal("area"));
                    
                    results.add(item);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Xóa tất cả records của một post (khi xóa post)
     */
    public void deleteByPostId(int postId) throws SQLException {
        String sql = "DELETE FROM post_facebook_pages WHERE post_id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, postId);
            ps.executeUpdate();
        }
    }
}