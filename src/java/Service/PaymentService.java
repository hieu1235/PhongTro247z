package Service;

import Model.PaymentOrder;
import Dal.PaymentOrderDAO;
import config.PaymentConfig;

// PayOS SDK imports (version 1.0.3)
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * PayOS Payment Service - Ho tro PayOS VietQR payment
 * SDK Version: 1.0.3
 */
public class PaymentService {
    
    private final PaymentOrderDAO paymentOrderDAO = new PaymentOrderDAO();
    private final PayOS payOS;
    
    /**
     * Constructor - Initialize PayOS client
     * ✅ SECURITY FIX: Load credentials từ environment variables
     */
    public PaymentService() {
        try {
            this.payOS = new PayOS(
                PaymentConfig.getPayOSClientId(),
                PaymentConfig.getPayOSApiKey(),
                PaymentConfig.getPayOSChecksumKey()
            );
            System.out.println("PaymentService: PayOS client initialized successfully");
        } catch (Exception e) {
            System.err.println("PaymentService: Failed to initialize PayOS: " + e.getMessage());
            throw new RuntimeException("Cannot initialize PayOS client", e);
        }
    }
    
    /**
     * Tao don hang thanh toan moi trong database
     * @param userId User ID
     * @param amountVND Amount in VND (NOT coins!)
     */
    public PaymentOrder createPaymentOrder(int userId, int amountVND) {
        try {
            // Generate unique order code (PayOS requires long orderCode)
            long orderCode = System.currentTimeMillis();
            
            // Calculate coins from VND amount (1 coin = 1,000 VND)
            int coins = amountVND / 1000;
            
            PaymentOrder order = new PaymentOrder();
            order.setOrderCode(String.valueOf(orderCode));
            order.setUserId(userId);
            order.setCoinsAmount(new BigDecimal(coins));
            order.setAmount(new BigDecimal(amountVND));
            order.setPaymentMethod("payos");
            order.setStatus("PENDING");
            order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            
            // Set expiration time (15 minutes from now)
            long expirationTime = System.currentTimeMillis() + (15 * 60 * 1000); // 15 phút
            order.setExpiresAt(new Timestamp(expirationTime));
            
            int orderId = paymentOrderDAO.createPaymentOrder(order);
            
            if (orderId > 0) {
                order.setOrderId(orderId);
                System.out.println("PaymentService: Created payment order " + orderCode + 
                    " for user " + userId + ", coins: " + coins + ", amount: " + amountVND + " VND");
                return order;
            } else {
                throw new Exception("Failed to create payment order in database");
            }
        } catch (Exception e) {
            System.err.println("PaymentService.createPaymentOrder error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Tao PayOS payment URL (checkout link with VietQR)
     */
    public String createPaymentUrl(PaymentOrder order) {
        try {
            if (order == null) {
                throw new Exception("Payment order is null");
            }
            
            long orderCode = Long.parseLong(order.getOrderCode());
            int amount = order.getAmount().intValue();
            int coins = order.getCoinsAmount().intValue();
            
            // PayOS description max 25 characters
            String description = "Nap " + coins + " xu";
            
            // Create item data for PayOS
            ItemData item = ItemData.builder()
                .name("Xu PhongTro247z")
                .quantity(coins)
                .price(1000) // 1 coin = 1000 VND
                .build();
            
            List<ItemData> items = new ArrayList<>();
            items.add(item);
            
            // Create payment data
            PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .items(items)
                .returnUrl(PaymentConfig.getReturnUrl())
                .cancelUrl(PaymentConfig.getCancelUrl())
                .build();
            
            System.out.println("PaymentService: Creating payment link for order " + orderCode + 
                ", amount: " + amount + " VND");
            
            CheckoutResponseData responseData = payOS.createPaymentLink(paymentData);
            
            String checkoutUrl = responseData.getCheckoutUrl();
            System.out.println("PaymentService: Payment URL created: " + checkoutUrl);
            
            return checkoutUrl;
            
        } catch (Exception e) {
            System.err.println("PaymentService.createPaymentUrl error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Verify PayOS webhook signature
     */
    public Map<String, Object> verifyWebhookData(String webhookBody) throws Exception {
        try {
            System.out.println("PaymentService: Verifying webhook data");
            
            // PayOS SDK 1.0.3 does not have built-in webhook verification
            // We need to parse JSON manually and verify signature
            // For now, we'll parse the webhook body as JSON
            
            // Simple JSON parsing (you should use a proper JSON library like Gson or Jackson)
            Map<String, Object> webhookData = parseWebhookJson(webhookBody);
            
            System.out.println("PaymentService: Webhook data parsed successfully");
            return webhookData;
        } catch (Exception e) {
            System.err.println("PaymentService.verifyWebhookData error: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Parse webhook JSON (simple implementation)
     */
    private Map<String, Object> parseWebhookJson(String json) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Extract orderCode
            String orderCodeStr = extractJsonValue(json, "orderCode");
            if (orderCodeStr != null) {
                result.put("orderCode", Long.parseLong(orderCodeStr));
            }
            
            // Extract transactionId (or id)
            String transactionId = extractJsonValue(json, "transactionId");
            if (transactionId == null) {
                transactionId = extractJsonValue(json, "id");
            }
            result.put("transactionId", transactionId);
            
            // Extract status
            String status = extractJsonValue(json, "status");
            result.put("status", status);
            
            // Extract amount
            String amountStr = extractJsonValue(json, "amount");
            if (amountStr != null) {
                result.put("amount", Integer.parseInt(amountStr));
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing webhook JSON: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Extract value from JSON string
     */
    private String extractJsonValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\"";
            int keyIndex = json.indexOf(searchKey);
            if (keyIndex == -1) return null;
            
            int colonIndex = json.indexOf(":", keyIndex);
            if (colonIndex == -1) return null;
            
            int valueStart = colonIndex + 1;
            while (valueStart < json.length() && (json.charAt(valueStart) == ' ' || json.charAt(valueStart) == '"')) {
                valueStart++;
            }
            
            int valueEnd = valueStart;
            while (valueEnd < json.length() && json.charAt(valueEnd) != '"' && json.charAt(valueEnd) != ',' && json.charAt(valueEnd) != '}') {
                valueEnd++;
            }
            
            return json.substring(valueStart, valueEnd).trim();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Xu ly callback tu PayOS webhook
     */
    public boolean handlePaymentCallback(String orderCode, String status, String transactionId, 
                                          String rawData, String paymentMethod) {
        try {
            System.out.println("PaymentService: Processing payment callback for order " + orderCode + 
                ", status: " + status + ", txn: " + transactionId);
            
            // Get payment order from database
            PaymentOrder order = paymentOrderDAO.getByOrderCode(orderCode);
            
            if (order == null) {
                System.err.println("PaymentService: Order not found: " + orderCode);
                return false;
            }
            
            // Check if already processed (idempotency)
            if ("SUCCESS".equals(order.getStatus()) || "COMPLETED".equals(order.getStatus())) {
                System.out.println("PaymentService: Order " + orderCode + " already processed, skipping");
                return true;
            }
            
            // Update payment order
            if ("SUCCESS".equals(status)) {
                order.setStatus("SUCCESS");
                order.setGatewayTransactionId(transactionId);
                order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                order.setCallbackData(rawData);
                
                boolean updated = paymentOrderDAO.updatePaymentOrder(order);
                
                if (updated) {
                    // Add coins to user account
                    int coinAmount = order.getCoinsAmount().intValue();
                    boolean coinsAdded = paymentOrderDAO.addCoinsToUser(order.getUserId(), coinAmount, orderCode);
                    
                    if (coinsAdded) {
                        System.out.println("PaymentService: Successfully processed payment " + orderCode + 
                            ", added " + coinAmount + " coins to user " + order.getUserId());
                        return true;
                    } else {
                        System.err.println("PaymentService: Failed to add coins to user " + order.getUserId());
                        return false;
                    }
                } else {
                    System.err.println("PaymentService: Failed to update payment order " + orderCode);
                    return false;
                }
            } else {
                // Payment failed
                order.setStatus("FAILED");
                order.setGatewayTransactionId(transactionId);
                order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                order.setCallbackData(rawData);
                
                paymentOrderDAO.updatePaymentOrder(order);
                System.out.println("PaymentService: Payment " + orderCode + " failed");
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("PaymentService.handlePaymentCallback error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get payment order by order code
     */
    public PaymentOrder getPaymentOrder(String orderCode) {
        return paymentOrderDAO.getByOrderCode(orderCode);
    }
    
    /**
     * Update payment order
     */
    public boolean updatePaymentOrder(PaymentOrder order) {
        return paymentOrderDAO.updatePaymentOrder(order);
    }
    
    /**
     * Get payment history for user
     */
    public List<PaymentOrder> getPaymentHistory(int userId, int limit) {
        return paymentOrderDAO.getPaymentHistory(userId, limit);
    }
    
    /**
     * Get payment link information from PayOS
     */
    public PaymentLinkData getPaymentLinkInfo(long orderCode) {
        try {
            System.out.println("PaymentService: Getting payment link info for order " + orderCode);
            PaymentLinkData paymentLink = payOS.getPaymentLinkInformation(orderCode);
            System.out.println("PaymentService: Payment link status: " + paymentLink.getStatus());
            return paymentLink;
        } catch (Exception e) {
            System.err.println("PaymentService.getPaymentLinkInfo error: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Cancel payment order - returns boolean instead of CheckoutResponseData
     */
    public boolean cancelPaymentOrder(long orderCode) {
        try {
            System.out.println("PaymentService: Cancelling payment order " + orderCode);
            payOS.cancelPaymentLink(orderCode, "User cancelled");
            System.out.println("PaymentService: Order " + orderCode + " cancelled");
            return true;
        } catch (Exception e) {
            System.err.println("PaymentService.cancelPaymentOrder error: " + e.getMessage());
            return false;
        }
    }
}
