package Service;

import Model.FacebookSettings;
import Model.Post;
import Model.PostImage;
import Dal.FacebookSettingsDAO;
import Dal.PostFacebookPageDAO;
import Dal.PostImageDAO;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Service để đăng tin lên Facebook Page
 * ✅ Tokens được tự động giải mã bởi FacebookSettingsDAO
 */
public class FacebookService {
    
    private final FacebookSettingsDAO fbSettingsDAO = new FacebookSettingsDAO();
    private final PostFacebookPageDAO postFacebookPageDAO = new PostFacebookPageDAO();
    private final PostImageDAO postImageDAO = new PostImageDAO();
    private static final String FB_GRAPH_URL = "https://graph.facebook.com/v21.0/";
    
    // ✅ Base URL config - tự động detect môi trường
    private static final String DEFAULT_BASE_URL = "http://localhost:8080/PhongTro247";
    
    /**
     * Get base URL for public image access
     * Ưu tiên:
     * 1. Environment variable APP_BASE_URL
     * 2. System property app.base.url
     * 3. Default localhost (development)
     * 
     * Production setup:
     * - Linux: export APP_BASE_URL="https://phongtro247.com"
     * - Windows: set APP_BASE_URL=https://phongtro247.com
     * - Tomcat: Thêm vào setenv.sh hoặc catalina.properties
     */
    private String getBaseUrl() {
        // 1. Đọc từ Environment Variable (Production)
        String envUrl = System.getenv("APP_BASE_URL");
        if (envUrl != null && !envUrl.isEmpty()) {
            System.out.println("✅ Using APP_BASE_URL from environment: " + envUrl);
            return envUrl;
        }
        
        // 2. Đọc từ System Property (Tomcat startup)
        String sysUrl = System.getProperty("app.base.url");
        if (sysUrl != null && !sysUrl.isEmpty()) {
            System.out.println("✅ Using app.base.url from system property: " + sysUrl);
            return sysUrl;
        }
        
        // 3. Fallback to localhost (Development)
        System.out.println("⚠️ Using default localhost URL (development mode)");
        return DEFAULT_BASE_URL;
    }
    
