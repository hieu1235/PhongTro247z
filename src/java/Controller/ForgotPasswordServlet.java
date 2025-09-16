package Controller;

import Dal.UserDAO;
import Model.User;
import Utility.EmailUtility;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name="ForgotPasswordServlet", urlPatterns={"/forgotPassword"})
public class ForgotPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    // Constants for security and configuration
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int CODE_LENGTH = 6;
    private static final int CODE_BOUND = 1_000_000;          // 0..999999
    private static final int EXPIRY_MINUTES = 5;              // Thời hạn mã (phút)
    private static final long RATE_LIMIT_MILLIS = 60_000L;    // 1 phút
    private static final int SESSION_TIMEOUT_SECONDS = 15 * 60; // 15 phút
    
    // Email validation pattern - more comprehensive
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    private static final Logger logger = Logger.getLogger(ForgotPasswordServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to forgot password JSP
        request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set encoding for Vietnamese characters
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String emailRaw = request.getParameter("email");
        
        // Validate input
        if (emailRaw == null || emailRaw.trim().isEmpty()) {
            setErrorAndForward(request, response, "Vui lòng nhập email");
            return;
        }

        String email = emailRaw.trim().toLowerCase();

        // Validate email format
        if (!email.matches(EMAIL_PATTERN)) {
            setErrorAndForward(request, response, "Định dạng email không hợp lệ");
            return;
        }

        // Validate email length
        if (email.length() > 100) {
            setErrorAndForward(request, response, "Email quá dài (tối đa 100 ký tự)");
            return;
        }

        try {
            HttpSession session = request.getSession();
            UserDAO userDAO = new UserDAO();

            // Rate limiting check (per session per email)
            if (isRateLimited(session, email)) {
                setErrorAndForward(request, response, "Vui lòng đợi 1 phút trước khi gửi lại mã xác nhận");
                return;
            }

            // Check if email exists in database
            boolean emailExists = userDAO.isEmailExist(email);
            
            if (!emailExists) {
                // Security: Don't reveal whether email exists or not
                // Always show success message to prevent email enumeration
                request.setAttribute("message", " Email không tồn tại trong hệ thống");
                request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
                return;
            }

            // Get user details for personalization
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                // This shouldn't happen if isEmailExist returned true, but handle it
                logger.log(Level.WARNING, "Email exists but user not found: {0}", email);
                request.setAttribute("message", "Nếu email tồn tại trong hệ thống, mã xác nhận đã được gửi đến email của bạn.");
                request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
                return;
            }

            // Generate verification code
            String resetCode = generateVerificationCode();
            long currentTime = System.currentTimeMillis();
            long expiryTime = currentTime + EXPIRY_MINUTES * 60_000L;

            // Store reset information in session
            storeResetInfoInSession(session, email, resetCode, expiryTime, currentTime);

            // Send verification email
            try {
                String subject = "Mã xác nhận đặt lại mật khẩu - PhongTro247";
                EmailUtility.sendForgotPasswordVerificationEmail(email, subject, resetCode, user.getFullName());
                
                logger.log(Level.INFO, "Password reset code sent to email: {0}", email);
                
                // Redirect to verification page to prevent form resubmission
                response.sendRedirect(request.getContextPath() + "/verifyCode");
                
            } catch (MessagingException me) {
                logger.log(Level.SEVERE, "Failed to send reset email to {0}: {1}", new Object[]{email, me.getMessage()});
                
                // Clean up session on email failure
                cleanupResetSession(session, email);
                
                setErrorAndForward(request, response, "Không thể gửi email xác nhận. Vui lòng kiểm tra kết nối mạng và thử lại sau");
                return;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in forgot password process: " + e.getMessage(), e);
            setErrorAndForward(request, response, "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau");
        }
    }

    /**
     * Check if the request is rate limited
     */
    private boolean isRateLimited(HttpSession session, String email) {
        String lastResetKey = "lastReset_" + email;
        Long lastResetTime = (Long) session.getAttribute(lastResetKey);
        long currentTime = System.currentTimeMillis();
        
        return lastResetTime != null && (currentTime - lastResetTime) < RATE_LIMIT_MILLIS;
    }

    /**
     * Generate a secure 6-digit verification code
     */
    private String generateVerificationCode() {
        int codeInt = SECURE_RANDOM.nextInt(CODE_BOUND);
        return String.format("%0" + CODE_LENGTH + "d", codeInt);
    }

    /**
     * Store reset information in session
     */
    private void storeResetInfoInSession(HttpSession session, String email, String resetCode, 
                                       long expiryTime, long currentTime) {
        // Store reset data for verification
        session.setAttribute("resetCode", resetCode);
        session.setAttribute("resetEmail", email);
        session.setAttribute("resetExpiry", expiryTime);
        
        // Store rate limiting info
        String lastResetKey = "lastReset_" + email;
        session.setAttribute(lastResetKey, currentTime);
        
        // Set session timeout for security
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
        
        logger.log(Level.INFO, "Reset session created for email: {0}", email);
    }

    /**
     * Clean up reset-related session attributes
     */
    private void cleanupResetSession(HttpSession session, String email) {
        session.removeAttribute("resetCode");
        session.removeAttribute("resetEmail");
        session.removeAttribute("resetExpiry");
        session.removeAttribute("lastReset_" + email);
        
        logger.log(Level.INFO, "Reset session cleaned up for email: {0}", email);
    }

    /**
     * Set error message and forward to JSP
     */
    private void setErrorAndForward(HttpServletRequest request, HttpServletResponse response, 
                                   String errorMessage) throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.setAttribute("email", request.getParameter("email")); // Preserve email input
        request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
    }
}