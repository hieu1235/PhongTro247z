    package Utility;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Email utility for PhongTro247
 * Supports registration verification, forgot password, and password reset emails
 */
public final class EmailUtility {

    // Fallback defaults for local dev - DO NOT commit real credentials
    private static final String FALLBACK_FROM_EMAIL = "phongtro247z@gmail.com";
    private static final String FALLBACK_APP_PASSWORD = "rkil tmgp zhox zpdw";

    private static final String FALLBACK_RESET_FROM_EMAIL = "phongtro247z@gmail.com";
    private static final String FALLBACK_RESET_APP_PASSWORD = "rkil tmgp zhox zpdw";

    private EmailUtility() {}

    private static String getEnvOrFallback(String envKey, String fallback) {
        String v = System.getenv(envKey);
        return (v != null && !v.isBlank()) ? v : fallback;
    }

    private static Session createSession(String username, String password) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.connectiontimeout", "10000"); // 10 seconds
        props.put("mail.smtp.timeout", "10000"); // 10 seconds

        final String user = username;
        final String pass = password;
        return Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });
    }

    private static void sendHtmlEmail(Session session, String fromEmail, String toEmail, String subject, String htmlContent) throws MessagingException {
        if (toEmail == null || toEmail.isBlank()) {
            throw new MessagingException("Recipient email is null or blank");
        }
        
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject, "UTF-8");
        message.setSentDate(new Date());
        message.setContent(htmlContent, "text/html; charset=UTF-8");
        
        Transport.send(message);
    }

    /**
     * Send registration verification email
     */
    public static void sendRegistrationVerificationEmail(String toEmail, String subject, String verificationCode) throws MessagingException {
        String from = getEnvOrFallback("PHONGTRO_EMAIL_FROM", FALLBACK_FROM_EMAIL);
        String pass = getEnvOrFallback("PHONGTRO_EMAIL_APP_PASSWORD", FALLBACK_APP_PASSWORD);

        Session session = createSession(from, pass);

        String htmlContent = "<html>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>"
                + "<h2 style='color: #2c3e50; text-align: center;'>PhongTro247 - Xác nhận đăng ký</h2>"
                + "<p>Chào bạn,</p>"
                + "<p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>PhongTro247</strong>!</p>"
                + "<p>Mã xác nhận của bạn là: <strong style='color:#007bff; font-size:18px; background-color:#f8f9fa; padding:10px; border-radius:5px;'>" + verificationCode + "</strong></p>"
                + "<p style='color:#dc3545; font-weight:bold;'>⚠️ Mã có hiệu lực trong <b>10 phút</b>. Vui lòng không chia sẻ mã này với người khác!</p>"
                + "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>"
                + "<p style='color:#6c757d; font-size:12px; text-align:center;'>Đây là email tự động, vui lòng không trả lời email này.</p>"
                + "<p style='text-align:center;'><strong>PhongTro247 Team</strong></p>"
                + "</div>"
                + "</body>"
                + "</html>";

        sendHtmlEmail(session, from, toEmail, subject, htmlContent);
    }

    /**
     * Send forgot password verification email - overloaded method without fullName
     */
    public static void sendForgotPasswordVerificationEmail(String toEmail, String subject, String verificationCode) throws MessagingException {
        sendForgotPasswordVerificationEmail(toEmail, subject, verificationCode, null);
    }

    /**
     * Send forgot password verification email with personalized greeting
     */
    public static void sendForgotPasswordVerificationEmail(String toEmail, String subject, String verificationCode, String fullName) throws MessagingException {
        String from = getEnvOrFallback("PHONGTRO_EMAIL_FROM", FALLBACK_FROM_EMAIL);
        String pass = getEnvOrFallback("PHONGTRO_EMAIL_APP_PASSWORD", FALLBACK_APP_PASSWORD);

        Session session = createSession(from, pass);

        // Personalize greeting if fullName is provided
        String greeting = (fullName != null && !fullName.trim().isEmpty()) 
            ? "Chào " + fullName.trim() + "," 
            : "Chào bạn,";

        String htmlContent = "<html>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>"
                + "<h2 style='color: #dc3545; text-align: center;'>PhongTro247 - Đặt lại mật khẩu</h2>"
                + "<p>" + greeting + "</p>"
                + "<p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản <strong>PhongTro247</strong>.</p>"
                + "<p>Mã xác nhận của bạn là: <strong style='color:#dc3545; font-size:18px; background-color:#f8f9fa; padding:10px; border-radius:5px;'>" + verificationCode + "</strong></p>"
                + "<p style='color:#dc3545; font-weight:bold;'>⚠️ Mã có hiệu lực trong <b>5 phút</b>. Vui lòng không chia sẻ mã này với người khác!</p>"
                + "<p style='color:#6c757d;'>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này và tài khoản của bạn sẽ vẫn an toàn.</p>"
                + "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>"
                + "<p style='color:#6c757d; font-size:12px; text-align:center;'>Đây là email tự động, vui lòng không trả lời email này.</p>"
                + "<p style='text-align:center;'><strong>PhongTro247 Team</strong></p>"
                + "</div>"
                + "</body>"
                + "</html>";

        sendHtmlEmail(session, from, toEmail, subject, htmlContent);
    }

    /**
     * Send new password after admin reset
     */
    public static void sendResetPasswordEmail(String toEmail, String subject, String newPassword) throws MessagingException {
        String from = getEnvOrFallback("PHONGTRO_RESET_EMAIL_FROM", FALLBACK_RESET_FROM_EMAIL);
        String pass = getEnvOrFallback("PHONGTRO_RESET_APP_PASSWORD", FALLBACK_RESET_APP_PASSWORD);

        Session session = createSession(from, pass);

        String htmlContent = "<html>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>"
                + "<h2 style='color: #28a745; text-align: center;'>PhongTro247 - Mật khẩu mới</h2>"
                + "<p>Xin chào,</p>"
                + "<p>Quản trị viên đã đặt lại mật khẩu cho tài khoản <strong>PhongTro247</strong> của bạn.</p>"
                + "<p>Mật khẩu mới của bạn là: <strong style='color:#28a745; font-size: 18px; background-color:#f8f9fa; padding:10px; border-radius:5px;'>" + newPassword + "</strong></p>"
                + "<p style='color: #dc3545; font-weight:bold;'>🔒 Vui lòng không chia sẻ mật khẩu này với người khác.</p>"
                + "<p style='color:#ffc107; font-weight:bold;'>⚡ Sau khi đăng nhập, hãy đổi mật khẩu ngay để đảm bảo an toàn cho tài khoản của bạn.</p>"
                + "<p>Để đổi mật khẩu: Đăng nhập → Tài khoản → Đổi mật khẩu</p>"
                + "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>"
                + "<p style='color:#6c757d; font-size:12px; text-align:center;'>Đây là email tự động, vui lòng không trả lời email này.</p>"
                + "<p style='text-align:center;'><strong>PhongTro247 Team</strong></p>"
                + "</div>"
                + "</body>"
                + "</html>";

        sendHtmlEmail(session, from, toEmail, subject, htmlContent);
    }

    /**
     * Backward-compatible simple send method
     */
    public static void sendEmail(String toEmail, String subject, String verificationCode) throws MessagingException {
        sendRegistrationVerificationEmail(toEmail, subject, verificationCode);
    }

    /**
     * Send welcome email after successful registration
     */
    public static void sendWelcomeEmail(String toEmail, String fullName) throws MessagingException {
        String subject = "Chào mừng đến với PhongTro247!";
        String from = getEnvOrFallback("PHONGTRO_EMAIL_FROM", FALLBACK_FROM_EMAIL);
        String pass = getEnvOrFallback("PHONGTRO_EMAIL_APP_PASSWORD", FALLBACK_APP_PASSWORD);

        Session session = createSession(from, pass);

        String greeting = (fullName != null && !fullName.trim().isEmpty()) 
            ? "Chào " + fullName.trim() + "," 
            : "Chào bạn,";

        String htmlContent = "<html>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>"
                + "<h2 style='color: #28a745; text-align: center;'>Chào mừng đến với PhongTro247!</h2>"
                + "<p>" + greeting + "</p>"
                + "<p>Chúc mừng bạn đã đăng ký thành công tài khoản tại <strong>PhongTro247</strong>!</p>"
                + "<p>Tại đây bạn có thể:</p>"
                + "<ul style='color: #495057;'>"
                + "<li>Đăng tin cho thuê phòng trọ</li>"
                + "<li>Quản lý các bài đăng của mình</li>"
                + "<li>Tìm kiếm phòng trọ phù hợp</li>"
                + "<li>Liên hệ trực tiếp với chủ phòng</li>"
                + "</ul>"
                + "<p style='text-align: center; margin: 30px 0;'>"
                + "<a href='#' style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 12px 30px; text-decoration: none; border-radius: 8px; font-weight: bold;'>Bắt đầu sử dụng</a>"
                + "</p>"
                + "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>"
                + "<p style='color:#6c757d; font-size:12px; text-align:center;'>Cảm ơn bạn đã tin tưởng PhongTro247!</p>"
                + "<p style='text-align:center;'><strong>PhongTro247 Team</strong></p>"
                + "</div>"
                + "</body>"
                + "</html>";

        sendHtmlEmail(session, from, toEmail, subject, htmlContent);
    }
}