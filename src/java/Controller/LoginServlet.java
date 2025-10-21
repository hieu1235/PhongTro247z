package Controller;

import Dal.UserDAO;
import Model.User;
import Utility.PasswordUtils;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    
    private static final int SESSION_TIMEOUT = 30 * 60; // 30 phút
    private static final String LOGIN_JSP = "login.jsp";
    private static final String HOME_URL = "/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            handleLogout(request, response);
            return;
        }
        
        // ✅ SỬA: Đổi từ "currentUser" thành "user"
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User currentUser = (User) session.getAttribute("user");
            String redirectUrl = getRedirectUrlByRole(currentUser, request.getContextPath());
            response.sendRedirect(redirectUrl);
            return;
        }
        
        request.getRequestDispatcher(LOGIN_JSP).forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set encoding để xử lý tiếng Việt
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        
        // Validate input
        if (!isValidInput(username, password)) {
            setErrorAndForward(request, response, "Vui lòng nhập đầy đủ thông tin đăng nhập", username);
            return;
        }
        
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByUsername(username.trim());
            
            if (isValidLogin(user, password)) {
                handleSuccessfulLogin(request, response, user, rememberMe);
            } else {
                handleFailedLogin(request, response, username);
            }
            
        } catch (Exception e) {
            handleLoginException(request, response, e);
        }
    }
    
    private boolean isValidInput(String username, String password) {
        return username != null && !username.trim().isEmpty() && 
               password != null && !password.trim().isEmpty();
    }
    
    private boolean isValidLogin(User user, String password) {
        if (user == null || user.getPassword() == null || !isValidUser(user)) {
            return false;
        }
        
        // Kiểm tra password với BCrypt verification
        return PasswordUtils.verifyPassword(password, user.getPassword());
    }
    
    private void handleSuccessfulLogin(HttpServletRequest request, HttpServletResponse response, 
                                     User user, String rememberMe) throws IOException {
        
        // Xử lý session fixation: invalidate session cũ và tạo session mới
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        
        HttpSession session = request.getSession(true);
        
        // Không lưu password vào session (security)
        user.setPassword(null);
        
        // ✅ SỬA: Đổi từ "currentUser" thành "user"
        session.setAttribute("user", user);
        
        // Set session timeout
        if ("on".equals(rememberMe)) {
            session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 ngày nếu remember me
        } else {
            session.setMaxInactiveInterval(SESSION_TIMEOUT); // 30 phút
        }
        
        // Log successful login
        System.out.println("LOGIN_SUCCESS: User logged in: " + user.getUsername() + 
                         " - Role: " + user.getRoleName() + " - IP: " + getClientIP(request) +
                         " - Time: " + new java.util.Date());
        
        String redirectUrl = getRedirectUrlByRole(user, request.getContextPath());
        response.sendRedirect(redirectUrl);
    }
    
    private void handleFailedLogin(HttpServletRequest request, HttpServletResponse response, 
                                 String username) throws ServletException, IOException {
        
        // Log failed login attempt
        System.out.println("LOGIN_FAILED: Failed login attempt for username: " + username + 
                         " - IP: " + getClientIP(request) + " - Time: " + new java.util.Date());
        
        setErrorAndForward(request, response, "Tên đăng nhập hoặc mật khẩu không đúng", username);
    }
    
    private void handleLoginException(HttpServletRequest request, HttpServletResponse response, 
                                    Exception e) throws ServletException, IOException {
        
        // Log exception
        System.out.println("LOGIN_ERROR: Login error: " + e.getMessage() + 
                         " - IP: " + getClientIP(request) + " - Time: " + new java.util.Date());
        e.printStackTrace();
        
        setErrorAndForward(request, response, "Có lỗi xảy ra. Vui lòng thử lại sau", null);
    }
    
    private void setErrorAndForward(HttpServletRequest request, HttpServletResponse response, 
                                  String errorMessage, String username) 
                                  throws ServletException, IOException {
        
        request.setAttribute("error", errorMessage);
        if (username != null) {
            request.setAttribute("username", username);
        }
        request.getRequestDispatcher(LOGIN_JSP).forward(request, response);
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            // ✅ SỬA: Đổi từ "currentUser" thành "user"
            User user = (User) session.getAttribute("user");
            if (user != null) {
                System.out.println("LOGOUT: User logged out: " + user.getUsername() + 
                                 " - IP: " + getClientIP(request) + " - Time: " + new java.util.Date());
            }
            session.invalidate();
        }
        
        // Xóa cache để đảm bảo page refresh
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        // Sử dụng hard-coded URL để tránh context path issues
        response.sendRedirect("/PhongTroNew/");
    }
    
    private String getRedirectUrlByRole(User user, String contextPath) {
        if (user.getRoleName() == null) {
            return contextPath + HOME_URL;
        }
        
        String roleName = user.getRoleName().toUpperCase();
        
        switch (roleName) {
            case "ADMIN":
                return contextPath + "/admin/posts"; // Chuyển thẳng đến trang quản lý bài đăng
            case "LANDLORD":
                return contextPath + "/post/my"; // Trang quản lý bài đăng của landlord
            default:
                return contextPath + HOME_URL; // Trang chủ cho các role khác
        }
    }
    
    private boolean isValidUser(User user) {
        if (user == null) {
            return false;
        }
        
        // Kiểm tra các field bắt buộc
        return user.getUserId() > 0 && 
               user.getUsername() != null && 
               !user.getUsername().trim().isEmpty() &&
               user.getRoleId() > 0 &&
               user.getRoleName() != null &&
               !user.getRoleName().trim().isEmpty();
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