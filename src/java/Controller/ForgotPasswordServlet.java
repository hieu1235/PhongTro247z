/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package Controller;

import Dal.UserDAO;
import Utility.EmailUtilityVerifyCode;

import java.io.IOException;
import java.security.SecureRandom;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name="ForgotPasswordServlet", urlPatterns={"/forgotPassword"})
public class ForgotPasswordServlet extends HttpServlet {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int CODE_LENGTH = 6;
    private static final int CODE_BOUND = 1_000_000;          // 0..999999
    private static final int EXPIRY_MINUTES = 5;              // Thời hạn mã (phút)
    private static final long RATE_LIMIT_MILLIS = 60_000L;    // 1 phút

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String emailRaw = request.getParameter("email");
        if (emailRaw == null || emailRaw.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        String email = emailRaw.trim().toLowerCase();

        try {
            HttpSession session = request.getSession();
            UserDAO userDAO = new UserDAO();

            // Kiểm tra email tồn tại
            if (!userDAO.isEmailExist(email)) {
                request.setAttribute("error", "Email không tồn tại trong hệ thống");
                request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
                return;
            }

            // Rate limiting
            String lastResetKey = "lastReset_" + email;
            Long lastResetTime = (Long) session.getAttribute(lastResetKey);
            long now = System.currentTimeMillis();
            if (lastResetTime != null && (now - lastResetTime) < RATE_LIMIT_MILLIS) {
                request.setAttribute("error", "Vui lòng đợi 1 phút trước khi gửi lại mã xác nhận");
                request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
                return;
            }

            // Sinh mã xác nhận bảo mật (6 chữ số có thể bắt đầu bằng 0)
            int codeInt = SECURE_RANDOM.nextInt(CODE_BOUND);
            String resetCode = String.format("%0" + CODE_LENGTH + "d", codeInt);

            long expiryTime = now + EXPIRY_MINUTES * 60_000L;

            // Lưu session
            session.setAttribute("resetCode", resetCode);
            session.setAttribute("resetEmail", email);
            session.setAttribute("resetExpiry", expiryTime);
            session.setAttribute(lastResetKey, now);

            // Gửi email (nội dung trong EmailUtilityVerifyCode đã cập nhật đúng 5 phút)
            String subject = "Mã xác nhận đặt lại mật khẩu - Phòng Trọ Hieu1235";
            EmailUtilityVerifyCode.sendEmail(email, subject, resetCode);

            request.setAttribute("email", email);
            request.setAttribute("message", "Mã xác nhận đã được gửi đến email của bạn");
            request.getRequestDispatcher("verifyCode.jsp").forward(request, response);

        } catch (Exception e) {
            System.out.println("Error in forgot password: " + e.getMessage());
            request.setAttribute("error", "Không thể gửi email. Vui lòng thử lại sau");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
        }
    }
}