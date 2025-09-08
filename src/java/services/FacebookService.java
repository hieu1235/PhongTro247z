package services;

import dao.UserDAO;
import dao.ListingDAO;
import dao.ActivityLogDAO;
import models.User;
import models.Listing;
import models.FacebookPostRequest;
import models.FacebookPostResponse;
import utils.FacebookAPIUtil;
import config.AppConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FacebookService {
    
    private UserDAO userDAO;
    private ListingDAO listingDAO;
    private ActivityLogDAO activityLogDAO;
    private FacebookAPIUtil facebookAPIUtil;
    private Gson gson;
    
    public FacebookService() {
        this.userDAO = new UserDAO();
        this.listingDAO = new ListingDAO();
        this.activityLogDAO = new ActivityLogDAO();
        this.facebookAPIUtil = new FacebookAPIUtil();
        this.gson = new Gson();
    }
    
    /**
     * Đăng bài lên Facebook cho một listing cụ thể
     */
    public FacebookPostResponse postListingToFacebook(Long listingId, Long userId) {
        try {
            // Lấy thông tin listing
            Listing listing = listingDAO.getListingById(listingId);
            if (listing == null) {
                return new FacebookPostResponse(false, "Không tìm thấy tin đăng", "LISTING_NOT_FOUND", "");
            }
            
            // Kiểm tra quyền sở hữu
            if (!listing.getLandlordId().equals(userId)) {
                return new FacebookPostResponse(false, "Bạn không có quyền đăng tin này", "PERMISSION_DENIED", "");
            }
            
            // Lấy thông tin user
            User user = userDAO.getUserById(userId);
            if (user == null || user.getFacebookPageId() == null || user.getFacebookPageAccessToken() == null) {
                return new FacebookPostResponse(false, "Chưa cấu hình Facebook Page", "NO_FACEBOOK_CONFIG", "");
            }
            
            // Tạo nội dung đăng bài
            String postMessage = generatePostMessage(listing);
            String postLink = AppConfig.getAppBaseUrl() + "/listing/" + listing.getListingId();
            
            // Lấy hình ảnh
            List<String> imageUrls = listingDAO.getListingImageUrls(listingId);
            
            // Tạo Facebook post request
            FacebookPostRequest postRequest = new FacebookPostRequest(
                postMessage,
                postLink,
                imageUrls.isEmpty() ? null : imageUrls,
                user.getFacebookPageAccessToken(),
                user.getFacebookPageId()
            );
            
            // Đăng bài
            FacebookPostResponse response = facebookAPIUtil.postToFacebookPage(postRequest);
            
            // Log activity
            logFacebookActivity(userId, listingId, response);
            
            return response;
            
        } catch (Exception e) {
            System.err.println("Lỗi đăng bài Facebook cho listing " + listingId + ": " + e.getMessage());
            e.printStackTrace();
            return new FacebookPostResponse(false, "Lỗi hệ thống: " + e.getMessage(), "SYSTEM_ERROR", e.toString());
        }
    }
    
    /**
     * Auto post khi listing được approve
     */
    public FacebookPostResponse autoPostListingToFacebook(Long listingId) {
        try {
            Listing listing = listingDAO.getListingById(listingId);
            if (listing == null) {
                return new FacebookPostResponse(false, "Không tìm thấy tin đăng", "LISTING_NOT_FOUND", "");
            }
            
            // Kiểm tra auto post có được bật không
            if (!listing.isAutoPostSocial()) {
                return new FacebookPostResponse(false, "Auto post chưa được bật cho tin đăng này", "AUTO_POST_DISABLED", "");
            }
            
            // Kiểm tra đã đăng chưa
            if (activityLogDAO.isListingPostedToFacebook(listingId, listing.getLandlordId())) {
                return new FacebookPostResponse(false, "Tin đăng này đã được đăng lên Facebook", "ALREADY_POSTED", "");
            }
            
            return postListingToFacebook(listingId, listing.getLandlordId());
            
        } catch (Exception e) {
            System.err.println("Lỗi auto post Facebook cho listing " + listingId + ": " + e.getMessage());
            e.printStackTrace();
            return new FacebookPostResponse(false, "Lỗi hệ thống: " + e.getMessage(), "SYSTEM_ERROR", e.toString());
        }
    }
    
    /**
     * Đăng nhiều listings cùng lúc (batch processing)
     */
    public void processPendingAutoPostListings() {
        try {
            List<Listing> pendingListings = listingDAO.getPendingAutoPostListings();
            System.out.println("Tìm thấy " + pendingListings.size() + " tin đăng cần auto post");
            
            for (Listing listing : pendingListings) {
                try {
                    // Delay giữa các post để tránh spam
                    Thread.sleep(AppConfig.getAutoPostDelayBetweenPostsSeconds() * 1000);
                    
                    FacebookPostResponse response = autoPostListingToFacebook(listing.getListingId());
                    
                    if (response.isSuccess()) {
                        System.out.println("Đã đăng thành công listing " + listing.getListingId() + " lên Facebook");
                    } else {
                        System.err.println("Đăng listing " + listing.getListingId() + " thất bại: " + response.getMessage());
                    }
                    
                } catch (InterruptedException e) {
                    System.err.println("Bị gián đoạn khi đăng listing " + listing.getListingId());
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi process pending auto post listings: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Validate Facebook configuration
     */
    public boolean validateFacebookConfig(Long userId) {
        try {
            User user = userDAO.getUserById(userId);
            if (user == null || user.getFacebookPageId() == null || user.getFacebookPageAccessToken() == null) {
                return false;
            }
            
            return facebookAPIUtil.validatePageAccessToken(user.getFacebookPageId(), user.getFacebookPageAccessToken());
            
        } catch (Exception e) {
            System.err.println("Lỗi validate Facebook config cho user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Tạo nội dung đăng bài
     */
    private String generatePostMessage(Listing listing) {
        StringBuilder message = new StringBuilder();
        
        // Tiêu đề
        message.append("🏠 ").append(listing.getTitle()).append("\n\n");
        
        // Thông tin cơ bản
        message.append("📍 Địa chỉ: ").append(listing.getFullAddress()).append("\n");
        
        // Giá thuê
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        currencyFormat.setCurrency(java.util.Currency.getInstance("VND"));
        message.append("💰 Giá thuê: ").append(currencyFormat.format(listing.getPricePerMonth())).append("/tháng\n");
        
        // Diện tích
        if (listing.getAreaSqm() != null) {
            message.append("📐 Diện tích: ").append(listing.getAreaSqm()).append(" m²\n");
        }
        
        // Số người ở
        if (listing.getMaxOccupants() != null && listing.getMaxOccupants() > 0) {
            message.append("👥 Tối đa: ").append(listing.getMaxOccupants()).append(" người\n");
        }
        
        // Mô tả
        if (listing.getDescription() != null && !listing.getDescription().trim().isEmpty()) {
            message.append("\n📝 Mô tả:\n").append(listing.getDescription()).append("\n");
        }
        
        // Call to action
        message.append("\n🔗 Xem chi tiết và liên hệ tại: ");
        message.append(AppConfig.getAppBaseUrl()).append("/listing/").append(listing.getListingId());
        
        // Hashtags
        message.append("\n\n#PhongTro247 #ChoThuePhongTro #BatDongSan #PhongTro");
        
        if (listing.getAddressCity() != null) {
            message.append(" #").append(listing.getAddressCity().replaceAll("\\s+", ""));
        }
        
        return message.toString();
    }
    
    /**
     * Log Facebook activity
     */
    private void logFacebookActivity(Long userId, Long listingId, FacebookPostResponse response) {
        try {
            JsonObject details = new JsonObject();
            details.addProperty("success", response.isSuccess());
            details.addProperty("postId", response.getPostId());
            details.addProperty("message", response.getMessage());
            details.addProperty("errorCode", response.getErrorCode());
            details.addProperty("listingId", listingId);
            
            String summary = response.isSuccess() 
                ? "Đăng bài lên Facebook thành công" 
                : "Đăng bài lên Facebook thất bại: " + response.getMessage();
            
            activityLogDAO.createActivityLog(
                userId,
                "FACEBOOK_POST",
                listingId.toString(),
                "LISTING",
                summary,
                details.toString()
            );
            
        } catch (Exception e) {
            System.err.println("Lỗi log Facebook activity: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Lấy thống kê Facebook posts
     */
    public JsonObject getFacebookPostStats(Long userId) {
        try {
            JsonObject stats = new JsonObject();
            
            // Posts thành công trong 7 ngày
            int successfulPosts7Days = activityLogDAO.getFacebookPostCount(userId, "success", 7);
            stats.addProperty("successfulPosts7Days", successfulPosts7Days);
            
            // Posts thất bại trong 7 ngày
            int failedPosts7Days = activityLogDAO.getFacebookPostCount(userId, "failed", 7);
            stats.addProperty("failedPosts7Days", failedPosts7Days);
            
            // Posts thành công trong 30 ngày
            int successfulPosts30Days = activityLogDAO.getFacebookPostCount(userId, "success", 30);
            stats.addProperty("successfulPosts30Days", successfulPosts30Days);
            
            // Posts thất bại trong 30 ngày
            int failedPosts30Days = activityLogDAO.getFacebookPostCount(userId, "failed", 30);
            stats.addProperty("failedPosts30Days", failedPosts30Days);
            
            // Tổng posts
            stats.addProperty("totalSuccessful", successfulPosts30Days);
            stats.addProperty("totalFailed", failedPosts30Days);
            stats.addProperty("totalPosts", successfulPosts30Days + failedPosts30Days);
            
            // Tỷ lệ thành công
            int totalPosts = successfulPosts30Days + failedPosts30Days;
            double successRate = totalPosts > 0 ? (double) successfulPosts30Days / totalPosts * 100 : 0;
            stats.addProperty("successRate", Math.round(successRate * 100.0) / 100.0);
            
            return stats;
            
        } catch (Exception e) {
            System.err.println("Lỗi get Facebook post stats cho user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return new JsonObject();
        }
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        try {
            if (facebookAPIUtil != null) {
                facebookAPIUtil.close();
            }
        } catch (Exception e) {
            System.err.println("Lỗi cleanup FacebookService: " + e.getMessage());
            e.printStackTrace();
        }
    }
}