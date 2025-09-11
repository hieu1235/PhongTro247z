package Controller;

import Dal.UserDAO;
import Model.User;

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
        
        // Xử lý logout nếu có action=logout
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            handleLogout(request, response);
            return;
        }
        
        // Kiểm tra nếu đã đăng nhập rồi thì redirect về home
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        // Forward đến trang login
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Validation
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin đăng nhập");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        try {
            // Xác thực user
            UserDAO userDAO = new UserDAO();
            User user = userDAO.login(username.trim(), password);
            
            if (user != null) {
                // Đăng nhập thành công
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);
                
                // Ghi log
                System.out.println("User logged in: " + user.getUsername() + " - Role: " + user.getRoleName());
                
                // Redirect theo role
                String redirectUrl = getRedirectUrlByRole(user, request.getContextPath());
                response.sendRedirect(redirectUrl);
                
            } else {
                // Đăng nhập thất bại
                request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
                request.setAttribute("username", username); // Giữ lại username
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
        // Lấy role_name từ database thông qua User object
        String roleName = user.getRoleName();
        
        switch (roleName) {
            case "ADMIN":
                return contextPath + "/admin-dashboard.jsp";
            case "LANDLORD":
                return contextPath + "/landlord-rooms.jsp";
            default:
                return contextPath + "/"; // Tenant hoặc role khác về trang chủ
        }
    }
}