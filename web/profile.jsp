<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cài đặt tài khoản - PhongTro247</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    
    <style>
        :root{--sidebar-width:230px;--primary:#007bff;--success:#28a745;--warning:#ffc107;--danger:#dc3545;--light:#f8f9fa;--dark:#343a40;--gold:#f39c12;--radius:8px;--transition:all 0.3s ease}
        *{margin:0;padding:0;box-sizing:border-box}
        body{font-family:'Segoe UI',sans-serif;background:#f5f5f5}
        .wrapper{display:flex;min-height:100vh}
        
        /* Sidebar */
        .sidebar{width:var(--sidebar-width);background:linear-gradient(135deg,#667eea,#764ba2);color:white;position:fixed;height:100vh;left:0;top:0;transition:var(--transition);z-index:1000;overflow-y:auto}
        .sidebar.collapsed{margin-left:calc(-1 * var(--sidebar-width))}
        .sidebar-header{padding:20px;border-bottom:1px solid rgba(255,255,255,0.2)}
        .user-info{display:flex;align-items:center;gap:12px}
        .user-avatar{width:50px;height:50px;border-radius:50%;background:rgba(255,255,255,0.2);display:flex;align-items:center;justify-content:center;font-size:24px}
        .user-details h5{margin:0;font-size:16px;font-weight:600}
        .user-details small{opacity:0.8}
        
        .sidebar-menu{padding:20px 0}
        .sidebar-menu ul{list-style:none}
        .sidebar-menu a{color:rgba(255,255,255,0.9);padding:12px 20px;display:flex;align-items:center;gap:12px;text-decoration:none;transition:var(--transition)}
        .sidebar-menu a:hover,.sidebar-menu a.active{background:rgba(255,255,255,0.1);color:white;transform:translateX(5px)}
        .sidebar-menu i{width:20px;text-align:center}
        
        /* Main Content */
        .main-content{flex:1;margin-left:var(--sidebar-width);transition:var(--transition)}
        .main-content.expanded{margin-left:0}
        
        /* Header */
        .top-nav{background:white;padding:15px 20px;box-shadow:0 2px 4px rgba(0,0,0,0.1);display:flex;align-items:center;justify-content:space-between}
        .sidebar-toggle{background:white;border:1px solid #dee2e6;border-radius:4px;padding:8px 12px;cursor:pointer}
        .nav-right{display:flex;align-items:center;gap:15px}
        
        /* Content */
        .content{padding:20px}
        
        /* Alerts */
        .alert{padding:12px 16px;border-radius:var(--radius);margin-bottom:20px;display:flex;align-items:center;gap:10px;position:relative}
        .alert-success{background:#d4edda;color:#155724;border:1px solid #c3e6cb}
        .alert-error{background:#f8d7da;color:#721c24;border:1px solid #f5c6cb}
        .alert-close{position:absolute;right:10px;background:none;border:none;font-size:18px;cursor:pointer;opacity:0.5}
        .alert-close:hover{opacity:1}
        
        /* Profile Container */
        .profile-container{display:grid;grid-template-columns:300px 1fr;gap:20px;max-width:1200px}
        
        /* Profile Card */
        .profile-card{background:white;border-radius:var(--radius);box-shadow:0 2px 10px rgba(0,0,0,0.1);overflow:hidden}
        .profile-header{background:linear-gradient(135deg,#667eea,#764ba2);color:white;padding:30px 20px;text-align:center}
        .profile-avatar{width:80px;height:80px;border-radius:50%;background:rgba(255,255,255,0.2);display:flex;align-items:center;justify-content:center;font-size:2rem;margin:0 auto 15px;border:3px solid rgba(255,255,255,0.3)}
        .profile-name{font-size:1.4rem;font-weight:600;margin-bottom:5px}
        .profile-role{opacity:0.9}
        .pro-badge{display:inline-flex;align-items:center;gap:5px;background:rgba(255,255,255,0.2);padding:5px 10px;border-radius:15px;font-size:0.8rem;margin-top:10px}
        .pro-badge.active{background:#28a745}
        
        /* Profile Body */
        .profile-body{padding:20px}
        .profile-stat{display:flex;justify-content:space-between;padding:10px 0;border-bottom:1px solid #e9ecef}
        .profile-stat:last-child{border-bottom:none}
        .stat-label{color:#6c757d;font-size:0.9rem}
        .stat-value{font-weight:600}
        
        /* Forms */
        .form-section{background:white;border-radius:var(--radius);box-shadow:0 2px 10px rgba(0,0,0,0.1);margin-bottom:20px}
        .form-header{padding:20px;border-bottom:1px solid #e9ecef;display:flex;align-items:center;gap:10px}
        .form-header h3{margin:0;color:var(--dark)}
        .form-body{padding:20px}
        
        .form-group{margin-bottom:20px}
        .form-group label{display:block;margin-bottom:8px;font-weight:500;color:var(--dark)}
        .form-group input{width:100%;padding:12px;border:1px solid #ddd;border-radius:var(--radius);font-size:14px;transition:var(--transition)}
        .form-group input:focus{outline:none;border-color:var(--primary);box-shadow:0 0 0 3px rgba(0,123,255,0.1)}
        .form-group.required label::after{content:" *";color:var(--danger)}
        
        .form-row{display:grid;grid-template-columns:1fr 1fr;gap:20px}
        
        /* Buttons */
        .btn{padding:12px 24px;border:none;border-radius:var(--radius);font-weight:500;text-decoration:none;display:inline-flex;align-items:center;gap:8px;cursor:pointer;transition:var(--transition);text-align:center;justify-content:center;line-height:1.4}
        .btn-primary{background:var(--primary);color:white}
        .btn-primary:hover{background:#0056b3;color:white}
        .btn-secondary{background:#6c757d;color:white}
        .btn-secondary:hover{background:#545b62;color:white}
        .btn-danger{background:var(--danger);color:white}
        .btn-danger:hover{background:#c82333;color:white}
        .btn-warning{background:var(--warning);color:white}
        .btn-warning:hover{background:#e0a800;color:white}
        .btn-gold{background:linear-gradient(45deg, #f39c12, #e67e22);color:white}
        .btn-gold:hover{background:linear-gradient(45deg, #e67e22, #d35400);color:white}
        
        /* Button group styling */
        .btn-group .btn{flex-direction:column;text-align:center}
        .btn-group .btn i{font-size:16px;margin-bottom:3px}
        .btn-group .btn small{font-size:11px;opacity:0.9;margin-top:2px}
        
        /* Pro Status */
        .pro-status{background:linear-gradient(135deg,#667eea,#764ba2);color:white;padding:20px;border-radius:var(--radius);margin-bottom:20px}
        .pro-status.expired{background:linear-gradient(135deg,#dc3545,#c82333)}
        .pro-status h3{margin:0 0 10px 0;display:flex;align-items:center;gap:10px}
        .pro-status p{margin:0;opacity:0.9}
        .pro-actions{margin-top:15px}
        .btn-upgrade{background:rgba(255,255,255,0.2);color:white;border:1px solid rgba(255,255,255,0.3)}
        .btn-upgrade:hover{background:rgba(255,255,255,0.3)}
        
        /* Responsive */
        @media (max-width:768px){
            .sidebar{margin-left:calc(-1 * var(--sidebar-width))}
            .sidebar.show{margin-left:0}
            .main-content{margin-left:0}
            .profile-container{grid-template-columns:1fr;gap:15px}
            .form-row{grid-template-columns:1fr}
            .content{padding:10px}
        }
        
        /* Animations */
        @keyframes fadeInUp{from{opacity:0;transform:translateY(30px)}to{opacity:1;transform:translateY(0)}}
        .profile-card,.form-section{animation:fadeInUp 0.5s ease forwards}
    </style>
</head>
<body>
    <div class="wrapper">
        <!-- Sidebar -->
        <nav class="sidebar" id="sidebar">
            <div class="sidebar-header">
                <div class="user-info">
                    <div class="user-avatar"><i class="fas fa-user-circle"></i></div>
                    <div class="user-details">
                        <h5>${user != null ? user.fullName : 'N/A'}</h5>
                        <small class="text-muted">${user != null ? user.roleName : 'Guest'}</small>
                    </div>
                </div>
            </div>
            
            <div class="sidebar-menu">
                <ul>
                    <li><a href="${pageContext.request.contextPath}/post/my"><i class="fas fa-tachometer-alt"></i><span>Dashboard</span></a></li>
                    <li><a href="${pageContext.request.contextPath}/post/my"><i class="fas fa-list-alt"></i><span>Danh sách tin đăng</span></a></li>
                    <li><a href="${pageContext.request.contextPath}/post/create"><i class="fas fa-plus-circle"></i><span>Đăng tin mới</span></a></li>
                    <li><a href="${pageContext.request.contextPath}/subscription"><i class="fas fa-crown"></i><span>Gói Pro</span></a></li>
                    <li><a href="${pageContext.request.contextPath}/facebook/manage"><i class="fab fa-facebook"></i><span>Cấu hình Facebook</span></a></li>
                    <li><a href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i><span>Xem trang chủ</span></a></li>
                    <li><a class="active" href="${pageContext.request.contextPath}/profile"><i class="fas fa-user-cog"></i><span>Cài đặt tài khoản</span></a></li>
                    <li class="logout"><a href="/PhongTroNew/logout" onclick="return confirm('Bạn có chắc chắn muốn đăng xuất?')"><i class="fas fa-sign-out-alt"></i><span>Đăng xuất</span></a></li>
                </ul>
            </div>
        </nav>

        <!-- Main Content -->
        <div class="main-content">
            <!-- Top Navigation -->
            <header class="top-nav">
                <button class="sidebar-toggle" id="sidebarToggle"><i class="fas fa-bars"></i></button>
                <h1>Cài đặt tài khoản</h1>
                <div class="nav-right">
                    <span>Chào mừng, ${user != null ? user.fullName : 'Guest'}!</span>
                </div>
            </header>

            <!-- Content Area -->
            <main class="content">
                <!-- Alert Messages -->
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i>
                        <c:choose>
                            <c:when test="${param.success == 'profile_updated'}">Cập nhật thông tin thành công!</c:when>
                            <c:when test="${param.success == 'password_changed'}">Đổi mật khẩu thành công!</c:when>
                            <c:otherwise>${param.success}</c:otherwise>
                        </c:choose>
                        <button class="alert-close" onclick="this.parentElement.style.display='none'">&times;</button>
                    </div>
                </c:if>
                
                <c:if test="${not empty param.error}">
                    <div class="alert alert-error">
                        <i class="fas fa-exclamation-circle"></i>
                        <c:choose>
                            <c:when test="${param.error == 'missing_fullname'}">Vui lòng nhập họ tên!</c:when>
                            <c:when test="${param.error == 'invalid_email'}">Email không hợp lệ!</c:when>
                            <c:when test="${param.error == 'email_exists'}">Email đã được sử dụng!</c:when>
                            <c:when test="${param.error == 'missing_current_password'}">Vui lòng nhập mật khẩu hiện tại!</c:when>
                            <c:when test="${param.error == 'weak_password'}">Mật khẩu mới phải có ít nhất 6 ký tự!</c:when>
                            <c:when test="${param.error == 'password_mismatch'}">Mật khẩu xác nhận không khớp!</c:when>
                            <c:when test="${param.error == 'wrong_current_password'}">Mật khẩu hiện tại không đúng!</c:when>
                            <c:when test="${param.error == 'update_failed'}">Cập nhật thất bại!</c:when>
                            <c:when test="${param.error == 'password_update_failed'}">Đổi mật khẩu thất bại!</c:when>
                            <c:when test="${param.error == 'system_error'}">Lỗi hệ thống, vui lòng thử lại!</c:when>
                            <c:otherwise>Đã xảy ra lỗi: ${param.error}</c:otherwise>
                        </c:choose>
                        <button class="alert-close" onclick="this.parentElement.style.display='none'">&times;</button>
                    </div>
                </c:if>

                <!-- Profile Container -->
                <div class="profile-container">
                    <!-- Profile Card -->
                    <div class="profile-card">
                        <div class="profile-header">
                            <div class="profile-avatar">
                                <i class="fas fa-user-circle"></i>
                            </div>
                            <div class="profile-name">${user != null ? user.fullName : 'N/A'}</div>
                            <div class="profile-role">${user != null ? user.roleName : 'Guest'}</div>
                            
                            <c:choose>
                                <c:when test="${user != null && user.isPro}">
                                    <div class="pro-badge active">
                                        <i class="fas fa-crown"></i>
                                        <span>PRO ACTIVE</span>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="pro-badge">
                                        <i class="fas fa-user"></i>
                                        <span>FREE USER</span>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <div class="profile-body">
                           
                            <div class="profile-stat">
                                <span class="stat-label">Tên đăng nhập</span>
                                <span class="stat-value">${user != null ? user.username : 'N/A'}</span>
                            </div>
                            <div class="profile-stat">
                                <span class="stat-label">Email</span>
                                <span class="stat-value">${user != null ? user.email : 'N/A'}</span>
                            </div>
                            <div class="profile-stat">
                                <span class="stat-label">Số điện thoại</span>
                                <span class="stat-value">
                                    <c:choose>
                                        <c:when test="${user != null && not empty user.phone}">${user.phone}</c:when>
                                        <c:otherwise><em>Chưa cập nhật</em></c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <div class="profile-stat">
                                <span class="stat-label">Số dư xu</span>
                                <span class="stat-value">
                                    <c:choose>
                                        <c:when test="${not empty balance}">
                                            <fmt:formatNumber value="${balance.availableCoins}" pattern="#,###"/> xu
                                        </c:when>
                                        <c:otherwise>0 xu</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <div class="profile-stat">
                                <span class="stat-label">Ngày tham gia</span>
                                <span class="stat-value">
                                    <c:if test="${user != null && not empty user.createdAt}">
                                        <fmt:formatDate value="${user.createdAt}" pattern="dd/MM/yyyy" />
                                    </c:if>
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Settings Forms -->
                    <div class="settings-forms">
                        <!-- Pro Status -->
                        <c:if test="${user != null && user.isPro}">
                            <div class="pro-status">
                                <h3><i class="fas fa-crown"></i>Tài khoản PRO</h3>
                                <p>Bạn đang sử dụng gói Pro với các quyền lợi đặc biệt!</p>
                                <c:if test="${not empty proInfo}">
                                    <p><strong>Hết hạn:</strong> 
                                        <fmt:formatDate value="${user.proExpiresAt}" pattern="dd/MM/yyyy HH:mm" />
                                        (còn ${proInfo.proDaysRemaining} ngày)
                                    </p>
                                    <p><strong>Bài đăng hôm nay:</strong> ${proInfo.postsToday}/${proInfo.maxPostsPerDay}</p>
                                </c:if>
                                <div class="pro-actions">
                                    <a href="${pageContext.request.contextPath}/subscription" class="btn btn-upgrade">
                                        <i class="fas fa-arrow-up"></i>Gia hạn Pro
                                    </a>
                                    <a href="${pageContext.request.contextPath}/subscription" class="btn btn-upgrade">
                                        <i class="fas fa-coins"></i>Nạp xu
                                    </a>
                                </div>
                            </div>
                        </c:if>
                        
                        <c:if test="${!user.isPro}">
                            <div class="pro-status expired">
                                <h3><i class="fas fa-user"></i>Tài khoản FREE</h3>
                                <p>Nâng cấp lên Pro để có thêm nhiều tính năng tuyệt vời!</p>
                                <p><strong>Quyền lợi Pro:</strong> Đăng 10 bài/ngày, hỗ trợ ưu tiên, tìm kiếm nâng cao</p>
                                <div class="pro-actions">
                                    <a href="${pageContext.request.contextPath}/subscription" class="btn btn-upgrade">
                                        <i class="fas fa-crown"></i>Nâng cấp Pro ngay
                                    </a>
                                    <a href="${pageContext.request.contextPath}/subscription" class="btn btn-upgrade">
                                        <i class="fas fa-coins"></i>Nạp xu
                                    </a>
                                </div>
                            </div>
                        </c:if>

                        <!-- Coin Management Section -->
                        <div class="form-section">
                            <div class="form-header">
                                <i class="fas fa-coins"></i>
                                <h3>Quản lý xu</h3>
                            </div>
                            <div class="form-body">
                                <div class="form-row">
                                    <div class="form-group">
                                        <label>Số xu hiện có</label>
                                        <div style="display: flex; align-items: center; gap: 15px;">
                                            <div style="font-size: 1.5em; font-weight: 700; color: #f39c12;">
                                                <i class="fas fa-coins"></i>
                                                <c:choose>
                                                    <c:when test="${not empty balance}">
                                                        <fmt:formatNumber value="${balance.availableCoins}" pattern="#,###"/>
                                                    </c:when>
                                                    <c:otherwise>0</c:otherwise>
                                                </c:choose>
                                                xu
                                            </div>
                                            <div style="color: #6c757d;">
                                                ≈ <c:choose>
                                                    <c:when test="${not empty balance}">
                                                        <fmt:formatNumber value="${balance.availableCoins * 1000}" pattern="#,###"/>
                                                    </c:when>
                                                    <c:otherwise>0</c:otherwise>
                                                </c:choose> VNĐ
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label>Nạp xu nhanh</label>
                                        <div class="btn-group" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 10px;">
                                            <a href="${pageContext.request.contextPath}/subscription" class="btn btn-primary" style="font-size: 13px; padding: 10px;">
                                                <i class="fas fa-coins"></i> 10 Xu<br><small>10.000đ</small>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/subscription" class="btn btn-primary" style="font-size: 13px; padding: 10px;">
                                                <i class="fas fa-coins"></i> 50 Xu<br><small>50.000đ</small>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/subscription" class="btn btn-warning" style="font-size: 13px; padding: 10px;">
                                                <i class="fas fa-crown"></i> 100 Xu<br><small>100.000đ - Gói Pro</small>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/subscription" class="btn btn-primary" style="font-size: 13px; padding: 10px; background: #28a745;">
                                                <i class="fas fa-gem"></i> 200 Xu<br><small>200.000đ - Khuyến mãi</small>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <a href="${pageContext.request.contextPath}/subscription" class="btn btn-gold">
                                        <i class="fas fa-wallet"></i>Nạp số tiền khác
                                    </a>
                                    <a href="${pageContext.request.contextPath}/payment/history" class="btn btn-secondary">
                                        <i class="fas fa-history"></i>Lịch sử giao dịch
                                    </a>
                                </div>
                                
                                <div style="background: #e7f3ff; padding: 15px; border-radius: 8px; margin-top: 15px; border-left: 4px solid #007bff;">
                                    <h6 style="margin: 0 0 8px 0; color: #0056b3;"><i class="fas fa-info-circle"></i> Thông tin xu</h6>
                                    <ul style="margin: 0; padding-left: 20px; color: #495057;">
                                        <li>1 xu = 1.000 VNĐ</li>
                                        <li>Gói Pro: 100 xu = 30 ngày Pro (10 bài/ngày)</li>
                                        <li>Xu không có thời hạn sử dụng</li>
                                        <li>Có thể sử dụng cho các tính năng premium khác</li>
                                    </ul>
                                </div>
                            </div>
                        </div>

                        <!-- Update Profile Form -->
                        <div class="form-section">
                            <div class="form-header">
                                <i class="fas fa-user-edit"></i>
                                <h3>Thông tin cá nhân</h3>
                            </div>
                            <div class="form-body">
                                <form action="${pageContext.request.contextPath}/profile" method="post">
                                    <input type="hidden" name="action" value="updateProfile">
                                    
                                    <div class="form-group required">
                                        <label for="fullName">Họ và tên</label>
                                        <input type="text" id="fullName" name="fullName" value="${user != null ? user.fullName : ''}" required>
                                    </div>
                                    
                                    <div class="form-row">
                                        <div class="form-group required">
                                            <label for="email">Email</label>
                                            <input type="email" id="email" name="email" value="${user != null ? user.email : ''}" required>
                                        </div>
                                        
                                        <div class="form-group">
                                            <label for="phone">Số điện thoại</label>
                                            <input type="tel" id="phone" name="phone" value="${user != null ? user.phone : ''}">
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <button type="submit" class="btn btn-primary">
                                            <i class="fas fa-save"></i>Cập nhật thông tin
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <!-- Change Password Form -->
                        <div class="form-section">
                            <div class="form-header">
                                <i class="fas fa-key"></i>
                                <h3>Đổi mật khẩu</h3>
                            </div>
                            <div class="form-body">
                                <form action="${pageContext.request.contextPath}/profile" method="post">
                                    <input type="hidden" name="action" value="changePassword">
                                    
                                    <div class="form-group required">
                                        <label for="currentPassword">Mật khẩu hiện tại</label>
                                        <input type="password" id="currentPassword" name="currentPassword" required>
                                    </div>
                                    
                                    <div class="form-row">
                                        <div class="form-group required">
                                            <label for="newPassword">Mật khẩu mới</label>
                                            <input type="password" id="newPassword" name="newPassword" minlength="6" required>
                                        </div>
                                        
                                        <div class="form-group required">
                                            <label for="confirmPassword">Xác nhận mật khẩu</label>
                                            <input type="password" id="confirmPassword" name="confirmPassword" minlength="6" required>
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <button type="submit" class="btn btn-danger">
                                            <i class="fas fa-key"></i>Đổi mật khẩu
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <script>
        // Profile page JavaScript
        document.addEventListener('DOMContentLoaded', function() {
            const sidebar = document.getElementById('sidebar');
            const sidebarToggle = document.getElementById('sidebarToggle');
            const mainContent = document.querySelector('.main-content');
            
            // Sidebar toggle
            if (sidebarToggle) {
                sidebarToggle.addEventListener('click', function() {
                    if (window.innerWidth <= 768) {
                        sidebar.classList.toggle('show');
                    } else {
                        sidebar.classList.toggle('collapsed');
                        mainContent.classList.toggle('expanded');
                    }
                });
            }
            
            // Handle window resize
            window.addEventListener('resize', function() {
                if (window.innerWidth > 768) {
                    sidebar.classList.remove('show');
                } else {
                    sidebar.classList.remove('collapsed');
                    mainContent.classList.remove('expanded');
                }
            });
            
            // Password confirmation validation
            const newPassword = document.getElementById('newPassword');
            const confirmPassword = document.getElementById('confirmPassword');
            
            if (newPassword && confirmPassword) {
                confirmPassword.addEventListener('input', function() {
                    if (newPassword.value !== confirmPassword.value) {
                        confirmPassword.setCustomValidity('Mật khẩu xác nhận không khớp');
                    } else {
                        confirmPassword.setCustomValidity('');
                    }
                });
                
                newPassword.addEventListener('input', function() {
                    if (confirmPassword.value && newPassword.value !== confirmPassword.value) {
                        confirmPassword.setCustomValidity('Mật khẩu xác nhận không khớp');
                    } else {
                        confirmPassword.setCustomValidity('');
                    }
                });
            }
            
            // Auto dismiss alerts
            setTimeout(function() {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(function(alert) {
                    alert.style.display = 'none';
                });
            }, 5000);
        });
    </script>
</body>
</html>