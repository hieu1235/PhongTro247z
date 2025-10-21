package Service;

import Dal.PostDAO;
import Model.Post;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service để quản lý và xử lý scheduled posts
 * Tự động publish các posts đã lập lịch khi đến thời gian
 */
public class PostSchedulerService {

    private static final Logger logger = Logger.getLogger(PostSchedulerService.class.getName());
    private final PostDAO postDAO;

    public PostSchedulerService() {
        this.postDAO = new PostDAO();
    }

    /**
     * Scan và publish các scheduled posts đã đến thời gian
     * @return số lượng posts đã được publish
     */
    public int processScheduledPosts() {
        logger.info("Starting scheduled posts processing...");
        
        try {
            // Lấy danh sách scheduled posts đã đến thời gian
            List<Post> scheduledPosts = postDAO.getScheduledPostsToPublish();
            
            if (scheduledPosts.isEmpty()) {
                logger.info("No scheduled posts found to publish");
                return 0;
            }
            
            int publishedCount = 0;
            
            for (Post post : scheduledPosts) {
                try {
                    // Publish từng post
                    boolean success = postDAO.publishScheduledPost(post.getPostId());
                    
                    if (success) {
                        publishedCount++;
                        logger.info("Successfully published scheduled post: " + post.getPostId() + " - " + post.getTitle());
                        
                        // TODO: Tích hợp với FacebookService nếu cần đăng lên Facebook
                        // publishToFacebook(post);
                        
                    } else {
                        logger.warning("Failed to publish scheduled post: " + post.getPostId() + " - " + post.getTitle());
                    }
                    
                } catch (Exception e) {
                    logger.severe("Error publishing post " + post.getPostId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            logger.info("Scheduled posts processing completed. Published: " + publishedCount + " out of " + scheduledPosts.size());
            return publishedCount;
            
        } catch (Exception e) {
            logger.severe("Error in processScheduledPosts: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Kiểm tra số lượng scheduled posts đang chờ
     * @return số lượng scheduled posts
     */
    public int getScheduledPostsCount() {
        try {
            List<Post> scheduledPosts = postDAO.getScheduledPostsToPublish();
            return scheduledPosts.size();
        } catch (Exception e) {
            logger.severe("Error getting scheduled posts count: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Lấy danh sách scheduled posts sắp được publish (trong 1 giờ tới)
     * @return danh sách scheduled posts
     */
    public List<Post> getUpcomingScheduledPosts() {
        try {
            return postDAO.getScheduledPostsToPublish();
        } catch (Exception e) {
            logger.severe("Error getting upcoming scheduled posts: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Method để tích hợp với FacebookService (tương lai)
     * Đăng scheduled post lên Facebook page
     */
    /*
    private void publishToFacebook(Post post) {
        try {
            // TODO: Implement Facebook publishing logic
            // FacebookService facebookService = new FacebookService();
            // facebookService.publishPost(post);
            logger.info("Published post to Facebook: " + post.getPostId());
        } catch (Exception e) {
            logger.warning("Failed to publish post to Facebook: " + post.getPostId() + " - " + e.getMessage());
        }
    }
    */
}