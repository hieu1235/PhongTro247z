<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>PhongTro247 - Đặt lại mật khẩu</title>
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
            .email-info {
                background: #f8f9fa;
                padding: 10px;
                border-radius: 8px;
                margin-bottom: 20px;
                text-align: center;
                color: #6c757d;
            }
            label {
                display: block;
                margin-bottom: 8px;
                color: #555;
                font-weight: 500;
            }
            input[type="password"] {
                width: 100%;
                padding: 12px;
                border: 2px solid #ddd;
                border-radius: 8px;
                font-size: 16px;
                margin-bottom: 20px;
                transition: border-color .3s;
            }
            input[type="password"]:focus {
                outline: none;
                border-color: #667eea;
            }
            input[type="password"].error {
                border-color: #dc3545;
            }
            .password-requirements {
                font-size: 12px;
                color: #6c757d;
                margin-bottom: 20px;
                padding: 10px;
                background: #f8f9fa;
                border-radius: 5px;
            }
            .password-requirements ul {
                margin-left: 20px;
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
            .password-strength {
                height: 5px;
                border-radius: 3px;
                margin-bottom: 10px;
                background: #e9ecef;
                overflow: hidden;
            }
            .strength-bar {
                height: 100%;
                transition: all 0.3s ease;
                border-radius: 3px;
            }
            .strength-weak { background: #dc3545; width: 33%; }
            .strength-medium { background: #ffc107; width: 66%; }
            .strength-strong { background: #28a745; width: 100%; }
        </style>
    </head>
    <body>
        <div class="form-container">
            <form action="${pageContext.request.contextPath}/resetPassword" method="post" id="resetForm" novalidate>
                <h3>Đặt lại mật khẩu</h3>

                <!-- Display email info -->
                <c:if test="${not empty email}">
                    <div class="email-info">
                        <i class="fa fa-envelope"></i> ${email}
                    </div>
                </c:if>

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

                <div class="password-requirements">
                    <strong>Yêu cầu mật khẩu:</strong>
                    <ul>
                        <li>Ít nhất 6 ký tự</li>
                        <li>Chứa ít nhất 1 chữ cái</li>
                        <li>Chứa ít nhất 1 chữ số</li>
                    </ul>
                </div>

                <label for="newPassword">Mật khẩu mới <span style="color: red;">*</span></label>
                <input type="password"
                       id="newPassword"
                       name="newPassword"
                       placeholder="Nhập mật khẩu mới"
                       required
                       minlength="6"
                       maxlength="100"
                       autocomplete="new-password" />
                
                <!-- Password strength indicator -->
                <div class="password-strength">
                    <div class="strength-bar" id="strengthBar"></div>
                </div>
                <div id="strengthText" style="font-size: 12px; margin-bottom: 15px; color: #6c757d;"></div>

                <label for="confirmPassword">Xác nhận mật khẩu <span style="color: red;">*</span></label>
                <input type="password"
                       id="confirmPassword"
                       name="confirmPassword"
                       placeholder="Nhập lại mật khẩu mới"
                       required
                       minlength="6"
                       maxlength="100"
                       autocomplete="new-password" />

                <button type="submit" id="resetBtn">
                    <i class="fa fa-key"></i> Đặt lại mật khẩu
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
            document.addEventListener('DOMContentLoaded', function() {
                const form = document.getElementById('resetForm');
                const newPasswordInput = document.getElementById('newPassword');
                const confirmPasswordInput = document.getElementById('confirmPassword');
                const resetBtn = document.getElementById('resetBtn');
                const strengthBar = document.getElementById('strengthBar');
                const strengthText = document.getElementById('strengthText');

                // Password strength checker
                function checkPasswordStrength(password) {
                    let strength = 0;
                    let strengthLabel = '';
                    
                    if (password.length >= 6) strength++;
                    if (/[a-zA-Z]/.test(password)) strength++;
                    if (/[0-9]/.test(password)) strength++;
                    if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) strength++;
                    if (password.length >= 8) strength++;

                    strengthBar.className = 'strength-bar';
                    
                    if (strength <= 2) {
                        strengthBar.classList.add('strength-weak');
                        strengthLabel = 'Yếu';
                    } else if (strength <= 3) {
                        strengthBar.classList.add('strength-medium');
                        strengthLabel = 'Trung bình';
                    } else {
                        strengthBar.classList.add('strength-strong');
                        strengthLabel = 'Mạnh';
                    }
                    
                    strengthText.textContent = password ? `Độ mạnh: ${strengthLabel}` : '';
                }

                // Real-time password strength checking
                newPasswordInput.addEventListener('input', function() {
                    const password = this.value;
                    checkPasswordStrength(password);
                    
                    // Reset error styling
                    this.classList.remove('error');
                    confirmPasswordInput.classList.remove('error');
                });

                // Real-time password confirmation checking
                confirmPasswordInput.addEventListener('input', function() {
                    const newPassword = newPasswordInput.value;
                    const confirmPassword = this.value;
                    
                    if (confirmPassword && newPassword !== confirmPassword) {
                        this.classList.add('error');
                    } else {
                        this.classList.remove('error');
                    }
                });

                // Form submission handling
                form.addEventListener('submit', function(e) {
                    const newPassword = newPasswordInput.value;
                    const confirmPassword = confirmPasswordInput.value;

                    // Validate new password
                    if (!newPassword) {
                        e.preventDefault();
                        Swal.fire({
                            icon: 'error',
                            title: 'Lỗi',
                            text: 'Vui lòng nhập mật khẩu mới',
                            confirmButtonText: 'OK'
                        });
                        newPasswordInput.focus();
                        return false;
                    }

                    if (newPassword.length < 6) {
                        e.preventDefault();
                        Swal.fire({
                            icon: 'error',
                            title: 'Lỗi',
                            text: 'Mật khẩu phải có ít nhất 6 ký tự',
                            confirmButtonText: 'OK'
                        });
                        newPasswordInput.focus();
                        return false;
                    }

                    // Check password pattern
                    if (!/[a-zA-Z]/.test(newPassword) || !/[0-9]/.test(newPassword)) {
                        e.preventDefault();
                        Swal.fire({
                            icon: 'error',
                            title: 'Lỗi',
                            text: 'Mật khẩu phải chứa ít nhất 1 chữ cái và 1 chữ số',
                            confirmButtonText: 'OK'
                        });
                        newPasswordInput.focus();
                        return false;
                    }

                    // Validate confirm password
                    if (!confirmPassword) {
                        e.preventDefault();
                        Swal.fire({
                            icon: 'error',
                            title: 'Lỗi',
                            text: 'Vui lòng xác nhận mật khẩu',
                            confirmButtonText: 'OK'
                        });
                        confirmPasswordInput.focus();
                        return false;
                    }

                    if (newPassword !== confirmPassword) {
                        e.preventDefault();
                        Swal.fire({
                            icon: 'error',
                            title: 'Lỗi',
                            text: 'Mật khẩu xác nhận không khớp',
                            confirmButtonText: 'OK'
                        });
                        confirmPasswordInput.focus();
                        return false;
                    }

                    // Prevent double submission
                    resetBtn.disabled = true;
                    resetBtn.innerHTML = '<i class="fa fa-spinner fa-spin"></i> Đang xử lý...';
                    
                    setTimeout(function() {
                        resetBtn.disabled = false;
                        resetBtn.innerHTML = '<i class="fa fa-key"></i> Đặt lại mật khẩu';
                    }, 3000);

                    return true;
                });

                // Auto-focus first password input
                newPasswordInput.focus();
            });
        </script>
    </body>
</html>