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

            form {
                background: white;
                padding: 40px;
                border-radius: 10px;
                box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
                width: 100%;
                max-width: 450px;
            }

            h3 {
                text-align: center;
                color: #333;
                margin-bottom: 20px;
                font-size: 24px;
                font-weight: 600;
            }

            p {
                text-align: center;
                color: #666;
                margin-bottom: 25px;
                line-height: 1.5;
            }

            .form__group {
                margin-bottom: 20px;
            }

            label {
                display: block;
                margin-bottom: 8px;
                color: #555;
                font-weight: 500;
            }

            .form__flex {
                display: flex;
                gap: 10px;
                align-items: center;
            }

            input[type="text"] {
                flex: 1;
                padding: 12px;
                border: 2px solid #ddd;
                border-radius: 8px;
                font-size: 16px;
                transition: border-color 0.3s ease;
            }

            input[type="text"]:focus {
                outline: none;
                border-color: #667eea;
            }

            .reset {
                padding: 12px 20px;
                background: #f8f9fa;
                color: #667eea;
                border: 2px solid #667eea;
                border-radius: 8px;
                cursor: pointer;
                font-weight: 500;
                transition: all 0.3s ease;
                opacity: 0.5;
                pointer-events: none;
            }

            .reset:hover {
                background: #667eea;
                color: white;
            }

            button[type="submit"] {
                width: 100%;
                padding: 12px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
                border-radius: 8px;
                font-size: 16px;
                font-weight: 600;
                cursor: pointer;
                transition: transform 0.2s ease;
                margin-bottom: 15px;
            }

            button[type="submit"]:hover {
                transform: translateY(-2px);
            }

            #countdown {
                text-align: center;
                color: #00ca92;
                font-size: 15px;
                margin-top: 10px;
            }

            .toast-message {
                position: fixed;
                top: 20px;
                right: 20px;
                padding: 15px 20px;
                border-radius: 8px;
                color: white;
                font-weight: 500;
                z-index: 1000;
                animation: slideIn 0.3s ease;
            }

            .toast-message.success {
                background: #28a745;
            }

            .toast-message.error {
                background: #dc3545;
            }

            @keyframes slideIn {
                from { transform: translateX(100%); }
                to { transform: translateX(0); }
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
            <c:remove var="message" scope="session" />
            <c:remove var="messageType" scope="session" />
        </c:if>

        <form action="verifyemail" method="post" id="verifyForm">
            <h3>Xác nhận email của bạn</h3>
            <p>Chúng tôi đã gửi mã xác nhận đến email của bạn. Vui lòng nhập mã để xác nhận tài khoản.</p>
            
            <div class="form__group">
                <label for="code">Mã xác nhận</label>
                <div class="form__flex verify">
                    <input type="text" name="code" id="code" placeholder="Nhập mã xác nhận" required>
                    <button type="button" class="reset" id="reset">Gửi lại mã</button>
                </div>
            </div>
            
            <button type="submit">Xác nhận</button>
            <p id="countdown"></p>
        </form>

        <script>
            // Toast message auto hide
            const toastMessage = document.getElementById("toastMessage");
            if (toastMessage) {
                setTimeout(() => {
                    toastMessage.style.animation = "slideOut 0.3s ease";
                    setTimeout(() => toastMessage.remove(), 300);
                }, 3000);
            }

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

            function startCountdown(durationInSeconds, displayElementId, resetElement) {
                const display = document.getElementById(displayElementId);
                const resetBtn = document.getElementById(resetElement);
                let timer = durationInSeconds;

                const interval = setInterval(() => {
                    const minutes = Math.floor(timer / 60);
                    const seconds = timer % 60;
                    const secondsFormat = seconds < 10 ? "0" + seconds : seconds;

                    display.textContent = "Mã sẽ hết hạn sau: " + minutes + ":" + secondsFormat;
                    
                    if (--timer < 0) {
                        clearInterval(interval);
                        document.getElementById("countdown").textContent = "Mã đã hết hạn!";
                        document.getElementById("countdown").style.color = "red";
                        document.getElementById("reset").style.opacity = "1";
                        document.getElementById("reset").style.pointerEvents = "auto";
                    }
                }, 1000);
            }

            const expiryTime = ${sessionScope.regExpiryTime};
            const currentTime = new Date().getTime();
            const remaining = Math.floor((expiryTime - currentTime) / 1000);

            if (remaining > 0) {
                startCountdown(remaining, "countdown", "reset");
            }
        </script>
    </body>
</html>