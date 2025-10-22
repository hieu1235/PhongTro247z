package Controller;

import Service.PaymentAutoCheckerService;

/**
 * Payment Service Handler - Quản lý auto checker cho MoMo payment
 * Chỉ hỗ trợ MoMo payment system
 */
public class PaymentWebhookHandler {

    private static PaymentAutoCheckerService autoChecker;
    
    // Khởi động auto checker service khi class load
    static {
        try {
            autoChecker = PaymentAutoCheckerService.getInstance();
            autoChecker.startAutoChecker();
            System.out.println("MoMo Payment Auto Checker started (expire orders only)!");
        } catch (Exception e) {
            System.err.println("Failed to start Payment Auto Checker: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Lấy status của auto checker
     */
    public static String getCheckerStatus() {
        try {
            return autoChecker.getCheckerStatus();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}