package Controller;

import Dal.UserDAO;
import Model.User;
import Model.UserBalance;
import Utility.PasswordUtils;
import Service.SubscriptionService;
import Service.BalanceService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final SubscriptionService subscriptionService = new SubscriptionService();
    private final BalanceService balanceService = new BalanceService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // Lấy thông tin user mới nhất từ database
            User latestUser = userDAO.getUserById(user.getUserId());
            if (latestUser != null) {
                // Cập nhật session với thông tin mới nhất
                session.setAttribute("user", latestUser);
                user = latestUser;
            }
            
            // Lấy Pro info nếu có
            if (user.getIsPro()) {
                SubscriptionService.UserProInfo proInfo = subscriptionService.getUserProInfo(user.getUserId());
                req.setAttribute("proInfo", proInfo);
                System.out.println("DEBUG: ProInfo set for user " + user.getUserId() + ", ProDaysRemaining: " + (proInfo != null ? proInfo.getProDaysRemaining() : "null"));
            }
            
            // Lấy thông tin số dư xu
            UserBalance balance = balanceService.getUserBalance(user.getUserId());
            if (balance == null) {
                balance = new UserBalance(user.getUserId());
            }
            req.setAttribute("balance", balance);
            
        } catch (Exception e) {
            System.out.println("ProfileServlet.doGet error: " + e.getMessage());
            e.printStackTrace();
            // Set default values to prevent JSP errors
            req.setAttribute("balance", new UserBalance(user.getUserId()));
        }
        
        req.getRequestDispatcher("/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        
        String action = req.getParameter("action");
        
        try {
            if ("updateProfile".equals(action)) {
                handleUpdateProfile(req, resp, user, session);
            } else if ("changePassword".equals(action)) {
                handleChangePassword(req, resp, user, session);
            } else {
                resp.sendRedirect(req.getContextPath() + "/profile?error=invalid_action");
            }
        } catch (Exception e) {
            System.out.println("ProfileServlet.doPost error: " + e.getMessage());
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/profile?error=system_error");
        }
    }
    
    /**
     * Xử lý cập nhật thông tin profile
     */
    private void handleUpdateProfile(HttpServletRequest req, HttpServletResponse resp, User user, HttpSession session) 
            throws ServletException, IOException {
        
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        
        // Validate input
        if (fullName == null || fullName.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/profile?error=missing_fullname");
            return;
        }
        
        if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
            resp.sendRedirect(req.getContextPath() + "/profile?error=invalid_email");
            return;
        }
        
        // Kiểm tra email đã tồn tại chưa (trừ email của user hiện tại)
        try {
            User existingUser = userDAO.getUserByEmail(email.trim());
            if (existingUser != null && existingUser.getUserId() != user.getUserId()) {
                resp.sendRedirect(req.getContextPath() + "/profile?error=email_exists");
                return;
            }
        } catch (Exception e) {
            System.out.println("ProfileServlet: Error checking email existence: " + e.getMessage());
        }
        
        // Cập nhật thông tin
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPhone(phone != null ? phone.trim() : "");
        
        try {
            boolean updated = userDAO.updateUserProfile(user.getUserId(), user.getFullName(), 
                                                      user.getEmail(), user.getPhone());
            
            if (updated) {
                // Cập nhật session với thông tin mới
                session.setAttribute("user", user);
                resp.sendRedirect(req.getContextPath() + "/profile?success=profile_updated");
                System.out.println("ProfileServlet: Updated profile for user " + user.getUserId());
            } else {
                resp.sendRedirect(req.getContextPath() + "/profile?error=update_failed");
            }
        } catch (Exception e) {
            System.out.println("ProfileServlet: Error updating profile: " + e.getMessage());
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/profile?error=system_error");
        }
    }
    
    /**
     * Xử lý đổi mật khẩu
     */
    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp, User user, HttpSession session) 
            throws ServletException, IOException {
        
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");
        
        // Validate input
        if (currentPassword == null || currentPassword.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/profile?error=missing_current_password");
            return;
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            resp.sendRedirect(req.getContextPath() + "/profile?error=weak_password");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            resp.sendRedirect(req.getContextPath() + "/profile?error=password_mismatch");
            return;
        }
        
        try {
            // Kiểm tra mật khẩu hiện tại
            if (!PasswordUtils.checkPassword(currentPassword, user.getPassword())) {
                resp.sendRedirect(req.getContextPath() + "/profile?error=wrong_current_password");
                return;
            }
            
            // Hash mật khẩu mới
            String hashedNewPassword = PasswordUtils.hashPassword(newPassword);
            
            // Cập nhật trong database
            boolean updated = userDAO.updateUserPassword(user.getUserId(), hashedNewPassword);
            
            if (updated) {
                // Cập nhật user object trong session
                user.setPassword(hashedNewPassword);
                session.setAttribute("user", user);
                
                resp.sendRedirect(req.getContextPath() + "/profile?success=password_changed");
                System.out.println("ProfileServlet: Changed password for user " + user.getUserId());
            } else {
                resp.sendRedirect(req.getContextPath() + "/profile?error=password_update_failed");
            }
        } catch (Exception e) {
            System.out.println("ProfileServlet: Error changing password: " + e.getMessage());
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/profile?error=system_error");
        }
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}