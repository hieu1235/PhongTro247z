package Service;

import Dal.PostDAO;
import Dal.PostImageDAO;
import DBcontext.DBContext;
import Model.Post;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class PostService {

    private final PostDAO postDAO = new PostDAO();
    private final PostImageDAO postImageDAO = new PostImageDAO();
    private final LocalFileService fileService = new LocalFileService();

    private static final int MAX_IMAGES = 5;

    // Simplified method for testing - takes byte arrays directly
    public int createPostWithImages(Post post, List<byte[]> imageBytes) throws SQLException, Exception {
        if (imageBytes != null && imageBytes.size() > MAX_IMAGES) {
            throw new IllegalArgumentException("Chỉ được upload tối đa " + MAX_IMAGES + " ảnh.");
        }

        Connection conn = null;
        List<String> uploadedUrls = new ArrayList<>();
        int postId = -1;

        try {
            // 1. Create post first
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            postId = postDAO.insert(post, conn);

            // 2. Upload images and save to DB
            if (imageBytes != null && !imageBytes.isEmpty()) {
                for (int i = 0; i < imageBytes.size(); i++) {
                    byte[] imageData = imageBytes.get(i);

                    String imageUrl = null;
                    try {
                        // Upload to Local File Storage
                        imageUrl = fileService.uploadImage(imageData, post.getUserId());
                        uploadedUrls.add(imageUrl);

                        // Save to database immediately  
                        boolean isThumbnail = (i == 0);
                        postImageDAO.insertImage(postId, imageUrl, isThumbnail, conn);

                    } catch (Exception imageEx) {
                        // If file upload failed, don't add to uploadedUrls
                        throw new Exception("Lỗi upload ảnh " + (i + 1) + ": " + imageEx.getMessage(), imageEx);
                    }
                }
            }

            // 3. Commit transaction
            conn.commit();
            return postId;

        } catch (Exception ex) {
            // Rollback database transaction
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    // Silent rollback
                }
            }

            // ✅ Only cleanup if we have uploaded URLs and it's NOT a database error
            if (!uploadedUrls.isEmpty()) {
                // Check if error is database-related vs upload-related
                String errorMsg = ex.getMessage().toLowerCase();
                if (!errorMsg.contains("duplicate") && !errorMsg.contains("constraint")
                        && !errorMsg.contains("foreign key") && !errorMsg.contains("sql")) {

                    // Probably upload error, cleanup Local File Storage
                    try {
                        fileService.deleteMultipleImages(uploadedUrls);
                    } catch (Exception cleanupEx) {
                        // Silent cleanup
                    }
                }
                // If database error, keep images on server for manual cleanup
            }

            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception connEx) {
                    // Silent
                }
            }
        }
    }

    // Method for updating posts with byte arrays (for EditPostServlet)
    public void updatePostWithImages(Post post, List<byte[]> newImageBytes) throws SQLException, Exception {
        if (newImageBytes != null && newImageBytes.size() > MAX_IMAGES) {
            throw new IllegalArgumentException("Khi thay ảnh mới, chỉ được upload tối đa " + MAX_IMAGES + " ảnh.");
        }

        Connection conn = null;
        List<String> newUploadedUrls = new ArrayList<>();
        List<String> oldImageUrls = null;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            postDAO.update(post, conn);

            if (newImageBytes != null && !newImageBytes.isEmpty()) {
                oldImageUrls = postImageDAO.findByPost(post.getPostId());

                for (int i = 0; i < newImageBytes.size(); i++) {
                    byte[] imageData = newImageBytes.get(i);

                    try {
                        String imageUrl = fileService.uploadImage(imageData, post.getUserId());
                        newUploadedUrls.add(imageUrl);

                    } catch (Exception uploadEx) {
                        throw new Exception("Lỗi upload ảnh thay thế " + (i + 1) + ": " + uploadEx.getMessage(), uploadEx);
                    }
                }

                postImageDAO.deleteByPost(post.getPostId(), conn);

                for (int i = 0; i < newUploadedUrls.size(); i++) {
                    String imageUrl = newUploadedUrls.get(i);
                    boolean isThumbnail = (i == 0);
                    postImageDAO.insertImage(post.getPostId(), imageUrl, isThumbnail, conn);
                }
            }

            conn.commit();

            if (oldImageUrls != null && !oldImageUrls.isEmpty() && !newUploadedUrls.isEmpty()) {
                try {
                    fileService.deleteMultipleImages(oldImageUrls);
                } catch (Exception cleanupEx) {
                    // Silent
                }
            }

        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    // Silent
                }
            }

            if (!newUploadedUrls.isEmpty()) {
                try {
                    fileService.deleteMultipleImages(newUploadedUrls);
                } catch (Exception cleanupEx) {
                    // Silent
                }
            }

            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception connEx) {
                    // Silent
                }
            }
        }
    }

    /**
     * Tạo scheduled post - không publish ngay mà lưu với status SCHEDULED
     */
    public int createScheduledPost(Post post, List<byte[]> imageBytes) throws SQLException, Exception {
        // Set status to SCHEDULED (status_id = 4 theo database schema)
        post.setStatusId(4); // SCHEDULED status
        
        // Sử dụng method createPostWithImages hiện tại
        return createPostWithImages(post, imageBytes);
    }

    /**
     * Update scheduled time cho một post
     */
    public boolean updateScheduledTime(int postId, java.sql.Timestamp scheduledAt) {
        return postDAO.updateScheduledTime(postId, scheduledAt);
    }

    /**
     * Publish một scheduled post ngay lập tức
     */
    public boolean publishScheduledPost(int postId) {
        return postDAO.publishScheduledPost(postId);
    }

    /**
     * Lấy danh sách scheduled posts của user
     */
    public List<Post> getScheduledPostsByUser(int userId, int page, int pageSize) {
        // Sử dụng method findByUser hiện tại, filter theo status ở service layer
        List<Post> allPosts = postDAO.findByUser(userId, page, pageSize);
        List<Post> scheduledPosts = new ArrayList<>();
        
        for (Post post : allPosts) {
            if ("SCHEDULED".equals(post.getStatusName())) {
                scheduledPosts.add(post);
            }
        }
        
        return scheduledPosts;
    }

    /**
     * Kiểm tra xem post có thể được scheduled không
     */
    public boolean canSchedulePost(Post post) {
        // Validate basic post information
        if (post == null || post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            return false;
        }
        
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            return false;
        }
        
        if (post.getAddress() == null || post.getAddress().trim().isEmpty()) {
            return false;
        }
        
        // Validate scheduled time is in future
        if (post.getScheduledAt() != null) {
            java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
            return post.getScheduledAt().after(now);
        }
        
        return true;
    }
}
