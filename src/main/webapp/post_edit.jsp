<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Sửa tin đăng - PhongTro247</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">

        <style>
            :root{
                --sidebar-width:230px;
                --primary:#007bff;
                --success:#28a745;
                --warning:#ffc107;
                --danger:#dc3545;
                --light:#f8f9fa;
                --dark:#343a40;
                --radius:8px;
                --transition:all 0.3s ease
            }
            *{
                margin:0;
                padding:0;
                box-sizing:border-box
            }
            body{
                font-family:'Segoe UI',sans-serif;
                background:#f5f5f5
            }
            .wrapper{
                display:flex;
                min-height:100vh
            }

            /* Sidebar */
            .sidebar{
                width:var(--sidebar-width);
                background:linear-gradient(135deg,#667eea,#764ba2);
                color:white;
                position:fixed;
                height:100vh;
                left:0;
                top:0;
                transition:var(--transition);
                z-index:1000;
                overflow-y:auto
            }
            .sidebar.collapsed{
                margin-left:calc(-1 * var(--sidebar-width))
            }
            .sidebar-header{
                padding:20px;
                border-bottom:1px solid rgba(255,255,255,0.2)
            }
            .user-info{
                display:flex;
                align-items:center;
                gap:12px
            }
            .user-avatar{
                width:50px;
                height:50px;
                border-radius:50%;
                background:rgba(255,255,255,0.2);
                display:flex;
                align-items:center;
                justify-content:center;
                font-size:24px
            }
            .user-details h5{
                margin:0;
                font-size:16px;
                font-weight:600
            }
            .user-details small{
                opacity:0.8
            }

            .sidebar-menu{
                padding:20px 0
            }
            .sidebar-menu ul{
                list-style:none
            }
            .sidebar-menu a{
                color:rgba(255,255,255,0.9);
                padding:12px 20px;
                display:flex;
                align-items:center;
                gap:12px;
                text-decoration:none;
                transition:var(--transition)
            }
            .sidebar-menu a:hover,.sidebar-menu a.active{
                background:rgba(255,255,255,0.1);
                color:white;
                transform:translateX(5px)
            }
            .sidebar-menu i{
                width:20px;
                text-align:center
            }

            /* Main Content */
            .main-content{
                flex:1;
                margin-left:var(--sidebar-width);
                transition:var(--transition)
            }
            .main-content.expanded{
                margin-left:0
            }

            /* Header */
            .top-nav{
                background:white;
                padding:15px 20px;
                box-shadow:0 2px 4px rgba(0,0,0,0.1);
                display:flex;
                align-items:center;
                justify-content:space-between
            }
            .sidebar-toggle{
                background:white;
                border:1px solid #dee2e6;
                border-radius:4px;
                padding:8px 12px;
                cursor:pointer
            }
            .nav-right{
                display:flex;
                align-items:center;
                gap:15px
            }

            /* Content */
            .content{
                padding:20px
            }

            /* Alerts */
            .alert{
                padding:12px 16px;
                border-radius:var(--radius);
                margin-bottom:20px;
                display:flex;
                align-items:center;
                gap:10px
            }
            .alert-success{
                background:#d4edda;
                color:#155724;
                border:1px solid #c3e6cb
            }
            .alert-error{
                background:#f8d7da;
                color:#721c24;
                border:1px solid #f5c6cb
            }

            /* Form Styles */
            .form-section{
                background:white;
                border-radius:var(--radius);
                box-shadow:0 2px 10px rgba(0,0,0,0.1)
            }
            .form-header{
                padding:20px;
                border-bottom:1px solid #e9ecef;
                display:flex;
                justify-content:space-between;
                align-items:center
            }
            .form-header h2{
                margin:0;
                display:flex;
                align-items:center;
                gap:10px;
                color:var(--dark)
            }
            .form-body{
                padding:20px
            }

            .form-grid{
                display:grid;
                grid-template-columns:1fr 1fr;
                gap:30px
            }

            .form-group{
                margin-bottom:20px
            }
            .form-group label{
                display:block;
                margin-bottom:8px;
                font-weight:600;
                color:var(--dark);
                font-size:14px
            }
            .form-group label i{
                margin-right:8px;
                color:var(--primary)
            }
            .required{
                color:var(--danger)
            }

            .form-control{
                width:100%;
                padding:12px;
                border:1px solid #ddd;
                border-radius:6px;
                font-size:14px;
                transition:var(--transition)
            }
            .form-control:focus{
                outline:none;
                border-color:var(--primary);
                box-shadow:0 0 0 3px rgba(0,123,255,0.1)
            }
            textarea.form-control{
                resize:vertical;
                min-height:120px
            }
            .form-row{
                display:grid;
                grid-template-columns:1fr 1fr;
                gap:15px
            }

            /* Current Images */
            .current-images{
                display:grid;
                grid-template-columns:repeat(auto-fill,minmax(120px,1fr));
                gap:10px;
                margin-top:10px
            }
            .current-image{
                position:relative;
                border-radius:8px;
                overflow:hidden;
                box-shadow:0 2px 8px rgba(0,0,0,0.1)
            }
            .current-image img{
                width:100%;
                height:90px;
                object-fit:cover
            }

            /* File Upload */
            .file-input-container{
                position:relative;
                border:2px dashed #ddd;
                border-radius:8px;
                padding:30px;
                text-align:center;
                transition:var(--transition);
                cursor:pointer
            }
            .file-input-container:hover{
                border-color:var(--primary);
                background:rgba(0,123,255,0.05)
            }
            .file-input{
                position:absolute;
                top:0;
                left:0;
                width:100%;
                height:100%;
                opacity:0;
                cursor:pointer
            }
            .file-input-label{
                color:#6c757d;
                font-size:14px
            }
            .file-input-label i{
                font-size:32px;
                margin-bottom:10px;
                color:var(--primary)
            }

            .images-info{
                margin-top:10px;
                padding:10px;
                background:#f8f9fa;
                border-radius:6px;
                display:none
            }
            .images-info.show{
                display:block
            }
            .files-selected{
                color:var(--warning);
                font-weight:600
            }

            /* Buttons */
            .btn{
                padding:12px 20px;
                border:none;
                border-radius:6px;
                font-weight:600;
                text-decoration:none;
                display:inline-flex;
                align-items:center;
                gap:8px;
                cursor:pointer;
                transition:var(--transition);
                font-size:14px
            }
            .btn-primary{
                background:var(--primary);
                color:white
            }
            .btn-primary:hover{
                background:#0056b3;
                color:white
            }
            .btn-secondary{
                background:#6c757d;
                color:white
            }
            .btn-secondary:hover{
                background:#545b62;
                color:white
            }

            /* ✅ Scheduled Publishing Styles */
            .publish-options{
                display:flex;
                gap:20px;
                margin:15px 0
            }
            .publish-option{
                display:flex;
                align-items:center;
                gap:8px
            }
            .publish-option input[type="radio"]{
                margin:0
            }
            .publish-option label{
                display:flex;
                align-items:center;
                gap:8px;
                cursor:pointer;
                font-weight:500;
                color:#495057
            }
            .publish-option label i{
                color:var(--primary)
            }
            .scheduled-options{
                background:#f8f9fa;
                padding:20px;
                border-radius:var(--radius);
                border:1px solid #dee2e6;
                margin-top:15px
            }

            .form-actions{
                display:flex;
                gap:10px;
                justify-content:flex-end;
                margin-top:30px;
                padding-top:20px;
                border-top:1px solid #e9ecef
            }

            /* Responsive */
            @media (max-width:768px){
                .sidebar{
                    margin-left:calc(-1 * var(--sidebar-width))
                }
                .sidebar.show{
                    margin-left:0
                }
                .main-content{
                    margin-left:0
                }
                .form-grid{
                    grid-template-columns:1fr
                }
                .form-row{
                    grid-template-columns:1fr
                }
                .content{
                    padding:10px
                }
                .form-body{
                    padding:15px
                }
                .form-actions{
                    flex-direction:column
                }
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
                            <small>${user.roleName}</small>
                        </div>
                    </div>
                </div>

                <div class="sidebar-menu">
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/post/my"><i class="fas fa-tachometer-alt"></i><span>Dashboard</span></a></li>
                        <li><a class="active" href="${pageContext.request.contextPath}/post/my"><i class="fas fa-list-alt"></i><span>Danh sách tin đăng</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/post/create"><i class="fas fa-plus-circle"></i><span>Đăng tin mới</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/facebook/manage"><i class="fab fa-facebook"></i><span>Cấu hình Facebook</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i><span>Xem trang chủ</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/statistics"><i class="fas fa-chart-bar"></i><span>Thống kê</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/profile"><i class="fas fa-user-cog"></i><span>Cài đặt tài khoản</span></a></li>
                        <li><a href="/logout" onclick="return confirm('Bạn có chắc chắn muốn đăng xuất?')"><i class="fas fa-sign-out-alt"></i><span>Đăng xuất</span></a></li>
                    </ul>
                </div>
            </nav>

            <!-- Main Content -->
            <div class="main-content">
                <!-- Top Navigation -->
                <header class="top-nav">
                    <button class="sidebar-toggle" id="sidebarToggle"><i class="fas fa-bars"></i></button>
                    <h1>Sửa tin đăng</h1>
                    <div class="nav-right">
                        <div class="notification"><i class="fas fa-bell"></i></div>
                    </div>
                </header>

                <!-- Content Area -->
                <main class="content">
                    <!-- Alert Messages -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-error">
                            <i class="fas fa-exclamation-circle"></i>
                            <span>${error}</span>
                        </div>
                    </c:if>

                    <!-- Form Section -->
                    <div class="form-section">
                        <div class="form-header">
                            <h2><i class="fas fa-edit"></i>Chỉnh sửa tin đăng</h2>
                        </div>

                        <div class="form-body">
                            <form action="${pageContext.request.contextPath}/post/edit" method="post" enctype="multipart/form-data" id="editForm">
                                <input type="hidden" name="postId" value="${post.postId}" />

                                <div class="form-grid">
                                    <!-- Left Column -->
                                    <div class="form-column">
                                        <div class="form-group">
                                            <label for="title">
                                                <i class="fas fa-heading"></i>
                                                Tiêu đề tin đăng <span class="required">*</span>
                                            </label>
                                            <input type="text" 
                                                   id="title" 
                                                   name="title" 
                                                   class="form-control"
                                                   value="${post.title}"
                                                   required 
                                                   maxlength="200"/>
                                        </div>

                                        <div class="form-group">
                                            <label for="content">
                                                <i class="fas fa-align-left"></i>
                                                Mô tả chi tiết
                                            </label>
                                            <textarea id="content" 
                                                      name="content" 
                                                      class="form-control">${post.content}</textarea>
                                        </div>

                                        <div class="form-group">
                                            <label for="address">
                                                <i class="fas fa-map-marker-alt"></i>
                                                Địa chỉ
                                            </label>
                                            <input type="text" 
                                                   id="address" 
                                                   name="address" 
                                                   class="form-control"
                                                   value="${post.address}"/>
                                        </div>

                                        <div class="form-row">
                                            <div class="form-group">
                                                <label for="lat">
                                                    <i class="fas fa-crosshairs"></i>
                                                    Latitude
                                                </label>
                                                <input type="text" 
                                                       id="lat" 
                                                       name="lat" 
                                                       class="form-control"
                                                       value="${post.lat}"/>
                                            </div>
                                            <div class="form-group">
                                                <label for="lng">
                                                    <i class="fas fa-crosshairs"></i>
                                                    Longitude
                                                </label>
                                                <input type="text" 
                                                       id="lng" 
                                                       name="lng" 
                                                       class="form-control"
                                                       value="${post.lng}"/>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Right Column -->
                                    <div class="form-column">
                                        <div class="form-row">
                                            <div class="form-group">
                                                <label for="price">
                                                    <i class="fas fa-money-bill-wave"></i>
                                                    Giá thuê (VNĐ/tháng)
                                                </label>
                                                <input type="number" 
                                                       id="price" 
                                                       name="price" 
                                                       class="form-control"
                                                       value="${post.price}"
                                                       step="10000" 
                                                       min="0"/>
                                            </div>

                                            <div class="form-group">
                                                <label for="area">
                                                    <i class="fas fa-ruler-combined"></i>
                                                    Diện tích (m²)
                                                </label>
                                                <input type="number" 
                                                       id="area" 
                                                       name="area" 
                                                       class="form-control"
                                                       value="${post.area}"
                                                       step="0.1" 
                                                       min="0"/>
                                            </div>
                                        </div>

                                        <!-- Current Images -->
                                        <div class="form-group">
                                            <label>
                                                <i class="fas fa-images"></i>
                                                Ảnh hiện có
                                            </label>
                                            <div class="current-images">
                                                <c:forEach var="img" items="${images}">
                                                    <div class="current-image">
                                                        <img src="${pageContext.request.contextPath}${img}" alt="Current image"/>
                                                        <div style="position: absolute; bottom: 5px; right: 5px; background: rgba(0,0,0,0.7); color: white; padding: 2px 6px; border-radius: 4px; font-size: 10px;">
                                                            Local Storage
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </div>

                                        <!-- New Images Upload -->
                                        <div class="form-group">
                                            <label for="images">
                                                <i class="fas fa-camera"></i>
                                                Thay thế ảnh mới (tối đa 5 ảnh)
                                            </label>
                                            <div class="file-input-container">
                                                <input type="file" 
                                                       id="images" 
                                                       name="images" 
                                                       multiple 
                                                       accept="image/*" 
                                                       onchange="onEditImagesChange(this)"
                                                       class="file-input"/>
                                                <div class="file-input-label">
                                                    <i class="fas fa-cloud-upload-alt"></i>
                                                    <div>Chọn ảnh mới để thay thế (sẽ xóa ảnh cũ)</div>
                                                </div>
                                            </div>
                                            <div id="editImagesInfo" class="images-info"></div>
                                        </div>
                                    </div>
                                </div>

                                <!-- ✅ Scheduled Publishing Section -->
                                <div class="form-section">
                                    <div class="form-header">
                                        <h3><i class="fas fa-clock"></i>Thời gian đăng tin</h3>
                                    </div>
                                    <div class="publish-options">
                                        <div class="publish-option">
                                            <input type="radio" 
                                                   id="immediate" 
                                                   name="publishType" 
                                                   value="immediate" 
                                                   ${post.scheduledAt == null ? 'checked' : ''} />
                                            <label for="immediate">
                                                <i class="fas fa-bolt"></i>
                                                Đăng ngay
                                            </label>
                                        </div>
                                        <div class="publish-option">
                                            <input type="radio" 
                                                   id="scheduled" 
                                                   name="publishType" 
                                                   value="scheduled"
                                                   ${post.scheduledAt != null ? 'checked' : ''} />
                                            <label for="scheduled">
                                                <i class="fas fa-calendar-alt"></i>
                                                Đặt lịch đăng
                                            </label>
                                        </div>
                                    </div>
                                    
                                    <div id="scheduledOptions" class="scheduled-options" ${post.scheduledAt == null ? 'style="display: none;"' : ''}>
                                        <div class="form-group">
                                            <label for="scheduledAt">
                                                <i class="fas fa-clock"></i>
                                                Thời gian đăng
                                            </label>
                                            <input type="datetime-local" 
                                                   id="scheduledAt" 
                                                   name="scheduledAt" 
                                                   class="form-control"
                                                   value="${post.scheduledAt != null ? post.scheduledAt.toLocalDateTime().toString().substring(0, 16) : ''}" />
                                            <small class="form-hint">Thời gian phải sau ít nhất 5 phút</small>
                                        </div>
                                    </div>
                                </div>

                                <!-- Action Buttons -->
                                <div class="form-actions">
                                    <a href="${pageContext.request.contextPath}/post/my" class="btn btn-secondary">
                                        <i class="fas fa-times"></i>
                                        Hủy
                                    </a>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-save"></i>
                                        Lưu thay đổi
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </main>
            </div>
        </div>

        <script>
            // Sidebar functionality (same as post_create.jsp)
            document.addEventListener('DOMContentLoaded', function () {
                const sidebar = document.getElementById('sidebar');
                const sidebarToggle = document.getElementById('sidebarToggle');
                const mainContent = document.querySelector('.main-content');

                if (sidebarToggle) {
                    sidebarToggle.addEventListener('click', function () {
                        if (window.innerWidth <= 768) {
                            sidebar.classList.toggle('show');
                        } else {
                            sidebar.classList.toggle('collapsed');
                            mainContent.classList.toggle('expanded');
                        }
                    });
                }

                window.addEventListener('resize', function () {
                    if (window.innerWidth > 768) {
                        sidebar.classList.remove('show');
                    } else {
                        sidebar.classList.remove('collapsed');
                        mainContent.classList.remove('expanded');
                    }
                });

                document.addEventListener('click', function (event) {
                    if (window.innerWidth <= 768 &&
                            !sidebar.contains(event.target) &&
                            !sidebarToggle.contains(event.target) &&
                            sidebar.classList.contains('show')) {
                        sidebar.classList.remove('show');
                    }
                });
            });

            // Image upload functionality
            const MAX_IMAGES = 5;

            function onEditImagesChange(input) {
                const info = document.getElementById('editImagesInfo');

                if (!input.files)
                    return;

                if (input.files.length > MAX_IMAGES) {
                    alert('Chỉ được chọn tối đa ' + MAX_IMAGES + ' ảnh.');
                    input.value = '';
                    info.innerHTML = '';
                    info.classList.remove('show');
                    return;
                }

                if (input.files.length > 0) {
                    info.innerHTML = `<div class="files-selected"><i class="fas fa-exclamation-triangle"></i> Sẽ thay thế tất cả ảnh cũ bằng <strong>${input.files.length}</strong> ảnh mới</div>`;
                    info.classList.add('show');
                } else {
                    info.innerHTML = '';
                    info.classList.remove('show');
                }
            }

            // ✅ Scheduled publishing functionality
            const publishTypeRadios = document.querySelectorAll('input[name="publishType"]');
            const scheduledOptions = document.getElementById('scheduledOptions');
            const scheduledAtInput = document.getElementById('scheduledAt');

            publishTypeRadios.forEach(radio => {
                radio.addEventListener('change', function() {
                    if (this.value === 'scheduled') {
                        scheduledOptions.style.display = 'block';
                        scheduledAtInput.required = true;
                        
                        // Set minimum datetime to 5 minutes from now
                        const now = new Date();
                        now.setMinutes(now.getMinutes() + 5);
                        const minDateTime = now.toISOString().slice(0, 16);
                        scheduledAtInput.min = minDateTime;
                        
                        // Set default value if empty
                        if (!scheduledAtInput.value) {
                            scheduledAtInput.value = minDateTime;
                        }
                    } else {
                        scheduledOptions.style.display = 'none';
                        scheduledAtInput.required = false;
                    }
                });
            });

            // Initialize on page load
            const selectedPublishType = document.querySelector('input[name="publishType"]:checked');
            if (selectedPublishType && selectedPublishType.value === 'scheduled') {
                scheduledOptions.style.display = 'block';
                scheduledAtInput.required = true;
                
                const now = new Date();
                now.setMinutes(now.getMinutes() + 5);
                scheduledAtInput.min = now.toISOString().slice(0, 16);
            }

            // Form validation
            document.getElementById('editForm').addEventListener('submit', function (e) {
                const title = document.getElementById('title').value.trim();
                if (!title) {
                    e.preventDefault();
                    alert('Vui lòng nhập tiêu đề tin đăng!');
                    document.getElementById('title').focus();
                    return;
                }

                // Validate scheduled time
                const publishType = document.querySelector('input[name="publishType"]:checked');
                if (publishType && publishType.value === 'scheduled') {
                    const scheduledAt = document.getElementById('scheduledAt').value;
                    if (!scheduledAt) {
                        e.preventDefault();
                        alert('Vui lòng chọn thời gian đăng tin!');
                        document.getElementById('scheduledAt').focus();
                        return;
                    }

                    const scheduledTime = new Date(scheduledAt);
                    const now = new Date();
                    const minTime = new Date(now.getTime() + 5 * 60000); // 5 minutes from now

                    if (scheduledTime < minTime) {
                        e.preventDefault();
                        alert('Thời gian đăng tin phải sau ít nhất 5 phút!');
                        document.getElementById('scheduledAt').focus();
                        return;
                    }
                }
            });
        </script>
    </body>
</html>
