package Controller;

import Service.PaymentAutoCheckerService;

/**
 * Payment Management Helper - Static methods để quản lý payment system
 */
public class PaymentManager {
    
    private static PaymentAutoCheckerService autoChecker;
    
    static {
        try {
            // Khởi động auto checker
            autoChecker = PaymentAutoCheckerService.getInstance();
            autoChecker.startAutoChecker();
            System.out.println("=== PAYMENT AUTO CHECKER STARTED ===");
            System.out.println("Checking pending payments every 30 seconds...");
            System.out.println("====================================");
        } catch (Exception e) {
            System.err.println("Failed to start Payment Auto Checker: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Process webhook from payment systems (MoMo only)
     */
    public static boolean processWebhook(String webhookData) {
        try {
            // Only MoMo payment supported
            System.out.println("Webhook processing - Only MoMo payment supported");
            return false;
        } catch (Exception e) {
            System.err.println("Error processing webhook: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get status của payment system
     */
    public static String getSystemStatus() {
        try {
            if (autoChecker != null) {
                return autoChecker.getCheckerStatus();
            }
            return "Auto Checker not initialized";
        } catch (Exception e) {
            return "Error getting status: " + e.getMessage();
        }
    }
    
    /**
     * Khởi động lại auto checker nếu cần
     */
    public static boolean restartAutoChecker() {
        try {
            if (autoChecker != null) {
                autoChecker.stopAutoChecker();
            }
            
            autoChecker = PaymentAutoCheckerService.getInstance();
            autoChecker.startAutoChecker();
            
            System.out.println("Payment Auto Checker restarted successfully!");
            return true;
        } catch (Exception e) {
            System.err.println("Failed to restart auto checker: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Test method để kiểm tra system hoạt động
     */
    public static void testSystem() {
        System.out.println("=== PAYMENT SYSTEM TEST ===");
        System.out.println("Auto Checker Status: " + (autoChecker != null ? "RUNNING" : "NOT RUNNING"));
        System.out.println("System Status: " + getSystemStatus());
        System.out.println("Current Time: " + new java.util.Date());
        System.out.println("========================");
    }
}