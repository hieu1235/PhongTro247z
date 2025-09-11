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

@WebServlet(name = "VerifyEmailServlet", urlPatterns = {"/verifyemail"})
public class VerifyEmailServlet extends HttpServlet {

   
     
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("regEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }
        
        String email = (String) session.getAttribute("regEmail");
        request.setAttribute("email", email);
        request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String inputCode = request.getParameter("code");
        String action = request.getParameter("action");
        
        // Xử lý resend code
        if ("resend".equals(action)) {
            handleResendCode(request, response);
            return;
        }
        
        HttpSession session = request.getSession();
        
        // Lấy thông tin từ session
        String email = (String) session.getAttribute("regEmail");
        String username = (String) session.getAttribute("regUsername");
        String password = (String) session.getAttribute("regPassword");
        String phone = (String) session.getAttribute("regPhone");
        String fullName = (String) session.getAttribute("regFullName");
        String userRole = (String) session.getAttribute("regUserRole");
        String sessionCode = (String) session.getAttribute("regVerificationCode");
        Long expiryTime = (Long) session.getAttribute("regExpiryTime");
        
        // Validation
        if (inputCode == null || inputCode.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã xác nhận");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra session data
        if (email == null || username == null || password == null || phone == null || 
            fullName == null || userRole == null || sessionCode == null || expiryTime == null) {
            
            request.setAttribute("error", "Phiên làm việc đã hết hạn. Vui lòng đăng ký lại");
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Kiểm tra thời gian hết hạn
        if (currentTime > expiryTime) {
            request.setAttribute("error", "Mã xác nhận đã hết hạn. Vui lòng yêu cầu mã mới");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra mã code
        if (!sessionCode.equals(inputCode.trim())) {
            request.setAttribute("error", "Mã xác nhận không đúng");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
            return;
        }
        
        try {
            // Mã code đúng - tạo user mới
            String hashedPassword = PasswordUtils.hashPassword(password);
            
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPasswordHash(hashedPassword);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setFullName(fullName);
            newUser.setUserRole(userRole);
            newUser.setEmailVerified(true);
            newUser.setStatus("ACTIVE");
            newUser.setAvatarUrl("images/avatar-default.png");
            
            UserDAO userDAO = new UserDAO();
            boolean registered = userDAO.register(newUser);
            
            if (registered) {
                // Xóa thông tin đăng ký khỏi session
                session.removeAttribute("regEmail");
                session.removeAttribute("regUsername");
                session.removeAttribute("regPassword");
                session.removeAttribute("regPhone");
                session.removeAttribute("regFullName");
                session.removeAttribute("regUserRole");
                session.removeAttribute("regVerificationCode");
                session.removeAttribute("regExpiryTime");
                
                request.setAttribute("success", "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                
            } else {
                request.setAttribute("error", "Có lỗi xảy ra khi tạo tài khoản. Vui lòng thử lại");
                request.setAttribute("email", email);
                request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            System.out.println("Error during user registration: " + e.getMessage());
            request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
        }
    }
    
    private void handleResendCode(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("regEmail");
        String fullName = (String) session.getAttribute("regFullName");
        
        if (email == null || fullName == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }
        
        try {
            // Sinh mã xác nhận mới
            String newCode = String.format("%06d", new Random().nextInt(1000000));
            long newExpiryTime = System.currentTimeMillis() + 10 * 60 * 1000; // 10 phút
            
            // Gửi email mới
            String subject = "Mã xác nhận mới - Phòng Trọ Hieu1235";
            EmailUtility.sendEmail(email, subject, newCode);
            
            // Cập nhật session
            session.setAttribute("regVerificationCode", newCode);
            session.setAttribute("regExpiryTime", newExpiryTime);
            
            request.setAttribute("message", "Mã xác nhận mới đã được gửi đến email của bạn");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.out.println("Error resending verification code: " + e.getMessage());
            request.setAttribute("error", "Không thể gửi lại mã xác nhận. Vui lòng thử lại sau");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
        }
    }
}