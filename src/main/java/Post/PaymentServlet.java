package Post;

import Model.*;
import Service.PaymentService;
import config.PaymentConfig;
import utils.WebhookSignatureVerifier;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet(name = "PaymentServlet", urlPatterns = {"/payment/*"})
public class PaymentServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(PaymentServlet.class.getName());
    private final PaymentService paymentService = new PaymentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null) {
            resp.sendRedirect(req.getContextPath() + "/subscription");
            return;
        }
        
        switch (pathInfo) {
            case "/return":
                handlePaymentReturn(req, resp);
                break;
            case "/cancel":
                handlePaymentCancel(req, resp);
                break;
            case "/status":
                handlePaymentStatus(req, resp);
                break;
            case "/history":
                handlePaymentHistory(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/subscription");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null) {
            resp.sendRedirect(req.getContextPath() + "/subscription");
            return;
        }
        
        switch (pathInfo) {
            case "/create":
                handleCreatePayment(req, resp);
                break;
            case "/webhook":
            case "/payos_transfer_handler":
                handlePayOSWebhook(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    /**
     * Tạo payment order và redirect đến gateway
     */
    private void handleCreatePayment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            String amountStr = req.getParameter("amount");
            String paymentMethod = req.getParameter("paymentMethod");
            
            if (amountStr == null || paymentMethod == null) {
                session.setAttribute("error", "Thông tin thanh toán không hợp lệ");
                resp.sendRedirect(req.getContextPath() + "/subscription");
                return;
            }
            
            int coinsAmount = Integer.parseInt(amountStr);
            
            // Validate amount
            if (coinsAmount < 10000 || coinsAmount > 10000000) {
                session.setAttribute("error", "Số tiền phải từ 10,000đ đến 10,000,000đ");
                resp.sendRedirect(req.getContextPath() + "/subscription");
                return;
            }
            
            // Use PayOS as default payment method
            if (paymentMethod == null || paymentMethod.isEmpty()) {
                paymentMethod = "payos";
            }
            
            System.out.println("PaymentServlet: Creating payment - User: " + user.getUserId() + 
                             ", Amount: " + coinsAmount + " VND, Method: " + paymentMethod);
            
            // Tạo payment order (chỉ cần userId và coinAmount)
            PaymentOrder order = paymentService.createPaymentOrder(user.getUserId(), coinsAmount);
            
            if (order == null) {
                session.setAttribute("error", "Không thể tạo đơn hàng thanh toán");
                resp.sendRedirect(req.getContextPath() + "/subscription");
                return;
            }
            
            // Tạo payment URL (PayOS checkout URL với VietQR)
            String paymentUrl = paymentService.createPaymentUrl(order);
            
            if (paymentUrl == null) {
                session.setAttribute("error", "Không thể tạo liên kết thanh toán");
                resp.sendRedirect(req.getContextPath() + "/subscription");
                return;
            }
            
            // Lưu order code vào session để tracking
            session.setAttribute("paymentOrderCode", order.getOrderCode());
            
            System.out.println("PaymentServlet: Created payment " + order.getOrderCode() + " for user " + user.getUserId());
            System.out.println("PaymentServlet: Redirecting to PayOS checkout: " + paymentUrl);
            
            // Redirect đến PayOS payment gateway (checkout URL có VietQR)
            resp.sendRedirect(paymentUrl);
            
        } catch (Exception e) {
            System.out.println("PaymentServlet.handleCreatePayment error: " + e.getMessage());
            e.printStackTrace();
            
            session.setAttribute("error", "Lỗi tạo thanh toán: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/subscription");
        }
    }
    
    /**
     * Xử lý callback sau khi thanh toán MoMo (Return URL)
     */
    private void handlePaymentReturn(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
        try {
            // MoMo parameters
            String partnerCode = req.getParameter("partnerCode");
            String orderId = req.getParameter("orderId");
            String requestId = req.getParameter("requestId");
            String amount = req.getParameter("amount");
            String orderInfo = req.getParameter("orderInfo");
            String orderType = req.getParameter("orderType");
            String transId = req.getParameter("transId");
            String resultCode = req.getParameter("resultCode");
            String message = req.getParameter("message");
            String payType = req.getParameter("payType");
            String responseTime = req.getParameter("responseTime");
            String extraData = req.getParameter("extraData");
            String signature = req.getParameter("signature");
            
            String paymentMethod = "momo";
            String status = "0".equals(resultCode) ? "SUCCESS" : "FAILED";
            
            if (orderId == null) {
                // Try session fallback
                orderId = (String) session.getAttribute("paymentOrderCode");
                if (orderId == null) {
                    session.setAttribute("error", "Không tìm thấy thông tin đơn hàng");
                    resp.sendRedirect(req.getContextPath() + "/subscription");
                    return;
                }
            }
            
            // Process payment result
            boolean processed = paymentService.handlePaymentCallback(
                orderId, status, transId, 
                req.getQueryString(), paymentMethod
            );
            
            if (processed && "SUCCESS".equals(status)) {
                session.setAttribute("success", "Thanh toán MoMo thành công! Xu đã được nạp vào tài khoản.");
                System.out.println("PaymentServlet: MoMo payment successful for order " + orderId);
            } else if ("SUCCESS".equals(status)) {
                session.setAttribute("error", "Thanh toán thành công nhưng có lỗi xử lý. Vui lòng liên hệ hỗ trợ.");
                System.out.println("PaymentServlet: MoMo payment successful but processing failed for order " + orderId);
            } else {
                session.setAttribute("error", "Thanh toán MoMo thất bại: " + message);
                System.out.println("PaymentServlet: MoMo payment failed for order " + orderId + " - " + message);
            }
            
        } catch (Exception e) {
            System.out.println("PaymentServlet.handlePaymentReturn error: " + e.getMessage());
            e.printStackTrace();
            
            if (session != null) {
                session.setAttribute("error", "Lỗi xử lý kết quả thanh toán");
            }
        }
        
        resp.sendRedirect(req.getContextPath() + "/subscription");
    }
    
    /**
     * Xử lý khi user hủy thanh toán (Cancel URL)
     */
    private void handlePaymentCancel(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
        try {
            // Get order code from session or parameter
            String orderCode = req.getParameter("orderCode");
            if (orderCode == null && session != null) {
                orderCode = (String) session.getAttribute("paymentOrderCode");
            }
            
            if (orderCode != null) {
                // Update order status to CANCELLED
                PaymentOrder order = paymentService.getPaymentOrder(orderCode);
                if (order != null && "PENDING".equals(order.getStatus())) {
                    order.setStatus("CANCELLED");
                    paymentService.updatePaymentOrder(order);
                    System.out.println("PaymentServlet: Order " + orderCode + " cancelled by user");
                }
                
                // Clear session
                if (session != null) {
                    session.removeAttribute("paymentOrderCode");
                }
            }
            
            // Set cancellation message
            if (session != null) {
                session.setAttribute("info", "Bạn đã hủy thanh toán. Vui lòng thử lại nếu muốn tiếp tục.");
            }
            
            System.out.println("PaymentServlet: Payment cancelled" + (orderCode != null ? " - Order: " + orderCode : ""));
            
        } catch (Exception e) {
            System.err.println("PaymentServlet.handlePaymentCancel error: " + e.getMessage());
            e.printStackTrace();
            
            if (session != null) {
                session.setAttribute("error", "Có lỗi khi xử lý hủy thanh toán");
            }
        }
        
        resp.sendRedirect(req.getContextPath() + "/subscription");
    }
    
    /**
     * Xử lý PayOS Webhook (Transfer Handler)
     */
    /**
     * Handle PayOS webhook callback
     * 
     * ✅ SECURITY FIX: Verify webhook signature để ngăn chặn fake webhooks
     */
    private void handlePayOSWebhook(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // 1. Read webhook body
            StringBuilder buffer = new StringBuilder();
            String line;
            try (java.io.BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            }
            
            String requestBody = buffer.toString();
            logger.info("PaymentServlet: Received PayOS webhook");
            
            // 2. ✅ CRITICAL: Verify webhook signature
            String signature = req.getHeader("X-PayOS-Signature");
            if (signature == null || signature.isEmpty()) {
                logger.warning("PayOS webhook rejected: Missing signature header");
                resp.setStatus(403);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\":403,\"message\":\"Missing signature\"}");
                return;
            }
            
            boolean isValidSignature = WebhookSignatureVerifier.verifyPayOSWebhook(requestBody, signature);
            if (!isValidSignature) {
                logger.warning("PayOS webhook rejected: Invalid signature");
                resp.setStatus(403);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\":403,\"message\":\"Invalid signature\"}");
                return;
            }
            
            logger.info("PayOS webhook signature verified successfully");
            
            // 3. Verify webhook data using PayOS SDK
            Map<String, Object> webhookData = paymentService.verifyWebhookData(requestBody);
            
            if (webhookData != null && !webhookData.isEmpty()) {
                String orderCode = String.valueOf(webhookData.get("orderCode"));
                String status = "SUCCESS"; // PayOS webhook only fires on success
                String transactionId = (String) webhookData.get("transactionId");
                
                // 4. Process payment callback
                boolean processed = paymentService.handlePaymentCallback(
                    orderCode, status, transactionId, requestBody, "payos"
                );
                
                logger.info("PayOS webhook processed: " + processed + " for order " + orderCode);
                
                // 5. Return 200 OK to PayOS
                resp.setStatus(200);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\":0,\"message\":\"Webhook received\"}");
            } else {
                throw new Exception("Invalid webhook data");
            }
            
        } catch (Exception e) {
            logger.severe("PaymentServlet.handlePayOSWebhook error: " + e.getMessage());
            e.printStackTrace();
            
            resp.setStatus(400);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":-1,\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Check payment status
     */
    private void handlePaymentStatus(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String orderCode = req.getParameter("orderCode");
        
        if (orderCode == null) {
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"Order code required\"}");
            return;
        }
        
        PaymentOrder order = paymentService.getPaymentOrder(orderCode);
        
        resp.setContentType("application/json");
        if (order != null) {
            resp.getWriter().write("{\"status\": \"" + order.getStatus() + "\", " +
                                 "\"expired\": " + order.isExpired() + ", " +
                                 "\"minutesLeft\": " + order.getMinutesUntilExpired() + "}");
        } else {
            resp.getWriter().write("{\"error\": \"Order not found\"}");
        }
    }
    
    /**
     * Show payment history
     */
    private void handlePaymentHistory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        
        List<PaymentOrder> payments = paymentService.getPaymentHistory(user.getUserId(), 20);
        req.setAttribute("payments", payments);
        req.getRequestDispatcher("/payment_history.jsp").forward(req, resp);
    }
}