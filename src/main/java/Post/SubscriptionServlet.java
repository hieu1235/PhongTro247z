package Post;

import Model.*;
import Service.BalanceService;
import Service.SubscriptionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name = "SubscriptionServlet", urlPatterns = {"/subscription"})
public class SubscriptionServlet extends HttpServlet {

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
            // Load Pro info and balance
            SubscriptionService.UserProInfo proInfo = subscriptionService.getUserProInfo(user.getUserId());
            UserBalance balance = balanceService.getUserBalance(user.getUserId());
            
            // Ensure balance is not null - create default if needed
            if (balance == null) {
                balance = new UserBalance(user.getUserId());
            }
            
            req.setAttribute("proInfo", proInfo);
            req.setAttribute("balance", balance);
            req.setAttribute("proCost", 100); // 100 xu = 100.000 VNĐ
            
        } catch (Exception e) {
            System.out.println("SubscriptionServlet: Error loading Pro info: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("error", "Lỗi tải thông tin Pro: " + e.getMessage());
        }
        
        req.getRequestDispatcher("/subscription.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String action = req.getParameter("action");
        
        try {
            switch (action) {
                case "buyPro":
                    handleBuyPro(req, resp, user);
                    break;
                case "addCoins":
                    handleAddCoins(req, resp, user);
                    break;
                default:
                    req.setAttribute("error", "Hành động không hợp lệ");
            }
        } catch (Exception e) {
            System.out.println("SubscriptionServlet: Error in doPost: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("error", "Lỗi xử lý: " + e.getMessage());
        }
        
        // Redirect to avoid resubmission
        resp.sendRedirect(req.getContextPath() + "/subscription");
    }
    
    /**
     * Mua gói Pro (100 xu = 30 ngày Pro)
     */
    private void handleBuyPro(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        boolean success = subscriptionService.buyProSubscription(user.getUserId());
        
        HttpSession session = req.getSession();
        if (success) {
            // Refresh user info from database to get updated Pro status
            try {
                Dal.UserDAO userDAO = new Dal.UserDAO();
                User updatedUser = userDAO.getUserById(user.getUserId());
                if (updatedUser != null) {
                    session.setAttribute("user", updatedUser);
                    System.out.println("SubscriptionServlet: User info refreshed after Pro purchase");
                }
            } catch (Exception e) {
                System.out.println("SubscriptionServlet: Error refreshing user info: " + e.getMessage());
            }
            
            session.setAttribute("success", "Mua gói Pro thành công! Bạn có thể đăng 10 bài/ngày trong 30 ngày tới.");
            System.out.println("SubscriptionServlet: User " + user.getUserId() + " successfully bought Pro");
        } else {
            session.setAttribute("error", "Không thể mua gói Pro. Vui lòng kiểm tra số dư xu (cần 100 xu).");
        }
    }
    
    /**
     * Handle add coins with simplified QR payment
     */
    private void handleAddCoins(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        String amountStr = req.getParameter("amount");
        
        if (amountStr == null || amountStr.isEmpty()) {
            throw new IllegalArgumentException("Amount is required");
        }
        
        double vndAmount = Double.parseDouble(amountStr);
        
        if (vndAmount < 10000 || vndAmount > 10000000) {
            throw new IllegalArgumentException("Amount must be between 10,000 and 10,000,000 VND");
        }
        
        // Redirect to subscription page with amount (MoMo payment only)
        resp.sendRedirect(req.getContextPath() + "/subscription?amount=" + (int)vndAmount);
    }
}