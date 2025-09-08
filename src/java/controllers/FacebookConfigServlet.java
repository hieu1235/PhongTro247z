package controllers;

import dao.UserDAO;
import models.User;
import utils.FacebookAPIUtil;
import utils.SessionUtil;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/facebook-config", "/facebook-test"})
public class FacebookConfigServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private FacebookAPIUtil facebookAPIUtil;
    
    @Override
    public void init() throws ServletException {
        this.userDAO = new UserDAO();
        this.facebookAPIUtil = new FacebookAPIUtil();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Hiển thị trang cấu hình Facebook
        Long userId = SessionUtil.getUserIdFromSession(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = userDAO.getUserById(userId);
        request.setAttribute("user", user);
        
        request.getRequestDispatcher("/facebook-config.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        
        String servletPath = request.getServletPath();
        
        if ("/facebook-test".equals(servletPath)) {
            handleTestConfig(request, response);
        } else if ("/facebook-config".equals(servletPath)) {
            handleSaveConfig(request, response);
        }
    }
    
    /**
     * Test Facebook configuration
     */
    private void handleTestConfig(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            String pageId = request.getParameter("pageId");
            String pageAccessToken = request.getParameter("pageAccessToken");
            
            if (pageId == null || pageAccessToken == null || 
                pageId.trim().isEmpty() || pageAccessToken.trim().isEmpty()) {
                
                response.getWriter().write("{\"success\": false, \"message\": \"Thiếu thông tin Page ID hoặc Access Token\"}");
                return;
            }
            
            // Test kết nối Facebook
            boolean isValid = facebookAPIUtil.validatePageAccessToken(pageId.trim(), pageAccessToken.trim());
            
            if (isValid) {
                // Lấy thông tin page name nếu có thể
                String pageName = getPageName(pageId.trim(), pageAccessToken.trim());
                
                String jsonResponse = String.format(
                    "{\"success\": true, \"message\": \"Kết nối thành công\", \"pageName\": \"%s\"}",
                    pageName != null ? pageName : "Unknown"
                );
                response.getWriter().write(jsonResponse);
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Kết nối thất bại. Vui lòng kiểm tra lại Page ID và Access Token\"}");
            }
            
        } catch (Exception e) {
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi hệ thống: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Save Facebook configuration
     */
    private void handleSaveConfig(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            Long userId = SessionUtil.getUserIdFromSession(request);
            if (userId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"success\": false, \"message\": \"Bạn cần đăng nhập\"}");
                return;
            }
            
            String pageId = request.getParameter("pageId");
            String pageAccessToken = request.getParameter("pageAccessToken");
            
            if (pageId == null || pageAccessToken == null || 
                pageId.trim().isEmpty() || pageAccessToken.trim().isEmpty()) {
                
                response.getWriter().write("{\"success\": false, \"message\": \"Thiếu thông tin Page ID hoặc Access Token\"}");
                return;
            }
            
            // Validate trước khi lưu
            boolean isValid = facebookAPIUtil.validatePageAccessToken(pageId.trim(), pageAccessToken.trim());
            if (!isValid) {
                response.getWriter().write("{\"success\": false, \"message\": \"Page ID hoặc Access Token không hợp lệ\"}");
                return;
            }
            
            // Lưu vào database
            boolean saved = userDAO.updateFacebookPageConfig(userId, pageId.trim(), pageAccessToken.trim());
            
            if (saved) {
                response.getWriter().write("{\"success\": true, \"message\": \"Lưu cấu hình thành công\"}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Lỗi khi lưu cấu hình\"}");
            }
            
        } catch (Exception e) {
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi hệ thống: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Lấy tên Facebook Page
     */
    private String getPageName(String pageId, String accessToken) {
        try {
            // Có thể implement logic để lấy page name từ Facebook API
            // Tạm thời return null
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}