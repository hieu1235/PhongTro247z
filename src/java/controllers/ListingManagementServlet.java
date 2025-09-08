package controllers;

import dao.ListingDAO;
import dao.ActivityLogDAO;
import models.Listing;
import services.FacebookService;
import models.FacebookPostResponse;
import utils.SessionUtil;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"/listing-management", "/approve-listing", "/toggle-auto-post"})
public class ListingManagementServlet extends HttpServlet {
    
    private ListingDAO listingDAO;
    private ActivityLogDAO activityLogDAO;
    private FacebookService facebookService;
    
    @Override
    public void init() throws ServletException {
        this.listingDAO = new ListingDAO();
        this.activityLogDAO = new ActivityLogDAO();
        this.facebookService = new FacebookService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String servletPath = request.getServletPath();
        
        if ("/listing-management".equals(servletPath)) {
            showListingManagement(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        
        String servletPath = request.getServletPath();
        
        switch (servletPath) {
            case "/approve-listing":
                handleApproveListing(request, response);
                break;
            case "/toggle-auto-post":
                handleToggleAutoPost(request, response);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"success\": false, \"message\": \"Endpoint không tồn tại\"}");
        }
    }
    
    /**
     * Hiển thị trang quản lý listing
     */
    private void showListingManagement(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Long userId = SessionUtil.getUserIdFromSession(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy danh sách listings của user
        List<Listing> userListings = listingDAO.getListingsByLandlordId(userId);
        request.setAttribute("userListings", userListings);
        
        // Lấy activity logs gần đây
        List<models.ActivityLog> recentLogs = activityLogDAO.getActivityLogsByUserId(userId, 20);
        request.setAttribute("recentLogs", recentLogs);
        
        request.getRequestDispatcher("/listing-management.jsp").forward(request, response);
    }
    
    /**
     * Approve listing và tự động post Facebook nếu được bật
     */
    private void handleApproveListing(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            Long userId = SessionUtil.getUserIdFromSession(request);
            if (userId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"success\": false, \"message\": \"Bạn cần đăng nhập\"}");
                return;
            }
            
            String listingIdStr = request.getParameter("listingId");
            if (listingIdStr == null || listingIdStr.trim().isEmpty()) {
                response.getWriter().write("{\"success\": false, \"message\": \"Thiếu thông tin tin đăng\"}");
                return;
            }
            
            Long listingId = Long.parseLong(listingIdStr);
            
            // Kiểm tra quyền (chỉ admin hoặc chủ listing mới được approve)
            // Tạm thời chỉ cho phép chủ listing
            if (!listingDAO.isListingOwner(listingId, userId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"success\": false, \"message\": \"Bạn không có quyền thực hiện hành động này\"}");
                return;
            }
            
            // Approve listing
            boolean approved = listingDAO.approveListing(listingId);
            
            if (approved) {
                // Tự động post lên Facebook nếu được bật
                FacebookPostResponse fbResponse = facebookService.autoPostListingToFacebook(listingId);
                
                String message = "Duyệt tin đăng thành công";
                if (fbResponse.isSuccess()) {
                    message += " và đã đăng lên Facebook";
                } else if (fbResponse.getMessage().contains("AUTO_POST_DISABLED")) {
                    message += " (không đăng Facebook vì chưa bật auto post)";
                } else {
                    message += " (lỗi đăng Facebook: " + fbResponse.getMessage() + ")";
                }
                
                response.getWriter().write("{\"success\": true, \"message\": \"" + message + "\"}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Lỗi khi duyệt tin đăng\"}");
            }
            
        } catch (NumberFormatException e) {
            response.getWriter().write("{\"success\": false, \"message\": \"ID tin đăng không hợp lệ\"}");
        } catch (Exception e) {
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi hệ thống: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Bật/tắt auto post Facebook cho listing
     */
    private void handleToggleAutoPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            Long userId = SessionUtil.getUserIdFromSession(request);
            if (userId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"success\": false, \"message\": \"Bạn cần đăng nhập\"}");
                return;
            }
            
            String listingIdStr = request.getParameter("listingId");
            String enabledStr = request.getParameter("enabled");
            
            if (listingIdStr == null || enabledStr == null) {
                response.getWriter().write("{\"success\": false, \"message\": \"Thiếu thông tin\"}");
                return;
            }
            
            Long listingId = Long.parseLong(listingIdStr);
            boolean enabled = Boolean.parseBoolean(enabledStr);
            
            // Kiểm tra quyền
            if (!listingDAO.isListingOwner(listingId, userId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"success\": false, \"message\": \"Bạn không có quyền thực hiện hành động này\"}");
                return;
            }
            
            // Cập nhật auto post setting
            boolean updated = listingDAO.updateAutoPostSocial(listingId, enabled);
            
            if (updated) {
                String message = enabled ? "Đã bật auto post Facebook" : "Đã tắt auto post Facebook";
                response.getWriter().write("{\"success\": true, \"message\": \"" + message + "\"}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Lỗi khi cập nhật cài đặt\"}");
            }
            
        } catch (NumberFormatException e) {
            response.getWriter().write("{\"success\": false, \"message\": \"Dữ liệu không hợp lệ\"}");
        } catch (Exception e) {
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi hệ thống: " + e.getMessage() + "\"}");
        }
    }
}