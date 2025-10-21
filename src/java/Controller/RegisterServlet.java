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

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int CODE_LENGTH = 6;
    private static final int CODE_BOUND = 1_000_000;
    private static final int EXPIRY_MINUTES = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Lấy parameters từ form - phù hợp với model User
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        String fullName = request.getParameter("fullName");

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
                setFormData(request, email, username, phone, fullName);
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            if (userDAO.isUsernameExist(username.trim())) {
                request.setAttribute("error", "Tên đăng nhập đã tồn tại");
                setFormData(request, email, username, phone, fullName);
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Sinh mã xác nhận
            int codeInt = SECURE_RANDOM.nextInt(CODE_BOUND);
            String verificationCode = String.format("%0" + CODE_LENGTH + "d", codeInt);
            long expiryTime = System.currentTimeMillis() + EXPIRY_MINUTES * 60_000L;

            // Hash password và tạo tài khoản trực tiếp
            String hashedPassword = PasswordUtils.hashPassword(password);

            // Tạo tài khoản trực tiếp không cần xác thực email
            User newUser = new User();
            newUser.setUsername(username.trim());
            newUser.setPassword(hashedPassword);
            newUser.setFullName(fullName.trim());
            newUser.setEmail(email.trim().toLowerCase());
            newUser.setPhone(phone.trim());
            newUser.setRoleId(2); // 2 = LANDLORD

            boolean registered = userDAO.addUser(newUser);
            
            if (registered) {
                // Set success message và redirect to login
                session.setAttribute("message", "Đăng ký thành công! Bạn có thể đăng nhập ngay.");
                session.setAttribute("messageType", "success");
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                request.setAttribute("error", "Có lỗi xảy ra khi tạo tài khoản. Vui lòng thử lại");
                setFormData(request, email, username, phone, fullName);
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }

        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau");
            setFormData(request, email, username, phone, fullName);
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }

    private void setFormData(HttpServletRequest request, String email, String username, String phone, String fullName) {
        request.setAttribute("email", email);
        request.setAttribute("username", username);
        request.setAttribute("phone", phone);
        request.setAttribute("fullName", fullName);
    }

    private boolean isValidRegistration(String email, String username, String password,
                                        String confirmPassword, String phone, String fullName,
                                        HttpServletRequest request) {

        // Kiểm tra các field bắt buộc từ model User
        if (email == null || email.trim().isEmpty() ||
                username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() ||
                fullName == null || fullName.trim().isEmpty()) {

            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin");
            return false;
        }

        // Validation email - phù hợp với model User.email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            request.setAttribute("error", "Email không hợp lệ");
            return false;
        }

        if (email.trim().length() > 100) {
            request.setAttribute("error", "Email quá dài (tối đa 100 ký tự)");
            return false;
        }

        // Validation username - phù hợp với model User.username
        if (username.trim().length() < 3 || username.trim().length() > 50) {
            request.setAttribute("error", "Tên đăng nhập phải từ 3 đến 50 ký tự");
            return false;
        }

        // Validation fullName - phù hợp với model User.fullName
        if (fullName.trim().length() < 2 || fullName.trim().length() > 100) {
            request.setAttribute("error", "Họ và tên phải từ 2 đến 100 ký tự");
            return false;
        }

        // Validation phone - phù hợp với model User.phone
        if (phone.trim().length() > 15) {
            request.setAttribute("error", "Số điện thoại quá dài (tối đa 15 ký tự)");
            return false;
        }

        if (!phone.matches("^[0-9]{10,11}$")) {
            request.setAttribute("error", "Số điện thoại không hợp lệ (10-11 chữ số)");
            return false;
        }

        // Validation password - phù hợp với model User.password
        if (password.length() < 6) {
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp");
            return false;
        }

        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*[0-9].*")) {
            request.setAttribute("error", "Mật khẩu phải chứa ít nhất 1 chữ cái và 1 chữ số");
            return false;
        }

        return true;
    }
}