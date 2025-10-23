package Dal;

import DBcontext.DBContext;
import Model.Post;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAL cho bảng posts, phong cách tương tự UserDAO trong project: - sử dụng
 * DBcontext.DBContext.getConnection() - log bằng System.out.println +
 * e.printStackTrace() - các phương thức transactional nhận Connection từ caller
 * khi cần
 */
public class PostDAO {

    /**
     * Chèn Post (caller quản lý transaction -> truyền Connection). Trả về
     * generated post_id.
     */
    public int insert(Post post, Connection conn) throws SQLException {
        String sql = "INSERT INTO posts (user_id, title, content, address, lat, lng, price, area, status_id, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, post.getUserId());
            ps.setString(2, post.getTitle());
            ps.setString(3, post.getContent());
            ps.setString(4, post.getAddress());

            BigDecimal lat = post.getLat();
            BigDecimal lng = post.getLng();
            if (lat != null) {
                ps.setBigDecimal(5, lat);
            } else {
                ps.setNull(5, Types.DECIMAL);
            }
            if (lng != null) {
                ps.setBigDecimal(6, lng);
            } else {
                ps.setNull(6, Types.DECIMAL);
            }

            if (post.getPrice() != null) {
                ps.setBigDecimal(7, post.getPrice());
            } else {
                ps.setNull(7, Types.DECIMAL);
            }

            if (post.getArea() != null) {
                ps.setBigDecimal(8, post.getArea());
            } else {
                ps.setNull(8, Types.DECIMAL);
            }

            ps.setInt(9, post.getStatusId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Insert post failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Lấy 1 post theo id (non-transactional).
     */
    public Optional<Post> findById(int postId) {
        String sql = "SELECT p.*, ps.status_name, u.full_name AS user_full_name, u.phone AS user_phone, u.email AS user_email "
                + "FROM posts p "
                + "LEFT JOIN post_status ps ON p.status_id = ps.status_id "
                + "LEFT JOIN users u ON p.user_id = u.user_id "
                + "WHERE p.post_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Post p = mapRowToPost(rs);
                    return Optional.of(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.findById: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Tìm các post của user theo paging.
     */
    public List<Post> findByUser(int userId, int page, int pageSize) {
        List<Post> list = new ArrayList<>();
        String sql = "SELECT p.*, ps.status_name "
                + "FROM posts p "
                + "LEFT JOIN post_status ps ON p.status_id = ps.status_id "
                + "WHERE p.user_id = ? "
                + "ORDER BY p.created_at DESC "
                + "OFFSET ? LIMIT ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Post p = mapRowToPost(rs);
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.findByUser: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm tổng post của user (dùng cho paging).
     */
    public int countByUser(int userId) {
        String sql = "SELECT COUNT(*) AS cnt FROM posts WHERE user_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.countByUser: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Cập nhật post (caller truyền Connection để quản lý transaction). Lưu ý:
     * chỉ cho phép update khi user_id khớp (SQL có điều kiện user_id).
     */
    public void update(Post post, Connection conn) throws SQLException {
        String sql = "UPDATE posts SET title = ?, content = ?, address = ?, lat = ?, lng = ?, price = ?, area = ?, updated_at = NOW() "
                + "WHERE post_id = ? AND user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getAddress());
            if (post.getLat() != null) {
                ps.setBigDecimal(4, post.getLat());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }
            if (post.getLng() != null) {
                ps.setBigDecimal(5, post.getLng());
            } else {
                ps.setNull(5, Types.DECIMAL);
            }
            if (post.getPrice() != null) {
                ps.setBigDecimal(6, post.getPrice());
            } else {
                ps.setNull(6, Types.DECIMAL);
            }
            if (post.getArea() != null) {
                ps.setBigDecimal(7, post.getArea());
            } else {
                ps.setNull(7, Types.DECIMAL);
            }
            ps.setInt(8, post.getPostId());
            ps.setInt(9, post.getUserId());
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Update failed or unauthorized");
            }
        }
    }

    /**
     * Xóa post (caller truyền Connection để quản lý transaction).
     */
    public void delete(int postId, Connection conn) throws SQLException {
        String sql = "DELETE FROM posts WHERE post_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.executeUpdate();
        }
    }

    /**
     * Map ResultSet -> Post object
     */
    private Post mapRowToPost(ResultSet rs) throws SQLException {
        Post p = new Post();
        p.setPostId(rs.getInt("post_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setTitle(rs.getString("title"));
        p.setContent(rs.getString("content"));
        p.setAddress(rs.getString("address"));
        p.setLat(rs.getBigDecimal("lat"));
        p.setLng(rs.getBigDecimal("lng"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setArea(rs.getBigDecimal("area"));
        p.setStatusId(rs.getInt("status_id"));
        try {
            p.setStatusName(rs.getString("status_name"));
        } catch (Exception ignored) {
        }
        p.setFacebookPostId(rs.getString("facebook_post_id"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // ✅ Add scheduled posting fields
        try {
            p.setScheduledAt(rs.getTimestamp("scheduled_at"));
        } catch (SQLException ignored) {
        }
        try {
            p.setAutoPublish(rs.getBoolean("auto_publish"));
        } catch (SQLException ignored) {
        }
        try {
            p.setPublishedAt(rs.getTimestamp("published_at"));
        } catch (SQLException ignored) {
        }
        
        try {
            p.setUserFullName(rs.getString("user_full_name"));
            p.setUserPhone(rs.getString("user_phone"));
            p.setUserEmail(rs.getString("user_email"));
        } catch (SQLException ignored) {
        }
        return p;
    }

    //phare2   
    public List<Post> findLatest(int page, int pageSize, Integer statusId) {
        List<Post> list = new ArrayList<>();
        String sql = "SELECT p.*, ps.status_name "
                + "FROM posts p "
                + "LEFT JOIN post_status ps ON p.status_id = ps.status_id "
                + (statusId != null ? "WHERE p.status_id = ? " : "")
                + "ORDER BY p.created_at DESC "
                + "OFFSET ? LIMIT ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            if (statusId != null) {
                ps.setInt(idx++, statusId);
            }
            ps.setInt(idx++, (page - 1) * pageSize);
            ps.setInt(idx, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Post p = mapRowToPost(rs);
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.findLatest: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm tổng bài đăng (có thể lọc trạng thái).
     */
    public int countAll(Integer statusId) {
        String sql = "SELECT COUNT(*) AS cnt FROM posts";
        if (statusId != null) {
            sql += " WHERE status_id = ?";
        }
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (statusId != null) {
                ps.setInt(1, statusId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.countAll: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public List<Post> search(
            String q, BigDecimal minPrice, BigDecimal maxPrice,
            BigDecimal minArea, BigDecimal maxArea,
            int page, int pageSize, Integer statusId) {
        List<Post> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.*, ps.status_name FROM posts p LEFT JOIN post_status ps ON p.status_id=ps.status_id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (statusId != null) {
            sql.append("AND p.status_id = ? ");
            params.add(statusId);
        }
        if (q != null && !q.trim().isEmpty()) {
            sql.append("AND (p.title LIKE ? OR p.address LIKE ? OR p.content LIKE ?) ");
            String keyword = "%" + q.trim() + "%";
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }
        if (minPrice != null) {
            sql.append("AND p.price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append("AND p.price <= ? ");
            params.add(maxPrice);
        }
        if (minArea != null) {
            sql.append("AND p.area >= ? ");
            params.add(minArea);
        }
        if (maxArea != null) {
            sql.append("AND p.area <= ? ");
            params.add(maxArea);
        }
        sql.append("ORDER BY p.created_at DESC OFFSET ? LIMIT ?");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); ++i) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToPost(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.search: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public int countSearch(String q, BigDecimal minPrice, BigDecimal maxPrice,
            BigDecimal minArea, BigDecimal maxArea, Integer statusId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM posts WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (statusId != null) {
            sql.append("AND status_id = ? ");
            params.add(statusId);
        }
        if (q != null && !q.trim().isEmpty()) {
            sql.append("AND (title LIKE ? OR address LIKE ? OR content LIKE ?) ");
            String keyword = "%" + q.trim() + "%";
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }
        if (minPrice != null) {
            sql.append("AND price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append("AND price <= ? ");
            params.add(maxPrice);
        }
        if (minArea != null) {
            sql.append("AND area >= ? ");
            params.add(minArea);
        }
        if (maxArea != null) {
            sql.append("AND area <= ? ");
            params.add(maxArea);
        }
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); ++i) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.countSearch: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Search posts with optional radius search around (centerLat, centerLng).
     * Nếu có centerLat, centerLng, radiusKm thì lọc theo bán kính, sort theo
     * khoảng cách. Nếu không thì search text như bình thường.
     */
    public List<Post> searchWithLocation(
            String q, BigDecimal minPrice, BigDecimal maxPrice,
            BigDecimal minArea, BigDecimal maxArea,
            Double centerLat, Double centerLng, Double radiusKm,
            int page, int pageSize, Integer statusId) {

        List<Post> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        boolean useLocation = (centerLat != null && centerLng != null && radiusKm != null);

        // 🔍 DEBUG: Log location search parameters
        System.out.println("=== PostDAO.searchWithLocation ===");
        System.out.println("CenterLat: " + centerLat);
        System.out.println("CenterLng: " + centerLng);
        System.out.println("RadiusKm: " + radiusKm);
        System.out.println("UseLocation: " + useLocation);
        System.out.println("Query: " + q);
        System.out.println("StatusId: " + statusId);

        if (useLocation) {
            sql.append("SELECT p.*, ps.status_name, ");
            sql.append("(6371.0 * ACOS( ");
            sql.append("COS(RADIANS(?)) * COS(RADIANS(p.lat)) * COS(RADIANS(p.lng) - RADIANS(?)) ");
            sql.append("+ SIN(RADIANS(?)) * SIN(RADIANS(p.lat)) ");
            sql.append(")) AS distance_km ");
            params.add(centerLat); // SELECT expr #1
            params.add(centerLng); // SELECT expr #2
            params.add(centerLat); // SELECT expr #3
            sql.append("FROM posts p LEFT JOIN post_status ps ON p.status_id = ps.status_id ");
            sql.append("WHERE p.lat IS NOT NULL AND p.lng IS NOT NULL ");
        } else {
            sql.append("SELECT p.*, ps.status_name FROM posts p LEFT JOIN post_status ps ON p.status_id = ps.status_id WHERE 1=1 ");
        }

        if (statusId != null) {
            sql.append("AND p.status_id = ? ");
            params.add(statusId);
        }

        if (q != null && !q.trim().isEmpty()) {
            sql.append("AND (p.title LIKE ? OR p.address LIKE ? OR p.content LIKE ?) ");
            String keyword = "%" + q.trim() + "%";
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }
        if (minPrice != null) {
            sql.append("AND p.price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append("AND p.price <= ? ");
            params.add(maxPrice);
        }
        if (minArea != null) {
            sql.append("AND p.area >= ? ");
            params.add(minArea);
        }
        if (maxArea != null) {
            sql.append("AND p.area <= ? ");
            params.add(maxArea);
        }

        if (useLocation) {
            sql.append("AND (6371.0 * ACOS( ");
            sql.append("COS(RADIANS(?)) * COS(RADIANS(p.lat)) * COS(RADIANS(p.lng) - RADIANS(?)) ");
            sql.append("+ SIN(RADIANS(?)) * SIN(RADIANS(p.lat)) ");
            sql.append(")) <= ? ");
            params.add(centerLat);
            params.add(centerLng);
            params.add(centerLat);
            params.add(radiusKm);
            sql.append("ORDER BY distance_km ASC ");
        } else {
            sql.append("ORDER BY p.created_at DESC ");
        }

        sql.append("OFFSET ? LIMIT ?");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        // 🔍 DEBUG: Log final SQL and parameters
        System.out.println("SQL: " + sql.toString());
        System.out.println("Params: " + params);
        System.out.println("==================================");

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); ++i) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Post p = mapRowToPost(rs);
                    list.add(p);
                }
                System.out.println("🔍 Found " + list.size() + " posts");
            }
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.searchWithLocation: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm số post khi search theo radius (hoặc search thường nếu không có
     * location)
     */
    public int countSearchWithLocation(
            String q, BigDecimal minPrice, BigDecimal maxPrice,
            BigDecimal minArea, BigDecimal maxArea,
            Double centerLat, Double centerLng, Double radiusKm,
            Integer statusId) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        boolean useLocation = (centerLat != null && centerLng != null && radiusKm != null);

        if (useLocation) {
            sql.append("SELECT COUNT(*) FROM posts p LEFT JOIN post_status ps ON p.status_id = ps.status_id WHERE p.lat IS NOT NULL AND p.lng IS NOT NULL ");
        } else {
            sql.append("SELECT COUNT(*) FROM posts p LEFT JOIN post_status ps ON p.status_id = ps.status_id WHERE 1=1 ");
        }

        if (statusId != null) {
            sql.append("AND p.status_id = ? ");
            params.add(statusId);
        }

        if (q != null && !q.trim().isEmpty()) {
            sql.append("AND (p.title LIKE ? OR p.address LIKE ? OR p.content LIKE ?) ");
            String keyword = "%" + q.trim() + "%";
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }
        if (minPrice != null) {
            sql.append("AND p.price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append("AND p.price <= ? ");
            params.add(maxPrice);
        }
        if (minArea != null) {
            sql.append("AND p.area >= ? ");
            params.add(minArea);
        }
        if (maxArea != null) {
            sql.append("AND p.area <= ? ");
            params.add(maxArea);
        }

        if (useLocation) {
            sql.append("AND (6371.0 * ACOS( ");
            sql.append("COS(RADIANS(?)) * COS(RADIANS(p.lat)) * COS(RADIANS(p.lng) - RADIANS(?)) ");
            sql.append("+ SIN(RADIANS(?)) * SIN(RADIANS(p.lat)) ");
            sql.append(")) <= ? ");
            params.add(centerLat);
            params.add(centerLng);
            params.add(centerLat);
            params.add(radiusKm);
        }

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); ++i) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.countSearchWithLocation: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Cập nhật status của post
     */
    public void updateStatus(int postId, int statusId) throws SQLException {
        String sql = "UPDATE posts SET status_id = ?, updated_at = NOW() WHERE post_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusId);
            ps.setInt(2, postId);
            ps.executeUpdate();
        }
    }

    /**
     * Cập nhật Facebook Post ID
     */
    public void updateFacebookPostId(int postId, String facebookPostId) throws SQLException {
        String sql = "UPDATE posts SET facebook_post_id = ?, updated_at = NOW() WHERE post_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, facebookPostId);
            ps.setInt(2, postId);
            ps.executeUpdate();
        }
    }

    /**
     * Đếm số bài đăng của user trong một ngày cụ thể (cho Pro system)
     */
    public int getPostsCountByUserAndDate(int userId, Date date) {
        String sql = "SELECT COUNT(*) FROM posts WHERE user_id = ? AND CAST(created_at AS DATE) = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setDate(2, date);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.getPostsCountByUserAndDate: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    /**
     * Get post by ID - convenience method for findById that returns Post directly
     */
    public Post getPostById(int postId) {
        Optional<Post> postOpt = findById(postId);
        return postOpt.orElse(null);
    }

    /**
     * Update scheduled time for a post
     */
    public boolean updateScheduledTime(int postId, Timestamp scheduledAt) {
        String sql = "UPDATE posts SET scheduled_at = ?, updated_at = NOW() WHERE post_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, scheduledAt);
            ps.setInt(2, postId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.updateScheduledTime: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Publish a scheduled post now - updates status to APPROVED and sets published_at
     */
    public boolean publishScheduledPost(int postId) {
        String sql = "UPDATE posts SET status_id = (SELECT status_id FROM post_status WHERE status_name = 'APPROVED'), " +
                     "published_at = NOW(), updated_at = NOW() WHERE post_id = ? AND status_id = (SELECT status_id FROM post_status WHERE status_name = 'SCHEDULED')";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, postId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.publishScheduledPost: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get scheduled posts that are ready to be published
     */
    public List<Post> getScheduledPostsToPublish() {
        String sql = "SELECT p.*, ps.status_name, u.full_name, u.phone, u.email " +
                     "FROM posts p " +
                     "JOIN post_status ps ON p.status_id = ps.status_id " +
                     "JOIN users u ON p.user_id = u.user_id " +
                     "WHERE ps.status_name = 'SCHEDULED' " +
                     "AND p.scheduled_at <= NOW() " +
                     "ORDER BY p.scheduled_at ASC";
        
        List<Post> posts = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Post post = mapRowToPost(rs);
                posts.add(post);
            }
            
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.getScheduledPostsToPublish: " + e.getMessage());
            e.printStackTrace();
        }
        
        return posts;
    }
    
    /**
     * Xóa post (dành cho admin)
     */
    public boolean deletePost(int postId) throws SQLException {
        String sql = "DELETE FROM posts WHERE post_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error in PostDAO.deletePost: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
