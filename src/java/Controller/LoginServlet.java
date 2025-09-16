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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            handleLogout(request, response);
            return;
        }
        
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin đăng nhập");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        try {
            UserDAO userDAO = new UserDAO();
            // Lấy user theo username, sau đó verify password bằng PasswordUtils
            User user = userDAO.getUserByUsername(username.trim());
            
            if (user != null && user.getPassword() != null 
                    && PasswordUtils.verifyPassword(password, user.getPassword())
                    && isValidUser(user)) {
                
                // Xử lý session fixation: invalidate session cũ và tạo session mới
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession session = request.getSession(true);
                
                // Không lưu hash password vào session
                user.setPassword(null);
                session.setAttribute("currentUser", user);
                // Tuỳ nhu cầu, cấu hình thời gian session
                session.setMaxInactiveInterval(30 * 60); // 30 phút
                
                System.out.println("User logged in: " + user.getUsername() + " - Role: " + user.getRoleName());
                
                String redirectUrl = getRedirectUrlByRole(user, request.getContextPath());
                response.sendRedirect(redirectUrl);
                
            } else {
                request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
                request.setAttribute("username", username);
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                System.out.println("User logged out: " + user.getUsername());
            }
            session.invalidate();
        }
        
        response.sendRedirect(request.getContextPath() + "/");
    }
    
    private String getRedirectUrlByRole(User user, String contextPath) {
        if (user.getRoleName() == null) {
            return contextPath + "/";
        }
        
        String roleName = user.getRoleName().toUpperCase();
        
        switch (roleName) {
            case "ADMIN":
                return contextPath + "/admin/dashboard";
            case "LANDLORD":
                return contextPath + "/landlord/dashboard";
            default:
                return contextPath + "/";
        }
    }
    
    // Kiểm tra User có hợp lệ không dựa trên model User
    private boolean isValidUser(User user) {
        if (user == null) {
            return false;
        }
        
        // Kiểm tra các field bắt buộc từ model User
        if (user.getUserId() <= 0 || 
            user.getUsername() == null || user.getUsername().trim().isEmpty() ||
            user.getRoleId() <= 0) {
            return false;
        }
        
        return true;
    }
}