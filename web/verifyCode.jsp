<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>PhongTro247 - Xác nhận mã</title>
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
                display:flex;
                align-items:center;
                justify-content:center;
                padding:20px;
            }
            form {
                background:white;
                padding:40px;
                border-radius:10px;
                box-shadow:0 15px 35px rgba(0,0,0,0.1);
                width:100%;
                max-width:400px;
            }
            h3 {
                text-align:center;
                color:#333;
                margin-bottom:30px;
                font-size:24px;
                font-weight:600;
            }
            label {
                display:block;
                margin-bottom:8px;
                color:#555;
                font-weight:500;
            }
            input[type="text"] {
                width:100%;
                padding:12px;
                border:2px solid #ddd;
                border-radius:8px;
                font-size:16px;
                margin-bottom:20px;
                transition:border-color .3s;
                text-align:center;
                font-weight:600;
                letter-spacing:2px;
            }
            input[type="text"]:focus {
                outline:none;
                border-color:#667eea;
            }
            button {
                width:100%;
                padding:12px;
                background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);
                color:white;
                border:none;
                border-radius:8px;
                font-size:16px;
                font-weight:600;
                cursor:pointer;
                transition:transform .2s;
                margin-bottom:15px;
            }
            button:hover {
                transform:translateY(-2px);
            }
            button:disabled {
                background:#ccc;
                cursor:not-allowed;
                transform:none;
            }
            #countdown {
                text-align:center;
                font-size:16px;
                color:#00ca92;
                margin-bottom:10px;
            }
            h4 {
                text-align:center;
            }
            h4 a {
                color:#667eea;
                text-decoration:none;
                font-weight:500;
            }
            h4 a:hover {
                text-decoration:underline;
            }
        </style>
    </head>
    <body>
        <form action="${pageContext.request.contextPath}/verifyCode" method="post">
            <h3>Nhập mã xác nhận</h3>

            <label for="code">Mã xác nhận</label>
            <input type="text" placeholder="Nhập mã 6 số" id="code" name="code" required maxlength="6" pattern="\d{6}" inputmode="numeric" autocomplete="one-time-code">

            <input type="hidden" name="email" value="<c:out value='${email}'/>">

            <button type="submit" id="verifyBtn">Xác nhận</button>
            <p id="countdown"></p>

            <h4>
                <!-- direct user to forgot-password flow (servlet) to request a new code -->
                <a href="${pageContext.request.contextPath}/forgotPassword">Gửi lại mã</a>
            </h4>
        </form>

        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

        <!-- Server-rendered alerts (safe, escaped) -->
        <c:if test="${not empty error}">
            <script>
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi',
                    text: '<c:out value="${error}" />'
                });
            </script>
        </c:if>

        <c:if test="${not empty success}">
            <script>
                Swal.fire({
                    icon: 'success',
                    title: 'Thành công',
                    text: '<c:out value="${success}" />'
                });
            </script>
        </c:if>

        <script>
            // Guarded expiry value from session; will only be set when attribute exists
            var expiryTimestamp = 0;
            <c:if test="${not empty sessionScope.resetExpiry}">
            expiryTimestamp = <c:out value='${sessionScope.resetExpiry}'/>;
            </c:if>

            function updateCountdown() {
                const countdownEl = document.getElementById("countdown");
                const verifyBtn = document.getElementById("verifyBtn");

                if (!expiryTimestamp || isNaN(expiryTimestamp)) {
                    countdownEl.innerHTML = "Không có thông tin phiên. Vui lòng yêu cầu mã mới.";
                    countdownEl.style.color = "red";
                    verifyBtn.disabled = true;
                    return;
                }

                const now = Date.now();
                const distance = expiryTimestamp - now;

                if (distance <= 0) {
                    countdownEl.innerHTML = "Mã đã hết hạn. Vui lòng gửi lại.";
                    countdownEl.style.color = "red";
                    verifyBtn.disabled = true;
                    return;
                }

                const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((distance % (1000 * 60)) / 1000);
                countdownEl.innerHTML = "Mã sẽ hết hạn sau: " + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
            }

            // initialize countdown only if expiryTimestamp set
            if (expiryTimestamp && !isNaN(expiryTimestamp)) {
                updateCountdown();
                setInterval(updateCountdown, 1000);
            } else {
                // if no expiry info, disable verify to avoid errors
                document.getElementById("verifyBtn").disabled = true;
            }

            // Client-side validation for code format
            document.querySelector('form').addEventListener('submit', function (e) {
                var codeInput = document.getElementById('code');
                var code = codeInput.value.trim();
                var codeRegex = /^\d{6}$/;

                if (!codeRegex.test(code)) {
                    e.preventDefault();
                    Swal.fire({
                        icon: 'error',
                        title: 'Lỗi',
                        text: 'Mã xác nhận phải có đúng 6 chữ số.'
                    });
                    codeInput.focus();
                    return false;
                }
            });
        </script>
    </body>
</html>