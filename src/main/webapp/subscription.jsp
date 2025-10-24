<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gói Pro - PhongTro247</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    
    <style>
        :root {
            --sidebar-width: 240px;
            --primary: #007bff;
            --success: #28a745;
            --warning: #ffc107;
            --danger: #dc3545;
            --light: #f8f9fa;
            --dark: #343a40;
            --gold: #FFD700;
            --radius: 12px;
            --transition: all 0.3s ease;
            --shadow: 0 4px 15px rgba(0,0,0,0.1);
            --shadow-hover: 0 8px 25px rgba(0,0,0,0.15);
        }
        
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', sans-serif;
            background: #f5f5f5;
        }
        
        .wrapper {
            display: flex;
            min-height: 100vh;
        }
        
        /* Sidebar Styles */
        .sidebar {
            width: var(--sidebar-width);
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            position: fixed;
            height: 100vh;
            left: 0;
            top: 0;
            transition: var(--transition);
            z-index: 1000;
            overflow-y: auto;
        }
        
        .sidebar.collapsed {
            margin-left: calc(-1 * var(--sidebar-width));
        }
        
        .sidebar-header {
            padding: 20px;
            border-bottom: 1px solid rgba(255,255,255,0.2);
        }
        
        .user-info {
            display: flex;
            align-items: center;
            gap: 12px;
        }
        
        .user-avatar {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: rgba(255,255,255,0.2);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
        }
        
        .user-details h5 {
            margin: 0;
            font-size: 16px;
            font-weight: 600;
        }
        
        .user-details small {
            opacity: 0.8;
        }
        
        .sidebar-menu {
            padding: 20px 0;
        }
        
        .sidebar-menu ul {
            list-style: none;
        }
        
        .sidebar-menu a {
            color: rgba(255,255,255,0.9);
            padding: 12px 20px;
            display: flex;
            align-items: center;
            gap: 12px;
            text-decoration: none;
            transition: var(--transition);
        }
        
        .sidebar-menu a:hover,
        .sidebar-menu a.active {
            background: rgba(255,255,255,0.1);
            color: white;
            transform: translateX(5px);
        }
        
        .sidebar-menu i {
            width: 20px;
            text-align: center;
        }
        
        /* Main Content */
        .main-content {
            flex: 1;
            margin-left: var(--sidebar-width);
            transition: var(--transition);
        }
        
        .main-content.expanded {
            margin-left: 0;
        }
        
        /* Top Navigation */
        .top-nav {
            background: white;
            padding: 20px 25px;
            box-shadow: var(--shadow);
            display: flex;
            align-items: center;
            justify-content: space-between;
            border-bottom: 1px solid rgba(0,0,0,0.05);
        }
        
        .sidebar-toggle {
            background: white;
            border: 2px solid #e9ecef;
            border-radius: 8px;
            padding: 12px 15px;
            cursor: pointer;
            transition: var(--transition);
            color: var(--dark);
        }
        
        .sidebar-toggle:hover {
            background: var(--light);
            border-color: var(--primary);
        }
        
        .top-nav h1 {
            font-size: 28px;
            font-weight: 700;
            color: var(--dark);
            margin: 0;
        }
        
        .nav-right {
            display: flex;
            align-items: center;
            gap: 20px;
        }
        
        /* Content Area */
        .content {
            padding: 30px;
        }
        
        /* Alert Styles */
        .alert {
            padding: 16px 20px;
            border-radius: var(--radius);
            margin-bottom: 25px;
            display: flex;
            align-items: center;
            gap: 12px;
            position: relative;
            border: none;
            box-shadow: var(--shadow);
        }
        
        .alert-success {
            background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
            color: #155724;
        }
        
        .alert-danger {
            background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
            color: #721c24;
        }
        
        .alert-close {
            position: absolute;
            right: 15px;
            background: none;
            border: none;
            font-size: 20px;
            cursor: pointer;
            opacity: 0.6;
            transition: var(--transition);
        }
        
        .alert-close:hover {
            opacity: 1;
        }
        
        /* Section Styles */
        .pro-section {
            background: white;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            margin-bottom: 30px;
            overflow: hidden;
        }
        
        .section-header {
            padding: 25px 30px;
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-bottom: 1px solid #e9ecef;
        }
        
        .section-header h2 {
            margin: 0;
            display: flex;
            align-items: center;
            gap: 12px;
            color: var(--dark);
            font-size: 22px;
            font-weight: 600;
        }
        
        .section-body {
            padding: 30px;
        }
        
        /* Stats Grid */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 25px;
            margin-bottom: 35px;
        }
        
        .stat-card {
            background: white;
            border-radius: var(--radius);
            padding: 25px;
            display: flex;
            align-items: center;
            gap: 20px;
            box-shadow: var(--shadow);
            transition: var(--transition);
            border: 1px solid rgba(0,0,0,0.05);
        }
        
        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: var(--shadow-hover);
        }
        
        .stat-icon {
            width: 70px;
            height: 70px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 28px;
            flex-shrink: 0;
        }
        
        .stat-card.blue .stat-icon {
            background: linear-gradient(135deg, var(--primary) 0%, #0056b3 100%);
        }
        
        .stat-card.green .stat-icon {
            background: linear-gradient(135deg, var(--success) 0%, #1e7e34 100%);
        }
        
        .stat-card.yellow .stat-icon {
            background: linear-gradient(135deg, var(--warning) 0%, #e0a800 100%);
        }
        
        .stat-card.red .stat-icon {
            background: linear-gradient(135deg, var(--danger) 0%, #c82333 100%);
        }
        
        .stat-content h3 {
            margin: 0 0 5px 0;
            font-size: 32px;
            font-weight: 700;
            color: var(--dark);
        }
        
        .stat-content p {
            margin: 0;
            color: #6c757d;
            font-size: 15px;
            font-weight: 500;
        }
        
        /* Plan Cards */
        .plans-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
            gap: 25px;
            margin-bottom: 35px;
        }
        
        .plan-card {
            background: white;
            border-radius: var(--radius);
            padding: 30px;
            box-shadow: var(--shadow);
            transition: var(--transition);
            border: 2px solid transparent;
            position: relative;
            overflow: hidden;
        }
        
        .plan-card:hover {
            transform: translateY(-5px);
            box-shadow: var(--shadow-hover);
        }
        
        .plan-card.recommended {
            border-color: var(--gold);
            background: linear-gradient(135deg, #fff9e6 0%, #fff3cd 100%);
        }
        
        .plan-card.recommended::before {
            content: 'KHUYẾN NGHỊ';
            position: absolute;
            top: -15px;
            left: 25px;
            background: linear-gradient(45deg, var(--gold) 0%, #ffb347 100%);
            color: white;
            padding: 8px 20px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 700;
            box-shadow: 0 4px 10px rgba(255,193,7,0.3);
        }
        
        .plan-header {
            text-align: center;
            margin-bottom: 25px;
        }
        
        .plan-icon {
            font-size: 55px;
            margin-bottom: 15px;
            color: var(--primary);
        }
        
        .plan-card.recommended .plan-icon {
            color: var(--gold);
        }
        
        .plan-name {
            font-size: 26px;
            font-weight: 700;
            color: var(--dark);
            margin-bottom: 8px;
        }
        
        .plan-price {
            font-size: 36px;
            font-weight: 700;
            color: var(--success);
            margin-bottom: 10px;
        }
        
        .plan-card.recommended .plan-price {
            color: var(--gold);
        }
        
        .plan-features {
            list-style: none;
            margin-bottom: 30px;
        }
        
        .plan-features li {
            padding: 10px 0;
            display: flex;
            align-items: center;
            gap: 10px;
            color: #495057;
            font-weight: 500;
        }
        
        .plan-features i {
            color: var(--success);
            width: 18px;
            font-size: 16px;
        }
        
        /* Button Styles */
        .btn {
            padding: 15px 30px;
            border: none;
            border-radius: var(--radius);
            font-weight: 600;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 10px;
            cursor: pointer;
            transition: var(--transition);
            text-align: center;
            justify-content: center;
            font-size: 15px;
            width: 100%;
        }
        
        .btn-primary {
            background: linear-gradient(45deg, var(--primary) 0%, #0056b3 100%);
            color: white;
            box-shadow: 0 4px 15px rgba(0,123,255,0.3);
        }
        
        .btn-primary:hover {
            background: linear-gradient(45deg, #0056b3 0%, #004085 100%);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(0,123,255,0.4);
            color: white;
        }
        
        .btn-success {
            background: linear-gradient(45deg, var(--success) 0%, #1e7e34 100%);
            color: white;
            box-shadow: 0 4px 15px rgba(40,167,69,0.3);
        }
        
        .btn-success:hover {
            background: linear-gradient(45deg, #1e7e34 0%, #155724 100%);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(40,167,69,0.4);
            color: white;
        }
        
        .btn-warning {
            background: linear-gradient(45deg, var(--warning) 0%, #e0a800 100%);
            color: white;
            box-shadow: 0 4px 15px rgba(255,193,7,0.3);
        }
        
        .btn-warning:hover {
            background: linear-gradient(45deg, #e0a800 0%, #d39e00 100%);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(255,193,7,0.4);
            color: white;
        }
        
        .btn-secondary {
            background: linear-gradient(45deg, #6c757d 0%, #545b62 100%);
            color: white;
        }
        
        .btn-secondary:hover {
            background: linear-gradient(45deg, #545b62 0%, #3d4142 100%);
            color: white;
        }
        
        .btn:disabled {
            background: #6c757d;
            cursor: not-allowed;
            opacity: 0.6;
            transform: none !important;
            box-shadow: none !important;
        }
        
        /* Badge Styles */
        .badge {
            font-size: 12px;
            font-weight: 600;
            padding: 8px 15px;
            border-radius: 20px;
            color: white;
        }
        
        .badge.pro {
            background: linear-gradient(45deg, var(--gold) 0%, #ffb347 100%);
            box-shadow: 0 4px 10px rgba(255,193,7,0.3);
        }
        
        .badge.secondary {
            background: #6c757d;
        }
        
        /* Coin Purchase Grid */
        .coin-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin: 25px 0;
        }
        
        .current-balance {
            background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
            border: 2px solid var(--warning);
            border-radius: var(--radius);
            padding: 20px;
            text-align: center;
            margin-bottom: 25px;
        }
        
        .balance-amount {
            font-size: 36px;
            font-weight: 700;
            color: var(--warning);
            margin-bottom: 5px;
        }
        
        .balance-text {
            color: #856404;
            font-weight: 500;
        }
        
        /* Responsive Design */
        @media (max-width: 768px) {
            .sidebar {
                margin-left: calc(-1 * var(--sidebar-width));
            }
            
            .sidebar.show {
                margin-left: 0;
            }
            
            .main-content {
                margin-left: 0;
            }
            
            .stats-grid,
            .plans-grid {
                grid-template-columns: 1fr;
            }
            
            .content {
                padding: 20px 15px;
            }
            
            .top-nav {
                padding: 15px 20px;
            }
            
            .top-nav h1 {
                font-size: 22px;
            }
        }
        
        @media (max-width: 576px) {
            .stat-card {
                text-align: center;
                flex-direction: column;
                gap: 15px;
            }
            
            .section-body {
                padding: 20px;
            }
            
            .plan-card {
                padding: 25px 20px;
            }
            
            .coin-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }
        
        /* Animations */
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
        
        .stat-card,
        .plan-card,
        .pro-section {
            animation: fadeInUp 0.6s ease forwards;
        }
        
        /* Scrollbar Styling */
        .sidebar::-webkit-scrollbar {
            width: 8px;
        }
        
        .sidebar::-webkit-scrollbar-track {
            background: rgba(255,255,255,0.1);
        }
        
        .sidebar::-webkit-scrollbar-thumb {
            background: rgba(255,255,255,0.3);
            border-radius: 4px;
        }
        
        .sidebar::-webkit-scrollbar-thumb:hover {
            background: rgba(255,255,255,0.5);
        }
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
                        <h5>${user.fullName}</h5>
                        <small class="text-muted">${user.roleName}</small>
                    </div>
                </div>
            </div>
            
            <div class="sidebar-menu">
                <ul>
                    <li><a href="${pageContext.request.contextPath}/post/my"><i class="fas fa-tachometer-alt"></i><span>Dashboard</span></a></li>
                    <li><a href="${pageContext.request.contextPath}/post/my"><i class="fas fa-list-alt"></i><span>Danh sách tin đăng</span></a></li>
                    <li><a href="${pageContext.request.contextPath}/post/create"><i class="fas fa-plus-circle"></i><span>Đăng tin mới</span></a></li>
                    <li><a class="active" href="${pageContext.request.contextPath}/subscription"><i class="fas fa-crown"></i><span>Gói Pro</span></a></li>
                    <li><a href="${pageContext.request.contextPath}/facebook/manage"><i class="fab fa-facebook"></i><span>Cấu hình Facebook</span></a></li>
                    <li><a href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i><span>Xem trang chủ</span></a></li>
                    <li><a href="${pageContext.request.contextPath}/profile"><i class="fas fa-user-cog"></i><span>Cài đặt tài khoản</span></a></li>
                    <li class="logout"><a href="/logout" onclick="return confirm('Bạn có chắc chắn muốn đăng xuất?')"><i class="fas fa-sign-out-alt"></i><span>Đăng xuất</span></a></li>
                </ul>
            </div>
        </nav>

        <!-- Main Content -->
        <div class="main-content">
            <!-- Top Navigation -->
            <header class="top-nav">
                <button class="sidebar-toggle" id="sidebarToggle"><i class="fas fa-bars"></i></button>
                <h1>Gói Pro</h1>
                <div class="nav-right">
                    <c:choose>
                        <c:when test="${not empty proInfo and proInfo.pro}">
                            <span class="badge pro">
                                <i class="fas fa-crown"></i> USER PRO
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge secondary">
                                <i class="fas fa-user"></i> USER FREE
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </header>

            <!-- Content Area -->
            <main class="content">
                <!-- Alert Messages -->
                <c:if test="${not empty sessionScope.success}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i>
                        ${sessionScope.success}
                        <button type="button" class="alert-close" onclick="this.parentElement.style.display='none'">&times;</button>
                    </div>
                    <c:remove var="success" scope="session"/>
                </c:if>

                <c:if test="${not empty sessionScope.error}">
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-triangle"></i>
                        ${sessionScope.error}
                        <button type="button" class="alert-close" onclick="this.parentElement.style.display='none'">&times;</button>
                    </div>
                    <c:remove var="error" scope="session"/>
                </c:if>

                <!-- Current Status -->
                <div class="stats-grid">
                    <div class="stat-card blue">
                        <div class="stat-icon">
                            <i class="fas ${not empty proInfo and proInfo.pro ? 'fa-crown' : 'fa-user'}"></i>
                        </div>
                        <div class="stat-content">
                            <h3>${not empty proInfo and proInfo.pro ? 'PRO' : 'FREE'}</h3>
                            <p>Gói hiện tại</p>
                        </div>
                    </div>
                    
                    <div class="stat-card green">
                        <div class="stat-icon">
                            <i class="fas fa-calendar-day"></i>
                        </div>
                        <div class="stat-content">
                            <h3>
                                <c:choose>
                                    <c:when test="${not empty proInfo}">
                                                                            <div class="stat-value">
                                        ${proInfo != null ? proInfo.postsToday : 0}/${proInfo != null ? proInfo.maxPostsPerDay : 3}
                                    </div>
                                    </c:when>
                                    <c:otherwise>
                                        0/1
                                    </c:otherwise>
                                </c:choose>
                            </h3>
                            <p>Bài đăng hôm nay</p>
                        </div>
                    </div>
                    
                    <div class="stat-card yellow">
                        <div class="stat-icon">
                            <i class="fas fa-coins"></i>
                        </div>
                        <div class="stat-content">
                            <h3>
                                <c:choose>
                                    <c:when test="${not empty balance}">
                                        <fmt:formatNumber value="${balance != null ? balance.availableCoins : 0}" pattern="#,###"/>
                                    </c:when>
                                    <c:otherwise>0</c:otherwise>
                                </c:choose>
                            </h3>
                            <p>Số xu hiện có</p>
                        </div>
                    </div>
                    
                    <div class="stat-card red">
                        <div class="stat-icon">
                            <i class="fas fa-facebook"></i>
                        </div>
                        <div class="stat-content">
                            <h3>${proInfo != null && proInfo.pro ? 'Có' : 'Không'}</h3>
                            <p>Đăng Facebook</p>
                        </div>
                    </div>
                </div>

                <!-- Pro Packages -->
                <div class="pro-section">
                    <div class="section-header">
                        <h2><i class="fas fa-crown"></i> Gói Pro</h2>
                    </div>
                    <div class="section-body">
                        <div class="plans-grid">
                            <!-- Free Plan -->
                            <div class="plan-card">
                                <div class="plan-header">
                                    <div class="plan-icon"><i class="fas fa-user"></i></div>
                                    <div class="plan-name">FREE</div>
                                    <div class="plan-price">Miễn phí</div>
                                </div>
                                <ul class="plan-features">
                                    <li><i class="fas fa-check"></i> 1 bài đăng/ngày</li>
                                    <li><i class="fas fa-check"></i> Đăng lên homepage</li>
                                    <li><i class="fas fa-check"></i> Đăng lên Facebook</li>
                                    <li><i class="fas fa-check"></i> Hỗ trợ cơ bản</li>
                                </ul>
                                <button class="btn btn-secondary" disabled>
                                    <i class="fas fa-check"></i> Gói hiện tại
                                </button>
                            </div>
                            
                            <!-- Pro Plan -->
                            <div class="plan-card recommended">
                                <div class="plan-header">
                                    <div class="plan-icon"><i class="fas fa-crown"></i></div>
                                    <div class="plan-name">PRO</div>
                                    <div class="plan-price">100 xu</div>
                                    <small class="text-muted">= 100.000 VNĐ / 30 ngày</small>
                                </div>
                                <ul class="plan-features">
                                    <li><i class="fas fa-check"></i> 10 bài đăng/ngày</li>
                                    <li><i class="fas fa-check"></i> Đăng lên homepage</li>
                                    <li><i class="fas fa-check"></i> Đăng lên Facebook</li>
                                    <li><i class="fas fa-check"></i> Hỗ trợ ưu tiên</li>
                                    <li><i class="fas fa-check"></i> Hiệu lực 30 ngày</li>
                                </ul>
                                <c:choose>
                                    <c:when test="${proInfo != null && proInfo.pro}">
                                        <button class="btn btn-success" disabled>
                                            <i class="fas fa-crown"></i> Đã kích hoạt PRO
                                        </button>
                                        <c:if test="${not empty proInfo.expirationDate}">
                                            <p class="text-muted mt-2 mb-0 small text-center">
                                                <i class="fas fa-clock"></i> Hết hạn: <fmt:formatDate value="${proInfo != null ? proInfo.expirationDate : null}" pattern="dd/MM/yyyy"/>
                                            </p>
                                        </c:if>
                                    </c:when>
                                    <c:when test="${not empty balance and balance.availableCoins >= 100}">
                                        <form method="post" action="subscription" style="display: inline; width: 100%;">
                                            <input type="hidden" name="action" value="buyPro">
                                            <button type="submit" class="btn btn-warning" onclick="return confirm('Xác nhận mua gói Pro với 100 xu?')">
                                                <i class="fas fa-shopping-cart"></i> Mua gói PRO
                                            </button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-secondary" disabled>
                                            <i class="fas fa-exclamation-triangle"></i> Không đủ xu
                                        </button>
                                        <p class="text-danger mt-2 mb-0 small text-center">
                                            Cần thêm <c:choose><c:when test="${not empty balance}">${100 - balance.availableCoins}</c:when><c:otherwise>100</c:otherwise></c:choose> xu
                                        </p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Coin Purchase -->
                <div class="pro-section">
                    <div class="section-header">
                        <h2><i class="fas fa-coins"></i> Nạp xu</h2>
                    </div>
                    <div class="section-body">
                        <!-- Current Balance -->
                        <div class="current-balance">
                            <div class="balance-amount">
                                <c:choose>
                                    <c:when test="${not empty balance}">
                                        <fmt:formatNumber value="${balance != null ? balance.availableCoins : 0}" pattern="#,###"/> xu
                                    </c:when>
                                    <c:otherwise>
                                        0 xu
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="balance-text">Số dư hiện tại</div>
                        </div>
                        
                        <!-- Coin Purchase Options -->
                        <div class="coin-grid">
                            <a href="javascript:void(0)" class="btn btn-primary" onclick="createPayOSPayment(10000); showLoader(this)">
                                <i class="fas fa-coins"></i> 10 Xu<br><small>10.000đ</small>
                            </a>
                            <a href="javascript:void(0)" class="btn btn-primary" onclick="createPayOSPayment(50000); showLoader(this)">
                                <i class="fas fa-coins"></i> 50 Xu<br><small>50.000đ</small>
                            </a>
                            <a href="javascript:void(0)" class="btn btn-warning" onclick="createPayOSPayment(100000); showLoader(this)">
                                <i class="fas fa-crown"></i> 100 Xu<br><small>100.000đ - Gói Pro</small>
                            </a>
                            <a href="javascript:void(0)" class="btn btn-success" onclick="createPayOSPayment(200000); showLoader(this)">
                                <i class="fas fa-gem"></i> 200 Xu<br><small>200.000đ - Khuyến mãi</small>
                            </a>
                        </div>
                        
                        <div class="text-center">
                            <button type="button" class="btn btn-warning" style="max-width: 300px;" onclick="showCustomAmountModal()">
                                <i class="fas fa-qrcode"></i> Nạp số tiền khác
                            </button>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <!-- Custom Amount Modal -->
    <div id="customAmountModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.7); z-index:9999; align-items:center; justify-content:center;">
        <div style="background:white; padding:30px; border-radius:12px; max-width:400px; width:90%;">
            <h3 style="margin-bottom:20px; color:#333;">
                <i class="fas fa-coins"></i> Nhập số tiền nạp
            </h3>
            <div style="margin-bottom:15px;">
                <label style="display:block; margin-bottom:8px; font-weight:600;">Số tiền (VNĐ):</label>
                <input type="number" id="customAmount" class="form-control" 
                       placeholder="Từ 10,000 đến 10,000,000" 
                       min="10000" max="10000000" step="1000"
                       style="width:100%; padding:10px; border:1px solid #ddd; border-radius:8px;">
                <small style="color:#666; display:block; margin-top:5px;">
                    1 xu = 1,000 VNĐ
                </small>
            </div>
            <div style="display:flex; gap:10px; justify-content:flex-end;">
                <button type="button" class="btn btn-secondary" onclick="closeCustomAmountModal()">
                    Hủy
                </button>
                <button type="button" class="btn btn-primary" onclick="processCustomAmount()">
                    <i class="fas fa-check"></i> Nạp xu
                </button>
            </div>
        </div>
    </div>

    <script>
        // Optimized JavaScript
        document.addEventListener('DOMContentLoaded', function(){
            const sidebar = document.getElementById('sidebar');
            const sidebarToggle = document.getElementById('sidebarToggle');
            const mainContent = document.querySelector('.main-content');
            
            // Sidebar toggle
            if(sidebarToggle){
                sidebarToggle.addEventListener('click', function(){
                    if(window.innerWidth <= 768){
                        sidebar.classList.toggle('show');
                    } else {
                        sidebar.classList.toggle('collapsed');
                        mainContent.classList.toggle('expanded');
                    }
                });
            }
            
            // Handle window resize
            window.addEventListener('resize', function(){
                if(window.innerWidth > 768){
                    sidebar.classList.remove('show');
                } else {
                    sidebar.classList.remove('collapsed');
                    mainContent.classList.remove('expanded');
                }
            });
            
            // Close sidebar on mobile when clicking outside
            document.addEventListener('click', function(event){
                if(window.innerWidth <= 768 && !sidebar.contains(event.target) && !sidebarToggle.contains(event.target)){
                    sidebar.classList.remove('show');
                }
            });
            
            // Auto dismiss alerts after 5 seconds
            setTimeout(function(){
                document.querySelectorAll('.alert').forEach(function(alert){
                    alert.style.opacity = '0';
                    setTimeout(() => alert.style.display = 'none', 300);
                });
            }, 5000);
        });
        
        // Loading function for payment buttons
        function showLoader(button) {
            // Show loading state
            const originalContent = button.innerHTML;
            button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang chuyển...';
            button.style.pointerEvents = 'none';
            
            // Add loading overlay with faster timing
            const overlay = document.createElement('div');
            overlay.style.cssText = `
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(255,255,255,0.9);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 10000;
                backdrop-filter: blur(1px);
            `;
            overlay.innerHTML = `
                <div style="text-align: center; color: #007bff;">
                    <div style="font-size: 1.5rem; margin-bottom: 10px;">
                        <i class="fas fa-spinner fa-spin"></i>
                    </div>
                    <div style="font-size: 1rem; font-weight: 600;">
                        Đang chuyển...
                    </div>
                </div>
            `;
            document.body.appendChild(overlay);
            
            // Shorter fallback time
            setTimeout(() => {
                if (document.body.contains(overlay)) {
                    document.body.removeChild(overlay);
                }
                button.innerHTML = originalContent;
                button.style.pointerEvents = 'auto';
            }, 2000);
        }
        
        // Function to create PayOS payment
        function createPayOSPayment(amount) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/payment/create';
            
            const amountInput = document.createElement('input');
            amountInput.type = 'hidden';
            amountInput.name = 'amount';
            amountInput.value = amount;
            
            const methodInput = document.createElement('input');
            methodInput.type = 'hidden';
            methodInput.name = 'paymentMethod';
            methodInput.value = 'payos';
            
            form.appendChild(amountInput);
            form.appendChild(methodInput);
            document.body.appendChild(form);
            form.submit();
        }
        
        // Show custom amount modal
        function showCustomAmountModal() {
            document.getElementById('customAmountModal').style.display = 'flex';
            document.getElementById('customAmount').value = '';
            document.getElementById('customAmount').focus();
        }
        
        // Close custom amount modal
        function closeCustomAmountModal() {
            document.getElementById('customAmountModal').style.display = 'none';
        }
        
        // Process custom amount
        function processCustomAmount() {
            const amount = parseInt(document.getElementById('customAmount').value);
            
            if (!amount || amount < 10000) {
                alert('Số tiền tối thiểu là 10,000 VNĐ');
                return;
            }
            
            if (amount > 10000000) {
                alert('Số tiền tối đa là 10,000,000 VNĐ');
                return;
            }
            
            if (amount % 1000 !== 0) {
                alert('Số tiền phải là bội số của 1,000 VNĐ');
                return;
            }
            
            closeCustomAmountModal();
            createPayOSPayment(amount);
        }
        
        // Close modal when clicking outside
        document.addEventListener('click', function(event) {
            const modal = document.getElementById('customAmountModal');
            if (event.target === modal) {
                closeCustomAmountModal();
            }
        });
        
        // Allow Enter key to submit
        document.addEventListener('keyup', function(event) {
            if (event.key === 'Enter' && document.getElementById('customAmountModal').style.display === 'flex') {
                processCustomAmount();
            }
            if (event.key === 'Escape') {
                closeCustomAmountModal();
            }
        });
    </script>
</body>
</html>
