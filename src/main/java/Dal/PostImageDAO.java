package Dal;

import DBcontext.DBContext;
import Model.PostImage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAL cho bảng post_images, quản lý image URLs từ local storage
 */
public class PostImageDAO {

    /**
     * Chèn image URL record (caller truyền Connection để nằm trong transaction).
     */
    public void insertImage(int postId, String imageUrl, boolean isThumbnail, Connection conn) throws SQLException {
        String sql = "INSERT INTO post_images (post_id, image_url, is_thumbnail) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setString(2, imageUrl); // Local file URL (e.g., /uploads/images/user_1_123456.jpg)
            ps.setBoolean(3, isThumbnail);
            ps.executeUpdate();
        }
    }

    /**
     * Lấy danh sách PostImage objects theo post_id để Facebook posting
     */
    public List<PostImage> getImagesByPostId(int postId) {
        String sql = "SELECT image_id, post_id, image_url, is_thumbnail FROM post_images WHERE post_id = ? ORDER BY image_id";
        List<PostImage> images = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PostImage image = new PostImage();
                    image.setImageId(rs.getInt("image_id"));
                    image.setPostId(rs.getInt("post_id"));
                    image.setImageUrl(rs.getString("image_url"));
                    image.setThumbnail(rs.getBoolean("is_thumbnail"));
                    images.add(image);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in PostImageDAO.getImagesByPostId: " + e.getMessage());
            e.printStackTrace();
        }
        return images;
    }

    /**
     * Lấy danh sách image URLs theo post_id (non-transactional).
     */
    public List<String> findByPost(int postId) {
        String sql = "SELECT image_url FROM post_images WHERE post_id = ? ORDER BY image_id";
        List<String> urls = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    urls.add(rs.getString("image_url")); // Local file URL
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in PostImageDAO.findByPost: " + e.getMessage());
            e.printStackTrace();
        }
        return urls;
    }

    /**
     * Lấy thumbnail URL (ảnh đầu tiên hoặc ảnh có is_thumbnail = 1) của post
     */
    public String getThumbnailByPost(int postId) {
        // Ưu tiên ảnh có is_thumbnail = 1, nếu không có thì lấy ảnh đầu tiên
        String sql = "SELECT image_url FROM post_images WHERE post_id = ? ORDER BY is_thumbnail DESC, image_id ASC LIMIT 1";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("image_url"); // Local file URL
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in PostImageDAO.getThumbnailByPost: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Xóa các bản ghi ảnh của post (caller truyền Connection để transaction).
     * Chỉ xóa record trong DB - không xóa ảnh physical.
     * LocalFileService sẽ xóa ảnh trên local storage riêng biệt.
     */
    public void deleteByPost(int postId, Connection conn) throws SQLException {
        String sql = "DELETE FROM post_images WHERE post_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.executeUpdate();
        }
    }
}