package Controller;

import Dal.UserDAO;
import Model.User;
import Utility.EmailUtility;
import Utility.PasswordUtils;

import java.io.IOException;
import java.util.Random;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra nếu đã đăng nhập rồi thì redirect về home
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        // Forward đến trang register
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Lấy parameters từ form (phù hợp với database mới)
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        String fullName = request.getParameter("full_name"); // Đổi từ fullName thành full_name
        
        try {
            // Validation
            if (!isValidRegistration(email, username, password, confirmPassword, phone, fullName, request)) {
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra email và username đã tồn tại chưa
            UserDAO userDAO = new UserDAO();
            if (userDAO.isEmailExist(email.trim())) {
                request.setAttribute("error", "Email đã tồn tại trong hệ thống");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
            
            if (userDAO.isUsernameExist(username.trim())) {
                request.setAttribute("error", "Tên đăng nhập đã tồn tại");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
            
            // Tạo user object mới (chỉ LANDLORD có thể đăng ký, theo yêu cầu)
            User newUser = new User();
            newUser.setUsername(username.trim());
            newUser.setPassword(PasswordUtils.hashPassword(password)); // Hash password
            newUser.setFullName(fullName.trim());
            newUser.setEmail(email.trim().toLowerCase());
            newUser.setPhone(phone.trim());
            newUser.setRoleId(2); // 2 = LANDLORD (theo database thiết kế)
            
            // Thêm user vào database
            boolean success = userDAO.addUser(newUser);
            
            if (success) {
                // Đăng ký thành công
                session.setAttribute("message", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
                session.setAttribute("messageType", "success");
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            } else {
                request.setAttribute("error", "Có lỗi xảy ra khi tạo tài khoản. Vui lòng thử lại");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
    
    private boolean isValidRegistration(String email, String username, String password, 
                                      String confirmPassword, String phone, String fullName, 
                                      HttpServletRequest request) {
        
        // Kiểm tra các trường bắt buộc
        if (email == null || email.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty() ||
            fullName == null || fullName.trim().isEmpty()) {
            
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin");
            return false;
        }
        
        // Kiểm tra email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            request.setAttribute("error", "Email không hợp lệ");
            return false;
        }
        
        // Kiểm tra username length (theo database: VARCHAR(50))
        if (username.trim().length() < 3 || username.trim().length() > 50) {
            request.setAttribute("error", "Tên đăng nhập phải từ 3 đến 50 ký tự");
            return false;
        }
        
        // Kiểm tra full_name length (theo database: VARCHAR(100))
        if (fullName.trim().length() < 2 || fullName.trim().length() > 100) {
            request.setAttribute("error", "Họ và tên phải từ 2 đến 100 ký tự");
            return false;
        }
        
        // Kiểm tra email length (theo database: VARCHAR(100))
        if (email.trim().length() > 100) {
            request.setAttribute("error", "Email quá dài (tối đa 100 ký tự)");
            return false;
        }
        
        // Kiểm tra phone length (theo database: VARCHAR(15))
        if (phone.trim().length() > 15) {
            request.setAttribute("error", "Số điện thoại quá dài (tối đa 15 ký tự)");
            return false;
        }
        
        // Kiểm tra password
        if (password.length() < 6) {
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp");
            return false;
        }
        
        // Kiểm tra password complexity
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*[0-9].*")) {
            request.setAttribute("error", "Mật khẩu phải chứa ít nhất 1 chữ cái và 1 chữ số");
            return false;
        }
        
        // Kiểm tra phone format
        if (!phone.matches("^[0-9]{10,11}$")) {
            request.setAttribute("error", "Số điện thoại không hợp lệ (10-11 chữ số)");
            return false;
        }
        
        return true;
    }
}