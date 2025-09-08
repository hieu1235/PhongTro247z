package controllers;

import services.FacebookService;
import models.FacebookPostResponse;
import utils.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/manual-facebook-post")
public class ManualFacebookPostServlet extends HttpServlet {
    
    private FacebookService facebookService;
    
    @Override
    public void init() throws ServletException {
        this.facebookService = new FacebookService();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        
        Long userId = SessionUtil.getUserIdFromSession(request);
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"message\": \"Bạn cần đăng nhập\"}");
            return;
        }
        
        try {
            String listingIdStr = request.getParameter("listingId");
            if (listingIdStr == null || listingIdStr.trim().isEmpty()) {
                response.getWriter().write("{\"success\": false, \"message\": \"Thiếu thông tin tin đăng\"}");
                return;
            }
            
            Long listingId = Long.parseLong(listingIdStr);
            
            // Đăng bài thủ công
            FacebookPostResponse fbResponse = facebookService.postListingToFacebook(listingId, userId);
            
            if (fbResponse.isSuccess()) {
                String jsonResponse = String.format(
                    "{\"success\": true, \"message\": \"%s\", \"postId\": \"%s\"}",
                    fbResponse.getMessage(),
                    fbResponse.getPostId()
                );
                response.getWriter().write(jsonResponse);
            } else {
                String jsonResponse = String.format(
                    "{\"success\": false, \"message\": \"%s\", \"errorCode\": \"%s\"}",
                    fbResponse.getMessage(),
                    fbResponse.getErrorCode()
                );
                response.getWriter().write(jsonResponse);
            }
            
        } catch (NumberFormatException e) {
            response.getWriter().write("{\"success\": false, \"message\": \"ID tin đăng không hợp lệ\"}");
        } catch (Exception e) {
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi hệ thống: " + e.getMessage() + "\"}");
        }
    }
}