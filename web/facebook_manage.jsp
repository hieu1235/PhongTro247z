<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Facebook Pages - PhongTro247</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">

        <style>
            :root{
                --sidebar-width:240px;
                --primary:#007bff;
                --success:#28a745;
                --warning:#ffc107;
                --danger:#dc3545;
                --light:#f8f9fa;
                --dark:#343a40;
                --gold:#FFD700;
                --radius:12px;
                --transition:all 0.3s ease;
                --shadow:0 4px 15px rgba(0,0,0,0.1);
                --shadow-hover:0 8px 25px rgba(0,0,0,0.15)
            }
            *{
                margin:0;
                padding:0;
                box-sizing:border-box
            }
            body{
                font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;
                background:linear-gradient(135deg,#f5f7fa 0%,#c3cfe2 100%);
                min-height:100vh
            }
            .wrapper{
                display:flex;
                min-height:100vh
            }

            /* Sidebar */
            .sidebar{
                width:var(--sidebar-width);
                background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);
                color:white;
                position:fixed;
                height:100vh;
                left:0;
                top:0;
                transition:var(--transition);
                z-index:1000;
                overflow-y:auto;
                box-shadow:4px 0 15px rgba(0,0,0,0.1)
            }
            .sidebar.collapsed{
                margin-left:calc(-1 * var(--sidebar-width))
            }
            .sidebar-header{
                padding:25px 20px;
                border-bottom:1px solid rgba(255,255,255,0.2);
                background:rgba(0,0,0,0.1)
            }
            .user-info{
                display:flex;
                align-items:center;
                gap:15px
            }
            .user-avatar{
                width:55px;
                height:55px;
                border-radius:50%;
                background:rgba(255,255,255,0.2);
                display:flex;
                align-items:center;
                justify-content:center;
                font-size:26px;
                border:2px solid rgba(255,255,255,0.3)
            }
            .user-details h5{
                margin:0 0 5px 0;
                font-size:17px;
                font-weight:600
            }
            .user-details small{
                opacity:0.8;
                font-size:13px
            }

            .sidebar-menu{
                padding:25px 0
            }
            .sidebar-menu ul{
                list-style:none
            }
            .sidebar-menu a{
                color:rgba(255,255,255,0.9);
                padding:15px 25px;
                display:flex;
                align-items:center;
                gap:15px;
                text-decoration:none;
                transition:var(--transition);
                font-weight:500;
                border-left:3px solid transparent
            }
            .sidebar-menu a:hover,.sidebar-menu a.active{
                background:rgba(255,255,255,0.15);
                color:white;
                border-left-color:var(--gold);
                transform:translateX(8px)
            }
            .sidebar-menu i{
                width:22px;
                text-align:center;
                font-size:16px
            }
            .logout{
                margin-top:auto;
                border-top:1px solid rgba(255,255,255,0.2);
                padding-top:20px
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

            /* Top Navigation */
            .top-nav{
                background:white;
                padding:20px 25px;
                box-shadow:var(--shadow);
                display:flex;
                align-items:center;
                justify-content:space-between;
                border-bottom:1px solid rgba(0,0,0,0.05)
            }
            .sidebar-toggle{
                background:white;
                border:2px solid #e9ecef;
                border-radius:8px;
                padding:12px 15px;
                cursor:pointer;
                transition:var(--transition);
                color:var(--dark)
            }
            .sidebar-toggle:hover{
                background:var(--light);
                border-color:var(--primary)
            }
            .top-nav h1{
                font-size:28px;
                font-weight:700;
                color:var(--dark);
                margin:0
            }
            .nav-right{
                display:flex;
                align-items:center;
                gap:20px
            }
            .notification{
                position:relative;
                font-size:20px;
                color:#666;
                cursor:pointer
            }
            .notification .badge{
                position:absolute;
                top:-8px;
                right:-8px;
                background:#e74c3c;
                color:white;
                border-radius:50%;
                width:20px;
                height:20px;
                font-size:12px;
                display:flex;
                align-items:center;
                justify-content:center
            }

            /* Content */
            .content{
                padding:30px;
                animation:fadeIn 0.5s ease
            }

            /* Alert Messages */
            .alert {
                padding: 1rem 1.25rem;
                margin-bottom: 1.5rem;
                border: 1px solid transparent;
                border-radius: var(--radius);
                display: flex;
                align-items: center;
                gap: 0.75rem;
                position: relative;
            }

            .alert-danger {
                color: #721c24;
                background-color: #f8d7da;
                border-color: #f5c6cb;
            }

            .alert-success {
                color: #155724;
                background-color: #d4edda;
                border-color: #c3e6cb;
            }

            .alert-dismissible {
                padding-right: 3rem;
            }

            .btn-close {
                position: absolute;
                top: 0.5rem;
                right: 0.5rem;
                background: none;
                border: none;
                font-size: 1.25rem;
                font-weight: 700;
                line-height: 1;
                color: #000;
                opacity: 0.5;
                cursor: pointer;
            }

            .btn-close:hover {
                opacity: 0.75;
            }

            .btn-close::before {
                content: "×";
            }

            /* Card Styles */
            .card{
                background:white;
                border-radius:var(--radius);
                box-shadow:0 2px 10px rgba(0,0,0,0.1);
                margin-bottom:24px;
                overflow:hidden;
                border:1px solid #e9ecef
            }
            .card-header{
                background:linear-gradient(135deg,#667eea,#764ba2);
                color:white;
                padding:20px;
                border-bottom:none
            }
            .card-header h5{
                margin:0;
                font-size:1.2rem;
                font-weight:600;
                display:flex;
                align-items:center;
                gap:10px
            }
            .card-body{
                padding:24px
            }

            /* Form Styles */
            .form-group, .mb-3{
                margin-bottom:1.5rem
            }
            .form-label{
                font-weight:600;
                margin-bottom:0.5rem;
                color:#333;
                display:block
            }
            .form-control{
                width:100%;
                padding:0.75rem 1rem;
                border:2px solid #e9ecef;
                border-radius:var(--radius);
                font-size:1rem;
                transition:var(--transition);
                background:white
            }
            .form-control:focus{
                outline:none;
                border-color:var(--primary);
                box-shadow:0 0 0 0.2rem rgba(0,123,255,0.25)
            }
            .form-text{
                font-size:0.875rem;
                color:#6c757d;
                margin-top:0.25rem
            }
            .form-check{
                display:flex;
                align-items:center;
                gap:0.5rem;
                margin-bottom:1rem
            }
            .form-check-input{
                width:1.25rem;
                height:1.25rem;
                margin:0
            }
            .form-check-label{
                font-weight:500;
                color:#333
            }

            /* Form Switch */
            .form-switch .form-check-input {
                width: 2rem;
                height: 1rem;
                border-radius: 1rem;
                background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='-4 -4 8 8'%3e%3ccircle r='3' fill='rgba%280,0,0,0.25%29'/%3e%3c/svg%3e");
                background-position: left center;
                background-size: contain;
                background-repeat: no-repeat;
                background-color: #adb5bd;
                cursor: pointer;
                transition: var(--transition);
            }

            .form-switch .form-check-input:checked {
                background-color: var(--primary);
                background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='-4 -4 8 8'%3e%3ccircle r='3' fill='%23fff'/%3e%3c/svg%3e");
                background-position: right center;
            }

            /* Table Styles */
            .table-responsive{
                overflow-x:auto
            }
            .table{
                width:100%;
                border-collapse:collapse;
                margin-bottom:0
            }
            .table th,.table td{
                padding:12px;
                text-align:left;
                border-bottom:1px solid #e9ecef
            }
            .table th{
                background:#f8f9fa;
                font-weight:600;
                color:#333
            }
            .table-hover tbody tr:hover{
                background:#f8f9fa
            }

            /* Button Styles */
            .btn{
                display:inline-flex;
                align-items:center;
                gap:8px;
                padding:10px 20px;
                border:none;
                border-radius:var(--radius);
                font-size:14px;
                font-weight:600;
                text-decoration:none;
                cursor:pointer;
                transition:var(--transition);
                white-space:nowrap
            }
            .btn-primary{
                background:var(--primary);
                color:white
            }
            .btn-primary:hover{
                background:#0056b3;
                transform:translateY(-1px)
            }
            .btn-secondary{
                background:#6c757d;
                color:white
            }
            .btn-secondary:hover{
                background:#545b62;
                transform:translateY(-1px)
            }
            .btn-outline-primary{
                background:transparent;
                color:var(--primary);
                border:2px solid var(--primary)
            }
            .btn-outline-primary:hover{
                background:var(--primary);
                color:white
            }
            .btn-outline-danger{
                background:transparent;
                color:var(--danger);
                border:2px solid var(--danger)
            }
            .btn-outline-danger:hover{
                background:var(--danger);
                color:white
            }
            .btn-sm{
                padding:6px 12px;
                font-size:12px
            }
            .btn-group{
                display:flex;
                gap:4px
            }

            /* Badge Styles */
            .badge{
                display:inline-block;
                padding:0.25rem 0.5rem;
                font-size:0.75rem;
                font-weight:600;
                line-height:1;
                text-align:center;
                white-space:nowrap;
                vertical-align:baseline;
                border-radius:var(--radius)
            }
            .bg-primary{
                background-color:var(--primary)!important;
                color:white
            }
            .bg-success{
                background-color:var(--success)!important;
                color:white
            }
            .bg-secondary{
                background-color:#6c757d!important;
                color:white
            }
            .ms-2{
                margin-left:0.5rem
            }

            /* Grid System */
            .row{
                display:flex;
                flex-wrap:wrap;
                margin-left:-12px;
                margin-right:-12px
            }
            .col-md-6{
                flex:0 0 50%;
                max-width:50%;
                padding-left:12px;
                padding-right:12px
            }
            .col-md-12{
                flex:0 0 100%;
                max-width:100%;
                padding-left:12px;
                padding-right:12px
            }

            /* Utilities */
            .text-center{
                text-align:center
            }
            .text-muted{
                color:#6c757d
            }
            .py-4{
                padding-top:1.5rem;
                padding-bottom:1.5rem
            }
            .mb-3{
                margin-bottom:1rem
            }
            .mb-4{
                margin-bottom:1.5rem
            }
            .mt-3{
                margin-top:1rem
            }
            .mt-4{
                margin-top:1.5rem
            }

            /* Animation */
            @keyframes fadeIn{
                from{
                    opacity:0;
                    transform:translateY(30px)
                }
                to{
                    opacity:1;
                    transform:translateY(0)
                }
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
                .col-md-6{
                    flex:0 0 100%;
                    max-width:100%
                }
                .top-nav{
                    padding:0 15px
                }
                .content{
                    padding:15px
                }
                .btn-group{
                    flex-direction:column
                }
            }

            /* Code styling */
            code {
                padding: 0.2rem 0.4rem;
                font-size: 0.875rem;
                color: #e83e8c;
                background-color: #f8f9fa;
                border-radius: 0.25rem;
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
                        <li><a href="${pageContext.request.contextPath}/post/my"><i class="fas fa-list-alt"></i><span>Danh sách tin đăng</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/post/create"><i class="fas fa-plus-circle"></i><span>Đăng tin mới</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/subscription"><i class="fas fa-crown"></i><span>Gói Pro</span></a></li>
                        <li><a class="active" href="${pageContext.request.contextPath}/facebook/manage"><i class="fab fa-facebook"></i><span>Cấu hình Facebook</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i><span>Xem trang chủ</span></a></li>
                        <li><a href="${pageContext.request.contextPath}/profile"><i class="fas fa-user-cog"></i><span>Cài đặt tài khoản</span></a></li>
                        <li class="logout"><a href="${pageContext.request.contextPath}/login?action=logout" onclick="return confirm('Bạn có chắc chắn muốn đăng xuất?')"><i class="fas fa-sign-out-alt"></i><span>Đăng xuất</span></a></li>
                    </ul>
                </div>
            </nav>

            <!-- Main Content -->
            <div class="main-content">
                <!-- Top Navigation -->
                <header class="top-nav">
                    <button class="sidebar-toggle" id="sidebarToggle"><i class="fas fa-bars"></i></button>
                    <h1>Quản lý Facebook Pages</h1>
                    <div class="nav-right">
                        <div class="notification"><i class="fas fa-bell"></i><span class="badge">3</span></div>
                    </div>
                </header>

                <!-- Content Area -->
                <main class="content">
                    <!-- Error/Success Messages -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible">
                            <i class="fas fa-exclamation-triangle"></i> ${error}
                            <button class="btn-close" onclick="this.parentElement.style.display = 'none'"></button>
                        </div>
                    </c:if>
                    
                    <c:if test="${not empty success}">
                        <div class="alert alert-success alert-dismissible">
                            <i class="fas fa-check-circle"></i> ${success}
                            <button class="btn-close" onclick="this.parentElement.style.display = 'none'"></button>
                        </div>
                    </c:if>
                    
                    <!-- Add New Page Form -->
                    <div class="card mb-4">
                        <div class="card-header">
                            <h5><i class="fas fa-plus-circle"></i> Thêm Facebook Page Mới</h5>
                        </div>
                        <div class="card-body">
                            <form method="post" action="${pageContext.request.contextPath}/facebook/manage">
                                <input type="hidden" name="action" value="add">
                                
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label for="pageId" class="form-label">Page ID *</label>
                                            <input type="text" class="form-control" id="pageId" name="pageId" required
                                                   placeholder="810103215515429">
                                            <div class="form-text">Lấy từ Facebook Graph API Explorer</div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label for="pageName" class="form-label">Tên Page *</label>
                                            <input type="text" class="form-control" id="pageName" name="pageName" required
                                                   placeholder="PhongTro247">
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="accessToken" class="form-label">Page Access Token *</label>
                                    <textarea class="form-control" id="accessToken" name="accessToken" rows="3" required
                                              placeholder="EAAIwY6SadI8BPYLZCS47mn9..."></textarea>
                                    <div class="form-text">Long-lived Page Access Token từ Facebook</div>
                                </div>
                                
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-check">
                                            <input class="form-check-input" type="checkbox" id="autoPost" name="autoPost" checked>
                                            <label class="form-check-label" for="autoPost">
                                                Tự động đăng bài
                                            </label>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-check">
                                            <input class="form-check-input" type="checkbox" id="isDefault" name="isDefault">
                                            <label class="form-check-label" for="isDefault">
                                                Đặt làm Page mặc định
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="mt-3">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-plus"></i> Thêm Page
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                    
                    <!-- Existing Pages List -->
                    <div class="card">
                        <div class="card-header">
                            <h5><i class="fab fa-facebook"></i> Danh sách Facebook Pages (${pageCount})</h5>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${empty facebookPages}">
                                    <div class="text-center py-4">
                                        <i class="fab fa-facebook fa-3x text-muted mb-3"></i>
                                        <p class="text-muted">Chưa có Facebook Page nào được cấu hình.</p>
                                        <p class="text-muted">Thêm Page để tự động đăng bài lên Facebook!</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="table-responsive">
                                        <table class="table table-hover">
                                            <thead>
                                                <tr>
                                                    <th>Page Name</th>
                                                    <th>Page ID</th>
                                                    <th>Status</th>
                                                    <th>Auto Post</th>
                                                    <th>Default</th>
                                                    <th>Created</th>
                                                    <th>Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="page" items="${facebookPages}">
                                                    <tr>
                                                        <td>
                                                            <strong>${page.pageName}</strong>
                                                            <c:if test="${page['default']}">
                                                                <span class="badge bg-primary ms-2">Default</span>
                                                            </c:if>
                                                        </td>
                                                        <td><code>${page.pageId}</code></td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${page.active}">
                                                                    <span class="badge bg-success">Active</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge bg-secondary">Inactive</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <div class="form-check form-switch">
                                                                <input class="form-check-input" type="checkbox" 
                                                                       ${page.autoPost ? 'checked' : ''}
                                                                       onchange="toggleAutoPost('${page.pageId}', this.checked)">
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <c:if test="${!page['default']}">
                                                                <button class="btn btn-sm btn-outline-primary" 
                                                                        onclick="setDefault('${page.pageId}')">
                                                                    Set Default
                                                                </button>
                                                            </c:if>
                                                        </td>
                                                        <td>${page.createdAt}</td>
                                                        <td>
                                                            <div class="btn-group" role="group">
                                                                <button class="btn btn-sm btn-outline-primary" 
                                                                        onclick="editPage('${page.pageId}', '${page.pageName}')">
                                                                    <i class="fas fa-edit"></i>
                                                                </button>
                                                                <button class="btn btn-sm btn-outline-danger" 
                                                                        onclick="deletePage('${page.pageId}', '${page.pageName}')">
                                                                    <i class="fas fa-trash"></i>
                                                                </button>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    
                    <!-- Back to Dashboard -->
                    <div class="mt-4">
                        <a href="${pageContext.request.contextPath}/post/my" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Về Dashboard
                        </a>
                    </div>
                </main>
            </div>
        </div>

        <script>
            // Sidebar toggle functionality
            document.getElementById('sidebarToggle').addEventListener('click', function() {
                const sidebar = document.getElementById('sidebar');
                const mainContent = document.querySelector('.main-content');
                
                sidebar.classList.toggle('collapsed');
                mainContent.classList.toggle('expanded');
            });

            // Mobile sidebar toggle
            if (window.innerWidth <= 768) {
                document.getElementById('sidebarToggle').addEventListener('click', function() {
                    const sidebar = document.getElementById('sidebar');
                    sidebar.classList.toggle('show');
                });
            }

            function toggleAutoPost(pageId, enabled) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/facebook/manage';
                
                form.innerHTML = `
                    <input type="hidden" name="action" value="toggle">
                    <input type="hidden" name="pageId" value="\${pageId}">
                    <input type="hidden" name="autoPost" value="\${enabled}">
                `;
                
                document.body.appendChild(form);
                form.submit();
            }
            
            function setDefault(pageId) {
                if (confirm('Đặt page này làm mặc định?')) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/facebook/manage';
                    
                    form.innerHTML = `
                        <input type="hidden" name="action" value="setDefault">
                        <input type="hidden" name="pageId" value="\${pageId}">
                    `;
                    
                    document.body.appendChild(form);
                    form.submit();
                }
            }
            
            function deletePage(pageId, pageName) {
                if (confirm(`Xóa Facebook Page "\${pageName}"?`)) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/facebook/manage';
                    
                    form.innerHTML = `
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="pageId" value="\${pageId}">
                    `;
                    
                    document.body.appendChild(form);
                    form.submit();
                }
            }
            
            function editPage(pageId, pageName) {
                // Implement edit functionality
                alert('Chức năng chỉnh sửa sẽ được triển khai sau');
            }
        </script>
    </body>
</html>