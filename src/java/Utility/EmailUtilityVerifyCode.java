package Utility;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Date;
import java.util.Properties;

/**
 * Utility gửi email mã xác nhận (verify code).
 *
 * - Lấy credentials từ environment variables nếu có.
 * - Kiểm tra đầu vào, thiết lập charset/subject, set sent date.
 * - Throws MessagingException để caller xử lý hoặc báo lỗi.
 *
 * ENV recommended:
 * - PHONGTRO_EMAIL_FROM
 * - PHONGTRO_EMAIL_APP_PASSWORD
 */
public final class EmailUtilityVerifyCode {

    private EmailUtilityVerifyCode() {
        // utility
    }

    // Fallback (DEV only). Không commit thật credentials vào repo.
    private static final String FALLBACK_FROM = "phongtro247z@gmail.com";
    private static final String FALLBACK_APP_PASSWORD = "rkil tmgp zhox zpdw"; // keep empty in repo
    private static final String COMPANY_NAME = "PhongTro247";

    private static String getEnvOrFallback(String key, String fallback) {
        String v = System.getenv(key);
        return (v != null && !v.isBlank()) ? v : fallback;
    }

    private static Session createSession(String username, String password) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        // Optional: props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        final String user = username;
        final String pass = password;
        return Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });
    }

    /**
     * Gửi email HTML chứa mã xác nhận.
     *
     * @param toEmail     địa chỉ nhận
     * @param subject     tiêu đề
     * @param messageText nội dung mã (ví dụ mã xác nhận)
     * @throws MessagingException nếu không gửi được
     */
    public static void sendEmail(String toEmail, String subject, String messageText) throws MessagingException {
        if (toEmail == null || toEmail.isBlank()) {
            throw new MessagingException("Recipient email is null or blank");
        }
        // prefer env variables
        String from = getEnvOrFallback("PHONGTRO_EMAIL_FROM", FALLBACK_FROM);
        String appPass = getEnvOrFallback("PHONGTRO_EMAIL_APP_PASSWORD", FALLBACK_APP_PASSWORD);
        if (appPass == null || appPass.isBlank()) {
            // It's ok to allow empty fallback in dev, but in prod you should ensure env var exists.
            // Here we still attempt to create session; sending will fail if credentials invalid.
        }

        Session session = createSession(from, appPass);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject != null ? subject : (COMPANY_NAME + " - Xác nhận"), "UTF-8");
        message.setSentDate(new Date());

        String htmlContent = "<html>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "<p>Chào bạn,</p>"
                + "<p>Mã xác nhận của bạn là: "
                + "<strong style='color:blue; font-size:16px;'>" + escapeHtml(messageText) + "</strong></p>"
                + "<p style='color:red;'>Mã có hiệu lực trong <b>5 phút</b>. Vui lòng không chia sẻ mã này với người khác!</p>"
                + "<br>"
                + "<p>Trân trọng,</p>"
                + "<p><strong>" + COMPANY_NAME + "</strong></p>"
                + "</body>"
                + "</html>";

        message.setContent(htmlContent, "text/html; charset=UTF-8");

        // Send (synchronous). Consider running this in a background thread for better UX.
        Transport.send(message);
    }

    // Very small helper to avoid trivial HTML injection for codes (codes usually safe)
    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#x27;");
    }
}