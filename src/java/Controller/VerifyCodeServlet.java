/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author thien
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
        
        HttpSession session = request.getSession();
        String sessionCode = (String) session.getAttribute("resetCode");
        Long expiryTime = (Long) session.getAttribute("resetExpiry");
        String sessionEmail = (String) session.getAttribute("resetEmail");
        
        long currentTime = System.currentTimeMillis();
        
        // Validation
        if (code == null || code.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã xác nhận");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyCode.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra session data
        if (sessionCode == null || sessionEmail == null || expiryTime == null) {
            request.setAttribute("error", "Phiên làm việc đã hết hạn. Vui lòng yêu cầu mã xác nhận mới");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyCode.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra email khớp
        if (!sessionEmail.equals(email)) {
            request.setAttribute("error", "Email không hợp lệ");
            request.setAttribute("email", email);
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
        
        // Mã code đúng - chuyển đến trang reset password
        session.setAttribute("resetEmail", email);
        response.sendRedirect(request.getContextPath() + "/resetPassword");
    }

}
