package Controller;

import Dal.UserDAO;
import Utility.PasswordUtils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/resetPassword"})
public class ResetPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ResetPasswordServlet.class.getName());
    
    // Password validation constants
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 100;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).{6,}$");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Kiểm tra session và quyền reset password
        if (session == null || 
            session.getAttribute("resetEmail") == null || 
            session.getAttribute("canResetPassword") == null) {
            
            logger.log(Level.WARNING, "Unauthorized access to reset password page");
            response.sendRedirect(request.getContextPath() + "/forgotPassword");
            return;
        }
        
        String email = (String) session.getAttribute("resetEmail");
        request.setAttribute("email", email);
        request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set encoding for Vietnamese
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        
        // Validate session
        if (session == null || 
            session.getAttribute("resetEmail") == null || 
            session.getAttribute("canResetPassword") == null) {
            
            logger.log(Level.WARNING, "Invalid session for password reset");
            setErrorAndForward(request, response, "Phiên làm việc không hợp lệ. Vui lòng bắt đầu lại từ đầu");
            return;
        }
        
        String sessionEmail = (String) session.getAttribute("resetEmail");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate inputs
        if (!validateInputs(newPassword, confirmPassword, request, response, sessionEmail)) {
            return;
        }
        
        try {
            // Hash password
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            
            // Update password in database
            UserDAO userDAO = new UserDAO();
            boolean updateSuccess = userDAO.updatePassword(sessionEmail, hashedPassword);
            
            if (updateSuccess) {
                // Clear all reset-related session data
                clearResetSession(session, sessionEmail);
                
                logger.log(Level.INFO, "Password successfully reset for email: {0}", sessionEmail);
                
                // Set success message and redirect to login
                session.setAttribute("message", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới");
                session.setAttribute("messageType", "success");
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                
            } else {
                setErrorAndForward(request, response, "Không thể cập nhật mật khẩu. Vui lòng thử lại");
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during password reset: " + e.getMessage(), e);
            setErrorAndForward(request, response, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau");
        }
    }
    
    /**
     * Validate password inputs
     */
    private boolean validateInputs(String newPassword, String confirmPassword, 
                                 HttpServletRequest request, HttpServletResponse response, 
                                 String email) throws ServletException, IOException {
        
        // Check if passwords are provided
        if (newPassword == null || newPassword.trim().isEmpty()) {
            setErrorAndForward(request, response, "Vui lòng nhập mật khẩu mới", email);
            return false;
        }
        
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            setErrorAndForward(request, response, "Vui lòng xác nhận mật khẩu", email);
            return false;
        }
        
        // Check password length
        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            setErrorAndForward(request, response, "Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự", email);
            return false;
        }
        
        if (newPassword.length() > MAX_PASSWORD_LENGTH) {
            setErrorAndForward(request, response, "Mật khẩu không được vượt quá " + MAX_PASSWORD_LENGTH + " ký tự", email);
            return false;
        }
        
        // Check password pattern (at least 1 letter and 1 number)
        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            setErrorAndForward(request, response, "Mật khẩu phải chứa ít nhất 1 chữ cái và 1 chữ số", email);
            return false;
        }
        
        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            setErrorAndForward(request, response, "Mật khẩu xác nhận không khớp", email);
            return false;
        }
        
        return true;
    }
    
    /**
     * Clear all reset-related session data
     */
    private void clearResetSession(HttpSession session, String email) {
        session.removeAttribute("resetCode");
        session.removeAttribute("resetEmail");
        session.removeAttribute("resetExpiry");
        session.removeAttribute("canResetPassword");
        session.removeAttribute("lastReset_" + email);
        
        logger.log(Level.INFO, "Reset session cleared for email: {0}", email);
    }
    
    /**
     * Set error and forward to reset password page
     */
    private void setErrorAndForward(HttpServletRequest request, HttpServletResponse response, 
                                   String errorMessage) throws ServletException, IOException {
        setErrorAndForward(request, response, errorMessage, null);
    }
    
    /**
     * Set error and forward to reset password page with email
     */
    private void setErrorAndForward(HttpServletRequest request, HttpServletResponse response, 
                                   String errorMessage, String email) throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        if (email != null) {
            request.setAttribute("email", email);
        }
        request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
    }
}