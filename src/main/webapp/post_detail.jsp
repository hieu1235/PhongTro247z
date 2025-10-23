<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết phòng trọ - ${fn:escapeXml(post.title)} | PhongTro247</title>
        <meta name="description" content="Chi tiết phòng trọ ${fn:escapeXml(post.title)} - Giá ${post.price}₫, diện tích ${post.area}m² tại ${fn:escapeXml(post.address)}">
        
        <!-- Font và Icon -->
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        
        <style>
            :root {
                --primary-color: #667eea;
                --secondary-color: #764ba2;
                --accent-color: #f093fb;
                --text-dark: #2d3748;
                --text-light: #718096;
                --bg-light: #f8fafc;
                --white: #ffffff;
                --success-color: #48bb78;
                --warning-color: #ed8936;
                --error-color: #e53e3e;
                --border-color: #e2e8f0;
                --shadow-light: 0 4px 6px rgba(0, 0, 0, 0.05);
                --shadow-medium: 0 10px 25px rgba(0, 0, 0, 0.1);
                --shadow-heavy: 0 20px 40px rgba(0, 0, 0, 0.15);
                --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                --border-radius: 16px;
            }

            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                color: var(--text-dark);
                line-height: 1.6;
            }

            /* Header Styles */
            .header {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(20px);
                position: sticky;
                top: 0;
                z-index: 1000;
                box-shadow: var(--shadow-light);
                border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            }

            .header-content {
                max-width: 1400px;
                margin: 0 auto;
                padding: 1rem 2rem;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .logo {
                display: flex;
                align-items: center;
                gap: 1rem;
                text-decoration: none;
                color: var(--text-dark);
                font-weight: 700;
                font-size: 1.5rem;
                transition: var(--transition);
            }

            .logo:hover {
                color: var(--primary-color);
                text-decoration: none;
            }

            .logo-icon {
                width: 40px;
                height: 40px;
                background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
                border-radius: 12px;
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                font-size: 1.2rem;
            }

            .nav-menu {
                display: flex;
                list-style: none;
                gap: 2rem;
                align-items: center;
            }

            .nav-link {
                color: var(--text-light);
                text-decoration: none;
                font-weight: 500;
                font-size: 0.95rem;
                padding: 0.5rem 1rem;
                border-radius: 8px;
                transition: var(--transition);
                position: relative;
            }

            .nav-link:hover {
                color: var(--primary-color);
                background: rgba(102, 126, 234, 0.1);
                text-decoration: none;
            }

            .nav-link.active {
                color: var(--primary-color);
                background: rgba(102, 126, 234, 0.15);
            }

            /* Container */
            .container {
                max-width: 1400px;
                margin: 0 auto;
                padding: 2rem;
            }

            /* Breadcrumb */
            .breadcrumb {
                background: rgba(255, 255, 255, 0.9);
                padding: 1rem 2rem;
                border-radius: var(--border-radius);
                margin-bottom: 2rem;
                box-shadow: var(--shadow-light);
            }

            .breadcrumb-list {
                display: flex;
                list-style: none;
                gap: 0.5rem;
                align-items: center;
                flex-wrap: wrap;
            }

            .breadcrumb-item {
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .breadcrumb-link {
                color: var(--text-light);
                text-decoration: none;
                font-size: 0.9rem;
                transition: var(--transition);
            }

            .breadcrumb-link:hover {
                color: var(--primary-color);
                text-decoration: none;
            }

            .breadcrumb-current {
                color: var(--text-dark);
                font-weight: 500;
                font-size: 0.9rem;
            }

            .breadcrumb-separator {
                color: var(--text-light);
                font-size: 0.8rem;
            }

            /* Main Content */
            .main-content {
                display: grid;
                grid-template-columns: 2fr 1fr;
                gap: 3rem;
                align-items: start;
            }

            .detail-card {
                background: white;
                border-radius: var(--border-radius);
                overflow: hidden;
                box-shadow: var(--shadow-medium);
                transition: var(--transition);
            }

            /* Gallery Section */
            .gallery-section {
                position: relative;
                height: 400px;
                overflow: hidden;
            }

            .main-image {
                width: 100%;
                height: 100%;
                object-fit: cover;
                transition: var(--transition);
            }

            .gallery-thumbnails {
                display: flex;
                gap: 0.5rem;
                padding: 1rem;
                overflow-x: auto;
                background: rgba(248, 250, 252, 0.8);
            }

            .thumbnail {
                flex-shrink: 0;
                width: 80px;
                height: 60px;
                border-radius: 8px;
                overflow: hidden;
                cursor: pointer;
                border: 2px solid transparent;
                transition: var(--transition);
            }

            .thumbnail.active {
                border-color: var(--primary-color);
            }

            .thumbnail img {
                width: 100%;
                height: 100%;
                object-fit: cover;
            }

            .no-image {
                height: 400px;
                background: linear-gradient(45deg, #f7fafc, #edf2f7);
                display: flex;
                align-items: center;
                justify-content: center;
                color: #a0aec0;
                font-size: 3rem;
            }

            /* Post Info */
            .post-info {
                padding: 2rem;
            }

            .post-title {
                font-size: 2rem;
                font-weight: 700;
                color: var(--text-dark);
                margin-bottom: 1rem;
                line-height: 1.3;
            }

            .post-price {
                font-size: 2.5rem;
                font-weight: 800;
                color: var(--error-color);
                margin-bottom: 1.5rem;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .price-label {
                font-size: 1rem;
                font-weight: 500;
                color: var(--text-light);
            }

            .post-details {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                gap: 1.5rem;
                margin-bottom: 2rem;
            }

            .detail-item {
                display: flex;
                align-items: center;
                gap: 0.8rem;
                padding: 1rem;
                background: var(--bg-light);
                border-radius: 12px;
                border: 1px solid var(--border-color);
                transition: var(--transition);
            }

            .detail-item:hover {
                border-color: var(--primary-color);
                transform: translateY(-2px);
                box-shadow: var(--shadow-light);
            }

            .detail-icon {
                width: 40px;
                height: 40px;
                background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
                border-radius: 10px;
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                font-size: 1.1rem;
            }

            .detail-content h4 {
                font-size: 0.9rem;
                color: var(--text-light);
                margin-bottom: 0.2rem;
                font-weight: 500;
            }

            .detail-content p {
                font-size: 1.1rem;
                font-weight: 600;
                color: var(--text-dark);
            }

            /* Description */
            .description-section {
                padding: 2rem;
                border-top: 1px solid var(--border-color);
            }

            .section-title {
                font-size: 1.3rem;
                font-weight: 600;
                color: var(--text-dark);
                margin-bottom: 1rem;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .description-content {
                color: var(--text-light);
                line-height: 1.7;
                font-size: 1.1rem;
            }

            /* Contact Info */
            .contact-card {
                background: white;
                border-radius: var(--border-radius);
                padding: 2rem;
                box-shadow: var(--shadow-medium);
                margin-bottom: 2rem;
            }

            .contact-header {
                display: flex;
                align-items: center;
                gap: 1rem;
                margin-bottom: 1.5rem;
            }

            .contact-avatar {
                width: 60px;
                height: 60px;
                background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                font-size: 1.5rem;
            }

            .contact-info h3 {
                font-size: 1.2rem;
                font-weight: 600;
                color: var(--text-dark);
            }

            .contact-info p {
                color: var(--text-light);
                font-size: 0.9rem;
            }

            .contact-details {
                display: flex;
                flex-direction: column;
                gap: 1rem;
            }

            .contact-item {
                display: flex;
                align-items: center;
                gap: 0.8rem;
                padding: 0.8rem;
                background: var(--bg-light);
                border-radius: 10px;
            }

            .contact-item i {
                color: var(--primary-color);
                width: 20px;
                text-align: center;
            }

            /* Map Section */
            .map-card {
                background: white;
                border-radius: var(--border-radius);
                overflow: hidden;
                box-shadow: var(--shadow-medium);
                margin-bottom: 2rem;
            }

            .map-header {
                padding: 1.5rem;
                border-bottom: 1px solid var(--border-color);
            }

            .map-container {
                height: 400px;
                position: relative;
            }

            #map {
                width: 100%;
                height: 100%;
            }

            /* Action Buttons */
            .action-buttons {
                display: flex;
                gap: 1rem;
                margin-top: 2rem;
            }

            .btn {
                padding: 0.8rem 1.5rem;
                border-radius: 10px;
                font-size: 1rem;
                font-weight: 600;
                text-decoration: none;
                transition: var(--transition);
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                border: none;
                cursor: pointer;
                font-family: inherit;
            }

            .btn-primary {
                background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
                color: white;
                box-shadow: var(--shadow-light);
            }

            .btn-primary:hover {
                transform: translateY(-2px);
                box-shadow: var(--shadow-medium);
                color: white;
                text-decoration: none;
            }

            .btn-outline {
                background: transparent;
                color: var(--primary-color);
                border: 2px solid var(--primary-color);
            }

            .btn-outline:hover {
                background: var(--primary-color);
                color: white;
                text-decoration: none;
            }

            .btn-danger {
                background: var(--error-color);
                color: white;
            }

            .btn-danger:hover {
                background: #c53030;
                transform: translateY(-2px);
                text-decoration: none;
                color: white;
            }

            /* Footer */
            .footer {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(20px);
                margin-top: 4rem;
                padding: 3rem 0 2rem;
                border-top: 1px solid var(--border-color);
            }

            .footer-content {
                max-width: 1400px;
                margin: 0 auto;
                padding: 0 2rem;
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 2rem;
            }

            .footer-section h3 {
                color: var(--text-dark);
                font-size: 1.1rem;
                font-weight: 600;
                margin-bottom: 1rem;
            }

            .footer-section p,
            .footer-section a {
                color: var(--text-light);
                text-decoration: none;
                font-size: 0.9rem;
                line-height: 1.6;
                transition: var(--transition);
            }

            .footer-section a:hover {
                color: var(--primary-color);
            }

            .footer-bottom {
                border-top: 1px solid var(--border-color);
                padding: 1.5rem 0;
                margin-top: 2rem;
                text-align: center;
                color: var(--text-light);
                font-size: 0.9rem;
            }

            /* Responsive */
            @media (max-width: 768px) {
                .header-content {
                    padding: 1rem;
                    flex-direction: column;
                    gap: 1rem;
                }

                .nav-menu {
                    gap: 1rem;
                }

                .container {
                    padding: 1rem;
                }

                .main-content {
                    grid-template-columns: 1fr;
                    gap: 2rem;
                }

                .post-title {
                    font-size: 1.5rem;
                }

                .post-price {
                    font-size: 2rem;
                }

                .post-details {
                    grid-template-columns: 1fr;
                }

                .action-buttons {
                    flex-direction: column;
                }
            }

            /* Alert Messages */
            .alert {
                padding: 1rem 1.5rem;
                border-radius: 12px;
                margin-bottom: 2rem;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .alert-error {
                background: linear-gradient(135deg, #fed7d7 0%, #feb2b2 100%);
                color: #c53030;
                border: 1px solid #feb2b2;
            }

            .alert-success {
                background: linear-gradient(135deg, #c6f6d5 0%, #9ae6b4 100%);
                color: #2f855a;
                border: 1px solid #9ae6b4;
            }
        </style>
    </head>
    <body>
        <!-- Header -->
        <header class="header">
            <div class="header-content">
                <a href="${pageContext.request.contextPath}/" class="logo">
                    <div class="logo-icon">
                        <i class="fas fa-home"></i>
                    </div>
                    <span>PhongTro247</span>
                </a>
                
                <nav>
                    <ul class="nav-menu">
                        <li><a href="${pageContext.request.contextPath}/" class="nav-link">
                            <i class="fas fa-search"></i> Tìm phòng
                        </a></li>
                        <li><a href="${pageContext.request.contextPath}/post/create" class="nav-link">
                            <i class="fas fa-plus-circle"></i> Đăng tin
                        </a></li>
                        <li><a href="${pageContext.request.contextPath}/about" class="nav-link">
                            <i class="fas fa-info-circle"></i> Giới thiệu
                        </a></li>
                        <li><a href="${pageContext.request.contextPath}/contact" class="nav-link">
                            <i class="fas fa-phone"></i> Liên hệ
                        </a></li>
                    </ul>
                </nav>
            </div>
        </header>

        <!-- Main Container -->
        <div class="container">
            <!-- Breadcrumb -->
            <nav class="breadcrumb">
                <ul class="breadcrumb-list">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/" class="breadcrumb-link">
                            <i class="fas fa-home"></i> Trang chủ
                        </a>
                    </li>
                    <li class="breadcrumb-separator">
                        <i class="fas fa-chevron-right"></i>
                    </li>
                    <li class="breadcrumb-item">
                        <span class="breadcrumb-current">Chi tiết phòng trọ</span>
                    </li>
                </ul>
            </nav>

            <!-- Error/Success Messages -->
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-triangle"></i>
                    ${error}
                </div>
            </c:if>

            <c:if test="${not empty success}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i>
                    ${success}
                </div>
            </c:if>

            <c:choose>
                <c:when test="${not empty post}">
                    <!-- Main Content -->
                    <div class="main-content">
                        <!-- Left Column - Post Details -->
                        <div class="detail-card">
                            <!-- Gallery Section -->
                            <c:choose>
                                <c:when test="${not empty images}">
                                    <div class="gallery-section">
                                        <img src="${pageContext.request.contextPath}${images[0]}" 
                                             alt="Hình ảnh phòng trọ" 
                                             class="main-image" 
                                             id="mainImage">
                                    </div>
                                    <c:if test="${fn:length(images) > 1}">
                                        <div class="gallery-thumbnails">
                                            <c:forEach var="img" items="${images}" varStatus="status">
                                                <div class="thumbnail ${status.index == 0 ? 'active' : ''}" 
                                                     onclick="changeMainImage('${pageContext.request.contextPath}${img}', this)">
                                                    <img src="${pageContext.request.contextPath}${img}" alt="Hình ${status.index + 1}">
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:if>
                                </c:when>
                                <c:otherwise>
                                    <div class="no-image">
                                        <i class="fas fa-image"></i>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <!-- Post Info -->
                            <div class="post-info">
                                <h1 class="post-title">${fn:escapeXml(post.title)}</h1>
                                
                                <div class="post-price">
                                    <fmt:formatNumber value="${post.price}" type="currency" currencySymbol="₫"/>
                                    <span class="price-label">/ tháng</span>
                                </div>

                                <!-- Post Details Grid -->
                                <div class="post-details">
                                    <div class="detail-item">
                                        <div class="detail-icon">
                                            <i class="fas fa-expand-arrows-alt"></i>
                                        </div>
                                        <div class="detail-content">
                                            <h4>Diện tích</h4>
                                            <p>${post.area} m²</p>
                                        </div>
                                    </div>

                                    <div class="detail-item">
                                        <div class="detail-icon">
                                            <i class="fas fa-map-marker-alt"></i>
                                        </div>
                                        <div class="detail-content">
                                            <h4>Địa chỉ</h4>
                                            <p>${fn:escapeXml(post.address)}</p>
                                        </div>
                                    </div>

                                    <div class="detail-item">
                                        <div class="detail-icon">
                                            <i class="fas fa-tag"></i>
                                        </div>
                                        <div class="detail-content">
                                            <h4>Trạng thái</h4>
                                            <p>${post.statusName}</p>
                                        </div>
                                    </div>

                                    <div class="detail-item">
                                        <div class="detail-icon">
                                            <i class="fas fa-calendar-alt"></i>
                                        </div>
                                        <div class="detail-content">
                                            <h4>Ngày đăng</h4>
                                            <p><fmt:formatDate value="${post.createdAt}" pattern="dd/MM/yyyy"/></p>
                                        </div>
                                    </div>
                                </div>

                                <!-- Action Buttons -->
                                <c:if test="${isOwner}">
                                    <div class="action-buttons">
                                        <a href="${pageContext.request.contextPath}/post/edit?id=${post.postId}" 
                                           class="btn btn-primary">
                                            <i class="fas fa-edit"></i> Chỉnh sửa
                                        </a>
                                        <form action="${pageContext.request.contextPath}/post/delete" 
                                              method="post" 
                                              style="display:inline" 
                                              onsubmit="return confirm('Bạn chắc chắn muốn xóa bài đăng này?');">
                                            <input type="hidden" name="id" value="${post.postId}"/>
                                            <button type="submit" class="btn btn-danger">
                                                <i class="fas fa-trash"></i> Xóa
                                            </button>
                                        </form>
                                    </div>
                                </c:if>
                            </div>

                            <!-- Description Section -->
                            <div class="description-section">
                                <h2 class="section-title">
                                    <i class="fas fa-file-alt"></i> Mô tả chi tiết
                                </h2>
                                <div class="description-content">
                                    ${fn:escapeXml(post.content)}
                                </div>
                            </div>
                        </div>

                        <!-- Right Column - Contact & Map -->
                        <div>
                            <!-- Contact Info -->
                            <div class="contact-card">
                                <div class="contact-header">
                                    <div class="contact-avatar">
                                        <i class="fas fa-user"></i>
                                    </div>
                                    <div class="contact-info">
                                        <h3>${fn:escapeXml(post.userFullName)}</h3>
                                        <p>Chủ phòng trọ</p>
                                    </div>
                                </div>

                                <div class="contact-details">
                                    <div class="contact-item">
                                        <i class="fas fa-phone"></i>
                                        <span>${post.userPhone}</span>
                                    </div>
                                    <div class="contact-item">
                                        <i class="fas fa-envelope"></i>
                                        <span>${post.userEmail}</span>
                                    </div>
                                </div>
                            </div>

                            <!-- Map Section -->
                            <c:if test="${not empty post.lat && not empty post.lng}">
                                <div class="map-card">
                                    <div class="map-header">
                                        <h3 class="section-title">
                                            <i class="fas fa-map"></i> Vị trí trên bản đồ
                                        </h3>
                                    </div>
                                    <div class="map-container">
                                        <div id="map"></div>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-error">
                        <i class="fas fa-exclamation-triangle"></i>
                        Không tìm thấy thông tin phòng trọ.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Footer -->
        <footer class="footer">
            <div class="footer-content">
                <div class="footer-section">
                    <h3>PhongTro247</h3>
                    <p>Nền tảng tìm kiếm phòng trọ hàng đầu Việt Nam. Kết nối người thuê và chủ phòng một cách nhanh chóng, tin cậy.</p>
                </div>
                <div class="footer-section">
                    <h3>Liên kết hữu ích</h3>
                    <p><a href="${pageContext.request.contextPath}/">Tìm phòng</a></p>
                    <p><a href="${pageContext.request.contextPath}/post/create">Đăng tin</a></p>
                    <p><a href="${pageContext.request.contextPath}/about">Giới thiệu</a></p>
                    <p><a href="${pageContext.request.contextPath}/contact">Liên hệ</a></p>
                </div>
                <div class="footer-section">
                    <h3>Hỗ trợ khách hàng</h3>
                    <p>Email: support@phongtro247.com</p>
                    <p>Hotline: 1900 1234</p>
                    <p>Thời gian: 8:00 - 22:00 (T2-CN)</p>
                </div>
                <div class="footer-section">
                    <h3>Theo dõi chúng tôi</h3>
                    <p><a href="#"><i class="fab fa-facebook"></i> Facebook</a></p>
                    <p><a href="#"><i class="fab fa-youtube"></i> YouTube</a></p>
                    <p><a href="#"><i class="fab fa-tiktok"></i> TikTok</a></p>
                </div>
            </div>
            <div class="footer-bottom">
                <div style="max-width: 1400px; margin: 0 auto; padding: 0 2rem;">
                    © 2024 PhongTro247. Tất cả quyền được bảo lưu.
                </div>
            </div>
        </footer>

        <!-- Scripts -->
        <script>
            // Image Gallery
            function changeMainImage(imageSrc, thumbnail) {
                document.getElementById('mainImage').src = imageSrc;
                
                // Update active thumbnail
                document.querySelectorAll('.thumbnail').forEach(thumb => {
                    thumb.classList.remove('active');
                });
                thumbnail.classList.add('active');
            }

            // Google Maps
            <c:if test="${not empty post.lat && not empty post.lng}">
                function initMap() {
                    var pos = {
                        lat: parseFloat('${post.lat}'),
                        lng: parseFloat('${post.lng}')
                    };

                    var map = new google.maps.Map(document.getElementById('map'), {
                        center: pos,
                        zoom: 16,
                        styles: [
                            {
                                "featureType": "all",
                                "elementType": "geometry.fill",
                                "stylers": [{"weight": "2.00"}]
                            },
                            {
                                "featureType": "all",
                                "elementType": "geometry.stroke",
                                "stylers": [{"color": "#9c9c9c"}]
                            },
                            {
                                "featureType": "all",
                                "elementType": "labels.text",
                                "stylers": [{"visibility": "on"}]
                            }
                        ]
                    });

                    var marker = new google.maps.Marker({
                        position: pos,
                        map: map,
                        title: '${fn:escapeXml(post.title)}',
                        animation: google.maps.Animation.DROP,
                        icon: {
                            url: 'data:image/svg+xml;base64,' + btoa('<svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none"><path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z" fill="#667eea"/><circle cx="12" cy="9" r="2.5" fill="white"/></svg>'),
                            scaledSize: new google.maps.Size(40, 40),
                            anchor: new google.maps.Point(20, 40)
                        }
                    });

                    var infoWindow = new google.maps.InfoWindow({
                        content: '<div style="padding: 10px; max-width: 250px;"><h4 style="margin: 0 0 8px 0; color: #2d3748;">${fn:escapeXml(post.title)}</h4><p style="margin: 0 0 8px 0; color: #718096; font-size: 14px;">${fn:escapeXml(post.address)}</p><p style="margin: 0; font-weight: 600; color: #e53e3e; font-size: 16px;">${post.price}₫</p></div>'
                    });

                    marker.addListener('click', function() {
                        infoWindow.open(map, marker);
                    });
                }
            </c:if>
        </script>

        <!-- Google Maps API -->
        <c:if test="${not empty post.lat && not empty post.lng}">
            <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBoXKWOAY7kpoON5iBnLpDaE4g8TmYdbJU&callback=initMap"></script>
        </c:if>
    </body>
</html>
