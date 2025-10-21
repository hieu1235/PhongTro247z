# PhongTro247z - Website Cho Thuê Phòng Trọ

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

Website cho thuê phòng trọ trực tuyến với đầy đủ tính năng quản lý, thanh toán và bảo mật.

## ✨ Tính năng

### 👤 Quản lý người dùng
- Đăng ký/Đăng nhập tài khoản
- Xác thực email
- Quên mật khẩu
- Đăng nhập Facebook OAuth

### 🏠 Quản lý phòng trọ
- Đăng tin cho thuê phòng
- Tìm kiếm và lọc phòng
- Xem chi tiết phòng
- Quản lý tin đăng của mình

### 💳 Thanh toán
- Tích hợp PayOS
- Thanh toán an toàn
- Xác minh webhook

### 🔒 Bảo mật
- Mã hóa mật khẩu (BCrypt)
- Environment variables
- User database riêng biệt
- Webhook signature verification

## 🛠️ Tech Stack

- **Backend**: Java EE (Servlet, JSP)
- **Database**: SQL Server
- **Connection Pool**: HikariCP
- **Payment**: PayOS API
- **Email**: Jakarta Mail
- **Authentication**: Facebook OAuth
- **Scheduler**: Quartz
- **Build**: Apache Ant

## 🚀 Cài đặt và Chạy

### Yêu cầu hệ thống
- JDK 11+
- SQL Server 2019+
- Apache Tomcat 10+
- NetBeans IDE (khuyến nghị)

### 1. Clone repository
```bash
git clone https://github.com/hieu1235/PhongTro247z.git
cd PhongTro247z
```

### 2. Cấu hình Database
```sql
-- Tạo database
CREATE DATABASE phongtro247_db;

-- Chạy script tạo user an toàn
-- File: setup_secure_db_user.sql
```

### 3. Cấu hình Environment
```bash
# Copy file template
cp .env.example .env

# Chỉnh sửa .env với thông tin thực tế
# DB_USERNAME=phongtro247_user
# DB_PASSWORD=your_password
# PAYOS_CLIENT_ID=your_client_id
# etc.
```

### 4. Chạy trong NetBeans
1. Mở NetBeans IDE
2. File > Open Project > Chọn thư mục PhongTro247z
3. Clean and Build (F11)
4. Run (F6)

Ứng dụng sẽ chạy tại: `http://localhost:8080/PhongTro247z`

## 🔐 Bảo mật

Dự án đã implement các biện pháp bảo mật quan trọng:

- ✅ **Environment Variables**: Credentials không hard-code
- ✅ **Secure DB User**: User riêng biệt với quyền hạn chế
- ✅ **Password Hashing**: BCrypt cho mật khẩu
- ✅ **Webhook Verification**: Xác minh tính toàn vẹn thanh toán
- ✅ **HTTPS Ready**: Sẵn sàng cho production

Xem chi tiết: [SECURITY_SETUP_GUIDE.md](SECURITY_SETUP_GUIDE.md)

## 📁 Cấu trúc dự án

```
PhongTro247z/
├── src/java/
│   ├── Controller/          # Servlet controllers
│   ├── Dal/                 # Data Access Layer
│   ├── DBcontext/           # Database connection
│   ├── Model/               # Entity models
│   ├── Service/             # Business logic
│   ├── Utility/             # Helper utilities
│   └── config/              # Configuration classes
├── web/
│   ├── *.jsp                # JSP pages
│   ├── css/                 # Stylesheets
│   └── WEB-INF/             # Web configuration
├── lib/                     # Dependencies
├── .env.example             # Environment template
├── setup_secure_db_user.sql # DB setup script
└── build.xml               # Ant build file
```

## 🔧 API Endpoints

### Authentication
- `POST /PhongTro247z/login` - Đăng nhập
- `POST /PhongTro247z/register` - Đăng ký
- `POST /PhongTro247z/forgot-password` - Quên mật khẩu

### Posts
- `GET /PhongTro247z/` - Trang chủ
- `GET /PhongTro247z/post-detail?id=123` - Chi tiết phòng
- `POST /PhongTro247z/create-post` - Đăng tin mới

### Payment
- `POST /PhongTro247z/payment` - Tạo thanh toán
- `POST /PhongTro247z/webhook` - Webhook PayOS

## 📧 Liên hệ

- **Email**: phongtro247z@gmail.com
- **GitHub**: [hieu1235](https://github.com/hieu1235)

## 📄 License

Dự án này sử dụng giấy phép MIT. Xem file [LICENSE](LICENSE) để biết thêm chi tiết.

---

**PhongTro247z Team** - Nơi tìm phòng trọ đáng tin cậy! 🏠✨