    /**
     * Đăng post lên Facebook Page mặc định
     */
    public String postToFacebook(Post post, int userId) {
        try {
            FacebookSettings fbSettings = fbSettingsDAO.getDefaultPage(userId);
            if (fbSettings == null || !fbSettings.isActive() || !fbSettings.isAutoPost()) {
                return null;
            }
            
            return postToSpecificPage(post, fbSettings);
            
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * Đăng post lên nhiều Pages được chọn
     */
    public Map<String, String> postToMultiplePages(Post post, int userId, List<String> selectedPageIds) {
        Map<String, String> results = new HashMap<>();
        
        if (selectedPageIds == null || selectedPageIds.isEmpty()) {
            return results;
        }
        
        try {
            List<FacebookSettings> userPages = fbSettingsDAO.getAllActivePages(userId);
            Map<String, FacebookSettings> pageMap = new HashMap<>();
            for (FacebookSettings page : userPages) {
                pageMap.put(page.getPageId(), page);
            }
            
            for (String pageId : selectedPageIds) {
                FacebookSettings pageSettings = pageMap.get(pageId);
                if (pageSettings == null) {
                    String error = "ERROR: Page không tồn tại hoặc không có quyền";
                    results.put(pageId, error);
                    updatePostPageResult(post.getPostId(), pageId, null, "FAILED", "Page không tồn tại");
                    continue;
                }
                
                if (!pageSettings.isAutoPost()) {
                    String error = "ERROR: Page không bật auto post";
                    results.put(pageId, error);
                    updatePostPageResult(post.getPostId(), pageId, null, "SKIPPED", "Auto post disabled");
                    continue;
                }
                
                try {
                    String fbPostId = postToSpecificPage(post, pageSettings);
                    if (fbPostId != null) {
                        results.put(pageId, fbPostId);
                        updatePostPageResult(post.getPostId(), pageId, fbPostId, "SUCCESS", null);
                    } else {
                        String error = "ERROR: Facebook API call failed";
                        results.put(pageId, error);
                        updatePostPageResult(post.getPostId(), pageId, null, "FAILED", "API call returned null");
                    }
                } catch (Exception e) {
                    String error = "ERROR: " + e.getMessage();
                    results.put(pageId, error);
                    updatePostPageResult(post.getPostId(), pageId, null, "FAILED", e.getMessage());
                }
            }
            
        } catch (Exception e) {
            // Silent handling
        }
        
        return results;
    }
    
    /**
     * Lấy danh sách Facebook Pages của user
     */
    public List<FacebookSettings> getUserPages(int userId) {
        System.out.println("DEBUG: FacebookService.getUserPages called for userId=" + userId);
        try {
            List<FacebookSettings> pages = fbSettingsDAO.getAllActivePages(userId);
            System.out.println("DEBUG: Found " + pages.size() + " active pages for user " + userId);
            for (FacebookSettings page : pages) {
                System.out.println("DEBUG: Page - " + page.getPageName() + " (ID: " + page.getPageId() + 
                                  ", Active: " + page.isActive() + ", AutoPost: " + page.isAutoPost() + 
                                  ", Default: " + page.isDefault() + ")");
            }
            return pages;
        } catch (Exception e) {
            System.out.println("DEBUG: Error in getUserPages: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Helper: Đăng post lên 1 page cụ thể với ảnh
     */
    private String postToSpecificPage(Post post, FacebookSettings pageSettings) {
        try {
            // 1. Lấy ảnh của post từ database
            List<PostImage> postImages = postImageDAO.getImagesByPostId(post.getPostId());
            System.out.println("DEBUG: Found " + postImages.size() + " images for post " + post.getPostId());
            
            String message = formatPostMessage(post);
            
            // 2. Nếu có ảnh, đăng post với ảnh
            if (!postImages.isEmpty()) {
                // ✅ Convert local URLs to absolute URLs
                List<PostImage> publicImages = convertToPublicUrls(postImages);
                return callFacebookAPIWithImages(pageSettings.getPageId(), pageSettings.getAccessToken(), message, publicImages);
            } else {
                // 3. Nếu không có ảnh, đăng post text only
                return callFacebookAPI(pageSettings.getPageId(), pageSettings.getAccessToken(), message, null);
            }
            
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * Convert local image URLs to publicly accessible URLs
     */
    private List<PostImage> convertToPublicUrls(List<PostImage> localImages) {
        String baseUrl = getBaseUrl();
        
        List<PostImage> publicImages = new ArrayList<>();
        for (PostImage image : localImages) {
            PostImage publicImage = new PostImage();
            publicImage.setImageId(image.getImageId());
            publicImage.setPostId(image.getPostId());
            publicImage.setThumbnail(image.isThumbnail());
            
            String localUrl = image.getImageUrl();
            String publicUrl;
            
            // ✅ Check if already a public URL (external CDN, http/https URLs, etc.)
            if (localUrl.startsWith("http")) {
                publicUrl = localUrl;
                System.out.println("DEBUG: Using existing public URL: " + publicUrl);
            } else {
                // Convert local path to public URL
                if (localUrl.startsWith("/uploads/")) {
                    publicUrl = baseUrl + localUrl;
                } else if (localUrl.startsWith("uploads/")) {
                    publicUrl = baseUrl + "/" + localUrl;
                } else {
                    // Fallback: assume it's a filename in uploads/images/
                    publicUrl = baseUrl + "/uploads/images/" + localUrl;
                }
                System.out.println("DEBUG: Converted local " + localUrl + " → public " + publicUrl);
            }
            
            publicImage.setImageUrl(publicUrl);
            publicImages.add(publicImage);
        }
        return publicImages;
    }
    
    /**
     * Helper: Cập nhật kết quả đăng Facebook vào database
     */
    private void updatePostPageResult(int postId, String pageId, String fbPostId, String status, String error) {
        try {
            postFacebookPageDAO.updatePostResult(postId, pageId, fbPostId, status, error);
        } catch (Exception e) {
            // Silent - không throw exception để không ảnh hưởng logic chính
            System.out.println("Warning: Failed to update post_facebook_pages result: " + e.getMessage());
        }
    }
    
    /**
     * Format nội dung post cho Facebook
     */
    private String formatPostMessage(Post post) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("🏠 ").append(post.getTitle()).append("\n\n");
        
        if (post.getPrice() != null) {
            sb.append("💰 Giá: ");
            try {
                java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");
                sb.append(df.format(post.getPrice()));
            } catch (Exception e) {
                sb.append(post.getPrice());
            }
            sb.append(" VNĐ/tháng\n");
        }
        
        if (post.getArea() != null) {
            sb.append("📐 Diện tích: ").append(post.getArea()).append(" m²\n");
        }
        
        if (post.getAddress() != null && !post.getAddress().isEmpty()) {
            sb.append("📍 Địa chỉ: ").append(post.getAddress()).append("\n");
        }
        
        if (post.getContent() != null && !post.getContent().isEmpty()) {
            sb.append("\n").append(post.getContent());
        }
        
        sb.append("\n\n#PhongTro247 #ChoThuePhongTro #PhongTro #NhaDat");
        
        return sb.toString();
    }
    
    /**
     * Call Facebook Graph API để đăng post với ảnh
     */
    private String callFacebookAPIWithImages(String pageId, String accessToken, String message, List<PostImage> images) {
        System.out.println("DEBUG: callFacebookAPIWithImages starting for pageId=" + pageId + " with " + images.size() + " images");
        
        try {
            // 1. Upload ảnh lên Facebook trước
            List<String> uploadedImageIds = new ArrayList<>();
            
            for (PostImage image : images) {
                try {
                    String imageId = uploadImageToFacebook(pageId, accessToken, image.getImageUrl());
                    if (imageId != null) {
                        uploadedImageIds.add(imageId);
                        System.out.println("DEBUG: Uploaded image to Facebook with ID=" + imageId);
                    }
                } catch (Exception e) {
                    System.out.println("DEBUG: Failed to upload image " + image.getImageUrl() + ": " + e.getMessage());
                    // Continue với ảnh khác
                }
            }
            
            // 2. Tạo post với ảnh đã upload
            if (!uploadedImageIds.isEmpty()) {
                return createPostWithUploadedImages(pageId, accessToken, message, uploadedImageIds);
            } else {
                // Fallback: đăng text only nếu không upload được ảnh nào
                System.out.println("DEBUG: No images uploaded successfully, posting text only");
                return callFacebookAPI(pageId, accessToken, message, null);
            }
            
        } catch (Exception ex) {
            System.out.println("DEBUG: Exception in callFacebookAPIWithImages: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Facebook API with images failed: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Upload 1 ảnh lên Facebook và trả về image ID
     */
    private String uploadImageToFacebook(String pageId, String accessToken, String imageUrl) throws Exception {
        System.out.println("DEBUG: Uploading image to Facebook: " + imageUrl);
        
        // ✅ Kiểm tra localhost URL (sẽ fail trên production Facebook)
        if (imageUrl.contains("localhost") || imageUrl.contains("127.0.0.1")) {
            System.out.println("WARNING: Localhost URL detected - Facebook cannot access this URL in production!");
            System.out.println("WARNING: For testing, you need to use ngrok or deploy to a public domain");
        }
        
        // Tạo URL để upload ảnh
        String uploadUrl = FB_GRAPH_URL + pageId + "/photos";
        
        // POST data cho upload ảnh
        StringBuilder postDataBuilder = new StringBuilder();
        postDataBuilder.append("url=").append(URLEncoder.encode(imageUrl, StandardCharsets.UTF_8));
        postDataBuilder.append("&access_token=").append(URLEncoder.encode(accessToken, StandardCharsets.UTF_8));
        postDataBuilder.append("&published=false"); // Upload nhưng chưa publish
        
        String postData = postDataBuilder.toString();
        System.out.println("DEBUG: Facebook image upload POST data length=" + postData.length() + " chars");
        
        HttpURLConnection conn = (HttpURLConnection) new URL(uploadUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        conn.setRequestProperty("User-Agent", "PhongTro247/2.0");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
        
        int responseCode = conn.getResponseCode();
        System.out.println("DEBUG: Facebook image upload response code=" + responseCode);
        
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                String responseStr = response.toString();
                System.out.println("DEBUG: Facebook image upload response=" + responseStr);
                
                // Extract image ID từ response
                if (responseStr.contains("\"id\":")) {
                    int startIdx = responseStr.indexOf("\"id\":\"") + 6;
                    int endIdx = responseStr.indexOf("\"", startIdx);
                    if (endIdx > startIdx) {
                        String imageId = responseStr.substring(startIdx, endIdx);
                        System.out.println("DEBUG: Successfully uploaded image, Facebook ID=" + imageId);
                        return imageId;
                    }
                }
                System.out.println("WARNING: Could not extract image ID from Facebook response");
            }
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream(), 
                StandardCharsets.UTF_8))) {
                StringBuilder error = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    error.append(line);
                }
                String errorStr = error.toString();
                System.out.println("DEBUG: Facebook image upload error=" + errorStr);
                
                // ✅ Enhanced error handling for common issues
                if (errorStr.contains("Invalid URL") || errorStr.contains("unreachable")) {
                    throw new RuntimeException("Facebook cannot access image URL (probably localhost): " + imageUrl);
                } else if (errorStr.contains("permission") || errorStr.contains("access")) {
                    throw new RuntimeException("Facebook permission error for image upload: " + errorStr);
                } else {
                    throw new RuntimeException("Facebook image upload failed (" + responseCode + "): " + errorStr);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Tạo post với các ảnh đã upload
     */
    private String createPostWithUploadedImages(String pageId, String accessToken, String message, List<String> imageIds) throws Exception {
        System.out.println("DEBUG: Creating post with " + imageIds.size() + " uploaded images");
        
        String url = FB_GRAPH_URL + pageId + "/feed";
        
        // POST data với ảnh đã upload
        StringBuilder postDataBuilder = new StringBuilder();
        postDataBuilder.append("message=").append(URLEncoder.encode(message, StandardCharsets.UTF_8));
        postDataBuilder.append("&access_token=").append(URLEncoder.encode(accessToken, StandardCharsets.UTF_8));
        postDataBuilder.append("&published=true");
        
        // Thêm ảnh vào post
        for (int i = 0; i < imageIds.size(); i++) {
            postDataBuilder.append("&attached_media[").append(i).append("]=")
                           .append("{\"media_fbid\":\"").append(imageIds.get(i)).append("\"}");
        }
        
        String postData = postDataBuilder.toString();
        System.out.println("DEBUG: Post with images data length=" + postData.length() + " chars");
        
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        conn.setRequestProperty("User-Agent", "PhongTro247/2.0");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
        
        int responseCode = conn.getResponseCode();
        System.out.println("DEBUG: Facebook post with images response code=" + responseCode);
        
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                String responseStr = response.toString();
                System.out.println("DEBUG: Facebook post with images success response=" + responseStr);
                
                if (responseStr.contains("\"id\":")) {
                    int startIdx = responseStr.indexOf("\"id\":\"") + 6;
                    int endIdx = responseStr.indexOf("\"", startIdx);
                    if (endIdx > startIdx) {
                        String postId = responseStr.substring(startIdx, endIdx);
                        System.out.println("DEBUG: Extracted Facebook post ID=" + postId);
                        return postId;
                    }
                }
            }
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream(), 
                StandardCharsets.UTF_8))) {
                StringBuilder error = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    error.append(line);
                }
                String errorStr = error.toString();
                System.out.println("DEBUG: Facebook post with images error=" + errorStr);
                throw new RuntimeException("Facebook post with images error " + responseCode + ": " + errorStr);
            }
        }
        
        return null;
    }
    
    /**
     * Call Facebook Graph API để đăng post (text only hoặc fallback)
     * ✅ Thêm debug logging chi tiết và test API connection
     */
    private String callFacebookAPI(String pageId, String accessToken, String message, List<String> imageIds) {
        System.out.println("DEBUG: callFacebookAPI starting for pageId=" + pageId);
        System.out.println("DEBUG: Message length=" + message.length() + " chars");
        System.out.println("DEBUG: Access token preview=" + (accessToken != null ? accessToken.substring(0, Math.min(20, accessToken.length())) + "..." : "null"));
        
        // ✅ Test 1: Verify Page access first
        try {
            String testUrl = FB_GRAPH_URL + pageId + "?access_token=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
            System.out.println("DEBUG: Testing page access with URL=" + testUrl);
            
            HttpURLConnection testConn = (HttpURLConnection) new URL(testUrl).openConnection();
            testConn.setRequestMethod("GET");
            testConn.setConnectTimeout(10000);
            testConn.setReadTimeout(10000);
            
            int testResponseCode = testConn.getResponseCode();
            System.out.println("DEBUG: Page access test response code=" + testResponseCode);
            
            if (testResponseCode != 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    testConn.getErrorStream() != null ? testConn.getErrorStream() : testConn.getInputStream(), 
                    StandardCharsets.UTF_8))) {
                    StringBuilder error = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        error.append(line);
                    }
                    System.out.println("DEBUG: Page access test error=" + error.toString());
                    throw new RuntimeException("Facebook Page access failed: " + error.toString());
                }
            } else {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(testConn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println("DEBUG: Page access test success=" + response.toString());
                }
            }
        } catch (Exception testEx) {
            System.out.println("DEBUG: Page access test failed: " + testEx.getMessage());
            throw new RuntimeException("Cannot access Facebook Page: " + testEx.getMessage(), testEx);
        }
        
        // ✅ Test 2: Post to Page
        try {
            String url = FB_GRAPH_URL + pageId + "/feed";
            System.out.println("DEBUG: Facebook API URL=" + url);
            
            // ✅ Improved POST data format
            StringBuilder postDataBuilder = new StringBuilder();
            postDataBuilder.append("message=").append(URLEncoder.encode(message, StandardCharsets.UTF_8));
            postDataBuilder.append("&access_token=").append(URLEncoder.encode(accessToken, StandardCharsets.UTF_8));
            // Optional: Add published=true to ensure immediate posting
            postDataBuilder.append("&published=true");
            
            String postData = postDataBuilder.toString();
            System.out.println("DEBUG: POST data length=" + postData.length() + " chars");
            
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setRequestProperty("User-Agent", "PhongTro247/2.0");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            
            System.out.println("DEBUG: Sending POST request to Facebook API...");
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }
            
            int responseCode = conn.getResponseCode();
            System.out.println("DEBUG: Facebook API response code=" + responseCode);
            
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String responseStr = response.toString();
                    System.out.println("DEBUG: Facebook API success response=" + responseStr);
                    
                    if (responseStr.contains("\"id\":")) {
                        int startIdx = responseStr.indexOf("\"id\":\"") + 6;
                        int endIdx = responseStr.indexOf("\"", startIdx);
                        if (endIdx > startIdx) {
                            String postId = responseStr.substring(startIdx, endIdx);
                            System.out.println("DEBUG: Extracted Facebook post ID=" + postId);
                            return postId;
                        }
                    }
                    System.out.println("DEBUG: Could not extract post ID from response");
                }
            } else {
                System.out.println("DEBUG: Facebook API error response code=" + responseCode);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream(), 
                    StandardCharsets.UTF_8))) {
                    StringBuilder error = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        error.append(line);
                    }
                    String errorStr = error.toString();
                    System.out.println("DEBUG: Facebook API error response=" + errorStr);
                    
                    // ✅ Throw exception với error details thay vì silent fail
                    throw new RuntimeException("Facebook API error " + responseCode + ": " + errorStr);
                }
            }
            
        } catch (Exception ex) {
            System.out.println("DEBUG: Exception in callFacebookAPI: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Facebook API call failed: " + ex.getMessage(), ex);
        }
        
        System.out.println("DEBUG: callFacebookAPI returning null (should not reach here)");
        return null;
    }
    
    /**
     * Kiểm tra Facebook settings của user có hợp lệ không
     */
    public boolean hasValidFacebookSettings(int userId) {
        try {
            List<FacebookSettings> pages = fbSettingsDAO.getAllActivePages(userId);
            return pages != null && !pages.isEmpty() && 
                   pages.stream().anyMatch(page -> 
                       page.isActive() && page.getAccessToken() != null && !page.getAccessToken().isEmpty());
        } catch (Exception ex) {
            return false;
        }
    }
}