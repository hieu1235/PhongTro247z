package Post;

import Model.Post;
import Model.User;
import Model.FacebookSettings;
import Service.PostService;
import Service.FacebookService;
import Service.LocalFileService;
import Service.SubscriptionService;
import Dal.PostDAO;
import Dal.PostFacebookPageDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@WebServlet(name = "CreatePostServlet", urlPatterns = {"/post/create"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 8 * 1024 * 1024,      // 8MB per file
        maxRequestSize = 40 * 1024 * 1024)  // 40MB total
public class CreatePostServlet extends HttpServlet {

    private final PostService postService = new PostService();
    private final FacebookService facebookService = new FacebookService();
    private final SubscriptionService subscriptionService = new SubscriptionService();
    private final PostDAO postDAO = new PostDAO();
    private final PostFacebookPageDAO postFacebookPageDAO = new PostFacebookPageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user != null) {
            try {
                // Load user's Facebook pages cho form selection
                List<FacebookSettings> userPages = facebookService.getUserPages(user.getUserId());
                req.setAttribute("facebookPages", userPages);
                System.out.println("DEBUG: CreatePostServlet loaded " + userPages.size() + " Facebook pages for user " + user.getUserId());
                
                // Debug each page
                for (FacebookSettings page : userPages) {
                    System.out.println("DEBUG: Setting page attribute - " + page.getPageName() + 
                                      " (ID: " + page.getPageId() + ", Default: " + page.isDefault() + 
                                      ", AutoPost: " + page.isAutoPost() + ")");
                }
                
            } catch (Exception e) {
                System.out.println("DEBUG: Error loading Facebook pages in CreatePostServlet: " + e.getMessage());
                e.printStackTrace();
                req.setAttribute("facebookPages", new ArrayList<>());
            }
        } else {
            System.out.println("DEBUG: No user in session for CreatePostServlet");
            req.setAttribute("facebookPages", new ArrayList<>());
        }
        
        req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

        // Kiểm tra quyền
        boolean isLandlord = (user.getRoleName() != null && user.getRoleName().equalsIgnoreCase("LANDLORD"))
                || user.getRoleId() == 2 || user.getRoleId() == 1;
        if (!isLandlord) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chỉ chủ trọ được phép đăng tin");
            return;
        }

        // ✅ KIỂM TRA DAILY POST LIMIT (PRO SYSTEM)
        try {
            boolean canPostToday = subscriptionService.canUserPostToday(user.getUserId());
            if (!canPostToday) {
                boolean isProActive = user.isProActive();
                String errorMsg = isProActive ? 
                    "Bạn đã đăng đủ 10 bài hôm nay. Gói Pro cho phép tối đa 10 bài/ngày." :
                    "Bạn đã đăng đủ 1 bài hôm nay. Hãy nâng cấp lên Pro để đăng 10 bài/ngày!";
                
                req.setAttribute("error", errorMsg);
                req.setAttribute("isPostLimitReached", true);
                req.setAttribute("currentPostCount", subscriptionService.getTodayPostCount(user.getUserId()));
                req.setAttribute("dailyLimit", isProActive ? 10 : 1);
                
                loadFacebookPagesForRequest(req, user.getUserId());
                req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
                return;
            }
        } catch (Exception e) {
            System.out.println("CreatePostServlet: Error checking post limit: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("error", "Lỗi kiểm tra giới hạn đăng bài");
            loadFacebookPagesForRequest(req, user.getUserId());
            req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
            return;
        }

        // Lấy parameters
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String address = req.getParameter("address");
        String latStr = req.getParameter("lat");
        String lngStr = req.getParameter("lng");
        String priceStr = req.getParameter("price");
        String areaStr = req.getParameter("area");
        
        // ✅ Lấy scheduled posting parameters
        String publishType = req.getParameter("publishType");
        String scheduledAtStr = req.getParameter("scheduledAt");
        boolean isScheduled = "scheduled".equals(publishType);
        
        // ✅ Lấy selected Facebook Pages
        String[] selectedPages = req.getParameterValues("selectedPages");
        List<String> selectedPageIds = selectedPages != null ? 
            Arrays.asList(selectedPages) : new ArrayList<>();
        
        System.out.println("CreatePostServlet: User selected " + selectedPageIds.size() + " Facebook pages: " + selectedPageIds);
        System.out.println("CreatePostServlet: Publish type: " + publishType + ", Scheduled at: " + scheduledAtStr);

        // ✅ Validation với feedback tốt hơn
        if (title == null || title.trim().isEmpty()) {
            req.setAttribute("error", "Tiêu đề không được để trống");
            loadFacebookPagesForRequest(req, user.getUserId());
            req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
            return;
        }

        // ✅ Validate scheduled posting
        java.sql.Timestamp scheduledAt = null;
        if (isScheduled) {
            if (scheduledAtStr == null || scheduledAtStr.trim().isEmpty()) {
                req.setAttribute("error", "Vui lòng chọn thời gian đăng bài");
                loadFacebookPagesForRequest(req, user.getUserId());
                req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
                return;
            }
            
            try {
                // Parse datetime-local format: "2025-10-07T14:30"
                scheduledAt = java.sql.Timestamp.valueOf(scheduledAtStr.replace("T", " ") + ":00");
                
                // Validate scheduled time
                java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                java.sql.Timestamp maxTime = new java.sql.Timestamp(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)); // 30 days
                
                if (scheduledAt.before(now)) {
                    req.setAttribute("error", "Thời gian đăng phải trong tương lai");
                    loadFacebookPagesForRequest(req, user.getUserId());
                    req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
                    return;
                }
                
                if (scheduledAt.after(maxTime)) {
                    req.setAttribute("error", "Chỉ được lập lịch tối đa 30 ngày trước");
                    loadFacebookPagesForRequest(req, user.getUserId());
                    req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
                    return;
                }
                
            } catch (Exception e) {
                req.setAttribute("error", "Định dạng thời gian không hợp lệ");
                loadFacebookPagesForRequest(req, user.getUserId());
                req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
                return;
            }
        }

        // ✅ Validate file size trước khi xử lý
        try {
            Collection<Part> parts = req.getParts();
            List<Part> imageParts = new ArrayList<>();
            
            for (Part part : parts) {
                if ("images".equals(part.getName()) && part.getSize() > 0) {
                    // Validate từng file
                    if (part.getSize() > 8 * 1024 * 1024) { // 8MB
                        req.setAttribute("error", "File " + part.getSubmittedFileName() + " quá lớn (tối đa 8MB)");
                        loadFacebookPagesForRequest(req, user.getUserId());
                        req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
                        return;
                    }
                    
                    String contentType = part.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        req.setAttribute("error", "File " + part.getSubmittedFileName() + " không phải là ảnh hợp lệ");
                        loadFacebookPagesForRequest(req, user.getUserId());
                        req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
                        return;
                    }
                    
                    imageParts.add(part);
                }
            }
            
            if (imageParts.size() > 5) {
                req.setAttribute("error", "Chỉ được upload tối đa 5 ảnh");
                loadFacebookPagesForRequest(req, user.getUserId());
                req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
                return;
            }
            
        } catch (Exception e) {
            req.setAttribute("error", "Lỗi xử lý file upload: " + e.getMessage());
            loadFacebookPagesForRequest(req, user.getUserId());
            req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
            return;
        }

        // Tạo Post object
        Post post = new Post();
        post.setUserId(user.getUserId());
        post.setTitle(title.trim());
        post.setContent(content != null ? content.trim() : "");
        post.setAddress(address != null ? address.trim() : "");
        
        try {
            if (latStr != null && !latStr.isEmpty()) post.setLat(new BigDecimal(latStr));
            if (lngStr != null && !lngStr.isEmpty()) post.setLng(new BigDecimal(lngStr));
            if (priceStr != null && !priceStr.isEmpty()) post.setPrice(new BigDecimal(priceStr));
            if (areaStr != null && !areaStr.isEmpty()) post.setArea(new BigDecimal(areaStr));
        } catch (NumberFormatException nfe) {
            req.setAttribute("error", "Giá trị lat/lng/price/area không hợp lệ");
            loadFacebookPagesForRequest(req, user.getUserId());
            req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
            return;
        }

        // ✅ Set status và scheduled posting fields
        if (isScheduled) {
            post.setStatusId(5); // SCHEDULED status
            post.setScheduledAt(scheduledAt);
            post.setAutoPublish(true);
        } else {
            post.setStatusId(1); // PENDING - sẽ được AI quyết định sau
        }

        // ✅ Upload ảnh lên Local File Storage
        Collection<Part> parts = req.getParts();
        
        // Convert Parts to byte arrays với validation
        List<byte[]> imageBytes = new ArrayList<>();
        if (parts != null) {
            for (Part part : parts) {
                if (part != null && "images".equals(part.getName()) && part.getSize() > 0) {
                    // Validate file size
                    if (!LocalFileService.isValidFileSize(part.getSize())) {
                        req.setAttribute("error", "File quá lớn. Tối đa " + (5) + "MB");
                        loadFacebookPagesForRequest(req, user.getUserId());
                        req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
                        return;
                    }
                    
                    String contentType = part.getContentType();
                    if (contentType != null && contentType.startsWith("image/")) {
                        try (java.io.InputStream inputStream = part.getInputStream()) {
                            java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                            int bytesRead;
                            byte[] data = new byte[8192];
                            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                                buffer.write(data, 0, bytesRead);
                            }
                            imageBytes.add(buffer.toByteArray());
                        }
                    }
                }
            }
        }

        try {
            // 1. Tạo post với ảnh upload lên Local File Storage
            int postId = postService.createPostWithImages(post, imageBytes);
            post.setPostId(postId);

            System.out.println("CreatePostServlet: User=" + user.getUserId() + " created post=" + postId + " with local storage");

            // 2. Lưu selected Facebook pages vào database
            if (!selectedPageIds.isEmpty()) {
                postFacebookPageDAO.insertPostPages(postId, selectedPageIds);
                System.out.println("CreatePostServlet: Saved " + selectedPageIds.size() + " selected pages for post " + postId);
            }

            // 3. Tự động duyệt post (loại bỏ AI check)
            System.out.println("CreatePostServlet: Auto-approving post " + postId);

            // 4. Cập nhật status thành APPROVED (chỉ với bài không phải scheduled)
            if (!isScheduled) {
                int approvedStatusId = 2; // APPROVED status
                if (approvedStatusId != post.getStatusId()) {
                    post.setStatusId(approvedStatusId);
                    postDAO.updateStatus(postId, approvedStatusId);
                    System.out.println("CreatePostServlet: Auto-approved post " + postId);
                }
            } else {
                System.out.println("CreatePostServlet: Scheduled post " + postId + " status remains SCHEDULED");
            }

            // 5. Nếu có Facebook pages được chọn -> đăng lên Facebook (chỉ với bài không scheduled)
            Map<String, String> fbResults = new HashMap<>();
            if (!selectedPageIds.isEmpty() && !isScheduled) {
                try {
                    fbResults = facebookService.postToMultiplePages(post, user.getUserId(), selectedPageIds);
                    
                    long successCount = fbResults.values().stream()
                        .filter(result -> !result.startsWith("ERROR"))
                        .count();
                    
                    System.out.println("CreatePostServlet: Posted to " + successCount + "/" + selectedPageIds.size() + " Facebook pages");
                    
                    // Log detailed results
                    fbResults.forEach((pageId, result) -> {
                        if (result.startsWith("ERROR")) {
                            System.out.println("CreatePostServlet: Failed to post to page " + pageId + ": " + result);
                        } else {
                            System.out.println("CreatePostServlet: Successfully posted to page " + pageId + ": " + result);
                        }
                    });
                    
                } catch (Exception fbEx) {
                    System.out.println("CreatePostServlet: Facebook posting failed: " + fbEx.getMessage());
                    fbEx.printStackTrace();
                }
            }

            // 6. Redirect với thông báo kết quả
            String message = buildSuccessMessage("ACCEPT", fbResults, selectedPageIds.size());
            String encodedMessage = java.net.URLEncoder.encode(message, "UTF-8");
            resp.sendRedirect(req.getContextPath() + "/post/my?msg=" + encodedMessage);

        } catch (Exception ex) {
            System.out.println("CreatePostServlet error: " + ex.getMessage());
            ex.printStackTrace();
            
            // ✅ Better error message cho user
            String userErrorMsg = "Lỗi hệ thống";
            if (ex.getMessage() != null) {
                if (ex.getMessage().contains("timeout") || ex.getMessage().contains("timed out")) {
                    userErrorMsg = "Upload ảnh mất quá nhiều thời gian. Vui lòng thử lại với ảnh nhỏ hơn.";
                } else if (ex.getMessage().contains("upload") || ex.getMessage().contains("File")) {
                    userErrorMsg = "Lỗi upload ảnh: " + ex.getMessage();
                } else {
                    userErrorMsg = "Lỗi hệ thống: " + ex.getMessage();
                }
            }
            
            req.setAttribute("error", userErrorMsg);
            loadFacebookPagesForRequest(req, user.getUserId());
            req.getRequestDispatcher("/post_create.jsp").forward(req, resp);
        }
    }
    
    /**
     * Helper method: Load Facebook pages cho request (dùng khi có error)
     */
    private void loadFacebookPagesForRequest(HttpServletRequest req, int userId) {
        try {
            List<FacebookSettings> userPages = facebookService.getUserPages(userId);
            req.setAttribute("facebookPages", userPages);
        } catch (Exception e) {
            System.out.println("Error loading Facebook pages for error case: " + e.getMessage());
            req.setAttribute("facebookPages", new ArrayList<>());
        }
    }

    /**
     * Tạo thông báo success cho user với thông tin multi-page posting
     */
    private String buildSuccessMessage(String recommendation, Map<String, String> fbResults, int totalSelectedPages) {
        StringBuilder message = new StringBuilder("Đăng tin thành công! ");

        switch (recommendation) {
            case "ACCEPT":
                message.append("✅ Tin của bạn đã được duyệt tự động và hiển thị trên trang chủ.");
                
                if (totalSelectedPages > 0) {
                    if (!fbResults.isEmpty()) {
                        long successCount = fbResults.values().stream()
                            .filter(result -> !result.startsWith("ERROR"))
                            .count();
                        long failCount = fbResults.size() - successCount;
                        
                        if (successCount > 0) {
                            message.append(" 🎉 Đã đăng thành công lên ")
                                   .append(successCount)
                                   .append("/")
                                   .append(totalSelectedPages)
                                   .append(" Facebook Page(s)!");
                        }
                        
                        if (failCount > 0) {
                            message.append(" ⚠️ ")
                                   .append(failCount)
                                   .append(" page(s) đăng thất bại.");
                        }
                    } else {
                        message.append(" ⚠️ Không thể đăng lên Facebook Pages (có lỗi xảy ra).");
                    }
                } else {
                    message.append(" (Không chọn Facebook Page nào để đăng)");
                }
                break;
                
            case "REVIEW":
                message.append("⏳ Tin của bạn đang chờ admin duyệt. Chúng tôi sẽ xem xét trong 24h.");
                if (totalSelectedPages > 0) {
                    message.append(" Facebook Pages sẽ được đăng sau khi duyệt.");
                }
                break;
                
            case "REJECT":
                message.append("❌ Tin của bạn cần chỉnh sửa. Vui lòng kiểm tra lại nội dung và thông tin.");
                break;
        }

        return message.toString();
    }
}