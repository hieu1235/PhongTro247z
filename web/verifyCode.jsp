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

            input[type="text"] {
                width: 100%;
                padding: 12px;
                border: 2px solid #ddd;
                border-radius: 8px;
                font-size: 16px;
                margin-bottom: 20px;
                transition: border-color 0.3s ease;
                text-align: center;
                font-weight: 600;
                letter-spacing: 2px;
            }

            input[type="text"]:focus {
                outline: none;
                border-color: #667eea;
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
                transition: transform 0.2s ease;
                margin-bottom: 15px;
            }

            button:hover {
                transform: translateY(-2px);
            }

            button:disabled {
                background: #ccc;
                cursor: not-allowed;
                transform: none;
            }

            #countdown {
                text-align: center;
                font-size: 16px;
                color: #00ca92;
                margin-bottom: 10px;
            }

            h4 {
                text-align: center;
            }

            h4 a {
                color: #667eea;
                text-decoration: none;
                font-weight: 500;
            }

            h4 a:hover {
                text-decoration: underline;
            }
        </style>
    </head>
    <body>
        <form action="verifyCode" method="post">
            <h3>Nhập mã xác nhận</h3>
            <label for="code">Mã xác nhận</label>
            <input type="text" placeholder="Nhập mã 6 số" id="code" name="code" required maxlength="6">
            <input type="hidden" name="email" value="${email}">
            
            <button type="submit" id="verifyBtn">Xác nhận</button>
            <p id="countdown"></p>
            <h4><a href="forgotPassword.jsp">Gửi lại mã</a></h4>
        </form>
    
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

            let expiryTimestamp = ${sessionScope.resetExpiry};
            
            function updateCountdown() {
                const now = new Date().getTime();
                const distance = expiryTimestamp - now;
                
                if (distance <= 0) {
                    document.getElementById("countdown").innerHTML = "Mã đã hết hạn. Vui lòng gửi lại.";
                    document.getElementById("countdown").style.color = "red";
                    document.getElementById("verifyBtn").disabled = true;
                    return;
                }
                
                const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((distance % (1000 * 60)) / 1000);
                document.getElementById("countdown").innerHTML = "Mã sẽ hết hạn sau: " + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
            }
            
            updateCountdown();
            setInterval(updateCountdown, 1000);

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