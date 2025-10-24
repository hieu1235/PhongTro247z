<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý tin đăng - PhongTro247</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    
    <style>
        :root{--sidebar-width:230px;--primary:#007bff;--success:#28a745;--warning:#ffc107;--danger:#dc3545;--light:#f8f9fa;--dark:#343a40;--radius:8px;--transition:all 0.3s ease}
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
        .notification{position:relative;cursor:pointer}
        .notification .badge{position:absolute;top:-8px;right:-8px;background:var(--danger);color:white;border-radius:50%;width:18px;height:18px;font-size:10px;display:flex;align-items:center;justify-content:center}
        
        /* Content */
        .content{padding:20px}
        
        /* Alerts */
        .alert{padding:12px 16px;border-radius:var(--radius);margin-bottom:20px;display:flex;align-items:center;gap:10px;position:relative}
        .alert-success{background:#d4edda;color:#155724;border:1px solid #c3e6cb}
        .alert-error{background:#f8d7da;color:#721c24;border:1px solid #f5c6cb}
        .alert-close{position:absolute;right:10px;background:none;border:none;font-size:18px;cursor:pointer;opacity:0.5}
        .alert-close:hover{opacity:1}
        
        /* Stats */
        .stats-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(250px,1fr));gap:20px;margin-bottom:30px}
        .stat-card{background:white;border-radius:var(--radius);padding:20px;display:flex;align-items:center;gap:15px;box-shadow:0 2px 10px rgba(0,0,0,0.1);transition:var(--transition)}
        .stat-card:hover{transform:translateY(-2px);box-shadow:0 4px 20px rgba(0,0,0,0.15)}
        .stat-card.blue .stat-icon{background:var(--primary)}
        .stat-card.green .stat-icon{background:var(--success)}
        .stat-card.yellow .stat-icon{background:var(--warning)}
        .stat-card.red .stat-icon{background:var(--danger)}
        .stat-icon{width:60px;height:60px;border-radius:50%;display:flex;align-items:center;justify-content:center;color:white;font-size:24px}
        .stat-content h3{margin:0;font-size:28px;font-weight:700;color:var(--dark)}
        .stat-content p{margin:0;color:#6c757d;font-size:14px}
        
        /* Posts Section */
        .posts-section{background:white;border-radius:var(--radius);box-shadow:0 2px 10px rgba(0,0,0,0.1)}
        .section-header{padding:20px;border-bottom:1px solid #e9ecef;display:flex;justify-content:space-between;align-items:center}
        .section-header h2{margin:0;display:flex;align-items:center;gap:10px;color:var(--dark)}
        
        /* Table */
        .posts-table-container{overflow-x:auto}
        .posts-table{width:100%;border-collapse:collapse}
        .posts-table th,.posts-table td{padding:12px;text-align:left;border-bottom:1px solid #e9ecef}
        .posts-table th{background:#f8f9fa;font-weight:600;color:var(--dark);font-size:14px}
        .posts-table td{font-size:14px}
        .posts-table tbody tr:hover{background:rgba(0,123,255,0.05)}
        .post-title{color:var(--primary);text-decoration:none}
        .post-title:hover{text-decoration:underline}
        .address{max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}
        .price{font-weight:600;color:var(--success)}
        .scheduled-time{font-size:13px;line-height:1.4}
        .scheduled-time .btn{margin-left:5px;padding:2px 6px}
        .time-published{color:var(--success);font-weight:600}
        .time-scheduled{color:var(--warning);font-weight:600}
        .time-not-published{color:#6c757d}
        
        /* Badges */
        .badge{font-size:11px;font-weight:500;padding:4px 8px;border-radius:4px;color:white}
        .badge.success{background:var(--success)}
        .badge.warning{background:var(--warning)}
        .badge.error{background:var(--danger)}
        .badge.secondary{background:#6c757d}
        
        /* Buttons */
        .btn{padding:8px 16px;border:none;border-radius:6px;font-weight:500;text-decoration:none;display:inline-flex;align-items:center;gap:6px;cursor:pointer;transition:var(--transition)}
        .btn-primary{background:var(--primary);color:white}
        .btn-primary:hover{background:#0056b3;color:white}
        .btn-sm{padding:4px 8px;font-size:12px}
        .btn-edit{background:#17a2b8;color:white}
        .btn-edit:hover{background:#138496}
        .btn-delete{background:var(--danger);color:white}
        .btn-delete:hover{background:#c82333}
        .btn-secondary{background:#6c757d;color:white}
        .btn-secondary:hover{background:#545b62}
        
        /* Actions */
        .actions{display:flex;gap:4px}
        
        /* Empty State */
        .empty-state{text-align:center;padding:60px 20px;color:#6c757d}
        .empty-state i{font-size:4rem;margin-bottom:20px;opacity:0.5}
        .empty-state h3{margin-bottom:10px}
        .empty-state p{margin-bottom:20px}
        
        /* Pagination */
        .pagination{display:flex;justify-content:center;gap:4px;padding:20px}
        .page-btn{padding:8px 12px;border:1px solid #dee2e6;background:white;color:var(--primary);text-decoration:none;border-radius:4px;transition:var(--transition)}
        .page-btn:hover,.page-btn.active{background:var(--primary);color:white;border-color:var(--primary)}
        
        /* Modal */
        .modal{position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.5);z-index:2000;display:none;align-items:center;justify-content:center}
        .modal.show{display:flex}
        .modal-content{background:white;border-radius:var(--radius);width:90%;max-width:500px;max-height:90vh;overflow:hidden}
        .modal-header{padding:15px 20px;border-bottom:1px solid #e9ecef;display:flex;justify-content:space-between;align-items:center}
        .modal-header h3{margin:0}
        .modal-close{background:none;border:none;font-size:24px;cursor:pointer;opacity:0.5}
        .modal-close:hover{opacity:1}
        .modal-body{padding:20px}
        .modal-body .warning{color:var(--warning);margin-top:10px;display:flex;align-items:center;gap:8px}
        .modal-footer{padding:15px 20px;border-top:1px solid #e9ecef;display:flex;gap:10px;justify-content:flex-end}
        
        /* Responsive */
        @media (max-width:768px){
            .sidebar{margin-left:calc(-1 * var(--sidebar-width))}
            .sidebar.show{margin-left:0}
            .main-content{margin-left:0}
            .stats-grid{grid-template-columns:1fr}
            .section-header{flex-direction:column;gap:15px;align-items:stretch}
            .posts-table{font-size:12px}
            .actions{flex-direction:column}
        }
        
        @media (max-width:576px){
            .content{padding:10px}
            .stat-card{text-align:center;flex-direction:column;gap:10px}
        }
        
        /* Animations */
        @keyframes fadeInUp{from{opacity:0;transform:translateY(30px)}to{opacity:1;transform:translateY(0)}}
        .stat-card{animation:fadeInUp 0.5s ease forwards}
        .posts-section{animation:fadeInUp 0.6s ease forwards}
        
        /* Scrollbar */
        .sidebar::-webkit-scrollbar{width:6px}
        .sidebar::-webkit-scrollbar-track{background:rgba(255,255,255,0.1)}
        .sidebar::-webkit-scrollbar-thumb{background:rgba(255,255,255,0.3);border-radius:3px}
        .sidebar::-webkit-scrollbar-thumb:hover{background:rgba(255,255,255,0.5)}
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
                    <li><a class="active" href="${pageContext.request.contextPath}/post/my"><i class="fas fa-tachometer-alt"></i><span>Dashboard</span></a></li>
                    <li><a href="${pageContext.request.contextPath}/post/my"><i class="fas fa-list-alt"></i><span>Danh sách tin đăng</span></a></li>
                    <li><a href="${pageContext.request.contextPath}/post/create"><i class="fas fa-plus-circle"></i><span>Đăng tin mới</span></a></li>
                    <li><a href="${pageContext.request.contextPath}/subscription"><i class="fas fa-crown"></i><span>Gói Pro</span></a></li>
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
                <h1>Quản lý tin đăng</h1>
                <div class="nav-right">
                    <div class="notification"><i class="fas fa-bell"></i><span class="badge">3</span></div>
                </div>
            </header>

            <!-- Content Area -->
            <main class="content">
                <!-- Alert Messages -->
                <c:if test="${not empty param.msg}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i>
                        <span>${param.msg}</span>
                        <button class="alert-close" onclick="this.parentElement.style.display='none'">&times;</button>
                    </div>
                </c:if>
                
                <c:if test="${not empty param.err}">
                    <div class="alert alert-error">
                        <i class="fas fa-exclamation-circle"></i>
                        <span>${param.err}</span>
                        <button class="alert-close" onclick="this.parentElement.style.display='none'">&times;</button>
                    </div>
                </c:if>
                
                <!-- Session Messages -->
                <c:if test="${not empty sessionScope.success}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i>
                        <span>${sessionScope.success}</span>
                        <button class="alert-close" onclick="this.parentElement.style.display='none'">&times;</button>
                    </div>
                    <c:remove var="success" scope="session" />
                </c:if>
                
                <c:if test="${not empty sessionScope.error}">
                    <div class="alert alert-error">
                        <i class="fas fa-exclamation-circle"></i>
                        <span>${sessionScope.error}</span>
                        <button class="alert-close" onclick="this.parentElement.style.display='none'">&times;</button>
                    </div>
                    <c:remove var="error" scope="session" />
                </c:if>                <!-- Stats Cards -->
                <div class="stats-grid">
                    <div class="stat-card blue">
                        <div class="stat-icon"><i class="fas fa-list-alt"></i></div>
                        <div class="stat-content">
                            <h3><c:choose><c:when test="${not empty posts}">${posts.size()}</c:when><c:otherwise>0</c:otherwise></c:choose></h3>
                            <p>Tổng tin đăng</p>
                        </div>
                    </div>
                    
                    <div class="stat-card green">
                        <div class="stat-icon"><i class="fas fa-check-circle"></i></div>
                        <div class="stat-content">
                            <h3>
                                <c:set var="approvedCount" value="0" />
                                <c:forEach var="post" items="${posts}">
                                    <c:if test="${post.statusName == 'APPROVED'}">
                                        <c:set var="approvedCount" value="${approvedCount + 1}" />
                                    </c:if>
                                </c:forEach>
                                ${approvedCount}
                            </h3>
                            <p>Đã duyệt</p>
                        </div>
                    </div>
                    
                    <div class="stat-card yellow">
                        <div class="stat-icon"><i class="fas fa-clock"></i></div>
                        <div class="stat-content">
                            <h3>
                                <c:set var="pendingCount" value="0" />
                                <c:forEach var="post" items="${posts}">
                                    <c:if test="${post.statusName == 'PENDING'}">
                                        <c:set var="pendingCount" value="${pendingCount + 1}" />
                                    </c:if>
                                </c:forEach>
                                ${pendingCount}
                            </h3>
                            <p>Chờ duyệt</p>
                        </div>
                    </div>
                    
                    <div class="stat-card yellow">
                        <div class="stat-icon"><i class="fas fa-clock"></i></div>
                        <div class="stat-content">
                            <h3>
                                <c:set var="scheduledCount" value="0" />
                                <c:forEach var="post" items="${posts}">
                                    <c:if test="${post.statusName == 'SCHEDULED'}">
                                        <c:set var="scheduledCount" value="${scheduledCount + 1}" />
                                    </c:if>
                                </c:forEach>
                                ${scheduledCount}
                            </h3>
                            <p>Đã lập lịch</p>
                        </div>
                    </div>
                    
                    <div class="stat-card red">
                        <div class="stat-icon"><i class="fas fa-times-circle"></i></div>
                        <div class="stat-content">
                            <h3>
                                <c:set var="rejectedCount" value="0" />
                                <c:forEach var="post" items="${posts}">
                                    <c:if test="${post.statusName == 'REJECTED'}">
                                        <c:set var="rejectedCount" value="${rejectedCount + 1}" />
                                    </c:if>
                                </c:forEach>
                                ${rejectedCount}
                            </h3>
                            <p>Bị từ chối</p>
                        </div>
                    </div>
                </div>

                <!-- Posts Table -->
                <div class="posts-section">
                    <div class="section-header">
                        <h2><i class="fas fa-list"></i>Danh sách tin đăng của tôi</h2>
                        <a href="${pageContext.request.contextPath}/post/create" class="btn btn-primary">
                            <i class="fas fa-plus"></i>Đăng tin mới
                        </a>
                    </div>
                    
                    <div class="posts-table-container">
                        <c:choose>
                            <c:when test="${not empty posts}">
                                <table class="posts-table">
                                    <thead>
                                        <tr>
                                            <th width="50">#</th>
                                            <th>Tiêu đề</th>
                                            <th width="200">Địa chỉ</th>
                                            <th width="120">Giá (VNĐ)</th>
                                            <th width="100">Diện tích</th>
                                            <th width="120">Trạng thái</th>
                                            <th width="150">Thời gian đăng</th>
                                            <th width="100">Ngày tạo</th>
                                            <th width="180">Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="p" items="${posts}" varStatus="st">
                                            <tr>
                                                <td>${(page-1)*pageSize + st.index + 1}</td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/post/detail?id=${p.postId}" class="post-title" target="_blank">
                                                        ${p.title}
                                                    </a>
                                                </td>
                                                <td class="address">${p.address}</td>
                                                <td class="price">
                                                    <c:if test="${p.price != null}">
                                                        <fmt:formatNumber value="${p.price}" pattern="#,###" /> VNĐ
                                                    </c:if>
                                                </td>
                                                <td>
                                                    <c:if test="${p.area != null}">${p.area} m²</c:if>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${p.statusName == 'APPROVED'}">
                                                            <span class="badge success">Đã duyệt</span>
                                                        </c:when>
                                                        <c:when test="${p.statusName == 'PENDING'}">
                                                            <span class="badge warning">Chờ duyệt</span>
                                                        </c:when>
                                                        <c:when test="${p.statusName == 'SCHEDULED'}">
                                                            <span class="badge warning">Đã lập lịch</span>
                                                        </c:when>
                                                        <c:when test="${p.statusName == 'REJECTED'}">
                                                            <span class="badge error">Bị từ chối</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge secondary">Nháp</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="scheduled-time">
                                                    <c:choose>
                                                        <c:when test="${p.statusName == 'SCHEDULED' && p.scheduledAt != null}">
                                                            <div class="time-scheduled">
                                                                <i class="fas fa-clock"></i>
                                                                <span id="scheduled-${p.postId}">
                                                                    <fmt:formatDate value="${p.scheduledAt}" pattern="dd/MM/yyyy" /><br>
                                                                    <small><fmt:formatDate value="${p.scheduledAt}" pattern="HH:mm" /></small>
                                                                </span>
                                                                <button class="btn btn-sm btn-secondary btn-edit-schedule" data-post-id="${p.postId}" data-post-title="${p.title}" title="Chỉnh sửa thời gian">
                                                                    <i class="fas fa-edit"></i>
                                                                </button>
                                                            </div>
                                                        </c:when>
                                                        <c:when test="${p.publishedAt != null}">
                                                            <div class="time-published">
                                                                <i class="fas fa-check-circle"></i>
                                                                <fmt:formatDate value="${p.publishedAt}" pattern="dd/MM/yyyy" /><br>
                                                                <small><fmt:formatDate value="${p.publishedAt}" pattern="HH:mm" /></small>
                                                            </div>
                                                        </c:when>
                                                        <c:when test="${p.statusName == 'APPROVED'}">
                                                            <div class="time-published">
                                                                <i class="fas fa-check-circle"></i>
                                                                <fmt:formatDate value="${p.createdAt}" pattern="dd/MM/yyyy" /><br>
                                                                <small><fmt:formatDate value="${p.createdAt}" pattern="HH:mm" /></small>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="time-not-published">
                                                                <i class="fas fa-minus-circle"></i>
                                                                Chưa đăng
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="date">
                                                    <c:if test="${p.createdAt != null}">
                                                        <fmt:formatDate value="${p.createdAt}" pattern="dd/MM/yyyy" />
                                                    </c:if>
                                                </td>
                                                <td class="actions">
                                                    <a href="${pageContext.request.contextPath}/post/edit?id=${p.postId}" class="btn btn-sm btn-edit" title="Sửa">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <c:if test="${p.statusName == 'SCHEDULED'}">
                                                        <button class="btn btn-sm btn-secondary btn-publish-now" data-post-id="${p.postId}" data-post-title="${p.title}" title="Đăng ngay">
                                                            <i class="fas fa-play"></i>
                                                        </button>
                                                    </c:if>
                                                    <button class="btn btn-sm btn-delete btn-delete-post" data-post-id="${p.postId}" data-post-title="${p.title}" title="Xóa">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <i class="fas fa-inbox"></i>
                                    <h3>Bạn chưa có tin đăng nào</h3>
                                    <p>Hãy bắt đầu đăng tin đầu tiên của bạn!</p>
                                    <a href="${pageContext.request.contextPath}/post/create" class="btn btn-primary">
                                        <i class="fas fa-plus"></i>Đăng tin ngay
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <div class="pagination">
                            <c:if test="${page > 1}">
                                <a href="${pageContext.request.contextPath}/post/my?page=${page-1}&pageSize=${pageSize}" class="page-btn">
                                    <i class="fas fa-chevron-left"></i>
                                </a>
                            </c:if>
                            
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <a href="${pageContext.request.contextPath}/post/my?page=${i}&pageSize=${pageSize}" class="page-btn ${i == page ? 'active' : ''}">
                                    ${i}
                                </a>
                            </c:forEach>
                            
                            <c:if test="${page < totalPages}">
                                <a href="${pageContext.request.contextPath}/post/my?page=${page+1}&pageSize=${pageSize}" class="page-btn">
                                    <i class="fas fa-chevron-right"></i>
                                </a>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </main>
        </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal" id="deleteModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Xác nhận xóa</h3>
                <button class="modal-close" onclick="closeModal('deleteModal')">&times;</button>
            </div>
            <div class="modal-body">
                <p>Bạn có chắc chắn muốn xóa tin đăng "<span id="postTitle"></span>" không?</p>
                <p class="warning"><i class="fas fa-exclamation-triangle"></i>Hành động này không thể hoàn tác!</p>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="closeModal('deleteModal')">Hủy</button>
                <form id="deleteForm" method="post" style="display: inline;">
                    <input type="hidden" name="id" id="deletePostId">
                    <button type="submit" class="btn btn-delete">
                        <i class="fas fa-trash"></i>Xóa
                    </button>
                </form>
            </div>
        </div>
    </div>

    <!-- Edit Schedule Time Modal -->
    <div class="modal" id="scheduleModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Chỉnh sửa thời gian đăng</h3>
                <button class="modal-close" onclick="closeModal('scheduleModal')">&times;</button>
            </div>
            <div class="modal-body">
                <p>Tin đăng: "<span id="schedulePostTitle"></span>"</p>
                <div style="margin: 15px 0;">
                    <label for="newScheduleTime" style="display: block; margin-bottom: 5px; font-weight: 500;">Thời gian đăng mới:</label>
                    <input type="datetime-local" id="newScheduleTime" class="form-control" style="width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                <p class="warning" style="font-size: 13px;"><i class="fas fa-info-circle"></i>Thời gian phải sau thời điểm hiện tại</p>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="closeModal('scheduleModal')">Hủy</button>
                <button class="btn btn-primary" onclick="updateScheduleTime()">
                    <i class="fas fa-save"></i>Cập nhật
                </button>
            </div>
        </div>
    </div>

    <!-- Publish Now Modal -->
    <div class="modal" id="publishModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Đăng bài ngay</h3>
                <button class="modal-close" onclick="closeModal('publishModal')">&times;</button>
            </div>
            <div class="modal-body">
                <p>Bạn có muốn đăng tin "<span id="publishPostTitle"></span>" ngay bây giờ không?</p>
                <p class="warning"><i class="fas fa-info-circle"></i>Bài viết sẽ được đăng lên trang chủ ngay lập tức</p>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="closeModal('publishModal')">Hủy</button>
                <form id="publishForm" method="post" style="display: inline;">
                    <input type="hidden" name="id" id="publishPostId">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-play"></i>Đăng ngay
                    </button>
                </form>
            </div>
        </div>
    </div>

    <script>
        // Global variables for modals
        let currentPostId = null;
        let currentPostTitle = null;

        // Optimized JavaScript - All in one minified
        document.addEventListener('DOMContentLoaded',function(){
            const sidebar=document.getElementById('sidebar');
            const sidebarToggle=document.getElementById('sidebarToggle');
            const mainContent=document.querySelector('.main-content');
            const deleteModal=document.getElementById('deleteModal');
            const scheduleModal=document.getElementById('scheduleModal');
            const publishModal=document.getElementById('publishModal');
            
            // Sidebar toggle
            if(sidebarToggle){
                sidebarToggle.addEventListener('click',function(){
                    if(window.innerWidth<=768){
                        sidebar.classList.toggle('show');
                    }else{
                        sidebar.classList.toggle('collapsed');
                        mainContent.classList.toggle('expanded');
                    }
                });
            }
            
            // Handle window resize
            window.addEventListener('resize',function(){
                if(window.innerWidth>768){
                    sidebar.classList.remove('show');
                }else{
                    sidebar.classList.remove('collapsed');
                    mainContent.classList.remove('expanded');
                }
            });
            
            // Close sidebar on mobile when clicking outside
            document.addEventListener('click',function(event){
                if(window.innerWidth<=768 && 
                   !sidebar.contains(event.target) && 
                   !sidebarToggle.contains(event.target) && 
                   sidebar.classList.contains('show')){
                    sidebar.classList.remove('show');
                }
            });
            
            // Set active menu
            const currentPath=window.location.pathname;
            const menuLinks=document.querySelectorAll('.sidebar-menu a');
            menuLinks.forEach(function(link){
                link.classList.remove('active');
                const href=link.getAttribute('href');
                if(href && (currentPath.includes(href) || 
                   (href.includes('my_posts') && currentPath.includes('post/my')))){
                    link.classList.add('active');
                }
            });
            
            // Auto dismiss alerts
            setTimeout(function(){
                const alerts=document.querySelectorAll('.alert');
                alerts.forEach(function(alert){
                    alert.style.display='none';
                });
            },5000);

            // ✅ Event listeners for scheduled post actions
            document.querySelectorAll('.btn-edit-schedule').forEach(function(btn){
                btn.addEventListener('click', function(){
                    const postId = this.getAttribute('data-post-id');
                    const postTitle = this.getAttribute('data-post-title');
                    editScheduleTime(postId, postTitle);
                });
            });

            document.querySelectorAll('.btn-publish-now').forEach(function(btn){
                btn.addEventListener('click', function(){
                    const postId = this.getAttribute('data-post-id');
                    const postTitle = this.getAttribute('data-post-title');
                    publishNow(postId, postTitle);
                });
            });

            document.querySelectorAll('.btn-delete-post').forEach(function(btn){
                btn.addEventListener('click', function(){
                    const postId = this.getAttribute('data-post-id');
                    const postTitle = this.getAttribute('data-post-title');
                    deletePost(postId, postTitle);
                });
            });
        });
        
        // ✅ Edit scheduled time function
        function editScheduleTime(postId, postTitle) {
            currentPostId = postId;
            currentPostTitle = postTitle;
            
            const modal = document.getElementById('scheduleModal');
            const postTitleElement = document.getElementById('schedulePostTitle');
            const timeInput = document.getElementById('newScheduleTime');
            
            postTitleElement.textContent = postTitle;
            
            // Set minimum time to current time
            const now = new Date();
            now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
            timeInput.min = now.toISOString().slice(0,16);
            
            // Get current scheduled time if available
            const currentTimeElement = document.getElementById('scheduled-' + postId);
            if (currentTimeElement) {
                const currentTime = currentTimeElement.textContent.trim();
                if (currentTime && currentTime !== '-') {
                    // Convert dd/MM/yyyy HH:mm to yyyy-MM-ddTHH:mm
                    const parts = currentTime.split(' ');
                    if (parts.length === 2) {
                        const dateParts = parts[0].split('/');
                        const timePart = parts[1];
                        if (dateParts.length === 3) {
                            const isoDateTime = dateParts[2] + '-' + dateParts[1] + '-' + dateParts[0] + 'T' + timePart;
                            timeInput.value = isoDateTime;
                        }
                    }
                }
            }
            
            modal.classList.add('show');
        }

        // ✅ Update scheduled time function
        function updateScheduleTime() {
            const timeInput = document.getElementById('newScheduleTime');
            const newTime = timeInput.value;
            
            if (!newTime) {
                alert('Vui lòng chọn thời gian đăng');
                return;
            }
            
            const selectedTime = new Date(newTime);
            const now = new Date();
            
            if (selectedTime <= now) {
                alert('Thời gian đăng phải sau thời điểm hiện tại');
                return;
            }
            
            // Create form and submit
            const form = document.createElement('form');
            form.method = 'post';
            form.action = '${pageContext.request.contextPath}/post/updateSchedule';
            
            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = currentPostId;
            
            const timeInputField = document.createElement('input');
            timeInputField.type = 'hidden';
            timeInputField.name = 'scheduledTime';
            timeInputField.value = newTime;
            
            form.appendChild(idInput);
            form.appendChild(timeInputField);
            document.body.appendChild(form);
            form.submit();
        }

        // ✅ Publish now function
        function publishNow(postId, postTitle) {
            const modal = document.getElementById('publishModal');
            const publishForm = document.getElementById('publishForm');
            const postTitleElement = document.getElementById('publishPostTitle');
            const publishPostIdInput = document.getElementById('publishPostId');
            
            publishForm.action = '${pageContext.request.contextPath}/post/publishNow';
            publishPostIdInput.value = postId;
            postTitleElement.textContent = postTitle;
            modal.classList.add('show');
        }
        
        // Delete post function
        function deletePost(postId,postTitle){
            const modal=document.getElementById('deleteModal');
            const deleteForm=document.getElementById('deleteForm');
            const postTitleElement=document.getElementById('postTitle');
            const deletePostIdInput=document.getElementById('deletePostId');
            
            deleteForm.action='${pageContext.request.contextPath}/post/delete';
            deletePostIdInput.value=postId;
            postTitleElement.textContent=postTitle;
            modal.classList.add('show');
        }
        
        // ✅ Close modal function - updated to handle multiple modals
        function closeModal(modalId) {
            if (modalId) {
                document.getElementById(modalId).classList.remove('show');
            } else {
                // Legacy support - close all modals
                document.querySelectorAll('.modal').forEach(function(modal) {
                    modal.classList.remove('show');
                });
            }
        }
        
        // Close modal when clicking outside
        document.addEventListener('click',function(event){
            const modals = document.querySelectorAll('.modal');
            modals.forEach(function(modal) {
                if(event.target === modal){
                    modal.classList.remove('show');
                }
            });
        });
    </script>
</body>
</html>
