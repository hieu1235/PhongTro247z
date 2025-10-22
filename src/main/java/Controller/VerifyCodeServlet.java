package Controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet để xác thực mã reset password (forgot password flow)
 */
@WebServlet(name = "VerifyCodeServlet", urlPatterns = {"/verifyCode"})
public class VerifyCodeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("resetEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/forgotPassword");
            return;
        }

        String email = (String) session.getAttribute("resetEmail");
        request.setAttribute("email", email);
        request.getRequestDispatcher("verifyCode.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String code = request.getParameter("code");

        HttpSession session = request.getSession(false); // do not create session here
        if (session == null) {
            // session hết hạn hoặc không tồn tại
            response.sendRedirect(request.getContextPath() + "/forgotPassword");
            return;
        }

        String sessionCode = (String) session.getAttribute("resetCode");
        Long expiryTime = (Long) session.getAttribute("resetExpiry");
        String sessionEmail = (String) session.getAttribute("resetEmail");

        long currentTime = System.currentTimeMillis();

        // Validation mã nhập
        if (code == null || code.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã xác nhận");
            request.setAttribute("email", (email != null ? email : sessionEmail));
            request.getRequestDispatcher("verifyCode.jsp").forward(request, response);
            return;
        }

        // Kiểm tra session data
        if (sessionCode == null || sessionEmail == null || expiryTime == null) {
            request.setAttribute("error", "Phiên làm việc đã hết hạn. Vui lòng yêu cầu mã xác nhận mới");
            request.setAttribute("email", (email != null ? email : sessionEmail));
            request.getRequestDispatcher("verifyCode.jsp").forward(request, response);
            return;
        }

        // Kiểm tra email khớp (an toàn hơn: compare both non-null)
        if (email == null || !sessionEmail.equals(email)) {
            request.setAttribute("error", "Email không hợp lệ");
            request.setAttribute("email", sessionEmail);
            request.getRequestDispatcher("verifyCode.jsp").forward(request, response);
            return;
        }

        // Kiểm tra thời gian hết hạn
        if (currentTime > expiryTime) {
            request.setAttribute("error", "Mã xác nhận đã hết hạn. Vui lòng yêu cầu mã mới");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyCode.jsp").forward(request, response);
            return;
        }

        // Kiểm tra mã code
        if (!sessionCode.equals(code.trim())) {
            request.setAttribute("error", "Mã xác nhận không đúng");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyCode.jsp").forward(request, response);
            return;
        }

        // Mã đúng: xóa mã và expiry để tránh reuse, giữ lại resetEmail để chuyển sang resetPassword
        session.removeAttribute("resetCode");
        session.removeAttribute("resetExpiry");
        // Optionally set a flag allowing reset step to proceed
        session.setAttribute("canResetPassword", Boolean.TRUE);

        response.sendRedirect(request.getContextPath() + "/resetPassword");
    }

}