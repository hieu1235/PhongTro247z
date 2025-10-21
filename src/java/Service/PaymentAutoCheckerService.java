package Service;

import Model.PaymentOrder;
import Dal.PaymentOrderDAO;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.List;

/**
 * Payment Auto Checker Service - Tự động expire các orders hết hạn
 * Chỉ hỗ trợ MoMo payment orders
 */
public class PaymentAutoCheckerService {
    
    private final PaymentOrderDAO paymentOrderDAO = new PaymentOrderDAO();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static PaymentAutoCheckerService instance;
    
    private PaymentAutoCheckerService() {}
    
    public static synchronized PaymentAutoCheckerService getInstance() {
        if (instance == null) {
            instance = new PaymentAutoCheckerService();
        }
        return instance;
    }
    
    /**
     * Bắt đầu auto payment checker
     */
    public void startAutoChecker() {
        // Chạy mỗi 30 giây, bắt đầu sau 1 phút
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkPendingPayments();
            } catch (Exception e) {
                System.err.println("Auto payment check error: " + e.getMessage());
                e.printStackTrace();
            }
        }, 60, 30, TimeUnit.SECONDS);
        
        System.out.println("Payment Auto Checker Service started!");
    }
    
    /**
     * Dừng auto checker
     */
    public void stopAutoChecker() {
        scheduler.shutdown();
        System.out.println("Payment Auto Checker Service stopped!");
    }
    
    /**
     * Kiểm tra tất cả payment đang pending - chỉ expire orders hết hạn
     * MoMo sử dụng IPN callback nên không cần auto check
     */
    private void checkPendingPayments() {
        try {
            // Lấy danh sách orders pending trong 24 giờ gần đây
            List<PaymentOrder> pendingOrders = paymentOrderDAO.getPendingOrdersInTimeRange(24); // 24 hours
            
            if (pendingOrders.size() > 0) {
                System.out.println("Checking " + pendingOrders.size() + " pending orders for expiration...");
            }
            
            for (PaymentOrder order : pendingOrders) {
                try {
                    // Kiểm tra xem order đã expire chưa
                    if (order.getExpiresAt().before(new Date())) {
                        // Đánh dấu expired
                        paymentOrderDAO.updatePaymentStatus(order.getOrderCode(), "EXPIRED", null, 
                                                          "{\"reason\":\"auto_expire\",\"time\":\"" + new Date() + "\"}");
                        System.out.println("Expired order: " + order.getOrderCode());
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error checking order " + order.getOrderCode() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error in checkPendingPayments: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Lấy thống kê auto checker
     */
    public String getCheckerStatus() {
        try {
            List<PaymentOrder> pendingOrders = paymentOrderDAO.getPendingOrdersInTimeRange(24); // 24 hours
            
            int totalPending = pendingOrders.size();
            int expiredCount = 0;
            
            Date now = new Date();
            for (PaymentOrder order : pendingOrders) {
                if (order.getExpiresAt().before(now)) {
                    expiredCount++;
                }
            }
            
            return String.format("Auto Checker Status: %d pending orders, %d expired, %d active", 
                               totalPending, expiredCount, (totalPending - expiredCount));
                               
        } catch (Exception e) {
            return "Auto Checker Status: Error - " + e.getMessage();
        }
    }
}