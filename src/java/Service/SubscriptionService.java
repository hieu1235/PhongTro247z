package Service;

import Model.*;
import Dal.SubscriptionDAO;
import Dal.BalanceDAO;
import Dal.PostDAO;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Simple Subscription Service - Chỉ có 2 loại user: Free (1 bài/ngày) và Pro (10 bài/ngày, 100 xu)
 */
public class SubscriptionService {
    
    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
    private final BalanceDAO balanceDAO = new BalanceDAO();
    private final PostDAO postDAO = new PostDAO();
    
    /**
     * Mua gói Pro (100 xu = 30 ngày Pro)
     */
    public boolean buyProSubscription(int userId) {
        try {
            // Kiểm tra số dư
            UserBalance balance = balanceDAO.getUserBalance(userId);
            if (balance == null || balance.getAvailableCoins().compareTo(BigDecimal.valueOf(100)) < 0) {
                System.out.println("SubscriptionService: User " + userId + " không đủ xu để mua Pro");
                return false;
            }
            
            // Trừ 100 xu
            boolean deducted = balanceDAO.updateBalance(userId, BigDecimal.valueOf(-100), 
                                                       "Mua gói Pro 30 ngày", "subscription");
            
            if (!deducted) {
                System.out.println("SubscriptionService: Không thể trừ xu cho user " + userId);
                return false;
            }
            
            // Cập nhật Pro status trong database (sử dụng stored procedure)
            boolean updated = subscriptionDAO.buyProSubscription(userId);
            
            if (updated) {
                System.out.println("SubscriptionService: User " + userId + " đã mua gói Pro thành công");
                return true;
            } else {
                // Rollback nếu update Pro status thất bại
                balanceDAO.updateBalance(userId, BigDecimal.valueOf(100), 
                                       "Hoàn xu do lỗi mua Pro", "refund");
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("SubscriptionService: Lỗi mua Pro cho user " + userId + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Kiểm tra user có phải Pro không (và còn hiệu lực)
     */
    public boolean isUserPro(int userId) {
        try {
            User user = subscriptionDAO.getUserById(userId);
            if (user == null) return false;
            
            // Kiểm tra Pro status và thời gian hết hạn
            if (user.isPro() && user.getProExpiresAt() != null) {
                return user.getProExpiresAt().after(new Timestamp(System.currentTimeMillis()));
            }
            
            return false;
        } catch (Exception e) {
            System.out.println("SubscriptionService: Lỗi kiểm tra Pro status user " + userId + " - " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy thông tin Pro của user
     */
    public UserProInfo getUserProInfo(int userId) {
        try {
            User user = subscriptionDAO.getUserById(userId);
            if (user == null) {
                return new UserProInfo(false, null, 1, 0);
            }
            
            boolean isPro = isUserPro(userId);
            Timestamp proExpires = user.getProExpiresAt();
            int maxPosts = isPro ? 10 : 1;
            int postsToday = getPostsCountToday(userId);
            
            return new UserProInfo(isPro, proExpires, maxPosts, postsToday);
            
        } catch (Exception e) {
            System.out.println("SubscriptionService: Lỗi lấy Pro info user " + userId + " - " + e.getMessage());
            return new UserProInfo(false, null, 1, 0);
        }
    }
    
    /**
     * Kiểm tra user có thể đăng bài không
     */
    public boolean canUserPost(int userId) {
        try {
            UserProInfo proInfo = getUserProInfo(userId);
            return proInfo.getPostsToday() < proInfo.getMaxPostsPerDay();
        } catch (Exception e) {
            System.out.println("SubscriptionService: Lỗi kiểm tra post permission user " + userId + " - " + e.getMessage());
            return false; // Safe default: không cho đăng nếu có lỗi
        }
    }
    
    /**
     * Kiểm tra user có thể đăng bài hôm nay không (alias cho canUserPost)
     */
    public boolean canUserPostToday(int userId) {
        return canUserPost(userId);
    }
    
    /**
     * Lấy số bài đã đăng hôm nay
     */
    public int getPostsCountToday(int userId) {
        try {
            LocalDate today = LocalDate.now();
            Date todayDate = Date.valueOf(today);
            return postDAO.getPostsCountByUserAndDate(userId, todayDate);
        } catch (Exception e) {
            System.out.println("SubscriptionService: Lỗi đếm posts hôm nay user " + userId + " - " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Lấy số bài đã đăng hôm nay (alias cho getPostsCountToday)
     */
    public int getTodayPostCount(int userId) {
        return getPostsCountToday(userId);
    }
    
    /**
     * Lấy số bài còn lại có thể đăng hôm nay
     */
    public int getRemainingPostsToday(int userId) {
        UserProInfo proInfo = getUserProInfo(userId);
        return Math.max(0, proInfo.getMaxPostsPerDay() - proInfo.getPostsToday());
    }
    
    /**
     * Cập nhật Pro status cho tất cả user (chạy định kỳ để check hết hạn)
     */
    public void updateAllProStatus() {
        try {
            int updatedUsers = subscriptionDAO.updateExpiredProUsers();
            if (updatedUsers > 0) {
                System.out.println("SubscriptionService: Đã downgrade " + updatedUsers + " user Pro hết hạn");
            }
        } catch (Exception e) {
            System.out.println("SubscriptionService: Lỗi cập nhật Pro status - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Lấy thống kê Pro users
     */
    public ProStats getProStats() {
        try {
            SubscriptionDAO.ProStats daoStats = subscriptionDAO.getProStats();
            double conversionRate = daoStats.getTotalUsers() > 0 ? 
                (double) daoStats.getActivePro() / daoStats.getTotalUsers() * 100 : 0.0;
            return new ProStats(daoStats.getTotalUsers(), daoStats.getActivePro(), conversionRate);
        } catch (Exception e) {
            System.out.println("SubscriptionService: Lỗi lấy Pro stats - " + e.getMessage());
            return new ProStats(0, 0, 0.0);
        }
    }
    
    /**
     * Validate post creation
     */
    public PostValidationResult validatePostCreation(int userId) {
        try {
            UserProInfo proInfo = getUserProInfo(userId);
            
            if (proInfo.getPostsToday() >= proInfo.getMaxPostsPerDay()) {
                String message = proInfo.isPro() ? 
                    "Bạn đã đăng đủ 10 bài hôm nay (gói Pro). Vui lòng thử lại vào ngày mai." :
                    "Bạn đã đăng đủ 1 bài hôm nay (gói Free). Nâng cấp Pro để đăng 10 bài/ngày!";
                
                return new PostValidationResult(false, message, proInfo);
            }
            
            return new PostValidationResult(true, "OK", proInfo);
            
        } catch (Exception e) {
            System.out.println("SubscriptionService: Lỗi validate post creation user " + userId + " - " + e.getMessage());
            return new PostValidationResult(false, "Có lỗi xảy ra. Vui lòng thử lại.", null);
        }
    }
    
    // ===== Inner Classes cho data transfer =====
    
    public static class UserProInfo {
        private boolean isPro;
        private Timestamp proExpiresAt;
        private int maxPostsPerDay;
        private int postsToday;
        
        public UserProInfo(boolean isPro, Timestamp proExpiresAt, int maxPostsPerDay, int postsToday) {
            this.isPro = isPro;
            this.proExpiresAt = proExpiresAt;
            this.maxPostsPerDay = maxPostsPerDay;
            this.postsToday = postsToday;
        }
        
        // Getters
        public boolean isPro() { return isPro; }
        public boolean getPro() { return isPro; } // Alternative getter for JSP EL
        public Timestamp getProExpiresAt() { return proExpiresAt; }
        public Timestamp getExpirationDate() { return proExpiresAt; } // Alias for JSP compatibility
        public int getMaxPostsPerDay() { return maxPostsPerDay; }
        public int getPostsToday() { return postsToday; }
        public int getRemainingPosts() { return Math.max(0, maxPostsPerDay - postsToday); }
        
        public int getProDaysRemaining() {
            if (!isPro || proExpiresAt == null) {
                return 0;
            }
            
            long now = System.currentTimeMillis();
            long expireTime = proExpiresAt.getTime();
            long diffInMillis = expireTime - now;
            
            if (diffInMillis <= 0) {
                return 0;
            }
            
            // Convert milliseconds to days
            return (int) Math.ceil(diffInMillis / (1000.0 * 60 * 60 * 24));
        }
        
        public String getProStatusText() {
            if (isPro) {
                return "Pro (hết hạn: " + (proExpiresAt != null ? proExpiresAt.toString() : "N/A") + ")";
            } else {
                return "Free";
            }
        }
    }
    
    public static class ProStats {
        private int totalUsers;
        private int proUsers;
        private double proPercentage;
        
        public ProStats(int totalUsers, int proUsers, double proPercentage) {
            this.totalUsers = totalUsers;
            this.proUsers = proUsers;
            this.proPercentage = proPercentage;
        }
        
        public int getTotalUsers() { return totalUsers; }
        public int getProUsers() { return proUsers; }
        public int getFreeUsers() { return totalUsers - proUsers; }
        public double getProPercentage() { return proPercentage; }
    }
    
    public static class PostValidationResult {
        private boolean canPost;
        private String message;
        private UserProInfo proInfo;
        
        public PostValidationResult(boolean canPost, String message, UserProInfo proInfo) {
            this.canPost = canPost;
            this.message = message;
            this.proInfo = proInfo;
        }
        
        public boolean canPost() { return canPost; }
        public String getMessage() { return message; }
        public UserProInfo getProInfo() { return proInfo; }
    }
}