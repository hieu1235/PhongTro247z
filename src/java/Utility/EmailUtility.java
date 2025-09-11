/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utility;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtility {

  
    private static final String FROM_EMAIL = "phongtro247z@gmail.com";
    private static final String APP_PASSWORD = "rkil tmgp zhox zpdw";

    public static void sendEmail(String toEmail, String subject, String messageText) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        // Expiry thực tế trong RegisterServlet: 10 phút
        String htmlContent = "<html>"
                + "<body>"
                + "<p>Chào bạn,</p>"
                + "<p>Mã xác nhận của bạn là: <strong style='color:blue; font-size:16px;'>" + messageText + "</strong></p>"
                + "<p style='color:red;'>Mã có hiệu lực trong <b>10 phút</b>. Vui lòng không chia sẻ mã này với người khác!</p>"
                + "</body>"
                + "</html>";

        message.setContent(htmlContent, "text/html; charset=UTF-8");
        Transport.send(message);
    }

    public static void sendResetPasswordEmail(String toEmail, String subject, String messageText) throws Exception {

        final String fromEmail = "hung70919@gmail.com";
        final String password = "swav xtfi qiqe oeqz";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        String htmlContent = "<html>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "<p>Xin chào,</p>"
                + "<p>Admin đã đặt lại mật khẩu cho tài khoản của bạn.</p>"
                + "<p>Mật khẩu mới của bạn là: <strong style='color:blue; font-size: 16px;'>" + messageText + "</strong></p>"
                + "<p style='color: red;'>Vui lòng không chia sẻ mật khẩu này với người khác.</p>"
                + "<p>Sau khi đăng nhập, hãy đổi mật khẩu để đảm bảo an toàn cho tài khoản của bạn.</p>"
                + "<br>"
                + "<p>Trân trọng,</p>"
                + "<p><strong>Pradise Hotel</strong></p>"
                + "</body>"
                + "</html>";

        message.setContent(htmlContent, "text/html; charset=UTF-8");
        Transport.send(message);
    }
}