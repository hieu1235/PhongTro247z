<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Đăng nhập - Phòng Trọ 247</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"/>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap"/>
        <style>
            /* Import Google Fonts */
            @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap');

            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                font-family: 'Poppins', sans-serif;
                display: flex;
                justify-content: center;
                align-items: center;
                min-height: 100vh;
                padding: 20px;
            }

            form {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                border-radius: 20px;
                padding: 40px;
                width: 100%;
                max-width: 400px;
                box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
                border: 1px solid rgba(255, 255, 255, 0.2);
            }

            h3 {
                color: #333;
                font-weight: 600;
                font-size: 28px;
                text-align: center;
                margin-bottom: 30px;
                text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }

            label {
                display: block;
                margin-bottom: 8px;
                color: #555;
                font-weight: 500;
                font-size: 14px;
            }

            input[type="text"],
            input[type="password"],
            input[type="email"] {
                width: 100%;
                padding: 15px;
                margin-bottom: 20px;
                border: 2px solid #e1e1e1;
                border-radius: 10px;
                font-size: 16px;
                font-family: 'Poppins', sans-serif;
                transition: all 0.3s ease;
                background: rgba(255, 255, 255, 0.9);
            }

            input[type="text"]:focus,
            input[type="password"]:focus,
            input[type="email"]:focus {
                outline: none;
                border-color: #667eea;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
                background: white;
            }

            .forgot-password {
                text-align: right;
                margin-bottom: 25px;
            }

            .forgot-password a {
                color: #667eea;
                text-decoration: none;
                font-size: 14px;
                font-weight: 500;
                transition: color 0.3s ease;
            }

            .forgot-password a:hover {
                color: #764ba2;
                text-decoration: underline;
            }

            button {
                width: 100%;
                padding: 15px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
                border-radius: 10px;
                font-size: 16px;
                font-weight: 600;
                font-family: 'Poppins', sans-serif;
                cursor: pointer;
                transition: all 0.3s ease;
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 8px;
            }

            button:hover {
                background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
            }

            button:active {
                transform: translateY(0);
            }

            .social {
                margin: 20px 0;
            }

            .social a {
                text-decoration: none;
            }

            .go {
                background: #db4437;
                color: white;
                padding: 12px;
                border-radius: 10px;
                text-align: center;
                font-weight: 500;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 10px;
            }

            .go:hover {
                background: #c23321;
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(219, 68, 55, 0.3);
            }

            h4 {
                text-align: center;
                color: #666;
                font-size: 14px;
                font-weight: 400;
                margin-top: 20px;
            }

            h4 a {
                color: #667eea;
                text-decoration: none;
                font-weight: 600;
                transition: color 0.3s ease;
            }

            h4 a:hover {
                color: #764ba2;
                text-decoration: underline;
            }

            /* Toast Message Styles */
            .toast-message {
                position: fixed;
                top: 20px;
                right: 20px;
                padding: 15px 20px;
                border-radius: 10px;
                color: white;
                font-weight: 500;
                z-index: 1000;
                display: flex;
                align-items: center;
                gap: 10px;
                min-width: 300px;
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
                animation: slideIn 0.3s ease;
            }

            .toast-message.success {
                background: linear-gradient(135deg, #28a745, #20c997);
            }

            .toast-message.error {
                background: linear-gradient(135deg, #dc3545, #e74c3c);
            }

            @keyframes slideIn {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }

            /* Responsive Design */
            @media (max-width: 480px) {
                body {
                    padding: 10px;
                }
                
                form {
                    padding: 30px 20px;
                }
                
                h3 {
                    font-size: 24px;
                    margin-bottom: 25px;
                }
                
                input[type="text"],
                input[type="password"],
                input[type="email"] {
                    padding: 12px;
                    font-size: 14px;
                }
                
                button {
                    padding: 12px;
                    font-size: 14px;
                }
                
                .toast-message {
                    right: 10px;
                    left: 10px;
                    min-width: auto;
                }
            }

            /* Loading state */
            button:disabled {
                opacity: 0.7;
                cursor: not-allowed;
                transform: none !important;
            }

            /* Input validation states */
            input.error {
                border-color: #dc3545;
                background-color: #ffeaea;
            }

            input.success {
                border-color: #28a745;
                background-color: #eafaf1;
            }

            /* Additional animations */
            form {
                animation: fadeInUp 0.6s ease;
            }

            @keyframes fadeInUp {
                from {
                    opacity: 0;
                    transform: translateY(30px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }
        </style>
    </head>
    <body>
        <c:if test="${not empty sessionScope.message}">
            <div id="toastMessage" class="toast-message ${sessionScope.messageType}">
                <c:choose>
                    <c:when test="${sessionScope.messageType == 'success'}">
                        <i class="fa fa-check-circle"></i>
                    </c:when>
                    <c:when test="${sessionScope.messageType == 'error'}">
                        <i class="fa fa-times-circle"></i>
                    </c:when>
                </c:choose>
                ${sessionScope.message}
            </div>

            <!-- Xóa message sau khi hiển thị -->
            <c:remove var="message" scope="session" />
            <c:remove var="messageType" scope="session" />
        </c:if>
        
        <form action="login" method="post">
            <h3>Đăng nhập hệ thống</h3>
            
            <label for="username">Tên đăng nhập</label>
            <input type="text" placeholder="Nhập tên đăng nhập" id="username" name="username" 
                   value="${param.username}" required>

            <label for="password">Mật khẩu</label>
            <input type="password" placeholder="Nhập mật khẩu" id="password" name="password" required> 

            <div class="forgot-password">
                <a href="${pageContext.request.contextPath}/forgotPassword.jsp">Quên mật khẩu?</a>
            </div>

            <button type="submit" id="login">
                <i class="fa fa-sign-in-alt"></i> Đăng nhập
            </button>
            
            <div class="social">
                <a href="https://accounts.google.com/o/oauth2/auth?scope=email%20profile%20openid&redirect_uri=${pageContext.request.contextPath}/register&response_type=code&client_id=370841450880-23fiie6auhj74f5f5lel16b2gujnt2ui.apps.googleusercontent.com&approval_prompt=force">
                    <div class="go"><i class="fab fa-google"></i> Đăng nhập với Google</div>
                </a>
            </div>
            
            <h4>Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register.jsp">Đăng ký ngay</a></h4>
        </form>

        <script src="${pageContext.request.contextPath}/js/toastMessage.js"></script>  
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script>
            var errorMsg = "${error != null ? error : ''}";
            var successMsg = "${success != null ? success : ''}";
            if (errorMsg && errorMsg.trim() !== "") {
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi',
                    text: errorMsg
                });
            } else if (successMsg && successMsg.trim() !== "") {
                Swal.fire({
                    icon: 'success',
                    title: 'Thành công',
                    text: successMsg
                });
            }
        </script>
    </body>
</html>