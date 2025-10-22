<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>PhongTro247 - Xác nhận email</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"/>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap"/>
        <style>
            * {
                margin:0;
                padding:0;
                box-sizing:border-box;
                font-family:'Poppins',sans-serif;
            }
            body {
                background: linear-gradient(135deg,#667eea 0%,#764ba2 100%);
                min-height:100vh;
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
                max-width:450px;
            }
            h3 {
                text-align:center;
                color:#333;
                margin-bottom:20px;
                font-size:24px;
                font-weight:600;
            }
            p {
                text-align:center;
                color:#666;
                margin-bottom:25px;
                line-height:1.5;
            }
            .form__group {
                margin-bottom:20px;
            }
            label {
                display:block;
                margin-bottom:8px;
                color:#555;
                font-weight:500;
            }
            .form__flex {
                display:flex;
                gap:10px;
                align-items:center;
            }
            input[type="text"] {
                flex:1;
                padding:12px;
                border:2px solid #ddd;
                border-radius:8px;
                font-size:16px;
                transition:border-color .3s;
            }
            input[type="text"]:focus {
                outline:none;
                border-color:#667eea;
            }
            .reset {
                padding:12px 20px;
                background:#f8f9fa;
                color:#667eea;
                border:2px solid #667eea;
                border-radius:8px;
                cursor:pointer;
                font-weight:500;
                transition:all .3s;
                opacity:0.5;
            }
            .reset[disabled] {
                opacity:0.5;
                pointer-events:none;
            }
            .reset.enabled {
                opacity:1;
                pointer-events:auto;
                background:#667eea;
                color:#fff;
            }
            button[type="submit"] {
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
            button[type="submit"]:hover {
                transform:translateY(-2px);
            }
            #countdown {
                text-align:center;
                color:#00ca92;
                font-size:15px;
                margin-top:10px;
            }
            .alert {
                padding:12px;
                margin-bottom:20px;
                border-radius:8px;
                text-align:center;
                font-weight:500;
            }
            .alert.error {
                background:#f8d7da;
                color:#721c24;
                border:1px solid #f5c6cb;
            }
            .alert.success {
                background:#d4edda;
                color:#155724;
                border:1px solid #c3e6cb;
            }
            .alert.info {
                background:#d1ecf1;
                color:#0c5460;
                border:1px solid #bee5eb;
            }
        </style>
    </head>
    <body>
        <!-- display server-side messages (escaped) -->
        <c:if test="${not empty error}">
            <div class="alert error">
                <i class="fa fa-times-circle"></i> <c:out value="${error}" />
            </div>
        </c:if>

        <c:if test="${not empty message}">
            <div class="alert info">
                <i class="fa fa-info-circle"></i> <c:out value="${message}" />
            </div>
        </c:if>

        <c:if test="${not empty success}">
            <div class="alert success">
                <i class="fa fa-check-circle"></i> <c:out value="${success}" />
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/verifyemail" method="post" id="verifyForm">
            <h3>Xác nhận email của bạn</h3>
            <p>Chúng tôi đã gửi mã xác nhận đến email <strong><c:out value="${email}" /></strong>. Vui lòng nhập mã để xác nhận tài khoản.</p>

            <div class="form__group">
                <label for="code">Mã xác nhận</label>
                <div class="form__flex verify">
                    <input type="text" name="code" id="code" placeholder="Nhập mã xác nhận" required maxlength="6" pattern="\d{6}" inputmode="numeric" autocomplete="one-time-code">
                    <button type="button" class="reset" id="reset" disabled>Gửi lại mã</button>
                </div>
            </div>

            <button type="submit">Xác nhận</button>
            <p id="countdown"></p>
        </form>

        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script>
            // Resend handler: set hidden action and submit
            document.getElementById("reset").addEventListener("click", function () {
                const form = document.getElementById("verifyForm");
                let actionInput = document.querySelector('input[name="action"]');
                if (!actionInput) {
                    actionInput = document.createElement("input");
                    actionInput.type = "hidden";
                    actionInput.name = "action";
                    form.appendChild(actionInput);
                }
                actionInput.value = "resend";
                form.submit();
            });

            function startCountdown(durationInSeconds, displayElementId) {
                const display = document.getElementById(displayElementId);
                let timer = durationInSeconds;
                const resetBtn = document.getElementById("reset");

                const interval = setInterval(() => {
                    const minutes = Math.floor(timer / 60);
                    const seconds = timer % 60;
                    const secondsFormat = seconds < 10 ? "0" + seconds : seconds;
                    display.textContent = "Mã sẽ hết hạn sau: " + minutes + ":" + secondsFormat;

                    if (--timer < 0) {
                        clearInterval(interval);
                        display.textContent = "Mã đã hết hạn!";
                        display.style.color = "red";
                        // enable resend button when code expired
                        resetBtn.disabled = false;
                        resetBtn.classList.add("enabled");
                    }
                }, 1000);
            }

            // Only embed expiry JS when session attribute exists
            <c:if test="${not empty sessionScope.regExpiryTime}">
            const expiryTime = <c:out value='${sessionScope.regExpiryTime}' />;
            const currentTime = Date.now();
            const remaining = Math.floor((expiryTime - currentTime) / 1000);
            if (remaining > 0) {
                startCountdown(remaining, "countdown");
            } else {
                document.getElementById("countdown").textContent = "Mã đã hết hạn!";
                document.getElementById("countdown").style.color = "red";
                const resetBtn = document.getElementById("reset");
                resetBtn.disabled = false;
                resetBtn.classList.add("enabled");
            }
            </c:if>

            // client-side validation
            document.getElementById('verifyForm').addEventListener('submit', function (e) {
                const code = document.getElementById('code').value.trim();
                if (!/^\d{6}$/.test(code)) {
                    e.preventDefault();
                    Swal.fire({
                        icon: 'error',
                        title: 'Lỗi',
                        text: 'Vui lòng nhập đúng mã xác nhận 6 chữ số.'
                    });
                }
            });
        </script>
    </body>
</html>