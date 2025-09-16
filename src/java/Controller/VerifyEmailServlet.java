package Controller;

import Dal.UserDAO;
import Model.User;
import Utility.EmailUtility;
import Utility.PasswordUtils;

import java.io.IOException;
import java.security.SecureRandom;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet xử lý bước xác nhận email khi đăng ký.
 *
 * Important notes applied:
 * - It reads the password hash from session if available ("regPasswordHash").
 * - If only raw "regPassword" exists (legacy), it will hash it here but you SHOULD prefer hashing
 *   earlier (in RegisterServlet) and store only "regPasswordHash" in session.
 * - After successful creation it removes all registration attributes from session and redirects
 *   to the login page to avoid form re-submission.
 */
@WebServlet(name = "VerifyEmailServlet", urlPatterns = {"/verifyemail"})
public class VerifyEmailServlet extends HttpServlet {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int CODE_LENGTH = 6;
    private static final int CODE_BOUND = 1_000_000;
    private static final int EXPIRY_MINUTES = 10;

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

        if ("resend".equals(action)) {
            handleResendCode(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        String email = (String) session.getAttribute("regEmail");
        String username = (String) session.getAttribute("regUsername");
        // Prefer the hashed password stored during registration flow
        String passwordHash = (String) session.getAttribute("regPasswordHash");
        // For backward compatibility: if only plaintext was stored (not recommended), hash it now.
        String legacyPlainPassword = (String) session.getAttribute("regPassword");

        String phone = (String) session.getAttribute("regPhone");
        String fullName = (String) session.getAttribute("regFullName");
        String sessionCode = (String) session.getAttribute("regVerificationCode");
        Long expiryTime = (Long) session.getAttribute("regExpiryTime");

        if (inputCode == null || inputCode.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã xác nhận");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
            return;
        }

        if (email == null || username == null || (passwordHash == null && legacyPlainPassword == null)
                || phone == null || fullName == null || sessionCode == null || expiryTime == null) {

            request.setAttribute("error", "Phiên làm việc đã hết hạn. Vui lòng đăng ký lại");
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (currentTime > expiryTime) {
            request.setAttribute("error", "Mã xác nhận đã hết hạn. Vui lòng yêu cầu mã mới");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
            return;
        }

        if (!sessionCode.equals(inputCode.trim())) {
            request.setAttribute("error", "Mã xác nhận không đúng");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
            return;
        }

        try {
            // Ensure we have a hashed password to store. Prefer regPasswordHash.
            String finalHashedPassword;
            if (passwordHash != null) {
                finalHashedPassword = passwordHash;
            } else {
                // Legacy: hash the plain password (not recommended - prefer hashing earlier)
                finalHashedPassword = PasswordUtils.hashPassword(legacyPlainPassword);
            }

            // Double-check uniqueness (race conditions)
            UserDAO userDAO = new UserDAO();
            if (userDAO.isEmailExist(email)) {
                request.setAttribute("error", "Email đã tồn tại. Vui lòng đăng nhập hoặc dùng email khác");
                request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
                return;
            }
            if (userDAO.isUsernameExist(username)) {
                request.setAttribute("error", "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác");
                request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
                return;
            }

            // Create user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(finalHashedPassword); // already hashed
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setRoleId(2); // 2 = LANDLORD according to schema

            boolean registered = userDAO.addUser(newUser);

            if (registered) {
                // Remove registration-related attributes from session to avoid leaks
                session.removeAttribute("regEmail");
                session.removeAttribute("regUsername");
                session.removeAttribute("regPassword");
                session.removeAttribute("regPasswordHash");
                session.removeAttribute("regPhone");
                session.removeAttribute("regFullName");
                session.removeAttribute("regVerificationCode");
                session.removeAttribute("regExpiryTime");

                // Redirect to login page (use redirect to avoid form re-submission)
                response.sendRedirect(request.getContextPath() + "/login?registered=true");
            } else {
                // Could be DB error or unique constraint triggered; show a generic message
                request.setAttribute("error", "Có lỗi xảy ra khi tạo tài khoản. Vui lòng thử lại");
                request.setAttribute("email", email);
                request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
            }

        } catch (Exception e) {
            System.out.println("Error during user registration: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
        }
    }

    private void handleResendCode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        String email = (String) session.getAttribute("regEmail");
        String fullName = (String) session.getAttribute("regFullName");

        if (email == null || fullName == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        try {
            // Sinh mã xác nhận mới
            int codeInt = SECURE_RANDOM.nextInt(CODE_BOUND);
            String newCode = String.format("%0" + CODE_LENGTH + "d", codeInt);
            long newExpiryTime = System.currentTimeMillis() + EXPIRY_MINUTES * 60_000L;

            // Gửi email
            String subject = "Mã xác nhận mới - PhongTro247";
            EmailUtility.sendRegistrationVerificationEmail(email, subject, newCode);

            // Cập nhật session
            session.setAttribute("regVerificationCode", newCode);
            session.setAttribute("regExpiryTime", newExpiryTime);

            request.setAttribute("message", "Mã xác nhận mới đã được gửi đến email của bạn");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);

        } catch (Exception e) {
            System.out.println("Error resending verification code: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Không thể gửi lại mã xác nhận. Vui lòng thử lại sau");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyEmail.jsp").forward(request, response);
        }
    }
}