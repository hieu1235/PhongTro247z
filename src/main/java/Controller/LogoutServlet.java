package Controller;

import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        handleLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        handleLogout(request, response);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        System.out.println("=== LOGOUT SERVLET ===");
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Get user info for logging
            User user = (User) session.getAttribute("user");
            if (user != null) {
                System.out.println("LOGOUT: User logging out: " + user.getUsername() + 
                                 " - IP: " + getClientIP(request) + 
                                 " - Time: " + new java.util.Date());
            }
            
            // Debug: List all session attributes before clearing
            java.util.Enumeration<String> attributeNames = session.getAttributeNames();
            System.out.println("Session attributes before logout:");
            while (attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                Object attributeValue = session.getAttribute(attributeName);
                System.out.println("  " + attributeName + " = " + attributeValue);
            }
            
            // Invalidate session completely
            try {
                session.invalidate();
                System.out.println("Session invalidated successfully!");
            } catch (IllegalStateException e) {
                System.out.println("Session already invalidated: " + e.getMessage());
            }
        } else {
            System.out.println("No session found to logout");
        }
        
        // Clear browser cache to prevent back button issues
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, private");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        // Clear any authentication cookies if they exist
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().contains("JSESSIONID") || 
                    cookie.getName().contains("auth") || 
                    cookie.getName().contains("user")) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    System.out.println("Cleared cookie: " + cookie.getName());
                }
            }
        }
        
        System.out.println("Redirecting to homepage...");
        System.out.println("========================");
        
        // Redirect to homepage
        response.sendRedirect(request.getContextPath() + "/");
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}