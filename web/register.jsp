<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng ký - Phòng Trọ 247</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"/>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap"/>

        <style>
            /* Consolidated and cleaned CSS for register page */
            * { margin: 0; padding: 0; box-sizing: border-box; }
            :root{
                --primary-color: #667eea;
                --secondary-color: #764ba2;
                --white: #ffffff;
                --gray-100: #f8f9fa;
                --danger-color: #dc3545;
                --success-color: #28a745;
                --border-radius: 10px;
            }

            body {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                font-family: 'Poppins', sans-serif;
                display: flex;
                justify-content: center;
                align-items: center;
                min-height: 100vh;
                padding: 20px;
                color: #343a40;
            }

            form {
                background: rgba(255,255,255,0.95);
                backdrop-filter: blur(8px);
                border-radius: 20px;
                padding: 40px;
                width: 100%;
                max-width: 450px;
                box-shadow: 0 15px 35px rgba(0,0,0,0.12);
                border: 1px solid rgba(255,255,255,0.2);
                animation: fadeInUp 0.6s ease;
            }

            .form__group { margin-bottom: 18px; }
            .form__flex { display: flex; flex-direction: column; }
            label { display:block; margin-bottom:8px; color:#555; font-weight:500; font-size:14px; }
            input[type="text"], input[type="password"], input[type="email"], select {
                width:100%; padding:12px 15px; border:2px solid #e1e1e1; border-radius:10px; font-size:16px;
                transition: all .25s ease; background: rgba(255,255,255,0.95);
            }
            input:focus { outline:none; border-color:var(--primary-color); box-shadow:0 0 0 3px rgba(102,126,234,0.08); }
            .form__error { color: var(--danger-color); font-size:12px; margin-top:6px; min-height:16px; display:block; }
            .form__success { background:#d4edda; color:#155724; border:1px solid #c3e6cb; border-radius:8px; padding:12px; margin:10px 0; text-align:center; }
            .password-hint { color:#666; font-size:12px; margin-top:6px; font-style:italic; }
            .form__checkbox { display:flex; gap:10px; align-items:flex-start; }
            .form__checkbox input[type="checkbox"] { margin-top:4px; }

            button {
                width:100%; padding:14px; background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
                color: var(--white); border:none; border-radius:10px; font-size:16px; font-weight:600; cursor:pointer;
                display:flex; gap:8px; align-items:center; justify-content:center;
            }
            button:disabled { opacity:.7; cursor:not-allowed; }
            button:hover:not(:disabled){ transform: translateY(-2px); box-shadow:0 8px 25px rgba(0,0,0,0.08); }

            .social { margin:18px 0; }
            .go { background:#db4437; color:white; padding:12px; border-radius:10px; display:flex; align-items:center; justify-content:center; gap:10px; }
            .go:hover { background:#c23321; transform:translateY(-2px); }

            h4 { text-align:center; color:#666; font-size:14px; font-weight:400; margin-top:20px; }
            h4 a { color: #7a7aff; text-decoration:none; font-weight:600; }
            h4 a:hover { text-decoration:underline; }

            /* Toast */
            .toast-message { position:fixed; top:20px; right:20px; padding:15px 20px; border-radius:10px; color:white; z-index:1000; display:flex; gap:10px; align-items:center; min-width:300px; box-shadow:0 5px 15px rgba(0,0,0,0.12); animation:slideIn .3s ease; }
            .toast-message.success { background: linear-gradient(135deg,#28a745,#20c997); }
            .toast-message.error { background: linear-gradient(135deg,#dc3545,#e74c3c); }

            @keyframes fadeInUp { from{opacity:0; transform:translateY(30px)} to{opacity:1; transform:none} }
            @keyframes slideIn { from{transform:translateX(100%); opacity:0} to{transform:none; opacity:1} }

            /* Responsive */
            @media (max-width:480px){
                form{ padding:30px 18px; max-width:100%; min-height:auto; }
                input, button{ font-size:14px; padding:12px; }
            }
        </style>
    </head>
    <body>
        <!-- Toast Message (session) -->
        <c:if test="${not empty sessionScope.message}">
            <div id="toastMessage" class="toast-message ${sessionScope.messageType}">
                <c:choose>
                    <c:when test="${sessionScope.messageType == 'success'}">
                        <i class="fa fa-check-circle"></i>
                    </c:when>
                    <c:when test="${sessionScope.messageType == 'error'}">
                        <i class="fa fa-times-circle"></i>
                    </c:when>
                    <c:otherwise>
                        <i class="fa fa-info-circle"></i>
                    </c:otherwise>
                </c:choose>
                <c:out value="${sessionScope.message}" />
            </div>

            <!-- Remove session messages after render -->
            <c:remove var="message" scope="session" />
            <c:remove var="messageType" scope="session" />
        </c:if>

        <!-- Registration Form -->
        <form action="${pageContext.request.contextPath}/register" method="post" id="form-register" style="min-height:650px;">
            <div style="text-align:center; font-size:28px; font-weight:600; margin-bottom:20px;">
                Đăng ký tài khoản
            </div>

            <!-- Full Name -->
            <div class="form__group">
                <div class="form__flex">
                    <label for="fullName">Họ và tên *</label>
                    <input type="text" placeholder="Nhập họ và tên" id="fullName" name="fullName"
                           value="<c:out value='${param.fullName != null ? param.fullName : fullName}'/>" required>
                </div>
                <p class="form__error fullName__error"></p>
            </div>

            <!-- Username -->
            <div class="form__group">
                <div class="form__flex">
                    <label for="username">Tên đăng nhập *</label>
                    <input type="text" placeholder="Nhập tên đăng nhập" id="username" name="username"
                           value="<c:out value='${param.username != null ? param.username : username}'/>" required>
                </div>
                <p class="form__error username__error"></p>
            </div>

            <!-- Email -->
            <div class="form__group">
                <div class="form__flex">
                    <label for="email">Email *</label>
                    <input type="email" placeholder="Nhập địa chỉ email" id="email" name="email"
                           value="<c:out value='${param.email != null ? param.email : email}'/>" required>
                </div>
                <p class="form__error email__error"></p>
            </div>

            <!-- Phone -->
            <div class="form__group">
                <div class="form__flex">
                    <label for="phone">Số điện thoại *</label>
                    <input type="text" placeholder="Nhập số điện thoại" id="phone" name="phone"
                           value="<c:out value='${param.phone != null ? param.phone : phone}'/>" required>
                </div>
                <p class="form__error phone__error"></p>
            </div>

            <!-- Password -->
            <div class="form__group">
                <div class="form__flex">
                    <label for="password">Mật khẩu *</label>
                    <input type="password" placeholder="Nhập mật khẩu" id="password" name="password" required>
                </div>
                <p class="form__error password__error"></p>
                <small class="password-hint">Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ và số</small>
            </div>

            <!-- Confirm Password -->
            <div class="form__group">
                <div class="form__flex">
                    <label for="confirmPassword">Xác nhận mật khẩu *</label>
                    <input type="password" placeholder="Nhập lại mật khẩu" id="confirmPassword" name="confirmPassword" required>
                </div>
                <p class="form__error confirmPassword__error"></p>
            </div>

            <!-- Terms and Conditions -->
            <div class="form__group">
                <div class="form__checkbox">
                    <input type="checkbox" id="terms" name="terms" required>
                    <label for="terms">
                        Tôi đồng ý với <a href="#" style="color: #7a7aff;">Điều khoản sử dụng</a>
                        và <a href="#" style="color: #7a7aff;">Chính sách bảo mật</a>
                    </label>
                </div>
                <p class="form__error terms__error"></p>
            </div>

            <!-- Error Message -->
            <c:if test="${not empty error}">
                <div class="form__error" style="margin:10px 0; text-align:center; background:#fee; padding:10px; border-radius:5px; color:#d00;">
                    <i class="fa fa-exclamation-triangle"></i>
                    <c:out value="${error}" />
                </div>
            </c:if>

            <!-- Success Message (request-scoped) -->
            <c:if test="${not empty message}">
                <div class="form__success" style="margin:10px 0; text-align:center;">
                    <i class="fa fa-check-circle"></i> <c:out value="${message}" />
                </div>
            </c:if>

            <!-- Submit Button -->
            <button type="submit" id="registerBtn">
                <i class="fa fa-user-plus"></i> Đăng ký
            </button>

            <!-- Social Login -->
            <div class="social">
                <a href="https://accounts.google.com/o/oauth2/auth?scope=email%20profile%20openid&redirect_uri=http://localhost:8080/PhongTro247/register&response_type=code&client_id=370841450880-23fiie6auhj74f5f5lel16b2gujnt2ui.apps.googleusercontent.com&approval_prompt=force">
                    <div class="go" style="width:100%">
                        <i class="fab fa-google"></i> Đăng ký với Google
                    </div>
                </a>
            </div>

            <!-- Login Link -->
            <h4>
                Đã có tài khoản?
                <a href="${pageContext.request.contextPath}/login.jsp">Đăng nhập ngay</a>
            </h4>
        </form>

        <!-- Toast message JS (client-side removal/auto-hide) -->
        <script>
            class ToastMessage {
                constructor() {
                    this.container = this.createContainer();
                    this.init();
                }
                createContainer() {
                    let container = document.querySelector('.toast-container');
                    if (!container) {
                        container = document.createElement('div');
                        container.className = 'toast-container';
                        document.body.appendChild(container);
                    }
                    return container;
                }
                init() {
                    const existingToasts = document.querySelectorAll('.toast-message');
                    existingToasts.forEach(toast => {
                        setTimeout(() => { this.hideToast(toast); }, 5000);
                    });
                }
                hideToast(toast) {
                    if (!toast) return;
                    toast.style.transform = 'translateX(100%)';
                    toast.style.opacity = '0';
                    setTimeout(() => { if (toast.parentElement) toast.parentElement.removeChild(toast); }, 300);
                }
            }
            // auto remove server-rendered toast after 5s
            setTimeout(() => {
                const t = document.getElementById('toastMessage');
                if (t) t.style.display = 'none';
            }, 5000);
        </script>

        <!-- Validation script (ensure path correct in your project) -->
        <script src="${pageContext.request.contextPath}/js/validationForm.js"></script>
        <script>
            // Initialize client-side validator (depends on your validationForm.js)
            if (typeof Validator !== 'undefined') {
                Validator({
                    form: '#form-register',
                    formGroupSelector: '.form__group',
                    errorSelector: '.form__error',
                    rules: [
                        Validator.isRequired('#fullName', 'Vui lòng nhập họ và tên'),
                        Validator.lengthRange('#fullName', 2, 100, 'Họ và tên phải từ 2 đến 100 ký tự'),
                        Validator.isRequired('#username', 'Vui lòng nhập tên đăng nhập'),
                        Validator.lengthRange('#username', 3, 50, 'Tên đăng nhập phải từ 3 đến 50 ký tự'),
                        Validator.isRequired('#email', 'Vui lòng nhập địa chỉ email'),
                        Validator.isEmail('#email'),
                        Validator.isRequired('#phone', 'Vui lòng nhập số điện thoại'),
                        Validator.isPhoneNumber('#phone', 'Số điện thoại không hợp lệ (10-11 chữ số)'),
                        Validator.isRequired('#password', 'Vui lòng nhập mật khẩu'),
                        Validator.minLength('#password', 6, 'Mật khẩu phải có ít nhất 6 ký tự'),
                        Validator.isPasswordComplex('#password', 'Mật khẩu phải chứa ít nhất 1 chữ cái và 1 chữ số'),
                        Validator.isRequired('#confirmPassword', 'Vui lòng xác nhận mật khẩu'),
                        Validator.isConfirmed('#confirmPassword', function () {
                            return document.querySelector('#form-register #password').value;
                        }, 'Mật khẩu xác nhận không khớp'),
                        Validator.isRequired('#terms', 'Vui lòng đồng ý với điều khoản sử dụng')
                    ],
                    onsubmit: function (formValue) {
                        const submitBtn = document.querySelector('#registerBtn');
                        submitBtn.disabled = true;
                        submitBtn.innerHTML = '<i class="fa fa-spinner fa-spin"></i> Đang xử lý...';
                        document.querySelector('#form-register').submit();
                    }
                });

                // Add isPasswordComplex if not present
                if (Validator.isPasswordComplex === undefined) {
                    Validator.isPasswordComplex = function (selector, message) {
                        return {
                            selector: selector,
                            test: function (value) {
                                const hasLetter = /[a-zA-Z]/.test(value);
                                const hasNumber = /[0-9]/.test(value);
                                return hasLetter && hasNumber ? undefined : message || 'Mật khẩu phải chứa ít nhất 1 chữ cái và 1 chữ số';
                            }
                        };
                    };
                }
            }
        </script>
    </body>
</html>