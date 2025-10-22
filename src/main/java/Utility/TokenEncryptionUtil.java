package Utility;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Utility class để mã hóa và giải mã Facebook Access Token
 * Sử dụng AES encryption để bảo vệ sensitive data
 */
public class TokenEncryptionUtil {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    
    // ✅ Secret key cho encryption - trong production nên lưu trong environment variable
    // Đây là key mẫu - trong thực tế nên generate key mới và lưu an toàn
    private static final String SECRET_KEY_STRING = "MySecretKey12345"; // 16 bytes for AES
    
    private static final SecretKey SECRET_KEY = new SecretKeySpec(SECRET_KEY_STRING.getBytes(), ALGORITHM);
    
    /**
     * Mã hóa access token
     * @param plainText Token gốc cần mã hóa
     * @return Token đã được mã hóa (Base64 encoded)
     */
    public static String encrypt(String plainText) {
        try {
            if (plainText == null || plainText.trim().isEmpty()) {
                return plainText;
            }
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
            
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encryptedBytes);
            
        } catch (Exception e) {
            System.out.println("ERROR: Failed to encrypt token: " + e.getMessage());
            e.printStackTrace();
            // Fallback: trả về original text nếu encryption fail
            return plainText;
        }
    }
    
    /**
     * Giải mã access token
     * @param encryptedText Token đã được mã hóa
     * @return Token gốc đã được giải mã
     */
    public static String decrypt(String encryptedText) {
        try {
            if (encryptedText == null || encryptedText.trim().isEmpty()) {
                return encryptedText;
            }
            
            // Kiểm tra xem có phải là token đã được mã hóa không
            // Nếu không phải Base64 valid thì có thể là token chưa mã hóa
            if (!isBase64(encryptedText)) {
                System.out.println("DEBUG: Token appears to be unencrypted, returning as-is");
                return encryptedText;
            }
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
            
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            
            return new String(decryptedBytes, "UTF-8");
            
        } catch (Exception e) {
            System.out.println("ERROR: Failed to decrypt token: " + e.getMessage());
            // Fallback: trả về original text nếu decryption fail (có thể là token cũ chưa encrypt)
            return encryptedText;
        }
    }
    
    /**
     * Kiểm tra xem string có phải là Base64 valid không
     */
    private static boolean isBase64(String str) {
        try {
            if (str == null || str.length() == 0) {
                return false;
            }
            // Base64 string phải có độ dài chia hết cho 4
            if (str.length() % 4 != 0) {
                return false;
            }
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Generate một secret key mới (dùng để setup initial key)
     * Chỉ dùng 1 lần khi setup hệ thống
     */
    public static String generateNewSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(128); // AES 128-bit
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            System.out.println("ERROR: Failed to generate secret key: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Test method để kiểm tra encryption/decryption
     */
    public static void testEncryption() {
        String originalToken = "EAABwzLixnjYBO1234567890abcdef";
        System.out.println("Original token: " + originalToken);
        
        String encrypted = encrypt(originalToken);
        System.out.println("Encrypted token: " + encrypted);
        
        String decrypted = decrypt(encrypted);
        System.out.println("Decrypted token: " + decrypted);
        
        System.out.println("Test passed: " + originalToken.equals(decrypted));
    }
    
    /**
     * Kiểm tra xem token có đã được mã hóa chưa
     */
    public static boolean isEncrypted(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        // Facebook access token thường bắt đầu bằng "EAA" hoặc có format đặc biệt
        // Nếu không có pattern này và là Base64 valid thì có thể đã encrypted
        if (token.startsWith("EAA") || token.startsWith("EAAG") || token.startsWith("EAAB")) {
            return false; // Chưa encrypt
        }
        
        return isBase64(token); // Có thể đã encrypt
    }
}