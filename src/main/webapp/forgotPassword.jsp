<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>PhongTro247 - Quên mật khẩu</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"/>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap"/>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
                font-family: 'Poppins', sans-serif;
            }
            body {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 20px;
            }
            .form-container {
                background: white;
                padding: 40px;
                border-radius: 10px;
                box-shadow: 0 15px 35px rgba(0,0,0,0.1);
                width: 100%;
                max-width: 400px;
            }
            h3 {
                text-align: center;
                color: #333;
                margin-bottom: 30px;
                font-size: 24px;
                font-weight: 600;
            }
            label {
                display: block;
                margin-bottom: 8px;
                color: #555;
                font-weight: 500;
            }
            input[type="email"] {
                width: 100%;
                padding: 12px;
                border: 2px solid #ddd;
                border-radius: 8px;
                font-size: 16px;
                margin-bottom: 20px;
                transition: border-color .3s;
            }
            input[type="email"]:focus {
                outline: none;
                border-color: #667eea;
            }
            input[type="email"].error {
                border-color: #dc3545;
            }
            button {
                width: 100%;
                padding: 12px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
                border-radius: 8px;
                font-size: 16px;
                font-weight: 600;
                cursor: pointer;
                transition: transform .2s ease;
            }
            button:hover {
                transform: translateY(-2px);
            }
            button:disabled {
                opacity: 0.6;
                cursor: not-allowed;
                transform: none;
            }
            .back-link {
                text-align: center;
                margin-top: 20px;
            }
            .back-link a {
                color: #667eea;
                text-decoration: none;
                font-weight: 500;
            }
            .back-link a:hover {
                text-decoration: underline;
            }
            .alert {
                padding: 12px;
                margin-bottom: 16px;
                border-radius: 8px;
                font-weight: 500;
                text-align: center;
            }
            .alert.error {
                background: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }
            .alert.success {
                background: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }
        </style>
    </head>
    <body>
        <div class="form-container">
            <form action="${pageContext.request.contextPath}/forgotPassword" method="post" id="forgotForm" novalidate>
                <h3>Quên mật khẩu</h3>

                <!-- Display error messages -->
                <c:if test="${not empty error}">
                    <div class="alert error">
                        <i class="fa fa-times-circle"></i> 
                        <c:out value="${error}" />
                    </div>
                </c:if>
                
                <!-- Display success messages -->
                <c:if test="${not empty message}">
                    <div class="alert success">
                        <i class="fa fa-check-circle"></i> 
                        <c:out value="${message}" />
                    </div>
                </c:if>

                <!-- Session-based messages (for redirect scenarios) -->
                <c:if test="${not empty sessionScope.message}">
                    <div class="alert ${sessionScope.messageType != null ? sessionScope.messageType : 'success'}">
                        <i class="fa ${sessionScope.messageType == 'error' ? 'fa-times-circle' : 'fa-check-circle'}"></i>
                        <c:out value="${sessionScope.message}" />
                    </div>
                    <!-- Clean up session messages after displaying -->
                    <c:remove var="message" scope="session" />
                    <c:remove var="messageType" scope="session" />
                </c:if>

                <label for="email">Nhập email của bạn <span style="color: red;">*</span></label>
                <input type="email"
                       id="email"
                       name="email"
                       placeholder="example@email.com"
                       required
                       maxlength="100"
                       pattern="^[^\s@]+@[^\s@]+\.[^\s@]+$"
                       inputmode="email"
                       autocomplete="email"
                       value="<c:out value='${param.email != null ? param.email : (email != null ? email : "")}'/>" />

                <button type="submit" id="sendBtn">
                    <i class="fa fa-paper-plane"></i> Gửi mã xác nhận
                </button>

                <div class="back-link">
                    <a href="${pageContext.request.contextPath}/login.jsp">
                        <i class="fa fa-arrow-left"></i> Quay lại đăng nhập
                    </a>
                </div>
            </form>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script>
            // Client-side validation and UX improvements
            document.addEventListener('DOMContentLoaded', function() {
                const form = document.getElementById('forgotForm');
                const emailInput = document.getElementById('email');
                const sendBtn = document.getElementById('sendBtn');

                // Real-time email validation
                emailInput.addEventListener('input', function() {
                    const email = this.value.trim();
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    
                    if (email && !emailRegex.test(email)) {
                        this.classList.add('error');
                    } else {
                        this.classList.remove('error');
                    }
                });

                // Form submission handling
                form.addEventListener('submit', function(e) {
                    const email = emailInput.value.trim();
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

                    // Validate email
                    if (!email) {
                        e.preventDefault();
                        Swal.fire({
                            icon: 'error', 
                            title: 'Lỗi', 
                            text: 'Vui lòng nhập email',
                            confirmButtonText: 'OK'
                        });
                        emailInput.focus();
                        return false;
                    }

                    if (!emailRegex.test(email)) {
                        e.preventDefault();
                        Swal.fire({
                            icon: 'error', 
                            title: 'Lỗi', 
                            text: 'Định dạng email không hợp lệ',
                            confirmButtonText: 'OK'
                        });
                        emailInput.focus();
                        return false;
                    }

                    // Prevent double submission
                    sendBtn.disabled = true;
                    sendBtn.innerHTML = '<i class="fa fa-spinner fa-spin"></i> Đang gửi...';
                    
                    // Re-enable button after 3 seconds in case of network issues
                    setTimeout(function() {
                        sendBtn.disabled = false;
                        sendBtn.innerHTML = '<i class="fa fa-paper-plane"></i> Gửi mã xác nhận';
                    }, 3000);

                    return true;
                });

                // Auto-focus email input if no error messages
                if (!document.querySelector('.alert.error')) {
                    emailInput.focus();
                }
            });
        </script>
    </body>
</html>