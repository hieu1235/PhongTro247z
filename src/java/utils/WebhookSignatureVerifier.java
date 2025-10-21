package utils;

import config.PaymentConfig;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * Utility class để verify webhook signature từ PayOS
 * 
 * ✅ SECURITY FIX: Implement webhook signature verification
 * để ngăn chặn fake webhook requests
 */
public class WebhookSignatureVerifier {
    
    private static final Logger logger = Logger.getLogger(WebhookSignatureVerifier.class.getName());
    private static final String HMAC_SHA256 = "HmacSHA256";
    
    /**
     * Verify PayOS webhook signature
     * 
     * @param payload Request body (JSON string)
     * @param signature Signature từ header X-PayOS-Signature
     * @return true nếu signature hợp lệ, false nếu không
     */
    public static boolean verifyPayOSWebhook(String payload, String signature) {
        if (payload == null || payload.isEmpty()) {
            logger.warning("Webhook payload is null or empty");
            return false;
        }
        
        if (signature == null || signature.isEmpty()) {
            logger.warning("Webhook signature is null or empty");
            return false;
        }
        
        try {
            // Compute HMAC-SHA256 signature
            String computedSignature = computeHmacSha256(
                payload, 
                PaymentConfig.getPayOSChecksumKey()
            );
            
            // Compare signatures using constant-time comparison
            // để ngăn chặn timing attacks
            boolean isValid = MessageDigest.isEqual(
                computedSignature.getBytes(StandardCharsets.UTF_8),
                signature.getBytes(StandardCharsets.UTF_8)
            );
            
            if (!isValid) {
                logger.warning("Webhook signature verification failed. " +
                    "Expected: " + computedSignature + ", Got: " + signature);
            }
            
            return isValid;
            
        } catch (Exception e) {
            logger.severe("Error verifying webhook signature: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Compute HMAC-SHA256 signature
     * 
     * @param data Data to sign
     * @param key Secret key
     * @return Hex string của signature
     */
    private static String computeHmacSha256(String data, String key) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        
        Mac mac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
            key.getBytes(StandardCharsets.UTF_8), 
            HMAC_SHA256
        );
        mac.init(secretKeySpec);
        
        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        // Convert to hex string
        return bytesToHex(hmacBytes);
    }
    
    /**
     * Convert byte array to hex string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    /**
     * Verify webhook signature với alternate formats
     * PayOS có thể dùng format khác nhau (base64, hex, etc)
     */
    public static boolean verifyPayOSWebhookFlexible(String payload, String signature, String format) {
        // TODO: Implement nếu PayOS dùng format khác
        // Hiện tại mặc định dùng hex format
        return verifyPayOSWebhook(payload, signature);
    }
}
