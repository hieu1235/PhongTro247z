<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Facebook Pages - PhongTro247</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">

        <style>
            :root{--sidebar-width:230px;--primary:#007bff;--success:#28a745;--warning:#ffc107;--danger:#dc3545;--light:#f8f9fa;--dark:#343a40;--gold:#f39c12;--radius:8px;--transition:all 0.3s ease}
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
            .sidebar{width:var(--sidebar-width);background:linear-gradient(135deg,#667eea,#764ba2);color:white;position:fixed;height:100vh;left:0;top:0;transition:var(--transition);z-index:1000;overflow-y:auto}
            .sidebar.collapsed{margin-left:calc(-1 * var(--sidebar-width))}
            .sidebar-header{padding:20px;border-bottom:1px solid rgba(255,255,255,0.2)}
            .user-info{display:flex;align-items:center;gap:12px}
            .user-avatar{width:50px;height:50px;border-radius:50%;background:rgba(255,255,255,0.2);display:flex;align-items:center;justify-content:center;font-size:24px}
            .user-details h5{margin:0;font-size:16px;font-weight:600}
            .user-details small{opacity:0.8}
            
        .sidebar-menu{padding:20px 0}
        .sidebar-menu a{color:rgba(255,255,255,0.9);padding:12px 20px;display:flex;align-items:center;gap:12px;text-decoration:none;transition:var(--transition)}
        .sidebar-menu a:hover,.sidebar-menu a.active{background:rgba(255,255,255,0.1);color:white;transform:translateX(5px)}
        .sidebar-menu i{width:20px;text-align:center}            /* Main Content */
            .main-content{
                flex:1;
                margin-left:var(--sidebar-width);
                transition:var(--transition)
            }
            .main-content.expanded{
                margin-left:0
            }

            /* Header */
            .top-nav{background:white;padding:15px 20px;box-shadow:0 2px 4px rgba(0,0,0,0.1);display:flex;align-items:center;justify-content:space-between}
            .sidebar-toggle{background:white;border:1px solid #dee2e6;border-radius:4px;padding:8px 12px;cursor:pointer}
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
            .content{padding:20px}

        /* Alerts */
        .alert{padding:12px 16px;border-radius:var(--radius);margin-bottom:20px;display:flex;align-items:center;gap:10px;position:relative}
        .alert-success{background:#d4edda;color:#155724;border:1px solid #c3e6cb}
        .alert-error{background:#f8d7da;color:#721c24;border:1px solid #f5c6cb}
        .alert-close{position:absolute;right:10px;background:none;border:none;font-size:18px;cursor:pointer;opacity:0.5}
        .alert-close:hover{opacity:1}

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
            @keyframes fadeInUp{from{opacity:0;transform:translateY(30px)}to{opacity:1;transform:translateY(0)}}

            /* Responsive */
            @media (max-width:768px){
                .sidebar{margin-left:calc(-1 * var(--sidebar-width))}
                .sidebar.show{margin-left:0}
                .main-content{margin-left:0}
                .profile-container{grid-template-columns:1fr;gap:15px}
                .form-row{grid-template-columns:1fr}
                .content{padding:10px}
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
                    <a href="${pageContext.request.contextPath}/post/my"><i class="fas fa-tachometer-alt"></i><span>Dashboard</span></a>
                    <a href="${pageContext.request.contextPath}/post/my"><i class="fas fa-list-alt"></i><span>Danh sách tin đăng</span></a>
                    <a href="${pageContext.request.contextPath}/post/create"><i class="fas fa-plus-circle"></i><span>Đăng tin mới</span></a>
                    <a href="${pageContext.request.contextPath}/subscription"><i class="fas fa-crown"></i><span>Gói Pro</span></a>
                    <a class="active" href="${pageContext.request.contextPath}/facebook/manage"><i class="fab fa-facebook"></i><span>Cấu hình Facebook</span></a>
                    <a href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i><span>Xem trang chủ</span></a>
                    <a href="${pageContext.request.contextPath}/profile"><i class="fas fa-user-cog"></i><span>Cài đặt tài khoản</span></a>
                    <a href="/logout" onclick="return confirm('Bạn có chắc chắn muốn đăng xuất?')" class="logout"><i class="fas fa-sign-out-alt"></i><span>Đăng xuất</span></a>
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
                        <div class="alert alert-error">
                            <i class="fas fa-exclamation-triangle"></i> ${error}
                            <button class="alert-close" onclick="this.parentElement.style.display='none'">&times;</button>
                        </div>
                    </c:if>
                    
                    <c:if test="${not empty success}">
                        <div class="alert alert-success">
                            <i class="fas fa-check-circle"></i> ${success}
                            <button class="alert-close" onclick="this.parentElement.style.display='none'">&times;</button>
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
                                    <div class="form-text">
                                        Long-lived Page Access Token từ Facebook
                                        <a href="https://docs.google.com/document/d/1aVIG-TAPMlIztdbuVbD0RiRxKb2TdfXDJ3b33_ioUBs/edit?tab=t.0#heading=h.rwl0ju8yqqnx" 
                                           target="_blank" class="text-primary ms-2" style="font-size: 0.8rem;">
                                            <i class="fas fa-external-link-alt"></i> Hướng dẫn lấy token
                                        </a>
                                    </div>
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

        <!-- Edit Page Modal -->
        <div class="modal fade" id="editPageModal" tabindex="-1" aria-labelledby="editPageModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editPageModalLabel">
                            <i class="fas fa-edit"></i> Chỉnh sửa Facebook Page
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/facebook/manage">
                        <input type="hidden" name="action" value="edit">
                        <input type="hidden" name="pageId" id="editPageId">
                        
                        <div class="modal-body">
                            <div class="alert alert-warning">
                                <i class="fas fa-exclamation-triangle"></i>
                                <strong>Lưu ý:</strong> Việc cập nhật Access Token sẽ thay thế token cũ. 
                                Đảm bảo token mới còn hạn và có đủ quyền.
                            </div>
                            
                            <div class="mb-3">
                                <label for="editPageName" class="form-label">Tên Page</label>
                                <input type="text" class="form-control" id="editPageName" readonly>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editAccessToken" class="form-label">Page Access Token Mới *</label>
                                <textarea class="form-control" id="editAccessToken" name="accessToken" rows="4" required
                                          placeholder="EAAIwY6SadI8BPYLZCS47mn9..."></textarea>
                                <div class="form-text">
                                    Nhập Page Access Token mới từ Facebook Graph API
                                    <a href="https://docs.google.com/document/d/1aVIG-TAPMlIztdbuVbD0RiRxKb2TdfXDJ3b33_ioUBs/edit?tab=t.0#heading=h.rwl0ju8yqqnx" 
                                       target="_blank" class="text-primary ms-2" style="font-size: 0.8rem;">
                                        <i class="fas fa-external-link-alt"></i> Hướng dẫn lấy token
                                    </a>
                                </div>
                            </div>
                            
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="editAutoPost" name="autoPost" checked>
                                <label class="form-check-label" for="editAutoPost">
                                    Tự động đăng bài
                                </label>
                            </div>
                        </div>
                        
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="fas fa-times"></i> Hủy
                            </button>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Lưu thay đổi
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script>
            // Sidebar functionality
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
            });

            // Facebook management functions
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
                // Điền thông tin cơ bản vào modal
                document.getElementById('editPageId').value = pageId;
                document.getElementById('editPageName').value = pageName;
                
                // Reset form
                document.getElementById('editAccessToken').value = '';
                document.getElementById('editAutoPost').checked = true;
                
                // Mở modal
                const modal = new bootstrap.Modal(document.getElementById('editPageModal'));
                modal.show();
            }

            // Auto dismiss alerts
            setTimeout(function() {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(function(alert) {
                    alert.style.display = 'none';
                });
            }, 5000);
        </script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
