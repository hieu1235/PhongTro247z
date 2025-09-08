package controllers;

import services.FacebookService;
import utils.SessionUtil;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/facebook-stats")
public class FacebookStatsServlet extends HttpServlet {
    
    private FacebookService facebookService;
    
    @Override
    public void init() throws ServletException {
        this.facebookService = new FacebookService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Long userId = SessionUtil.getUserIdFromSession(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Lấy thống kê
            JsonObject stats = facebookService.getFacebookPostStats(userId);
            request.setAttribute("facebookStats", stats);
            
            // Forward đến JSP
            request.getRequestDispatcher("/facebook-stats.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Có lỗi khi lấy thống kê: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
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
            JsonObject stats = facebookService.getFacebookPostStats(userId);
            response.getWriter().write(stats.toString());
            
        } catch (Exception e) {
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi hệ thống: " + e.getMessage() + "\"}");
        }
    }
}