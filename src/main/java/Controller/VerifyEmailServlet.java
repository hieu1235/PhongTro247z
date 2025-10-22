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

        // Email verification is disabled - redirect to register
        response.sendRedirect(request.getContextPath() + "/register");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Email verification is disabled - redirect to register
        response.sendRedirect(request.getContextPath() + "/register");
    }


}