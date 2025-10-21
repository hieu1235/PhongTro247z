package config;

/**
 * Configuration cho hệ thống thanh toán PayOS
 * Migrated từ MoMo sang PayOS
 * 
 * ✅ SECURITY FIX: API keys được load từ environment variables
 * thay vì hard-code trong code
 */
public class PaymentConfig {
    
    // ===== COMMON CONFIGURATION =====
    
    // Payment timeouts (minutes)
    public static final int PAYMENT_TIMEOUT_MINUTES = 15;
    
    // Exchange rate
    public static final int COINS_PER_VND = 1000; // 1 xu = 1000 VNĐ
    
    // Minimum and maximum amounts
    public static final long MIN_AMOUNT_VND = 10000;    // 10,000 VNĐ
    public static final long MAX_AMOUNT_VND = 10000000; // 10,000,000 VNĐ
    
    // ===== PAYOS CONFIGURATION =====
    
    /**
     * ✅ SECURE: Load PayOS credentials từ environment variables
     */
    public static String getPayOSClientId() {
        return EnvConfig.getPayOSClientId();
    }
    
    public static String getPayOSApiKey() {
        return EnvConfig.getPayOSApiKey();
    }
    
    public static String getPayOSChecksumKey() {
        return EnvConfig.getPayOSChecksumKey();
    }
    
    // PayOS API Endpoints
    public static final String PAYOS_API_BASE_URL = "https://api-merchant.payos.vn";
    public static final String PAYOS_CREATE_PAYMENT_LINK = PAYOS_API_BASE_URL + "/v2/payment-requests";
    public static final String PAYOS_QUERY_PAYMENT = PAYOS_API_BASE_URL + "/v2/payment-requests/{orderCode}";
    public static final String PAYOS_CANCEL_PAYMENT = PAYOS_API_BASE_URL + "/v2/payment-requests/{orderCode}/cancel";
    
    // PayOS Payment Methods
    public static final String PAYMENT_METHOD_PAYOS = "payos";
    
    /**
     * Check if we're in production mode
     */
    public static boolean isProduction() {
        return EnvConfig.isProduction();
    }
    
    /**
     * Get base URL from environment
     */
    public static String getBaseUrl() {
        return EnvConfig.getAppBaseUrl();
    }
    
    /**
     * Get return URL for payment
     */
    public static String getReturnUrl() {
        return getBaseUrl() + "/payment/return";
    }
    
    /**
     * Get cancel URL for payment
     */
    public static String getCancelUrl() {
        return getBaseUrl() + "/payment/cancel";
    }
    
    /**
     * Get webhook URL for PayOS callback
     */
    public static String getWebhookUrl() {
        return getBaseUrl() + "/payment/webhook";
    }
    
    /**
     * Validate payment amount
     */
    public static boolean isValidAmount(long amountVnd) {
        return amountVnd >= MIN_AMOUNT_VND && amountVnd <= MAX_AMOUNT_VND;
    }
    
    /**
     * Convert VNĐ to coins
     */
    public static double convertVndToCoins(long amountVnd) {
        return (double) amountVnd / COINS_PER_VND;
    }
    
    /**
     * Convert coins to VNĐ
     */
    public static long convertCoinsToVnd(double coins) {
        return Math.round(coins * COINS_PER_VND);
    }
}
